package team.startup.expo.domain.excel.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.stereotype.Service;
import team.startup.expo.domain.excel.service.TraineeAttendanceToExcelService;
import team.startup.expo.domain.expo.entity.Expo;
import team.startup.expo.domain.trainee.entity.Trainee;
import team.startup.expo.domain.trainee.repository.TraineeRepository;
import team.startup.expo.domain.training.entity.TrainingProgram;
import team.startup.expo.domain.training.entity.TrainingProgramUser;
import team.startup.expo.domain.training.repository.TrainingProgramUserRepository;
import team.startup.expo.global.annotation.ReadOnlyTransactionService;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.StringUtils.containsAny;

@ReadOnlyTransactionService
@RequiredArgsConstructor
@Service
public class TraineeAttendanceToExcelServiceImpl implements TraineeAttendanceToExcelService {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private final TraineeRepository traineeRepository;
    private final TrainingProgramUserRepository trainingProgramUserRepository;

    @Override
    public void execute(Long traineeId, HttpServletResponse res) {
        Trainee trainee = traineeRepository.findById(traineeId)
                .orElseThrow(() -> new IllegalArgumentException("해당 연수자를 찾을 수 없습니다."));

        Expo expo = trainee.getExpo();

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy.MM.dd.(E)", Locale.KOREAN);
        DateTimeFormatter dayFormatter = DateTimeFormatter.ofPattern("MM.dd.(E)", Locale.KOREAN);
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

        List<TrainingProgramUser> appliedPrograms = trainingProgramUserRepository.findByTrainee(trainee);

        Set<LocalDate> appliedDatesSet = appliedPrograms.stream()
                .map(TrainingProgramUser::getAttendanceDate)
                .collect(Collectors.toCollection(TreeSet::new));

        List<LocalDate> dateList = new ArrayList<>(appliedDatesSet);

        if (dateList.isEmpty()) {
            throw new IllegalArgumentException("해당 연수자가 신청한 프로그램이 없습니다.");
        }

        String trainingDateText;
        if (dateList.size() == 1) {
            trainingDateText = dateList.get(0).format(dateFormatter);
        } else {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < dateList.size(); i++) {
                LocalDate d = dateList.get(i);
                if (i == 0) {
                    sb.append(d.format(dateFormatter));
                } else {
                    sb.append("/").append(d.format(dayFormatter));
                }
            }
            sb.append(" 해당일 입력");
            trainingDateText = sb.toString();
        }

        List<TrainingProgram> programs = appliedPrograms.stream()
                .map(TrainingProgramUser::getTrainingProgram)
                .distinct()
                .toList();

        List<TrainingProgram> keynotes = programs.stream()
                .filter(p -> containsAny(p.getTitle(), "기조", "기조 강연"))
                .toList();
        List<TrainingProgram> specials = programs.stream()
                .filter(p -> containsAny(p.getTitle(), "특별"))
                .toList();
        List<TrainingProgram> relays = programs.stream()
                .filter(p -> containsAny(p.getTitle(), "교사", "릴레이"))
                .toList();

        List<TrainingProgram> electiveAll = programs.stream()
                .filter(p -> !containsAny(p.getTitle(), "기조", "기조 강연", "특별", "교사", "릴레이"))
                .toList();

        boolean hasKeynote = !keynotes.isEmpty();
        boolean hasSpecial = !specials.isEmpty();
        boolean hasRelay   = !relays.isEmpty();

        int electiveLimit = hasKeynote ? 2 : 4;
        List<TrainingProgram> elective = electiveAll.stream()
                .limit(electiveLimit)
                .toList();

        int electiveCount = elective.size();
        int idxForKeynote = Math.max(1, Math.min(2, electiveCount));
        int idxForOthers  = Math.max(1, Math.min(4, electiveCount));

        List<Integer> giNumbers = new ArrayList<>();
        if (hasKeynote) giNumbers.add(0 + idxForKeynote);
        if (hasSpecial) giNumbers.add(2 + idxForOthers);
        if (hasRelay)   giNumbers.add(6 + idxForOthers);

        String giText = "";
        if (!giNumbers.isEmpty()) {
            String joined = giNumbers.stream()
                    .map(String::valueOf)
                    .collect(Collectors.joining(","));
            giText = " (" + joined + "기)";
        }

        String courseName = expo.getTitle() + " 직무연수" + giText;

        String schoolName = "학교명";
        String traineeName = trainee.getName();
        String trainingId = trainee.getTrainingId();
        try {
            String infoJson = trainee.getInformationJson();
            if (infoJson != null && !infoJson.isBlank()) {
                @SuppressWarnings("unchecked")
                Map<String, Object> parsed = OBJECT_MAPPER.readValue(infoJson, Map.class);
                schoolName = Optional.ofNullable((String) parsed.get("학교명"))
                        .orElseGet(() -> Optional.ofNullable((String) parsed.get("소속")).orElse("학교명"));
                traineeName = Optional.ofNullable((String) parsed.get("이름")).orElse(traineeName);
                trainingId = Optional.ofNullable((String) parsed.get("연수원아이디")).orElse(trainingId);
            }
        } catch (Exception e) {
        }

