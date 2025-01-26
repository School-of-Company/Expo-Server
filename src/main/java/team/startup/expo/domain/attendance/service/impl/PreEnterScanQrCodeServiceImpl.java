package team.startup.expo.domain.attendance.service.impl;

import lombok.RequiredArgsConstructor;
import team.startup.expo.domain.admin.entity.Authority;
import team.startup.expo.domain.attendance.exception.AlreadyEnterExpoUserException;
import team.startup.expo.domain.attendance.presentation.dto.request.PreEnterScanQrCodeRequestDto;
import team.startup.expo.domain.attendance.presentation.dto.response.PreEnterScanQrCodeResponseDto;
import team.startup.expo.domain.attendance.service.PreEnterScanQrCodeService;
import team.startup.expo.domain.expo.entity.Expo;
import team.startup.expo.domain.expo.exception.NotFoundExpoException;
import team.startup.expo.domain.expo.repository.ExpoRepository;
import team.startup.expo.domain.participant.entity.StandardParticipant;
import team.startup.expo.domain.participant.repository.StandardParticipantRepository;
import team.startup.expo.domain.sms.exception.NotFoundParticipantException;
import team.startup.expo.domain.sms.exception.NotFoundTraineeException;
import team.startup.expo.domain.trainee.entity.Trainee;
import team.startup.expo.domain.trainee.repository.TraineeRepository;
import team.startup.expo.global.annotation.TransactionService;

@TransactionService
@RequiredArgsConstructor
public class PreEnterScanQrCodeServiceImpl implements PreEnterScanQrCodeService {

    private final TraineeRepository traineeRepository;
    private final StandardParticipantRepository standardParticipantRepository;
    private final ExpoRepository expoRepository;

    public PreEnterScanQrCodeResponseDto execute(String expoId, PreEnterScanQrCodeRequestDto dto) {
        PreEnterScanQrCodeResponseDto responseDto = null;

        Expo expo = expoRepository.findById(expoId)
                .orElseThrow(NotFoundExpoException::new);

        if (dto.getAuthority() == Authority.ROLE_STANDARD) {
            StandardParticipant participant = standardParticipantRepository.findByPhoneNumberAndExpo(dto.getPhoneNumber(), expo)
                    .orElseThrow(NotFoundParticipantException::new);

            if (participant.getAttendanceStatus())
                throw new AlreadyEnterExpoUserException();

            participant.changeAttendanceStatus();

            responseDto = PreEnterScanQrCodeResponseDto.builder()
                    .name(participant.getName())
                    .qrCode(participant.getQrCode())
                    .build();
        } else if (dto.getAuthority() == Authority.ROLE_TRAINEE) {
            Trainee trainee = traineeRepository.findByPhoneNumberAndExpo(dto.getPhoneNumber(), expo)
                    .orElseThrow(NotFoundTraineeException::new);

            if (trainee.getAttendanceStatus())
                throw new AlreadyEnterExpoUserException();

            trainee.changeAttendanceStatus();

            responseDto = PreEnterScanQrCodeResponseDto.builder()
                    .name(trainee.getName())
                    .qrCode(trainee.getQrCode())
                    .build();
        }

        return responseDto;
    }
}
