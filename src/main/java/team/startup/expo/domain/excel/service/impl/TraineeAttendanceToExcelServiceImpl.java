package team.startup.expo.domain.excel.service.impl;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import team.startup.expo.domain.excel.service.TraineeAttendanceToExcelService;
import team.startup.expo.domain.expo.entity.Expo;
import team.startup.expo.domain.trainee.entity.Trainee;
import team.startup.expo.domain.trainee.repository.TraineeRepository;
import team.startup.expo.global.annotation.ReadOnlyTransactionService;

import java.io.IOException;

@ReadOnlyTransactionService
@RequiredArgsConstructor
@Service
public class TraineeAttendanceToExcelServiceImpl implements TraineeAttendanceToExcelService {

    private final TraineeRepository traineeRepository;

    @Override
    public void execute(Long traineeId, HttpServletResponse res) {
        Trainee trainee = traineeRepository.findById(traineeId)
                .orElseThrow(() -> new IllegalArgumentException("해당 연수자를 찾을 수 없습니다."));

        Expo expo = trainee.getExpo();

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("출석부");
            sheet.setDefaultColumnWidth(14);
            sheet.setColumnWidth(0, 2200);
            sheet.setColumnWidth(1, 3800);
            sheet.setColumnWidth(2, 3800);
            sheet.setColumnWidth(3, 3400);
            sheet.setColumnWidth(4, 3400);
            sheet.setColumnWidth(5, 3400);
            sheet.setColumnWidth(6, 3400);
            sheet.setColumnWidth(7, 3400);
            sheet.setColumnWidth(8, 3800);
            sheet.setColumnWidth(9, 3800);

            XSSFFont defaultFont = (XSSFFont) workbook.createFont();
            defaultFont.setFontHeightInPoints((short) 11);

            // 헤더(연번/소속/성명/해당날짜/이수여부/비고)
            XSSFCellStyle headerStyle = (XSSFCellStyle) workbook.createCellStyle();
            headerStyle.setAlignment(HorizontalAlignment.CENTER);
            headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            headerStyle.setFont(defaultFont);
            headerStyle.setBorderTop(BorderStyle.THIN);
            headerStyle.setBorderBottom(BorderStyle.THIN);
            headerStyle.setBorderLeft(BorderStyle.THIN);
            headerStyle.setBorderRight(BorderStyle.THIN);
            headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setWrapText(true); // 이수여부(이수/미이수) 줄바꿈용

            // 데이터(연번/소속/성명/이수/비고)
            XSSFCellStyle bodyStyle = (XSSFCellStyle) workbook.createCellStyle();
            bodyStyle.setBorderTop(BorderStyle.THIN);
            bodyStyle.setBorderBottom(BorderStyle.THIN);
            bodyStyle.setBorderLeft(BorderStyle.THIN);
            bodyStyle.setBorderRight(BorderStyle.THIN);
            bodyStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            bodyStyle.setAlignment(HorizontalAlignment.CENTER);
            bodyStyle.setFont(defaultFont);

            // 공통/선택1~4 칸 (회색 + 줄바꿈)
            XSSFCellStyle programCellStyle = (XSSFCellStyle) workbook.createCellStyle();
            programCellStyle.cloneStyleFrom(bodyStyle);
            programCellStyle.setWrapText(true);
            programCellStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            programCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            // 상단 정보 라벨
            XSSFCellStyle infoLabelStyle = (XSSFCellStyle) workbook.createCellStyle();
            infoLabelStyle.setAlignment(HorizontalAlignment.CENTER);
            infoLabelStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            infoLabelStyle.setBorderTop(BorderStyle.DOTTED);
            infoLabelStyle.setBorderBottom(BorderStyle.DOTTED);
            infoLabelStyle.setBorderLeft(BorderStyle.DOTTED);
            infoLabelStyle.setBorderRight(BorderStyle.DOTTED);
            XSSFFont infoLabelFont = (XSSFFont) workbook.createFont();
            infoLabelFont.setBold(true);
            infoLabelFont.setFontHeightInPoints((short) 13);
            infoLabelStyle.setFont(infoLabelFont);
            infoLabelStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            infoLabelStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            // 상단 정보 값
            XSSFFont infoValueFont = (XSSFFont) workbook.createFont();
            infoValueFont.setFontHeightInPoints((short) 13);
            infoValueFont.setColor(IndexedColors.BLUE.getIndex());

            XSSFCellStyle infoValueStyle = (XSSFCellStyle) workbook.createCellStyle();
            infoValueStyle.setAlignment(HorizontalAlignment.LEFT);
            infoValueStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            infoValueStyle.setBorderTop(BorderStyle.DOTTED);
            infoValueStyle.setBorderBottom(BorderStyle.DOTTED);
            infoValueStyle.setBorderLeft(BorderStyle.DOTTED);
            infoValueStyle.setBorderRight(BorderStyle.DOTTED);
            infoValueStyle.setFont(infoValueFont);

            int rowIdx = 0;

            // 제목 행
            Row titleRow = sheet.createRow(rowIdx++);
            titleRow.setHeightInPoints(40);
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue("출 석 부");
            XSSFCellStyle titleStyle = (XSSFCellStyle) workbook.createCellStyle();
            XSSFFont titleFont = (XSSFFont) workbook.createFont();
            titleFont.setBold(true);
            titleFont.setFontHeightInPoints((short) 18);
            titleStyle.setFont(titleFont);
            titleStyle.setAlignment(HorizontalAlignment.CENTER);
            titleStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            titleStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            titleStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            titleCell.setCellStyle(titleStyle);
            sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(0, 0, 0, 9));

            // 상단 정보 4줄
            rowIdx++;

