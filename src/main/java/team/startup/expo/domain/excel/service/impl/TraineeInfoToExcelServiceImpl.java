package team.startup.expo.domain.excel.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import team.startup.expo.domain.excel.service.TraineeInfoToExcelService;
import team.startup.expo.domain.expo.entity.Expo;
import team.startup.expo.domain.expo.exception.NotFoundExpoException;
import team.startup.expo.domain.expo.repository.ExpoRepository;
import team.startup.expo.domain.mongo.entity.DynamicJsonData;
import team.startup.expo.domain.mongo.entity.OwnerType;
import team.startup.expo.domain.mongo.repository.DynamicJsonDataRepository;
import team.startup.expo.domain.trainee.entity.Trainee;
import team.startup.expo.domain.trainee.repository.TraineeRepository;
import team.startup.expo.domain.training.entity.Category;
import team.startup.expo.domain.training.entity.TrainingProgramUser;
import team.startup.expo.domain.training.repository.TrainingProgramUserRepository;
import team.startup.expo.global.annotation.ReadOnlyTransactionService;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@ReadOnlyTransactionService
@RequiredArgsConstructor
public class TraineeInfoToExcelServiceImpl implements TraineeInfoToExcelService {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final int EXCEL_CELL_MAX_LEN = 32767;
    private static final int HEADER_ROW_INDEX = 0;
    private static final int DATA_START_ROW_INDEX = 1;
    private static final int DEFAULT_COLUMN_WIDTH = 20;
    private static final byte[] HEADER_BG_COLOR = {(byte) 34, (byte) 37, (byte) 41};
    private static final byte[] HEADER_FONT_COLOR = {(byte) 255, (byte) 255, (byte) 255};

    private final TraineeRepository traineeRepository;
    private final ExpoRepository expoRepository;
    private final DynamicJsonDataRepository dynamicJsonDataRepository;
    private final MongoTemplate mongoTemplate;
    private final TrainingProgramUserRepository trainingProgramUserRepository;

    @Override
    public void execute(String expoId, HttpServletResponse res) {
        try {
            Expo expo = expoRepository.findById(expoId)
                    .orElseThrow(NotFoundExpoException::new);

            List<Trainee> traineeList = traineeRepository.findByExpo(expo);

            List<Long> traineeIds = traineeList.stream()
                    .map(Trainee::getId)
                    .collect(Collectors.toList());

            Map<Long, Map<String, String>> dynamicDataMap = loadDynamicAnswersBatch(traineeIds);
            Map<Long, List<TrainingProgramUser>> programUserMap = loadTrainingProgramUsersBatch(traineeIds);

            Workbook workbook = createWorkbook(traineeList, dynamicDataMap, programUserMap);

            writeToResponse(res, workbook);

        } catch (IOException e) {
            log.error("엑셀 파일 생성 중 IO 오류 발생", e);
            throw new RuntimeException("엑셀 파일 생성 실패", e);
        } catch (Exception e) {
            log.error("엑셀 파일 생성 중 예상치 못한 오류 발생", e);
            throw new RuntimeException("예상치 못한 오류 발생", e);
        }
    }

    private Workbook createWorkbook(
            List<Trainee> traineeList,
            Map<Long, Map<String, String>> dynamicDataMap,
            Map<Long, List<TrainingProgramUser>> programUserMap
    ) {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("사전 교원연수자 정보");
        sheet.setDefaultColumnWidth(DEFAULT_COLUMN_WIDTH);

        XSSFCellStyle headerStyle = createHeaderStyle(workbook);
        XSSFCellStyle bodyStyle = createBodyStyle(workbook);

        Set<String> dynamicKeys = collectDynamicKeys(traineeList, dynamicDataMap);

        List<String> headers = createHeaders(dynamicKeys);
        createHeaderRow(sheet, headers, headerStyle);

        createDataRows(sheet, traineeList, dynamicDataMap, programUserMap, dynamicKeys, bodyStyle);

        return workbook;
    }

