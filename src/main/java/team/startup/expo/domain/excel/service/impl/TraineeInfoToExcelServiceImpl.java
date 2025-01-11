package team.startup.expo.domain.excel.service.impl;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import team.startup.expo.domain.excel.service.TraineeInfoToExcelService;
import team.startup.expo.domain.expo.entity.Expo;
import team.startup.expo.domain.expo.exception.NotFoundExpoException;
import team.startup.expo.domain.expo.repository.ExpoRepository;
import team.startup.expo.domain.trainee.Trainee;
import team.startup.expo.domain.trainee.repository.TraineeRepository;
import team.startup.expo.global.annotation.ReadOnlyTransactionService;

import java.util.List;

@ReadOnlyTransactionService
@RequiredArgsConstructor
public class TraineeInfoToExcelServiceImpl implements TraineeInfoToExcelService {

    private final TraineeRepository traineeRepository;
    private final ExpoRepository expoRepository;

    public void execute(String expoId, HttpServletResponse res) {
        Expo expo = expoRepository.findById(expoId)
                .orElseThrow(NotFoundExpoException::new);

        List<Trainee> traineeList = traineeRepository.findByExpo(expo);

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet( "사전 교원연수자 정보");
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

        String[] headers = {
                "연수원 ID", "노트북 지참 여부", "전화번호",
                "직급", "학교급", "소속", "이름",
                "개인정보동의 여부", "참석 여부", "신청 형식"
        };

        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        int rowCount = 1;
        for (Trainee trainee : traineeList) {
            Row row = sheet.createRow(rowCount++);

//            row.createCell(0).setCellValue(trainee.getTrainingId());
//            row.createCell(1).setCellValue(trainee.getLaptopStatus() ? "사용 가능" : "사용 불가");
//            row.createCell(2).setCellValue(trainee.getPhoneNumber());
//            row.createCell(3).setCellValue(trainee.getPosition());
//            row.createCell(4).setCellValue(trainee.getSchoolLevel());
//            row.createCell(5).setCellValue(trainee.getOrganization());
//            row.createCell(6).setCellValue(trainee.getName());
//            row.createCell(7).setCellValue(trainee.getInformationStatus() ? "True" : "False");
//            row.createCell(8).setCellValue(trainee.getAttendanceStatus() ? "출석" : "미출석");
//            row.createCell(9).setCellValue(trainee.getApplicationType().toString());

            // Body 스타일 적용
            for (int i = 0; i < headers.length; i++) {
                row.getCell(i).setCellStyle(bodyStyle);
            }
        }


        String fileName = "연수자 정보";

        res.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        res.setHeader("Content-Disposition", "attachment; filename=" + fileName + ".xlsx");

        try (ServletOutputStream outputStream = res.getOutputStream()) {
            workbook.write(outputStream);
            workbook.close();
        } catch (Exception e) {
            throw new RuntimeException("엑셀 파일 생성 중 오류 발생: " + e.getMessage());
        }
    }
}