            String[][] info = {
                    {"과정명", expo.getTitle()},
                    {"연수일시", expo.getStartedDay() + " ~ " + expo.getFinishedDay()},
                    {"장소", expo.getLocation()},
                    {"담당자", "남인혜 장학사(380-4587)"}
            };

            for (String[] line : info) {
                Row row = sheet.createRow(rowIdx++);
                row.setHeightInPoints(28);
                Cell cell0 = row.createCell(0);
                cell0.setCellValue(line[0]);
                cell0.setCellStyle(infoLabelStyle);

                Cell cell1 = row.createCell(1);
                cell1.setCellValue(line[1]);
                cell1.setCellStyle(infoValueStyle);

                // 값 영역은 끝까지 넓게
                sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(
                        row.getRowNum(), row.getRowNum(), 1, 9));
            }

            // 빈 줄 하나
            rowIdx++;

            // 헤더 상단
            Row headerTopRow = sheet.createRow(rowIdx++);
            headerTopRow.setHeightInPoints(44);

            // 헤더 하단
            Row headerBottomRow = sheet.createRow(rowIdx++);
            headerBottomRow.setHeightInPoints(28);

            for (int i = 0; i < 10; i++) {
                Cell cell = headerTopRow.createCell(i);
                cell.setCellStyle(headerStyle);
            }

            headerTopRow.getCell(0).setCellValue("연번");
            headerTopRow.getCell(1).setCellValue("소속");
            headerTopRow.getCell(2).setCellValue("성명");
            headerTopRow.getCell(3).setCellValue("해당날짜");
            // 이수여부 두 줄
            headerTopRow.getCell(8).setCellValue("이수여부\n(이수/미이수)");
            headerTopRow.getCell(9).setCellValue("비고(연수원아이디)");

            String[] bottomHeaders = {
                    "연번", "소속", "성명",
                    "공통",
                    "선택1", "선택2", "선택3", "선택4",
                    "이수여부", "비고(연수원아이디)"
            };

            for (int i = 0; i < bottomHeaders.length; i++) {
                Cell cell = headerBottomRow.createCell(i);
                cell.setCellValue(bottomHeaders[i]);
                cell.setCellStyle(headerStyle);
            }

            int topRowNum = headerTopRow.getRowNum();
            int bottomRowNum = headerBottomRow.getRowNum();

            sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(topRowNum, bottomRowNum, 0, 0));
            sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(topRowNum, bottomRowNum, 1, 1));
            sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(topRowNum, bottomRowNum, 2, 2));
            sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(topRowNum, topRowNum, 3, 7));
            sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(topRowNum, bottomRowNum, 8, 8));
            sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(topRowNum, bottomRowNum, 9, 9));

            // === 프로그램 정보 줄 (공통/선택1~4 회색, 두 줄 텍스트) ===
            Row programRow = sheet.createRow(rowIdx++);
            programRow.setHeightInPoints(28); // 헤더보다 낮게 = 반토막 느낌

            int pCol = 0;
            // 연번 / 소속 / 성명 칸 비워두고 스타일만
            for (int i = 0; i < 3; i++) {
                Cell emptyCell = programRow.createCell(pCol++);
                emptyCell.setCellStyle(bodyStyle);
            }

            // 공통: 기조강연 / 시간 두 줄
            Cell commonProgramCell = programRow.createCell(pCol++);
            commonProgramCell.setCellValue("기조강연\n(14:00~15:00)");
            commonProgramCell.setCellStyle(programCellStyle);

            // 선택1~4: 과정명 / 시간 두 줄
            for (int i = 0; i < 4; i++) {
                Cell c = programRow.createCell(pCol++);
                c.setCellValue("과정명\n(시간)");
                c.setCellStyle(programCellStyle);
            }

            // 이수여부 / 비고 칸 (프로그램 줄에서는 비워두고 스타일만)
            for (int i = 0; i < 2; i++) {
                Cell emptyTail = programRow.createCell(pCol++);
                emptyTail.setCellStyle(bodyStyle);
            }

            // === 실제 데이터 줄 (홍길동) ===
            Row dataRow = sheet.createRow(rowIdx++);
            dataRow.setHeightInPoints(28);
            int col = 0;

            Cell seqCell = dataRow.createCell(col++);
            seqCell.setCellValue("1");
            seqCell.setCellStyle(bodyStyle);

            Cell schoolCell = dataRow.createCell(col++);
            schoolCell.setCellValue("학교명");
            schoolCell.setCellStyle(bodyStyle);

            Cell nameCell = dataRow.createCell(col++);
            nameCell.setCellValue(trainee.getName());
            nameCell.setCellStyle(bodyStyle);

            // 홍길동 옆 공통/선택1~4 칸은 전부 빈칸 + 테두리만
            for (int i = 0; i < 5; i++) {
                Cell emptyProgram = dataRow.createCell(col++);
                emptyProgram.setCellStyle(bodyStyle);
            }

            // 이수여부
            Cell completeCell = dataRow.createCell(col++);
            completeCell.setCellValue("이수");
            completeCell.setCellStyle(bodyStyle);

            // 비고(연수원아이디)
            Cell noteCell = dataRow.createCell(col++);
            noteCell.setCellValue(trainee.getTrainingId());
            noteCell.setCellStyle(bodyStyle);

            String fileName = "Trainee_Attendance_" + trainee.getName();
            res.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            res.setHeader("Content-Disposition", "attachment; filename=" + fileName + ".xlsx");

            try (ServletOutputStream outputStream = res.getOutputStream()) {
                workbook.write(outputStream);
                outputStream.flush();
            }
        } catch (IOException e) {
            throw new RuntimeException("출석부 엑셀 생성 중 오류 발생", e);
        }
    }
}