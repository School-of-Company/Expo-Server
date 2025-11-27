package team.startup.expo.domain.application.service.impl;

import lombok.RequiredArgsConstructor;
import team.startup.expo.domain.application.exception.AlreadyApplicationUserException;
import team.startup.expo.domain.application.presentation.dto.request.ApplicationForTraineeRequestDto;
import team.startup.expo.domain.application.service.PreApplicationForTraineeService;
import team.startup.expo.domain.expo.entity.Expo;
import team.startup.expo.domain.expo.exception.NotFoundExpoException;
import team.startup.expo.domain.expo.exception.NotInProgressExpoException;
import team.startup.expo.domain.expo.repository.ExpoRepository;
import team.startup.expo.domain.form.entity.Form;
import team.startup.expo.domain.form.entity.ParticipationType;
import team.startup.expo.domain.form.exception.NotFoundFormException;
import team.startup.expo.domain.form.exception.OutOfRegistrationPeriodException;
import team.startup.expo.domain.form.repository.FormRepository;
import team.startup.expo.domain.trainee.entity.ApplicationType;
import team.startup.expo.domain.trainee.entity.Trainee;
import team.startup.expo.domain.trainee.repository.TraineeRepository;
import team.startup.expo.global.annotation.TransactionService;
import team.startup.expo.global.date.DateUtil;

import java.time.LocalDateTime;

@TransactionService
@RequiredArgsConstructor
public class PreApplicationForTraineeServiceImpl implements PreApplicationForTraineeService {

    private final TraineeRepository traineeRepository;
    private final ExpoRepository expoRepository;
    private final FormRepository formRepository;
    private final DateUtil dateUtil;
    private final DynamicJsonDataRepository dynamicJsonDataRepository;

    public void execute(String expoId, ApplicationForTraineeRequestDto dto) {
        Expo expo = expoRepository.findById(expoId)
                .orElseThrow(NotFoundExpoException::new);

        if (!dateUtil.dateComparison(expo.getStartedDay(), expo.getFinishedDay()))
            throw new NotInProgressExpoException();

        Form form = formRepository.findByExpoAndParticipationTypeAndApplicationType(expo, ParticipationType.TRAINEE, ApplicationType.PRE)
                .orElseThrow(NotFoundFormException::new);

        if (!dateUtil.dateTimeComparison(form.getStartDate(), form.getEndDate()))
            throw new OutOfRegistrationPeriodException();

        if (traineeRepository.existsByTrainingIdAndExpo(dto.getTrainingId(), expo) || traineeRepository.existsByPhoneNumberAndExpo(dto.getPhoneNumber(), expo)) {
            throw new AlreadyApplicationUserException();
        }

        saveTrainee(dto, expo);
    }

    private void saveTrainee(ApplicationForTraineeRequestDto dto, Expo expo) {
        Trainee trainee = traineeRepository.findByPhoneNumberAndExpoForWrite(dto.getPhoneNumber(), expo)
                .orElse(Trainee.builder()
                        .trainingId(dto.getTrainingId())
                        .phoneNumber(dto.getPhoneNumber())
                        .name(dto.getName())
                        .informationJson(dto.getInformationJson())
                        .name(dto.getName())
                        .applicationType(ApplicationType.PRE)
                        .personalInformationStatus(dto.getPersonalInformationStatus())
                        .expo(expo)
                        .applicationDate(LocalDateTime.now())
                        .build());

        traineeRepository.save(trainee);
    }
}