        try (SXSSFWorkbook workbook = new SXSSFWorkbook(200)) {
            workbook.setCompressTempFiles(true);
            Sheet sheet = workbook.createSheet("출석부");
            sheet.setDefaultColumnWidth(14);

            sheet.setColumnWidth(0, 1500);
            sheet.setColumnWidth(1, 5500);
            sheet.setColumnWidth(2, 3500);
            sheet.setColumnWidth(3, 6500);
            sheet.setColumnWidth(4, 6500);
            sheet.setColumnWidth(5, 6500);
            sheet.setColumnWidth(6, 6500);
            sheet.setColumnWidth(7, 6500);
            sheet.setColumnWidth(8, 3800);
            sheet.setColumnWidth(9, 3800);

            XSSFFont defaultFont = (XSSFFont) workbook.createFont();
            defaultFont.setFontHeightInPoints((short) 11);

            XSSFCellStyle headerStyle = (XSSFCellStyle) workbook.createCellStyle();
            headerStyle.setAlignment(HorizontalAlignment.CENTER);
            headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            headerStyle.setFont(defaultFont);
            headerStyle.setBorderTop(BorderStyle.THIN);
            headerStyle.setBorderBottom(BorderStyle.THIN);
            headerStyle.setBorderLeft(BorderStyle.THIN);
            headerStyle.setBorderRight(BorderStyle.THIN);
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setFillForegroundColor(new org.apache.poi.xssf.usermodel.XSSFColor(new java.awt.Color(242, 242, 242), null));
            headerStyle.setWrapText(true);

            XSSFCellStyle bodyStyle = (XSSFCellStyle) workbook.createCellStyle();
            bodyStyle.setBorderTop(BorderStyle.THIN);
            bodyStyle.setBorderBottom(BorderStyle.THIN);
            bodyStyle.setBorderLeft(BorderStyle.THIN);
            bodyStyle.setBorderRight(BorderStyle.THIN);
            bodyStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            bodyStyle.setAlignment(HorizontalAlignment.CENTER);
            bodyStyle.setFont(defaultFont);

            XSSFCellStyle programCellStyle = (XSSFCellStyle) workbook.createCellStyle();
            programCellStyle.cloneStyleFrom(bodyStyle);
            programCellStyle.setWrapText(true);
            programCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            programCellStyle.setFillForegroundColor(new org.apache.poi.xssf.usermodel.XSSFColor(new java.awt.Color(242, 242, 242), null));

            XSSFCellStyle infoLabelStyle = (XSSFCellStyle) workbook.createCellStyle();
            infoLabelStyle.setAlignment(HorizontalAlignment.CENTER);
            infoLabelStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            infoLabelStyle.setBorderTop(BorderStyle.DOTTED);
            infoLabelStyle.setBorderBottom(BorderStyle.DOTTED);
            infoLabelStyle.setBorderLeft(BorderStyle.DOTTED);
            infoLabelStyle.setBorderRight(BorderStyle.DOTTED);
            XSSFFont infoLabelFont = (XSSFFont) workbook.createFont();
            infoLabelFont.setBold(true);
            infoLabelFont.setFontHeightInPoints((short) 11);
            infoLabelStyle.setFont(infoLabelFont);
            infoLabelStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            infoLabelStyle.setFillForegroundColor(new org.apache.poi.xssf.usermodel.XSSFColor(new java.awt.Color(242, 242, 242), null));

            XSSFFont infoValueFont = (XSSFFont) workbook.createFont();
            infoValueFont.setFontHeightInPoints((short) 11);
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

            Row titleRow = sheet.createRow(rowIdx++);
            titleRow.setHeightInPoints(40);
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue("출  석  부");
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

            rowIdx++;

            String[][] info = {
                    {"과정명", courseName},
                    {"연수일시", trainingDateText},
                    {"장소", "김대중컨벤션센터"},
                    {"담당자", "남인혜 장학사(380-4587)"}
            };

            for (int i = 0; i < info.length; i++) {
                String[] line = info[i];
                Row row = sheet.createRow(rowIdx++);
                row.setHeightInPoints(30);

                Cell cell0 = row.createCell(0);
                cell0.setCellValue(line[0]);
                cell0.setCellStyle(infoLabelStyle);

                Cell cell1 = row.createCell(1);
                cell1.setCellStyle(infoLabelStyle);

                Cell cell2 = row.createCell(2);
                cell2.setCellValue(line[1]);
                cell2.setCellStyle(infoValueStyle);

                sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(
                        row.getRowNum(), row.getRowNum(), 0, 1));

                sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(
                        row.getRowNum(), row.getRowNum(), 2, 9));
            }

            rowIdx++;

            for (int dateIdx = 0; dateIdx < dateList.size(); dateIdx++) {
                LocalDate date = dateList.get(dateIdx);
                String dateText = date.format(dayFormatter);

                Row headerTopRow = sheet.createRow(rowIdx++);
                headerTopRow.setHeightInPoints(40);

                Row headerBottomRow = sheet.createRow(rowIdx++);
                headerBottomRow.setHeightInPoints(34);

                for (int i = 0; i < 10; i++) {
                    Cell cell = headerTopRow.createCell(i);
                    cell.setCellStyle(headerStyle);
                }

                headerTopRow.getCell(0).setCellValue("연번");
                headerTopRow.getCell(1).setCellValue("소속");
                headerTopRow.getCell(2).setCellValue("성명");
                headerTopRow.getCell(3).setCellValue(dateText);
                headerTopRow.getCell(8).setCellValue("이수여부\n(이수/미이수)");
                headerTopRow.getCell(9).setCellValue("비고\n(연수원 아이디)");

                String[] bottomHeaders = {
                        "연번", "소속", "성명",
                        "공통",
                        "선택1", "선택2", "선택3", "선택4",
                        "이수여부", "비고\n(연수원 아이디)"
                };

                for (int i = 0; i < bottomHeaders.length; i++) {
                    Cell cell = headerBottomRow.createCell(i);
                    cell.setCellValue(bottomHeaders[i]);
                    cell.setCellStyle(headerStyle);
                }

                int topRowNum = headerTopRow.getRowNum();

                Row programRow = sheet.createRow(rowIdx++);
                programRow.setHeightInPoints(60);

                int pCol = 0;
                for (int i = 0; i < 3; i++) {
                    Cell emptyCell = programRow.createCell(pCol++);
                    emptyCell.setCellStyle(bodyStyle);
                }

                TrainingProgram keynoteProgram = keynotes.isEmpty() ? null : keynotes.get(0);
                String keynoteTitle = keynoteProgram == null ? "기조강연" : keynoteProgram.getTitle();
                String keynoteTime = keynoteProgram == null
                        ? "(시간)"
                        : "(" + formatTime(LocalDateTime.parse(keynoteProgram.getStartedAt()), timeFormatter)
                        + "~" + formatTime(LocalDateTime.parse(keynoteProgram.getEndedAt()), timeFormatter) + ")";
                Cell commonProgramCell = programRow.createCell(pCol++);
                commonProgramCell.setCellValue(keynoteTitle + "\n" + keynoteTime);
                commonProgramCell.setCellStyle(programCellStyle);

                for (int i = 0; i < 4; i++) {
                    Cell c = programRow.createCell(pCol++);
                    if (i < elective.size()) {
                        TrainingProgram p = elective.get(i);
                        String title = p.getTitle();
                        String time = "(" + formatTime(LocalDateTime.parse(p.getStartedAt()), timeFormatter)
                                + "~" + formatTime(LocalDateTime.parse(p.getEndedAt()), timeFormatter) + ")";
                        c.setCellValue(title + "\n" + time);
                    } else {
                        c.setCellValue("");
                    }
                    c.setCellStyle(programCellStyle);
                }

                for (int i = 0; i < 2; i++) {
                    Cell emptyTail = programRow.createCell(pCol++);
                    emptyTail.setCellStyle(bodyStyle);
                }

                int programRowNum = programRow.getRowNum();

                sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(topRowNum, programRowNum, 0, 0));
                sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(topRowNum, programRowNum, 1, 1));
                sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(topRowNum, programRowNum, 2, 2));
                sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(topRowNum, topRowNum, 3, 7));
                sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(topRowNum, programRowNum, 8, 8));
                sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(topRowNum, programRowNum, 9, 9));

                Row dataRow = sheet.createRow(rowIdx++);
                dataRow.setHeightInPoints(34);
                int col = 0;

                Cell seqCell = dataRow.createCell(col++);
                seqCell.setCellValue("1");
                seqCell.setCellStyle(bodyStyle);

                Cell schoolCell = dataRow.createCell(col++);
                schoolCell.setCellValue(schoolName);
                schoolCell.setCellStyle(bodyStyle);

                Cell nameCell = dataRow.createCell(col++);
                nameCell.setCellValue(traineeName);
                nameCell.setCellStyle(bodyStyle);

                for (int i = 0; i < 5; i++) {
                    Cell emptyProgram = dataRow.createCell(col++);
                    emptyProgram.setCellStyle(bodyStyle);
                }

                Cell completeCell = dataRow.createCell(col++);
                completeCell.setCellStyle(bodyStyle);

                Cell noteCell = dataRow.createCell(col++);
                noteCell.setCellValue(trainingId);
                noteCell.setCellStyle(bodyStyle);

                if (dateIdx < dateList.size() - 1) {
                    rowIdx++;
                }
            }

            String fileName = "Trainee_Attendance_" + trainee.getName();
            res.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            res.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + ".xlsx\"");

            try (ServletOutputStream outputStream = res.getOutputStream()) {
                workbook.write(outputStream);
                outputStream.flush();
            } finally {
                workbook.dispose();
            }
        } catch (IOException e) {
            throw new RuntimeException("출석부 엑셀 생성 중 오류 발생", e);
        }
    }

    private String formatTime(LocalDateTime dateTime, DateTimeFormatter formatter) {
        return dateTime.format(formatter);
    }
}