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
import team.startup.expo.domain.survey.answer.entity.StandardParticipantSurveyAnswer;
import team.startup.expo.domain.survey.answer.repository.StandardParticipantSurveyAnswerRepository;
import team.startup.expo.global.annotation.ReadOnlyTransactionService;

import java.util.*;

@ReadOnlyTransactionService
@RequiredArgsConstructor
public class StandardParticipantInfoToExcelServiceImpl implements StandardParticipantInfoToExcelService {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final StandardParticipantRepository standardParticipantRepository;
    private final ExpoRepository expoRepository;
    private final StandardParticipantSurveyAnswerRepository standardParticipantSurveyAnswerRepository;

    // JSON 문자열 내부 개행문자 등 문제있는 문자 치환 함수
    private String sanitizeJson(String json) {
        if (json == null) return null;
        String unescaped = StringEscapeUtils.unescapeJson(json);
        // 개행문자를 공백으로 치환 (필요시 "\\n"으로 치환 가능)
        return unescaped.replaceAll("\\r?\\n", " ");
    }

    public void execute(String expoId, HttpServletResponse res) throws JsonProcessingException {
        try {
            Expo expo = expoRepository.findById(expoId)
                    .orElseThrow(NotFoundExpoException::new);

            List<StandardParticipant> standardParticipantList = standardParticipantRepository.findByExpo(expo);

            if (standardParticipantList.isEmpty()) {
                throw new RuntimeException("참가자 정보가 존재하지 않습니다.");
            }

            List<Long> standardParticipantIds =
                    standardParticipantList.stream().map(StandardParticipant::getId).toList();

            List<StandardParticipantSurveyAnswer> standardParticipantSurveyAnswerList =
                    standardParticipantSurveyAnswerRepository.findStandardParticipantSurveyAnswersByStandardParticipantIds(standardParticipantIds);

            Map<Long, StandardParticipantSurveyAnswer> participantSurveyAnswerMap = new HashMap<>();
            for (StandardParticipantSurveyAnswer answer : standardParticipantSurveyAnswerList) {
                participantSurveyAnswerMap.put(answer.getStandardParticipant().getId(), answer);
            }

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

            List<String> headers = new ArrayList<>(List.of("이름", "전화번호", "개인정보 동의 여부", "신청 방식"));

            StandardParticipant firstParticipant = standardParticipantList.get(0);

            Set<String> infoDynamicKeys = new LinkedHashSet<>();
            String infoHeaderJson = firstParticipant.getInformationJson();
            if (infoHeaderJson != null) {
                String sanitizedInfoHeaderJson = sanitizeJson(infoHeaderJson);
                Map<String, String> infoHeaderJsonMap = objectMapper.readValue(sanitizedInfoHeaderJson, Map.class);
                infoDynamicKeys.addAll(infoHeaderJsonMap.keySet());
            }

            Set<String> surveyDynamicKeys = new LinkedHashSet<>();
            StandardParticipantSurveyAnswer firstAnswer = participantSurveyAnswerMap.get(firstParticipant.getId());
            if (firstAnswer != null && firstAnswer.getAnswerJson() != null) {
                String sanitizedSurveyHeaderJson = sanitizeJson(firstAnswer.getAnswerJson());
                Map<String, String> surveyHeaderJsonMap = objectMapper.readValue(sanitizedSurveyHeaderJson, Map.class);
                surveyDynamicKeys.addAll(surveyHeaderJsonMap.keySet());
            }

            headers.addAll(infoDynamicKeys);
            headers.addAll(surveyDynamicKeys);

            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.size(); i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers.get(i));
                cell.setCellStyle(headerStyle);
            }

            int rowCount = 1;
            for (StandardParticipant participant : standardParticipantList) {
                Row row = sheet.createRow(rowCount++);
                int cellIndex = 0;

                row.createCell(cellIndex++).setCellValue(participant.getName());
                row.createCell(cellIndex++).setCellValue(participant.getPhoneNumber());
                row.createCell(cellIndex++).setCellValue(participant.getPersonalInformationStatus() ? "동의" : "미동의");

                Cell applyTypeCell = row.createCell(cellIndex++);
                switch (participant.getApplicationType()) {
                    case PRE -> applyTypeCell.setCellValue("사전신청");
                    case FIELD -> applyTypeCell.setCellValue("현장신청");
                }

                Map<String, String> infoJsonMap = new HashMap<>();
                String escapedInfoJson = participant.getInformationJson();
                if (escapedInfoJson != null) {
                    String sanitizedInfoJson = sanitizeJson(escapedInfoJson);
                    infoJsonMap = objectMapper.readValue(sanitizedInfoJson, Map.class);
                }

                for (String key : infoDynamicKeys) {
                    row.createCell(cellIndex++).setCellValue(infoJsonMap.getOrDefault(key, ""));
                }

                Map<String, String> answerJsonMap = new HashMap<>();
                StandardParticipantSurveyAnswer surveyAnswer = participantSurveyAnswerMap.get(participant.getId());
                if (surveyAnswer != null && surveyAnswer.getAnswerJson() != null) {
                    String sanitizedAnswerJson = sanitizeJson(surveyAnswer.getAnswerJson());
                    answerJsonMap = objectMapper.readValue(sanitizedAnswerJson, Map.class);
                }

                for (String key : surveyDynamicKeys) {
                    row.createCell(cellIndex++).setCellValue(answerJsonMap.getOrDefault(key, ""));
                }
            }

            String fileName = "Participant_Information";

            res.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            res.setHeader("Content-Disposition", "attachment; filename=" + fileName + ".xlsx");

            ServletOutputStream outputStream = res.getOutputStream();
            workbook.write(outputStream);
            workbook.close();
        } catch (Exception e) {
            throw new RuntimeException("엑셀 파일 생성 중 오류 발생: " + e.getMessage(), e);
        }
    }
}
