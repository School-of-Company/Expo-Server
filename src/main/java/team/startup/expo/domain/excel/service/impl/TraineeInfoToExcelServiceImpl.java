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
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import team.startup.expo.domain.excel.service.TraineeInfoToExcelService;
import team.startup.expo.domain.expo.entity.Expo;
import team.startup.expo.domain.expo.exception.NotFoundExpoException;
import team.startup.expo.domain.expo.repository.ExpoRepository;
import team.startup.expo.domain.trainee.entity.Trainee;
import team.startup.expo.domain.trainee.repository.TraineeRepository;
import team.startup.expo.domain.training.entity.Category;
import team.startup.expo.domain.training.entity.TrainingProgram;
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
    private final TrainingProgramUserRepository trainingProgramUserRepository;

    @Override
    public void execute(String expoId, HttpServletResponse res) {
        try {
            Expo expo = expoRepository.findById(expoId)
                    .orElseThrow(NotFoundExpoException::new);

            List<Trainee> traineeList = traineeRepository.findByExpo(expo);

            if (traineeList.isEmpty()) {
                throw new RuntimeException("연수자가 존재하지 않습니다.");
            }

            List<Long> traineeIds = traineeList.stream()
                    .map(Trainee::getId)
                    .collect(Collectors.toList());

            Map<Long, Map<String, String>> dynamicDataMap = new HashMap<>();
            for (Trainee trainee : traineeList) {
                Map<String, String> parsed = parseJson(trainee.getInformationJson());
                dynamicDataMap.put(trainee.getId(), parsed);
            }

            Map<Long, List<TrainingProgramUser>> programUserMap = loadTrainingProgramUsersBatch(traineeIds);

            SXSSFWorkbook workbook = createWorkbook(traineeList, dynamicDataMap, programUserMap);

            writeToResponse(res, workbook);

        } catch (IOException e) {
            log.error("엑셀 파일 생성 중 IO 오류 발생", e);
            throw new RuntimeException("엑셀 파일 생성 실패", e);
        } catch (Exception e) {
            log.error("엑셀 파일 생성 중 예상치 못한 오류 발생", e);
            throw new RuntimeException("예상치 못한 오류 발생", e);
        }
    }

    private SXSSFWorkbook createWorkbook(
            List<Trainee> traineeList,
            Map<Long, Map<String, String>> dynamicDataMap,
            Map<Long, List<TrainingProgramUser>> programUserMap
    ) {
        SXSSFWorkbook workbook = new SXSSFWorkbook(500);
        workbook.setCompressTempFiles(true);
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
        headers.add("공통 강연");
        headers.add("선택 강연");
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
            Cell c0 = row.createCell(cellIndex++);
            c0.setCellValue(safe(trainee.getName()));
            c0.setCellStyle(bodyStyle);

            Cell c1 = row.createCell(cellIndex++);
            c1.setCellValue(safe(trainee.getTrainingId()));
            c1.setCellStyle(bodyStyle);

            Cell c2 = row.createCell(cellIndex++);
            c2.setCellValue(safe(trainee.getPhoneNumber()));
            c2.setCellStyle(bodyStyle);

            Cell c3 = row.createCell(cellIndex++);
            c3.setCellValue(safe(trainee.getApplicationType().getKoreanName()));
            c3.setCellStyle(bodyStyle);

            Map<String, String> jsonMap = dynamicDataMap.getOrDefault(trainee.getId(), Collections.emptyMap());
            for (String key : dynamicKeys) {
                String value = jsonMap.getOrDefault(key, "");
                Cell cell = row.createCell(cellIndex++);
                cell.setCellValue(safe(value));
                cell.setCellStyle(bodyStyle);
            }

            List<TrainingProgramUser> programs = programUserMap.getOrDefault(trainee.getId(), Collections.emptyList());

            String commonTitles = programs.stream()
                    .filter(tpu -> tpu.getTrainingProgram() != null &&
                            tpu.getTrainingProgram().getCategory() == Category.ESSENTIAL)
                    .map(tpu -> tpu.getTrainingProgram().getTitle())
                    .distinct()
                    .collect(Collectors.joining(", "));

            String selectiveTitles = programs.stream()
                    .filter(tpu -> tpu.getTrainingProgram() != null &&
                            tpu.getTrainingProgram().getCategory() != Category.ESSENTIAL)
                    .map(tpu -> tpu.getTrainingProgram().getTitle())
                    .distinct()
                    .collect(Collectors.joining(", "));

            Cell commonCell = row.createCell(cellIndex++);
            commonCell.setCellValue(safe(commonTitles));
            commonCell.setCellStyle(bodyStyle);

            Cell selectiveCell = row.createCell(cellIndex++);
            selectiveCell.setCellValue(safe(selectiveTitles));
            selectiveCell.setCellStyle(bodyStyle);
        }
    }

    private String extractProgramTitles(List<TrainingProgramUser> programs, Category filterCategory) {
        return programs.stream()
                .filter(tpu -> tpu.getTrainingProgram() != null)
                .filter(tpu -> filterCategory == null
                        ? tpu.getTrainingProgram().getCategory() != Category.ESSENTIAL
                        : tpu.getTrainingProgram().getCategory() == filterCategory)
                .map(TrainingProgramUser::getTrainingProgram)
                .map(TrainingProgram::getTitle)
                .filter(Objects::nonNull)
                .map(TraineeInfoToExcelServiceImpl::safe)
                .collect(Collectors.joining(", "));
    }

    private Map<Long, List<TrainingProgramUser>> loadTrainingProgramUsersBatch(List<Long> traineeIds) {
        if (traineeIds.isEmpty()) {
            return Collections.emptyMap();
        }

        List<TrainingProgramUser> allPrograms = trainingProgramUserRepository.findAllByTraineeIdIn(traineeIds);

        return allPrograms.stream()
                .collect(Collectors.groupingBy(tpu -> tpu.getTrainee().getId()));
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

    private void writeToResponse(HttpServletResponse res, SXSSFWorkbook workbook) throws IOException {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String fileName = "교원연수자_정보_" + timestamp;

        res.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        res.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + ".xlsx\"");

        try (ServletOutputStream outputStream = res.getOutputStream()) {
            workbook.write(outputStream);
            outputStream.flush();
        } finally {
            workbook.dispose();
            workbook.close();
        }
    }

    private static String safe(String s) {
        if (s == null) return "";
        String cleaned = s
                .replace("\r", "")
                .replace("\n", "")
                .replaceAll("[\\x00-\\x08\\x0B\\x0C\\x0E-\\x1F]", "");
        if (cleaned.length() > EXCEL_CELL_MAX_LEN) {
            cleaned = cleaned.substring(0, EXCEL_CELL_MAX_LEN);
        }
        return cleaned;
    }
}