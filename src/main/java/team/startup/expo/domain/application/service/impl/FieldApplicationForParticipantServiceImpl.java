package team.startup.expo.domain.application.service.impl;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import team.startup.expo.domain.mongo.entity.DynamicJsonData;
import team.startup.expo.domain.mongo.entity.OwnerType;
import team.startup.expo.domain.mongo.repository.DynamicJsonDataRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import team.startup.expo.domain.admin.entity.Authority;
import team.startup.expo.domain.expo.entity.Expo;
import team.startup.expo.domain.expo.exception.NotFoundExpoException;
import team.startup.expo.domain.expo.exception.NotInProgressExpoException;
import team.startup.expo.domain.expo.repository.ExpoRepository;
import team.startup.expo.domain.application.exception.AlreadyApplicationUserException;
import team.startup.expo.domain.application.presentation.dto.request.ApplicationForParticipantRequestDto;
import team.startup.expo.domain.application.service.FieldApplicationForParticipantService;
import team.startup.expo.domain.participant.entity.StandardParticipant;
import team.startup.expo.domain.participant.repository.StandardParticipantRepository;
import team.startup.expo.domain.application.event.SendQrEvent;
import team.startup.expo.domain.trainee.entity.ApplicationType;
import team.startup.expo.global.annotation.TransactionService;
import team.startup.expo.global.date.DateUtil;
import team.startup.expo.global.exception.ErrorCode;
import team.startup.expo.global.exception.GlobalException;

@TransactionService
@RequiredArgsConstructor
public class FieldApplicationForParticipantServiceImpl implements FieldApplicationForParticipantService {

    private final ExpoRepository expoRepository;
    private final StandardParticipantRepository standardParticipantRepository;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final DateUtil dateUtil;
    private final DynamicJsonDataRepository dynamicJsonDataRepository;
    private final ObjectMapper objectMapper;

    public void execute(String expoId, ApplicationForParticipantRequestDto dto) {
        Expo expo = expoRepository.findById(expoId)
                .orElseThrow(NotFoundExpoException::new);

        if (!dateUtil.dateComparison(expo.getStartedDay(), expo.getFinishedDay()))
            throw new NotInProgressExpoException();

        StandardParticipant standardParticipant = standardParticipantRepository.findByPhoneNumberAndExpoForNullCheck(dto.getPhoneNumber(), expo);

        if (standardParticipant != null) {
            if (standardParticipant.getSmsTryTime() >= 2) {
                throw new AlreadyApplicationUserException();
            }
        } else {
            saveParticipant(expo, dto);
            expo.plusApplicationPerson();
        }


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
                        .applicationType(ApplicationType.FIELD)
                        .personalInformationStatus(dto.getPersonalInformationStatus())
                        .smsTryTime(0)
                        .expo(expo)
                        .build());

        standardParticipantRepository.save(standardParticipant);
        Map<String, Object> answers = parseJson(dto.getInformationJson());
        DynamicJsonData doc = new DynamicJsonData(
                null,
                OwnerType.STANDARD_PARTICIPANT,
                standardParticipant.getId(),
                answers
        );
        dynamicJsonDataRepository.save(doc);
    }

    private Map<String, Object> parseJson(String raw) {
        try {
            if (raw == null || raw.isBlank()) return null;
            return objectMapper.readValue(raw, new TypeReference<Map<String, Object>>() {});
        } catch (Exception e) {
            return null;
        }
    }
}