    private Set<String> collectDynamicKeys(
            List<Trainee> traineeList,
            Map<Long, Map<String, String>> dynamicDataMap
    ) {
        Set<String> dynamicKeys = new LinkedHashSet<>();
        for (Trainee trainee : traineeList) {
            Map<String, String> parsed = dynamicDataMap.getOrDefault(trainee.getId(), Collections.emptyMap());
            if (!parsed.isEmpty()) {
                dynamicKeys.addAll(parsed.keySet());
            }
        }
        return dynamicKeys;
    }

    private List<String> createHeaders(Set<String> dynamicKeys) {
        List<String> headers = new ArrayList<>(List.of("이름", "연수원아이디", "전화번호", "신청방식"));
        headers.addAll(dynamicKeys);
        headers.add("공통 연수");
        headers.add("선택 연수");
        return headers;
    }

    private void createHeaderRow(Sheet sheet, List<String> headers, XSSFCellStyle headerStyle) {
        Row headerRow = sheet.createRow(HEADER_ROW_INDEX);
        for (int i = 0; i < headers.size(); i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(safe(headers.get(i)));
            cell.setCellStyle(headerStyle);
        }
    }

    private void createDataRows(
            Sheet sheet,
            List<Trainee> traineeList,
            Map<Long, Map<String, String>> dynamicDataMap,
            Map<Long, List<TrainingProgramUser>> programUserMap,
            Set<String> dynamicKeys,
            XSSFCellStyle bodyStyle
    ) {
        int rowCount = DATA_START_ROW_INDEX;
        for (Trainee trainee : traineeList) {
            Row row = sheet.createRow(rowCount++);

            int cellIndex = 0;
            row.createCell(cellIndex++).setCellValue(safe(trainee.getName()));
            row.createCell(cellIndex++).setCellValue(safe(trainee.getTrainingId()));
            row.createCell(cellIndex++).setCellValue(safe(trainee.getPhoneNumber()));
            row.createCell(cellIndex++).setCellValue(safe(trainee.getApplicationType().toString()));

            Map<String, String> jsonMap = dynamicDataMap.getOrDefault(trainee.getId(), Collections.emptyMap());
            for (String key : dynamicKeys) {
                String value = jsonMap.getOrDefault(key, "");
                row.createCell(cellIndex++).setCellValue(safe(value));
            }

            List<TrainingProgramUser> programs = programUserMap.getOrDefault(trainee.getId(), Collections.emptyList());
            String commonTitles = extractProgramTitles(programs, Category.ESSENTIAL);
            String selectiveTitles = extractProgramTitles(programs, null);

            row.createCell(cellIndex++).setCellValue(safe(commonTitles));
            row.createCell(cellIndex++).setCellValue(safe(selectiveTitles));
        }
    }

    private String extractProgramTitles(List<TrainingProgramUser> programs, Category filterCategory) {
        return programs.stream()
                .filter(tpu -> tpu.getTrainingProgram() != null)
                .filter(tpu -> filterCategory == null
                        ? tpu.getTrainingProgram().getCategory() != Category.ESSENTIAL
                        : tpu.getTrainingProgram().getCategory() == filterCategory)
                .map(tpu -> tpu.getTrainingProgram().getTitle())
                .filter(Objects::nonNull)
                .map(TraineeInfoToExcelServiceImpl::safe)
                .collect(Collectors.joining(", "));
    }

    private Map<Long, Map<String, String>> loadDynamicAnswersBatch(List<Long> ownerIds) {
        if (ownerIds.isEmpty()) {
            return Collections.emptyMap();
        }

        try {
            List<DynamicJsonData> dataList = dynamicJsonDataRepository
                    .findByOwnerTypeAndOwnerIdIn(OwnerType.TRAINEE, ownerIds);

            Map<Long, Map<String, String>> result = dataList.stream()
                    .filter(data -> data.getAnswers() != null && !data.getAnswers().isBlank())
                    .collect(Collectors.toMap(
                            DynamicJsonData::getOwnerId,
                            data -> parseJson(data.getAnswers())
                    ));

            Set<Long> foundIds = result.keySet();
            List<Long> missingIds = ownerIds.stream()
                    .filter(id -> !foundIds.contains(id))
                    .collect(Collectors.toList());

            if (!missingIds.isEmpty()) {
                Map<Long, Map<String, String>> fallbackData = loadFromMongoTemplateBatch(missingIds);
                result.putAll(fallbackData);
            }

            return result;
        } catch (Exception e) {
            log.error("동적 데이터 조회 중 오류 발생", e);
            return Collections.emptyMap();
        }
    }

