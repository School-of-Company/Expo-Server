package team.startup.expo.domain.attendance.service.impl;

import lombok.RequiredArgsConstructor;
import team.startup.expo.domain.admin.Authority;
import team.startup.expo.domain.attendance.exception.AlreadyEnterExpoUserException;
import team.startup.expo.domain.attendance.presentation.dto.request.PreEnterScanQrCodeRequestDto;
import team.startup.expo.domain.attendance.service.PreEnterScanQrCodeService;
import team.startup.expo.domain.participant.ExpoParticipant;
import team.startup.expo.domain.participant.repository.ParticipantRepository;
import team.startup.expo.domain.sms.exception.NotFoundParticipantException;
import team.startup.expo.domain.sms.exception.NotFoundTraineeException;
import team.startup.expo.domain.trainee.Trainee;
import team.startup.expo.domain.trainee.repository.TraineeRepository;
import team.startup.expo.global.annotation.TransactionService;

@TransactionService
@RequiredArgsConstructor
public class PreEnterScanQrCodeServiceImpl implements PreEnterScanQrCodeService {

    private final TraineeRepository traineeRepository;
    private final ParticipantRepository participantRepository;

    public void execute(PreEnterScanQrCodeRequestDto dto) {
        if (dto.getAuthority() == Authority.ROLE_STANDARD) {
            ExpoParticipant participant = participantRepository.findByPhoneNumber(dto.getPhoneNumber())
                    .orElseThrow(NotFoundParticipantException::new);

            if (participant.getAttendanceStatus())
                throw new AlreadyEnterExpoUserException();

            participant.changeAttendanceStatus();
        } else if (dto.getAuthority() == Authority.ROLE_TRAINEE) {
            Trainee trainee = traineeRepository.findByPhoneNumber(dto.getPhoneNumber())
                    .orElseThrow(NotFoundTraineeException::new);

            if (trainee.getAttendanceStatus())
                throw new AlreadyEnterExpoUserException();

            trainee.changeAttendanceStatus();
        }
    }
}
