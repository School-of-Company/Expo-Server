package team.startup.expo.domain.application.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import team.startup.expo.domain.admin.entity.Authority;
import team.startup.expo.domain.application.exception.NotEnterSchoolDetailException;
import team.startup.expo.domain.expo.entity.Expo;
import team.startup.expo.domain.expo.exception.NotFoundExpoException;
import team.startup.expo.domain.expo.exception.NotInProgressExpoException;
import team.startup.expo.domain.expo.repository.ExpoRepository;
import team.startup.expo.domain.application.exception.AlreadyApplicationUserException;
import team.startup.expo.domain.application.presentation.dto.request.ApplicationForParticipantRequestDto;
import team.startup.expo.domain.application.service.PreApplicationForParticipantService;
import team.startup.expo.domain.participant.entity.SchoolLevel;
import team.startup.expo.domain.participant.entity.StandardParticipant;
import team.startup.expo.domain.participant.repository.StandardParticipantRepository;
import team.startup.expo.domain.sms.event.SendQrEvent;
import team.startup.expo.domain.trainee.entity.ApplicationType;
import team.startup.expo.domain.trainee.repository.TraineeRepository;
import team.startup.expo.global.annotation.TransactionService;
import team.startup.expo.global.date.DateUtil;
import team.startup.expo.global.exception.ErrorCode;
import team.startup.expo.global.exception.GlobalException;

@TransactionService
@RequiredArgsConstructor
public class PreApplicationForParticipantServiceImpl implements PreApplicationForParticipantService {

    private final ExpoRepository expoRepository;
    private final StandardParticipantRepository standardParticipantRepository;
    private final TraineeRepository traineeRepository;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final DateUtil dateUtil;

    public void execute(String expoId, ApplicationForParticipantRequestDto dto) {
        Expo expo = expoRepository.findById(expoId)
                .orElseThrow(NotFoundExpoException::new);

        if (!dateUtil.dateComparison(expo.getStartedDay(), expo.getFinishedDay()))
            throw new NotInProgressExpoException();

        if (standardParticipantRepository.existsByPhoneNumberAndExpo(dto.getPhoneNumber(), expo) || traineeRepository.existsByPhoneNumberAndExpo(dto.getPhoneNumber(), expo))
            throw new AlreadyApplicationUserException();

        if (dto.getSchoolDetail() == null && dto.getSchoolLevel() != SchoolLevel.KINDERGARTEN && dto.getSchoolLevel() != SchoolLevel.OTHER)
            throw new NotEnterSchoolDetailException();

        saveParticipant(expo, dto);

        try {
            applicationEventPublisher.publishEvent(new SendQrEvent(expoId, dto.getPhoneNumber(), Authority.ROLE_STANDARD));
        } catch (Exception e) {
            throw new GlobalException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    private void saveParticipant(Expo expo, ApplicationForParticipantRequestDto dto) {
        StandardParticipant standardParticipant = standardParticipantRepository.findByPhoneNumberAndExpoForWrite(dto.getPhoneNumber(), expo)
                .orElse(StandardParticipant.builder()
                        .name(dto.getName())
                        .phoneNumber(dto.getPhoneNumber())
                        .authority(Authority.ROLE_STANDARD)
                        .affiliation(dto.getAffiliation())
                        .schoolLevel(dto.getSchoolLevel())
                        .schoolDetail(dto.getSchoolDetail())
                        .informationJson(dto.getInformationJson())
                        .applicationType(ApplicationType.FIELD)
                        .personalInformationStatus(dto.getPersonalInformationStatus())
                        .expo(expo)
                        .build());

        standardParticipantRepository.save(standardParticipant);
    }
}
