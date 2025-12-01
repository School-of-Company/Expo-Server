package team.startup.expo.domain.excel.service.impl;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
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

import java.util.ArrayList;
import java.util.List;

@ReadOnlyTransactionService
@RequiredArgsConstructor
public class ProgramParticipantInfoToExcelServiceImpl implements ProgramParticipantInfoToExcelService {

    private final StandardProgramUserRepository standardProgramUserRepository;
    private final StandardProgramRepository standardProgramRepository;

    public void execute(String expoId, Long programId, HttpServletResponse res) {
        try (Workbook workbook = new XSSFWorkbook()) {
            StandardProgram standardProgram = standardProgramRepository.findByIdAndExpoId(programId, expoId)
                    .orElseThrow(NotFoundStandardProgramException::new);

            List<StandardProgramUser> standardProgramUsers = standardProgramUserRepository.findByStandardProgram(standardProgram);
            List<StandardParticipant> standardParticipants = standardProgramUsers.stream()
                    .map(StandardProgramUser::getStandardParticipant)
                    .toList();

            Sheet sheet = workbook.createSheet("프로그램 참가자 정보");
            sheet.setDefaultColumnWidth(20);

            XSSFFont headerFont = (XSSFFont) workbook.createFont();
            headerFont.setBold(true);
            headerFont.setColor((short) IndexedColors.WHITE.getIndex());

            XSSFCellStyle headerStyle = (XSSFCellStyle) workbook.createCellStyle();
            headerStyle.setFillForegroundColor(IndexedColors.BLACK.getIndex());
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

            List<String> headers = new ArrayList<>(List.of("순위", "이름", "전화번호", "개인정보 동의 여부"));

            List<String> additionHeader = new ArrayList<>(List.of("학번", "서명", "비고"));
            headers.addAll(additionHeader);

            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.size(); i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers.get(i));
                cell.setCellStyle(headerStyle);
            }

            int rowCount = 1;
            int rank = 1;
            for (StandardParticipant participant : standardParticipants) {
                Row row = sheet.createRow(rowCount++);

                int cellIndex = 0;
                row.createCell(cellIndex++).setCellValue(rank++); // 순위
                row.createCell(cellIndex++).setCellValue(participant.getName());
                row.createCell(cellIndex++).setCellValue(participant.getPhoneNumber());
                row.createCell(cellIndex++).setCellValue(Boolean.TRUE.equals(participant.getPersonalInformationStatus()) ? "동의" : "미동의");

                for (int i = 0; i < 3; i++) {
                    Cell c = row.createCell(cellIndex++);
                    c.setCellValue("");
                    c.setCellStyle(bodyStyle);
                }
            }

            String fileName = "Program_Participant_Information";
            res.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            res.setHeader("Content-Disposition", "attachment; filename=" + fileName + ".xlsx");

            try (ServletOutputStream outputStream = res.getOutputStream()) {
                workbook.write(outputStream);
                outputStream.flush();
            }
        } catch (Exception e) {
            throw new RuntimeException("엑셀 파일 생성 중 오류 발생", e);
        }
    }
}