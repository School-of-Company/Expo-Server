package team.startup.expo.domain.attendance.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import team.startup.expo.domain.admin.entity.Authority;
import team.startup.expo.domain.attendance.entity.LeaveManager;
import team.startup.expo.domain.attendance.event.LeaveSmsEvent;
import team.startup.expo.domain.attendance.exception.AlreadyEnterExpoUserException;
import team.startup.expo.domain.attendance.exception.NotEnterAfterThirtySecondException;
import team.startup.expo.domain.attendance.presentation.dto.request.PreEnterScanQrCodeRequestDto;
import team.startup.expo.domain.attendance.presentation.dto.response.PreEnterScanQrCodeResponseDto;
import team.startup.expo.domain.attendance.repository.LeaveManagerRepository;
import team.startup.expo.domain.attendance.service.PreEnterScanQrCodeService;
import team.startup.expo.domain.expo.entity.Expo;
import team.startup.expo.domain.expo.exception.NotFoundExpoException;
import team.startup.expo.domain.expo.exception.NotInProgressExpoException;
import team.startup.expo.domain.expo.repository.ExpoRepository;
import team.startup.expo.domain.form.entity.ParticipationType;
import team.startup.expo.domain.participant.entity.StandardParticipant;
import team.startup.expo.domain.participant.entity.StandardParticipantParticipation;
import team.startup.expo.domain.participant.repository.StandardParticipantParticipationRepository;
import team.startup.expo.domain.participant.repository.StandardParticipantRepository;
import team.startup.expo.domain.sms.exception.NotFoundParticipantException;
import team.startup.expo.domain.sms.exception.NotFoundTraineeException;
import team.startup.expo.domain.trainee.entity.Trainee;
import team.startup.expo.domain.trainee.entity.TraineeParticipation;
import team.startup.expo.domain.trainee.repository.TraineeParticipationRepository;
import team.startup.expo.domain.trainee.repository.TraineeRepository;
import team.startup.expo.global.annotation.TransactionService;
import team.startup.expo.global.date.DateUtil;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

@TransactionService
@RequiredArgsConstructor
public class PreEnterScanQrCodeServiceImpl implements PreEnterScanQrCodeService {

    private final ExpoRepository expoRepository;
    private final TraineeRepository traineeRepository;
    private final StandardParticipantRepository standardParticipantRepository;
    private final TraineeParticipationRepository traineeParticipationRepository;
    private final StandardParticipantParticipationRepository standardParticipantParticipationRepository;
    private final DateUtil dateUtil;
    private final LeaveManagerRepository leaveManagerRepository;
    private final ApplicationEventPublisher applicationEventPublisher;

    public PreEnterScanQrCodeResponseDto execute(String expoId, PreEnterScanQrCodeRequestDto dto) {
        Expo expo = expoRepository.findById(expoId)
                .orElseThrow(NotFoundExpoException::new);

        if (!dateUtil.dateComparison(expo.getStartedDay(), expo.getFinishedDay())) {
            throw new NotInProgressExpoException();
        }

        PreEnterScanQrCodeResponseDto responseDto = null;
        switch (dto.getAuthority()){
            case ROLE_STANDARD -> responseDto = standardParticipantEnterProcess(expo, dto);
            case ROLE_TRAINEE -> responseDto = traineeEnterProcess(expo, dto);
        }

        return responseDto;
    }

    private PreEnterScanQrCodeResponseDto standardParticipantEnterProcess(Expo expo, PreEnterScanQrCodeRequestDto dto) {
        StandardParticipant standardParticipant = standardParticipantRepository.findByPhoneNumberAndExpo(dto.getPhoneNumber(), expo)
                .orElseThrow(NotFoundParticipantException::new);

        Optional<StandardParticipantParticipation> standardParticipantParticipation =
                standardParticipantParticipationRepository.findByExpoAndStandardParticipantAndAttendanceDateForWrite(expo, standardParticipant, LocalDate.now());

        if (leaveManagerRepository.existsById(standardParticipant.getPhoneNumber())) {
            throw new NotEnterAfterThirtySecondException();
        }

        if (standardParticipantParticipation.isEmpty()) {
            StandardParticipantParticipation newStandardParticipantParticipation = StandardParticipantParticipation.builder()
                    .entryTime(LocalDateTime.now())
                    .attendanceDate(LocalDate.now())
                    .standardParticipant(standardParticipant)
                    .expo(expo)
                    .build();

            standardParticipantParticipationRepository.save(newStandardParticipantParticipation);

            LeaveManager leaveManager = LeaveManager.builder()
                    .phoneNumber(standardParticipant.getPhoneNumber())
                    .expoId(expo.getId())
                    .participationType(ParticipationType.STANDARD)
                    .build();

            leaveManagerRepository.save(leaveManager);
        }

        return PreEnterScanQrCodeResponseDto.builder()
                .id(standardParticipant.getId())
                .name(standardParticipant.getName())
                .phoneNumber(standardParticipant.getPhoneNumber())
                .personalInformationStatus(standardParticipant.getPersonalInformationStatus())
                .participationType(ParticipationType.STANDARD)
                .build();
    }

    private PreEnterScanQrCodeResponseDto traineeEnterProcess(Expo expo, PreEnterScanQrCodeRequestDto dto) {
        Trainee trainee = traineeRepository.findByPhoneNumberAndExpo(dto.getPhoneNumber(), expo)
                .orElseThrow(NotFoundTraineeException::new);

        Optional<TraineeParticipation> traineeParticipation =
                traineeParticipationRepository.findByExpoAndTraineeAndAttendanceDateForWrite(expo, trainee, LocalDate.now());

        if (leaveManagerRepository.existsById(trainee.getPhoneNumber())) {
            throw new NotEnterAfterThirtySecondException();
        }

        if (traineeParticipation.isEmpty()) {
            TraineeParticipation newTraineeParticipation = TraineeParticipation.builder()
                    .entryTime(LocalDateTime.now())
                    .attendanceDate(LocalDate.now())
                    .trainee(trainee)
                    .expo(expo)
                    .build();

            traineeParticipationRepository.save(newTraineeParticipation);

            LeaveManager leaveManager = LeaveManager.builder()
                    .phoneNumber(trainee.getPhoneNumber())
                    .expoId(expo.getId())
                    .participationType(ParticipationType.TRAINEE)
                    .build();

            leaveManagerRepository.save(leaveManager);
        }

        return PreEnterScanQrCodeResponseDto.builder()
                .id(trainee.getId())
                .name(trainee.getName())
                .phoneNumber(trainee.getPhoneNumber())
                .personalInformationStatus(trainee.getPersonalInformationStatus())
                .participationType(ParticipationType.TRAINEE)
                .build();
    }
}
