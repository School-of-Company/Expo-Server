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
import team.startup.expo.domain.excel.service.StandardParticipantInfoToExcelService;
import team.startup.expo.domain.expo.entity.Expo;
import team.startup.expo.domain.expo.exception.NotFoundExpoException;
import team.startup.expo.domain.expo.repository.ExpoRepository;
import team.startup.expo.domain.participant.entity.StandardParticipant;
import team.startup.expo.domain.participant.repository.StandardParticipantRepository;
import team.startup.expo.global.annotation.ReadOnlyTransactionService;

import java.util.*;

@ReadOnlyTransactionService
@RequiredArgsConstructor
public class StandardParticipantInfoToExcelServiceImpl implements StandardParticipantInfoToExcelService {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final StandardParticipantRepository standardParticipantRepository;
    private final ExpoRepository expoRepository;

    public void execute(String expoId, HttpServletResponse res) throws JsonProcessingException {
        try {
            Expo expo = expoRepository.findById(expoId)
                    .orElseThrow(NotFoundExpoException::new);

            List<StandardParticipant> standardParticipantList = standardParticipantRepository.findByExpo(expo);

            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("박람회 참가자 정보");
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
            List<String> headers = new ArrayList<>(List.of("이름", "전화번호", "소속", "학교급", "학교 이름", "개인정보 동의 여부", "신청 방식"));

            String escapedJson = standardParticipantList.get(0).getInformationJson();

            String unescapedJson = StringEscapeUtils.unescapeJson(escapedJson);
            Map<String, String> jsonMap = objectMapper.readValue(unescapedJson, Map.class);
            Set<String> dynamicKeys = new LinkedHashSet<>(jsonMap.keySet());

            headers.addAll(dynamicKeys);

            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.size(); i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers.get(i));
                cell.setCellStyle(headerStyle);
            }

            int rowCount = 1;
            for (StandardParticipant participant : standardParticipantList) {
                Row row = sheet.createRow(rowCount++);
                Cell cell3 = row.createCell(3);
                Cell cell6 = row.createCell(6);
                switch (participant.getSchoolLevel()) { // 학교급 Enum 구분
                    case KINDERGARTEN -> cell3.setCellValue("유치원");
                    case ELEMENTARY -> cell3.setCellValue("초등학교");
                    case MIDDLE -> cell3.setCellValue("중학교");
                    case HIGH -> cell3.setCellValue("고등학교");
                    default -> cell3.setCellValue("기타");
                }

                switch (participant.getApplicationType()) { // 신청방식 Enum 구분
                    case PRE -> cell6.setCellValue("사전신청");
                    case FIELD -> cell6.setCellValue("현장신청");
                }
                row.createCell(0).setCellValue(participant.getName());
                row.createCell(1).setCellValue(participant.getPhoneNumber());
                row.createCell(2).setCellValue(participant.getAffiliation());
                row.createCell(4).setCellValue(participant.getSchoolDetail());
                row.createCell(5).setCellValue(participant.getPersonalInformationStatus() ? "동의" : "미동의");

                // JSON 데이터를 엑셀에 추가
                int cellIndex = 7;
                for (String key : jsonMap.keySet()) {
                    row.createCell(cellIndex++).setCellValue(jsonMap.get(key));
                }
            }

            String fileName = "Participant Information";
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