    private Map<Long, Map<String, String>> loadFromMongoTemplateBatch(List<Long> ownerIds) {
        Criteria ownerTypeCriteria = new Criteria().orOperator(
                Criteria.where("ownerType").is(OwnerType.TRAINEE),
                Criteria.where("ownerType").is(OwnerType.TRAINEE.name())
        );

        Query query = new Query(new Criteria().andOperator(
                ownerTypeCriteria,
                Criteria.where("ownerId").in(ownerIds)
        ));

        List<Document> documents = mongoTemplate.find(query, Document.class, "dynamic_json_data");

        if (documents.isEmpty()) {
            documents = mongoTemplate.find(query, Document.class, "dynamicJsonDataRepository");
        }

        return documents.stream()
                .collect(Collectors.toMap(
                        doc -> doc.getLong("ownerId"),
                        doc -> {
                            String raw = (doc.get("answers") instanceof String)
                                    ? (String) doc.get("answers")
                                    : (String) doc.get("json");
                            return parseJson(raw);
                        },
                        (a, b) -> a
                ));
    }

    private Map<String, String> parseJson(String json) {
        if (json == null || json.isBlank()) {
            return Collections.emptyMap();
        }
        try {
            return OBJECT_MAPPER.readValue(json, new TypeReference<Map<String, String>>() {});
        } catch (Exception e) {
            log.warn("JSON 파싱 실패: {}", json, e);
            return Collections.emptyMap();
        }
    }

    private Map<Long, List<TrainingProgramUser>> loadTrainingProgramUsersBatch(List<Long> traineeIds) {
        if (traineeIds.isEmpty()) {
            return Collections.emptyMap();
        }

        List<TrainingProgramUser> allPrograms = trainingProgramUserRepository.findAllByTraineeIdIn(traineeIds);

        return allPrograms.stream()
                .collect(Collectors.groupingBy(tpu -> tpu.getTrainee().getId()));
    }

    private XSSFCellStyle createHeaderStyle(Workbook workbook) {
        XSSFFont headerFont = (XSSFFont) workbook.createFont();
        headerFont.setBold(true);
        headerFont.setColor(new XSSFColor(HEADER_FONT_COLOR));

        XSSFCellStyle style = (XSSFCellStyle) workbook.createCellStyle();
        style.setFillForegroundColor(new XSSFColor(HEADER_BG_COLOR));
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        setBorders(style);
        style.setFont(headerFont);
        return style;
    }

    private XSSFCellStyle createBodyStyle(Workbook workbook) {
        XSSFCellStyle style = (XSSFCellStyle) workbook.createCellStyle();
        setBorders(style);
        return style;
    }

    private void setBorders(CellStyle style) {
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
    }

    private void writeToResponse(HttpServletResponse res, Workbook workbook) throws IOException {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String fileName = "교원연수자_정보_" + timestamp;

        res.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        res.setHeader("Content-Disposition", "attachment; filename=" + fileName + ".xlsx");

        try (ServletOutputStream outputStream = res.getOutputStream()) {
            workbook.write(outputStream);
        } finally {
            workbook.close();
        }
    }

    private static String safe(String s) {
        if (s == null) return "";
        String cleaned = s.replaceAll("[\\x00-\\x08\\x0B\\x0C\\x0E-\\x1F]", "");
        if (cleaned.length() > EXCEL_CELL_MAX_LEN) {
            cleaned = cleaned.substring(0, EXCEL_CELL_MAX_LEN);
        }
        return cleaned;
    }
}