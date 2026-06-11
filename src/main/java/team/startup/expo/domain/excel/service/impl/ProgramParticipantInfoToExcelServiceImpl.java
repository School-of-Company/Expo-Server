package team.startup.expo.domain.excel.service.impl;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
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
        try (SXSSFWorkbook workbook = new SXSSFWorkbook(500)) {
            workbook.setCompressTempFiles(true);

            StandardProgram standardProgram = standardProgramRepository.findByIdAndExpoId(programId, expoId)
                    .orElseThrow(NotFoundStandardProgramException::new);

            List<StandardProgramUser> standardProgramUsers = standardProgramUserRepository.findByStandardProgramIdWithFetch(programId);

            Sheet sheet = workbook.createSheet("프로그램 참가자 정보");
            sheet.setDefaultColumnWidth(20);

            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setColor(IndexedColors.WHITE.getIndex());

            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFillForegroundColor(IndexedColors.BLACK.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setBorderTop(BorderStyle.THIN);
            headerStyle.setBorderBottom(BorderStyle.THIN);
            headerStyle.setBorderLeft(BorderStyle.THIN);
            headerStyle.setBorderRight(BorderStyle.THIN);
            headerStyle.setFont(headerFont);

            CellStyle bodyStyle = workbook.createCellStyle();
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
            for (StandardProgramUser spu : standardProgramUsers) {
                StandardParticipant participant = spu.getStandardParticipant();

                Row row = sheet.createRow(rowCount++);

                int cellIndex = 0;

                Cell rankCell = row.createCell(cellIndex++);
                rankCell.setCellValue(rank++);
                rankCell.setCellStyle(bodyStyle);

                Cell nameCell = row.createCell(cellIndex++);
                nameCell.setCellValue(participant.getName());
                nameCell.setCellStyle(bodyStyle);

                Cell phoneCell = row.createCell(cellIndex++);
                phoneCell.setCellValue(participant.getPhoneNumber());
                phoneCell.setCellStyle(bodyStyle);

                Cell consentCell = row.createCell(cellIndex++);
                consentCell.setCellValue(Boolean.TRUE.equals(participant.getPersonalInformationStatus()) ? "동의" : "미동의");
                consentCell.setCellStyle(bodyStyle);

                for (int i = 0; i < 3; i++) {
                    Cell c = row.createCell(cellIndex++);
                    c.setCellValue("");
                    c.setCellStyle(bodyStyle);
                }
            }

            String fileName = "Program_Participant_Information";
            res.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            res.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + ".xlsx\"");

            try (ServletOutputStream outputStream = res.getOutputStream()) {
                workbook.write(outputStream);
                outputStream.flush();
            } finally {
                workbook.dispose();
            }
        } catch (Exception e) {
            throw new RuntimeException("엑셀 파일 생성 중 오류 발생", e);
        }
    }
}