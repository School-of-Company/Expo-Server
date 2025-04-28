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
import team.startup.expo.domain.attendance.exception.NotFoundStandardProgramException;
import team.startup.expo.domain.excel.service.ProgramParticipantInfoToExcelService;
import team.startup.expo.domain.participant.entity.StandardParticipant;
import team.startup.expo.domain.standard.entity.StandardProgram;
import team.startup.expo.domain.standard.entity.StandardProgramUser;
import team.startup.expo.domain.standard.repository.StandardProgramRepository;
import team.startup.expo.domain.standard.repository.StandardProgramUserRepository;
import team.startup.expo.global.annotation.ReadOnlyTransactionService;

import java.util.*;

@ReadOnlyTransactionService
@RequiredArgsConstructor
public class ProgramParticipantInfoToExcelServiceImpl implements ProgramParticipantInfoToExcelService {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final StandardProgramUserRepository standardProgramUserRepository;
    private final StandardProgramRepository standardProgramRepository;

    public void execute(String expoId, Long programId, HttpServletResponse res) throws JsonProcessingException {
        try {
            StandardProgram standardProgram = standardProgramRepository.findByIdAndExpoId(programId, expoId)
                    .orElseThrow(NotFoundStandardProgramException::new);

            List<StandardProgramUser> standardProgramUsers = standardProgramUserRepository.findByStandardProgram(standardProgram);

            List<StandardParticipant> standardParticipants = standardProgramUsers.stream().map(StandardProgramUser::getStandardParticipant).toList();

            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("박람회 참가자 정보");
            sheet.setDefaultColumnWidth(20);

            // 스타일 설정
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

            // 헤더 설정
            List<String> headers = new ArrayList<>(List.of("이름", "전화번호", "개인정보 동의 여부"));

            String infoHeaderJson = standardParticipants.get(0).getInformationJson();
            String unescapedInfoHeaderJson = StringEscapeUtils.unescapeJson(infoHeaderJson);
            Map<String, String> infoHeaderJsonMap = objectMapper.readValue(unescapedInfoHeaderJson, Map.class);
            Set<String> infoDynamicKeys = new LinkedHashSet<>(infoHeaderJsonMap.keySet());

            headers.addAll(infoDynamicKeys);

            Set<String> additionHeader = Set.of("학번", "비고");
            headers.addAll(additionHeader);

            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.size(); i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers.get(i));
                cell.setCellStyle(headerStyle);
            }

            int rowCount = 1;
            for (StandardParticipant participant : standardParticipants) {
                Row row = sheet.createRow(rowCount++);

                int cellIndex = 0;

                // 기본 정보
                row.createCell(cellIndex++).setCellValue(participant.getName());
                row.createCell(cellIndex++).setCellValue(participant.getPhoneNumber());
                row.createCell(cellIndex++).setCellValue(participant.getPersonalInformationStatus() ? "동의" : "미동의");

                String escapedInfoJson = participant.getInformationJson();
                String unescapedInfoJson = StringEscapeUtils.unescapeJson(escapedInfoJson);
                Map<String, String> infoJsonMap = objectMapper.readValue(unescapedInfoJson, Map.class);

                for (String key : infoDynamicKeys) {
                    row.createCell(cellIndex++).setCellValue(infoJsonMap.getOrDefault(key, ""));
                }
            }

            // 파일명 설정
            String fileName = "Program_Participant_Information";

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
