package team.startup.expo.domain.excel.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import team.startup.expo.domain.excel.service.TraineeInfoToExcelService;
import team.startup.expo.domain.expo.entity.Expo;
import team.startup.expo.domain.expo.exception.NotFoundExpoException;
import team.startup.expo.domain.expo.repository.ExpoRepository;
import team.startup.expo.domain.trainee.entity.Trainee;
import team.startup.expo.domain.trainee.repository.TraineeRepository;
import team.startup.expo.global.annotation.ReadOnlyTransactionService;

import java.util.*;

@ReadOnlyTransactionService
@RequiredArgsConstructor
public class TraineeInfoToExcelServiceImpl implements TraineeInfoToExcelService {

    private final TraineeRepository traineeRepository;
    private final ExpoRepository expoRepository;
    private final ObjectMapper objectMapper = new ObjectMapper(); // JSON 변환기

    public void execute(String expoId, HttpServletResponse res) throws JsonProcessingException {
        try {
            Expo expo = expoRepository.findById(expoId)
                    .orElseThrow(NotFoundExpoException::new);

            List<Trainee> traineeList = traineeRepository.findByExpo(expo);

            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("사전 교원연수자 정보");
            sheet.setDefaultColumnWidth(20);

            XSSFFont headerFont = (XSSFFont) workbook.createFont();
            headerFont.setBold(true);
            headerFont.setColor(new XSSFColor(new byte[]{(byte) 255, (byte) 255, (byte) 255}));

            XSSFCellStyle headerStyle = (XSSFCellStyle) workbook.createCellStyle();
            headerStyle.setFillForegroundColor(new XSSFColor(new byte[]{(byte) 34, (byte) 37, (byte) 41}));
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setBorderTop(BorderStyle.THIN);
            headerStyle.setBorderBottom(BorderStyle.THIN);
            headerStyle.setBorderLeft(BorderStyle.THIN);
            headerStyle.setBorderRight(BorderStyle.THIN);
            headerStyle.setFont(headerFont);

            XSSFCellStyle bodyStyle = (XSSFCellStyle) workbook.createCellStyle();
            bodyStyle.setBorderTop(BorderStyle.THIN);
            bodyStyle.setBorderBottom(BorderStyle.THIN);
            bodyStyle.setBorderLeft(BorderStyle.THIN);
            bodyStyle.setBorderRight(BorderStyle.THIN);

            // 기본 헤더
            List<String> headers = new ArrayList<>(List.of("이름", "연수원아이디", "전화번호", "신청방식"));

            // 모든 Trainee의 JSON key를 모아서 dynamicKeys 생성
            Set<String> dynamicKeys = new LinkedHashSet<>();
            for (Trainee trainee : traineeList) {
                String escapedJson = trainee.getInformationJson();
                if (escapedJson != null) {
                    try {
                        String unescapedJson = StringEscapeUtils.unescapeJson(escapedJson);
                        Map<String, String> jsonMap = objectMapper.readValue(unescapedJson, Map.class);
                        dynamicKeys.addAll(jsonMap.keySet());
                    } catch (Exception ignored) {}
                }
            }

            headers.addAll(dynamicKeys);

            // 헤더 생성
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.size(); i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers.get(i));
                cell.setCellStyle(headerStyle);
            }

            int rowCount = 1;
            for (Trainee trainee : traineeList) {
                Row row = sheet.createRow(rowCount++);
                row.createCell(0).setCellValue(trainee.getName());
                row.createCell(1).setCellValue(trainee.getTrainingId());
                row.createCell(2).setCellValue(trainee.getPhoneNumber());
                row.createCell(3).setCellValue(trainee.getApplicationType().toString());

                Map<String, String> jsonMap = new HashMap<>();
                String escapedJson = trainee.getInformationJson();
                if (escapedJson != null) {
                    try {
                        String unescapedJson = StringEscapeUtils.unescapeJson(escapedJson);
                        jsonMap = objectMapper.readValue(unescapedJson, Map.class);
                    } catch (Exception ignored) {}
                }

                int cellIndex = 4;
                for (String key : dynamicKeys) {
                    String value = jsonMap.getOrDefault(key, "");
                    row.createCell(cellIndex++).setCellValue(value);
                }
            }

            String fileName = "Trainee_Information";
            res.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            res.setHeader("Content-Disposition", "attachment; filename=" + fileName + ".xlsx");

            ServletOutputStream outputStream = res.getOutputStream();
            workbook.write(outputStream);
            workbook.close();
        } catch (Exception e) {
            throw new RuntimeException("엑셀 파일 생성 중 오류 발생: " + e.getMessage());
        }
    }
}
