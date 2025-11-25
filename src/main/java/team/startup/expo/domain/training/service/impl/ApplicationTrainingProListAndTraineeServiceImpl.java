package team.startup.expo.domain.training.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import team.startup.expo.domain.admin.entity.Authority;
import team.startup.expo.domain.application.exception.AlreadyApplicationUserException;
import team.startup.expo.domain.expo.entity.Expo;
import team.startup.expo.domain.expo.exception.NotFoundExpoException;
import team.startup.expo.domain.expo.exception.NotInProgressExpoException;
import team.startup.expo.domain.expo.repository.ExpoRepository;
import team.startup.expo.domain.form.entity.Form;
import team.startup.expo.domain.form.entity.ParticipationType;
import team.startup.expo.domain.form.exception.NotFoundFormException;
import team.startup.expo.domain.form.exception.OutOfRegistrationPeriodException;
import team.startup.expo.domain.form.repository.FormRepository;
import team.startup.expo.domain.mongo.entity.DynamicJsonData;
import team.startup.expo.domain.mongo.entity.OwnerType;
import team.startup.expo.domain.trainee.entity.ApplicationType;
import team.startup.expo.domain.trainee.entity.Trainee;
import team.startup.expo.domain.trainee.repository.TraineeRepository;
import team.startup.expo.domain.training.entity.Category;
import team.startup.expo.domain.training.entity.TrainingProgram;
import team.startup.expo.domain.training.entity.TrainingProgramUser;
import team.startup.expo.domain.training.event.TrainingSmsEvent;
import team.startup.expo.domain.training.presentation.dto.request.ApplicationTrainingProListAndTraineeRequestDto;
import team.startup.expo.domain.training.repository.TrainingProgramRepository;
import team.startup.expo.domain.training.repository.TrainingProgramUserRepository;
import team.startup.expo.domain.training.service.ApplicationTrainingProListAndTraineeService;
import team.startup.expo.global.annotation.TransactionService;
import team.startup.expo.global.date.DateUtil;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@TransactionService
@RequiredArgsConstructor
public class ApplicationTrainingProListAndTraineeServiceImpl implements ApplicationTrainingProListAndTraineeService {

    private final ExpoRepository expoRepository;
    private final TraineeRepository traineeRepository;
    private final TrainingProgramRepository trainingProgramRepository;
    private final TrainingProgramUserRepository trainingProgramUserRepository;
    private final DateUtil dateUtil;
    private final ObjectMapper objectMapper;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final MongoTemplate mongoTemplate;
    private final FormRepository formRepository;

    @Override
    public void execute(String expoId, ApplicationTrainingProListAndTraineeRequestDto dto) {
        Expo expo = expoRepository.findById(expoId)
                .orElseThrow(NotFoundExpoException::new);

        if (!dateUtil.dateComparison(expo.getStartedDay(), expo.getFinishedDay()))
            throw new NotInProgressExpoException();

        Form form = formRepository.findByExpoAndParticipationTypeAndApplicationType(expo, ParticipationType.TRAINEE, ApplicationType.PRE)
                .orElseThrow(NotFoundFormException::new);

        if (!dateUtil.dateTimeComparison(form.getStartDate(), form.getEndDate()))
            throw new OutOfRegistrationPeriodException();

        Trainee trainee = saveTrainee(dto, expo);

        Map<Long, Integer> programParticipantCountMap = trainingProgramUserRepository.countTrainingProgramUserByTrainingProIdIn(dto.getTrainingProIds());

        List<TrainingProgram> trainingProgramList = trainingProgramRepository.findAllByIdIn(dto.getTrainingProIds());
        Map<Long, TrainingProgram> programById = trainingProgramList.stream()
                .collect(java.util.stream.Collectors.toMap(TrainingProgram::getId, java.util.function.Function.identity()));

        List<String> fullProgramTitles = new java.util.ArrayList<>();
        for (Long programId : dto.getTrainingProIds()) {
            int count = programParticipantCountMap.getOrDefault(programId, 0);
            TrainingProgram program = programById.get(programId);
            int limit = (program.getCategory() == Category.ESSENTIAL) ? 99999: 25;
            if (count >= limit) {
                fullProgramTitles.add(program.getTitle());
            }
        }

        if (!fullProgramTitles.isEmpty()) {
            throw new IllegalStateException(
                    String.join(", ", fullProgramTitles) + " 연수 프로그램의 정원이 초과되었습니다."
            );
        }

        if (trainingProgramUserRepository.existsByTraineeIdAndIdIn(trainee.getId(), dto.getTrainingProIds()))
            throw new AlreadyApplicationUserException();

        trainingProgramUserRepository.deleteAllByTraineeId(trainee.getId());

        trainingProgramList.forEach(trainingProgram -> {saveTrainingProUser(trainingProgram, trainee);});

        applicationEventPublisher.publishEvent(new TrainingSmsEvent(trainee.getExpo().getId(), trainee.getPhoneNumber(), trainingProgramList));
    }

    private Trainee saveTrainee(ApplicationTrainingProListAndTraineeRequestDto dto, Expo expo) {
        Trainee trainee = traineeRepository.findByPhoneNumberAndExpoForWrite(dto.getPhoneNumber(), expo)
                .orElse(Trainee.builder()
                        .trainingId(dto.getTrainingId())
                        .phoneNumber(dto.getPhoneNumber())
                        .authority(Authority.ROLE_TRAINEE)
                        .name(dto.getName())
                        .applicationType(ApplicationType.PRE)
                        .personalInformationStatus(dto.getPersonalInformationStatus())
                        .expo(expo)
                        .applicationDate(LocalDateTime.now())
                        .build());

        trainee = traineeRepository.save(trainee);

        Query query = Query.query(
                Criteria.where("ownerType").is(OwnerType.TRAINEE)
                        .and("ownerId").is(trainee.getId())
        );
        Update update = new Update()
                .set("answers", dto.getInformationJson())
                .setOnInsert("ownerType", OwnerType.TRAINEE)
                .setOnInsert("ownerId", trainee.getId());

        mongoTemplate.upsert(query, update, DynamicJsonData.class);

        return trainee;
    }

    private void saveTrainingProUser(TrainingProgram trainingProgram, Trainee trainee) {
        trainingProgramUserRepository.save(TrainingProgramUser.builder()
                .attendanceDate(parseDate(trainingProgram.getStartedAt()))
                .trainingProgram(trainingProgram)
                .trainee(trainee)
                .status(false)
                .build());
    }

    private LocalDate parseDate(String dateTimeStr) {
        return LocalDate.parse(dateTimeStr.substring(0, 10)); // "yyyy-MM-dd"
    }
}
