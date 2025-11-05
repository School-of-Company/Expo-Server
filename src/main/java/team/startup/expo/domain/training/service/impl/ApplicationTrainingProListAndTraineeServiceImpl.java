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
import team.startup.expo.domain.mongo.entity.OwnerType;
import team.startup.expo.domain.mongo.repository.DynamicJsonDataRepository;
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

    @Override
    public void execute(String expoId, ApplicationTrainingProListAndTraineeRequestDto dto) {
        Expo expo = expoRepository.findById(expoId)
                .orElseThrow(NotFoundExpoException::new);

        if (!dateUtil.dateComparison(expo.getStartedDay(), expo.getFinishedDay()))
            throw new NotInProgressExpoException();

        ParsedInfo parsedInfo = extractNameAndPhoneAndTrainingId(dto.getInformationJson());

        Trainee trainee = saveTrainee(dto, expo, parsedInfo);

        Map<Long, Integer> programParticipantCountMap = trainingProgramUserRepository.countTrainingProgramUserByTrainingProIdIn(dto.getTrainingProIds());

        List<TrainingProgram> trainingProgramList = trainingProgramRepository.findAllByIdIn(dto.getTrainingProIds());
        Map<Long, TrainingProgram> programById = trainingProgramList.stream()
                .collect(java.util.stream.Collectors.toMap(TrainingProgram::getId, java.util.function.Function.identity()));

        List<String> fullProgramTitles = new java.util.ArrayList<>();
        for (Long programId : dto.getTrainingProIds()) {
            int count = programParticipantCountMap.getOrDefault(programId, 0);
            TrainingProgram program = programById.get(programId);
            int limit = (program.getCategory() == Category.ESSENTIAL) ? 200 : 25;
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

    private Trainee saveTrainee(ApplicationTrainingProListAndTraineeRequestDto dto, Expo expo, ParsedInfo parsedInfo) {
        Trainee trainee = traineeRepository.findByPhoneNumberAndExpoForWrite(parsedInfo.phoneNumber, expo)
                .orElse(Trainee.builder()
                        .trainingId(parsedInfo.trainingId)
                        .phoneNumber(parsedInfo.phoneNumber)
                        .authority(Authority.ROLE_TRAINEE)
                        .name(parsedInfo.name)
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
                .set("json", dto.getInformationJson())
                .setOnInsert("ownerType", OwnerType.TRAINEE)
                .setOnInsert("ownerId", trainee.getId());

        mongoTemplate.upsert(query, update, DynamicJsonDataRepository.class);

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

    private ParsedInfo extractNameAndPhoneAndTrainingId(String informationJson) {
        Map<String, Object> map;
        try {
            map = objectMapper.readValue(informationJson, new TypeReference<Map<String, Object>>() {});
        } catch (Exception e) {
            throw new IllegalArgumentException("informationJson parse error", e);
        }

        String name = null;
        String phone = null;
        String trainingId = null;

        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String rawKey = entry.getKey();
            Object val = entry.getValue();
            if (val == null) continue;
            String value = String.valueOf(val).trim();
            if (value.isEmpty()) continue;

            String norm = normalizeLabel(rawKey);

            if (isNameLabel(norm)) {
                name = value;
            } else if (isPhoneLabel(norm)) {
                phone = onlyDigits(value);
            } else if (isTrainingIdLabel(norm)) {
                trainingId = value;
            }
        }

        if (phone == null) {
            for (Object v : map.values()) {
                if (v == null) continue;
                String digits = onlyDigits(String.valueOf(v));
                if (digits.matches("^01[016789]\\d{7,8}$")) {
                    phone = digits;
                    break;
                }
            }
        }

        if (trainingId == null) {
            trainingId = "";
        }

        if (name == null || phone == null) {
            throw new IllegalStateException("이름 또는 전화번호, 연수원 아이디 추출 실패");
        }
        return new ParsedInfo(name, phone, trainingId);
    }

    private String normalizeLabel(String s) {
        if (s == null) return "";
        String noParen = s.replaceAll("\\([^)]*\\)", "");
        String compact = noParen.replaceAll("\\s+", "")
                .replaceAll("[^\\p{L}\\p{N}]", "")
                .toLowerCase();
        return compact;
    }

    private boolean isNameLabel(String norm) {
        return norm.contains("성함") || norm.equals("이름") || norm.equals("name");
    }

    private boolean isPhoneLabel(String norm) {
        return norm.equals("휴대폰번호")
                || norm.equals("휴대폰")
                || norm.contains("전화번호")
                || norm.equals("연락처")
                || norm.equals("phonenumber")
                || norm.equals("phone")
                || norm.equals("mobile");
    }

    private String onlyDigits(String s) {
        if (s == null) return null;
        return s.replaceAll("\\D", "");
    }

    private record ParsedInfo(String name, String phoneNumber, String trainingId) {}

    private boolean isTrainingIdLabel(String norm) {
        return norm.contains("연수원아이디")
                || norm.contains("trainingid")
                || norm.contains("traineeid");
    }
}
