package team.startup.expo.domain.excel.service.impl;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import team.startup.expo.domain.attendance.exception.NotFoundStandardProgramException;
import team.startup.expo.domain.excel.service.ProgramParticipantInfoToExcelService;
import team.startup.expo.domain.mongo.entity.DynamicJsonData;
import team.startup.expo.domain.mongo.entity.OwnerType;
import team.startup.expo.domain.mongo.repository.DynamicJsonDataRepository;
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

    private final StandardProgramUserRepository standardProgramUserRepository;
    private final StandardProgramRepository standardProgramRepository;
    private final DynamicJsonDataRepository dynamicJsonDataRepository;

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

            // 헤더 설정 (기본)
            List<String> headers = new ArrayList<>(List.of("순위", "이름", "전화번호", "개인정보 동의 여부"));

            // 동적 헤더 키 수집 (Mongo answers 기준)
            Set<String> infoDynamicKeys = new LinkedHashSet<>();
            if (!standardParticipants.isEmpty()) {
                StandardParticipant first = standardParticipants.get(0);
                DynamicJsonData firstInfo = dynamicJsonDataRepository
                        .findByOwnerTypeAndOwnerId(OwnerType.STANDARD_PARTICIPANT, first.getId())
                        .orElse(null);
                if (firstInfo != null && firstInfo.getAnswers() != null) {
                    infoDynamicKeys.addAll(firstInfo.getAnswers().keySet());
                }
            }

            headers.addAll(infoDynamicKeys);

            List<String> additionHeader = new ArrayList<>(List.of("학번", "서명", "비고"));
            headers.addAll(additionHeader);

            // 헤더 행 생성
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.size(); i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers.get(i));
                cell.setCellStyle(headerStyle);
            }

            // 데이터 행
            int rowCount = 1;
            int rank = 1;
            for (StandardParticipant participant : standardParticipants) {
                Row row = sheet.createRow(rowCount++);

                int cellIndex = 0;
                row.createCell(cellIndex++).setCellValue(rank++); // 순위
                row.createCell(cellIndex++).setCellValue(participant.getName());
                row.createCell(cellIndex++).setCellValue(participant.getPhoneNumber());
                row.createCell(cellIndex++).setCellValue(Boolean.TRUE.equals(participant.getPersonalInformationStatus()) ? "동의" : "미동의");

                // Mongo에서 동적 값 로드
                Map<String, String> infoJsonMap = new HashMap<>();
                DynamicJsonData infoDoc = dynamicJsonDataRepository
                        .findByOwnerTypeAndOwnerId(OwnerType.STANDARD_PARTICIPANT, participant.getId())
                        .orElse(null);
                if (infoDoc != null && infoDoc.getAnswers() != null) {
                    for (Map.Entry<String, Object> entry : infoDoc.getAnswers().entrySet()) {
                        infoJsonMap.put(entry.getKey(), entry.getValue() != null ? String.valueOf(entry.getValue()) : "");
                    }
                }

                // 동적 키 값 채우기
                for (String key : infoDynamicKeys) {
                    Cell c = row.createCell(cellIndex++);
                    c.setCellValue(infoJsonMap.getOrDefault(key, ""));
                    c.setCellStyle(bodyStyle);
                }

                // 추가 컬럼 (학번, 서명, 비고) - 비워두기
                for (int i = 0; i < 3; i++) {
                    Cell c = row.createCell(cellIndex++);
                    c.setCellValue("");
                    c.setCellStyle(bodyStyle);
                }
            }

            // 파일명/헤더
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
