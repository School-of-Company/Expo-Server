package team.startup.expo.domain.application.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import team.startup.expo.domain.admin.entity.Authority;
import team.startup.expo.domain.application.presentation.dto.request.ApplicationTemporaryQrRequestDto;
import team.startup.expo.domain.application.presentation.dto.response.ApplicationTemporaryQrResponseDto;
import team.startup.expo.domain.application.service.FieldApplicationTemporaryQrService;
import team.startup.expo.domain.expo.entity.Expo;
import team.startup.expo.domain.expo.exception.NotFoundExpoException;
import team.startup.expo.domain.expo.exception.NotInProgressExpoException;
import team.startup.expo.domain.expo.repository.ExpoRepository;
import team.startup.expo.domain.mongo.entity.DynamicJsonData;
import team.startup.expo.domain.mongo.entity.OwnerType;
import team.startup.expo.domain.mongo.repository.DynamicJsonDataRepository;
import team.startup.expo.domain.participant.entity.StandardParticipant;
import team.startup.expo.domain.participant.repository.StandardParticipantRepository;
import team.startup.expo.domain.trainee.entity.ApplicationType;
import team.startup.expo.global.annotation.TransactionService;
import team.startup.expo.global.date.DateUtil;

import java.util.Map;
import java.util.UUID;

@TransactionService
@RequiredArgsConstructor
public class FieldApplicationTemporaryQrServiceImpl implements FieldApplicationTemporaryQrService {

    private final ExpoRepository expoRepository;
    private final StandardParticipantRepository standardParticipantRepository;
    private final DateUtil dateUtil;
    private final DynamicJsonDataRepository dynamicJsonDataRepository;
    private final ObjectMapper objectMapper;

    public ApplicationTemporaryQrResponseDto execute(String expoId, ApplicationTemporaryQrRequestDto dto) {
        Expo expo = expoRepository.findById(expoId)
                .orElseThrow(NotFoundExpoException::new);

        if (!dateUtil.dateComparison(expo.getStartedDay(), expo.getFinishedDay()))
            throw new NotInProgressExpoException();

        StandardParticipant standardParticipant = saveParticipant(expo, dto);

        expo.plusApplicationPerson();

        return ApplicationTemporaryQrResponseDto.builder()
                .participantId(standardParticipant.getId())
                .phoneNumber(standardParticipant.getPhoneNumber())
                .expoId(expo.getId())
                .build();
    }

    private StandardParticipant saveParticipant(Expo expo, ApplicationTemporaryQrRequestDto dto) {
        String phoneNumber;
        do {
            phoneNumber = generateUniquePhoneNumber();
        } while (standardParticipantRepository.existsByPhoneNumber(phoneNumber));

        StandardParticipant standardParticipant = StandardParticipant.builder()
                .name(dto.getName())
                .phoneNumber(phoneNumber)
                .authority(Authority.ROLE_STANDARD)
                .applicationType(ApplicationType.FIELD)
                .personalInformationStatus(dto.getPersonalInformationStatus())
                .smsTryTime(0)
                .expo(expo)
                .build();

        standardParticipantRepository.save(standardParticipant);

        Map<String, Object> answers = parseJson(dto.getInformationJson());
        DynamicJsonData doc = new DynamicJsonData(
                null,
                OwnerType.STANDARD_PARTICIPANT,
                standardParticipant.getId(),
                answers
        );
        dynamicJsonDataRepository.save(doc);

        return standardParticipant;
    }

    public String generateUniquePhoneNumber() {
        String temporaryPhoneNumber = UUID.randomUUID().toString().replaceAll("[^0-9]", "").substring(0, 8);
        return "000" + temporaryPhoneNumber.substring(0, 4) + temporaryPhoneNumber.substring(4);
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
