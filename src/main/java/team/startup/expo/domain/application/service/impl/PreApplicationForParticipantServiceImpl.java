package team.startup.expo.domain.application.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import team.startup.expo.domain.admin.entity.Authority;
import team.startup.expo.domain.expo.entity.Expo;
import team.startup.expo.domain.expo.exception.NotFoundExpoException;
import team.startup.expo.domain.expo.exception.NotInProgressExpoException;
import team.startup.expo.domain.expo.repository.ExpoRepository;
import team.startup.expo.domain.application.exception.AlreadyApplicationUserException;
import team.startup.expo.domain.application.presentation.dto.request.ApplicationForParticipantRequestDto;
import team.startup.expo.domain.application.service.PreApplicationForParticipantService;
import team.startup.expo.domain.participant.entity.StandardParticipant;
import team.startup.expo.domain.participant.repository.StandardParticipantRepository;
import team.startup.expo.domain.application.event.SendQrEvent;
import team.startup.expo.domain.mongo.entity.DynamicJsonData;
import team.startup.expo.domain.mongo.entity.OwnerType;
import team.startup.expo.domain.mongo.repository.DynamicJsonDataRepository;
import team.startup.expo.domain.trainee.entity.ApplicationType;
import team.startup.expo.domain.trainee.repository.TraineeRepository;
import team.startup.expo.global.annotation.TransactionService;
import team.startup.expo.global.date.DateUtil;
import team.startup.expo.global.exception.ErrorCode;
import team.startup.expo.global.exception.GlobalException;

import java.time.LocalDateTime;
import java.util.Map;

@TransactionService
@RequiredArgsConstructor
public class PreApplicationForParticipantServiceImpl implements PreApplicationForParticipantService {

    private final ExpoRepository expoRepository;
    private final StandardParticipantRepository standardParticipantRepository;
    private final TraineeRepository traineeRepository;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final DateUtil dateUtil;
    private final DynamicJsonDataRepository dynamicJsonDataRepository;
    private final ObjectMapper objectMapper;

    public void execute(String expoId, ApplicationForParticipantRequestDto dto) {
        Expo expo = expoRepository.findById(expoId)
                .orElseThrow(NotFoundExpoException::new);

        if (!dateUtil.dateComparison(expo.getStartedDay(), expo.getFinishedDay()))
            throw new NotInProgressExpoException();

        ParsedInfo parsedInfo = extractNameAndPhone(dto.getInformationJson());

        StandardParticipant standardParticipant = standardParticipantRepository.findByPhoneNumberAndExpoForNullCheck(parsedInfo.phoneNumber, expo);

        if (standardParticipant != null) {
            if (standardParticipant.getSmsTryTime() >= 2) {
                throw new AlreadyApplicationUserException();
            }
        } else {
            saveParticipant(expo, dto, parsedInfo);
            expo.plusApplicationPerson();
        }

        try {
            applicationEventPublisher.publishEvent(new SendQrEvent(expoId, parsedInfo.phoneNumber, Authority.ROLE_STANDARD));
        } catch (Exception e) {
            throw new GlobalException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    private void saveParticipant(Expo expo, ApplicationForParticipantRequestDto dto, ParsedInfo parsedInfo) {
        StandardParticipant standardParticipant = standardParticipantRepository.findByPhoneNumberAndExpoForWrite(dto.getPhoneNumber(), expo)
                .orElse(StandardParticipant.builder()
                        .name(parsedInfo.name)
                        .phoneNumber(parsedInfo.phoneNumber)
                        .authority(Authority.ROLE_STANDARD)
                        .applicationType(ApplicationType.PRE)
                        .personalInformationStatus(dto.getPersonalInformationStatus())
                        .expo(expo)
                        .smsTryTime(0)
                        .applicationDate(LocalDateTime.now())
                        .build());

        standardParticipantRepository.save(standardParticipant);

        DynamicJsonData doc = new DynamicJsonData(
                null,
                OwnerType.STANDARD_PARTICIPANT,
                standardParticipant.getId(),
                dto.getInformationJson()
        );
        dynamicJsonDataRepository.save(doc);
    }

    private ParsedInfo extractNameAndPhone(String informationJson) {
        Map<String, Object> map;
        try {
            map = objectMapper.readValue(informationJson, new TypeReference<Map<String, Object>>() {});
        } catch (Exception e) {
            throw new IllegalArgumentException("informationJson parse error", e);
        }

        String name = null;
        String phone = null;

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

        if (name == null || phone == null) {
            throw new IllegalStateException("이름 또는 전화번호 추출 실패");
        }
        return new ParsedInfo(name, phone);
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
        return norm.equals("성명") || norm.equals("이름") || norm.equals("name");
    }

    private boolean isPhoneLabel(String norm) {
        return norm.equals("휴대폰번호")
                || norm.equals("휴대폰")
                || norm.equals("전화번호")
                || norm.equals("연락처")
                || norm.equals("phonenumber")
                || norm.equals("phone")
                || norm.equals("mobile");
    }

    private String onlyDigits(String s) {
        if (s == null) return null;
        return s.replaceAll("\\D", "");
    }

    private record ParsedInfo(String name, String phoneNumber) {}
}
