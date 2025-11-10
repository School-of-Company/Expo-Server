package team.startup.expo.domain.application.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import team.startup.expo.domain.admin.entity.Authority;
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
import team.startup.expo.domain.mongo.entity.DynamicJsonData;
import team.startup.expo.domain.mongo.entity.OwnerType;
import team.startup.expo.domain.mongo.repository.DynamicJsonDataRepository;
import team.startup.expo.domain.participant.repository.StandardParticipantRepository;
import team.startup.expo.domain.trainee.entity.ApplicationType;
import team.startup.expo.domain.trainee.entity.Trainee;
import team.startup.expo.domain.trainee.repository.TraineeRepository;
import team.startup.expo.global.annotation.TransactionService;
import team.startup.expo.global.date.DateUtil;

import java.time.LocalDateTime;
import java.util.Map;

@TransactionService
@RequiredArgsConstructor
public class PreApplicationForTraineeServiceImpl implements PreApplicationForTraineeService {

    private final TraineeRepository traineeRepository;
    private final ExpoRepository expoRepository;
    private final FormRepository formRepository;
    private final StandardParticipantRepository standardParticipantRepository;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final DateUtil dateUtil;
    private final DynamicJsonDataRepository dynamicJsonDataRepository;
    private final ObjectMapper objectMapper;

    public void execute(String expoId, ApplicationForTraineeRequestDto dto) {
        Expo expo = expoRepository.findById(expoId)
                .orElseThrow(NotFoundExpoException::new);

        if (!dateUtil.dateComparison(expo.getStartedDay(), expo.getFinishedDay()))
            throw new NotInProgressExpoException();

        Form form = formRepository.findByExpoAndParticipationTypeAndApplicationType(expo, ParticipationType.TRAINEE, ApplicationType.PRE)
                .orElseThrow(NotFoundFormException::new);

        if (!dateUtil.dateTimeComparison(form.getStartDate(), form.getEndDate()))
            throw new OutOfRegistrationPeriodException();

        ParsedInfo parsedInfo = extractNameAndPhone(dto.getInformationJson());

        if (traineeRepository.existsByTrainingIdAndExpo(parsedInfo.trainingId, expo) || traineeRepository.existsByPhoneNumberAndExpo(parsedInfo.phoneNumber, expo)) {
            throw new AlreadyApplicationUserException();
        }

        saveTrainee(dto, expo, parsedInfo);
    }

    private void saveTrainee(ApplicationForTraineeRequestDto dto, Expo expo, PreApplicationForTraineeServiceImpl.ParsedInfo parsedInfo) {
        Trainee trainee = traineeRepository.findByPhoneNumberAndExpoForWrite(dto.getPhoneNumber(), expo)
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

        traineeRepository.save(trainee);

        DynamicJsonData doc = new DynamicJsonData(
                null,
                OwnerType.TRAINEE,
                trainee.getId(),
                dto.getInformationJson()
        );
        dynamicJsonDataRepository.save(doc);
    }

    private PreApplicationForTraineeServiceImpl.ParsedInfo extractNameAndPhone(String informationJson) {
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
        return new PreApplicationForTraineeServiceImpl.ParsedInfo(name, phone, trainingId);
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
