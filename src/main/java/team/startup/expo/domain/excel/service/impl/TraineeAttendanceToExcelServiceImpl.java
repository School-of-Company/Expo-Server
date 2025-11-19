package team.startup.expo.domain.excel.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import team.startup.expo.domain.mongo.entity.DynamicJsonData;
import team.startup.expo.domain.mongo.entity.OwnerType;
import team.startup.expo.domain.mongo.repository.DynamicJsonDataRepository;
import team.startup.expo.domain.training.entity.TrainingProgram;
import team.startup.expo.domain.training.entity.TrainingProgramUser;
import team.startup.expo.domain.training.repository.TrainingProgramUserRepository;
import team.startup.expo.domain.trainee.entity.Trainee;
import team.startup.expo.domain.trainee.repository.TraineeRepository;
import team.startup.expo.global.annotation.ReadOnlyTransactionService;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@ReadOnlyTransactionService
@RequiredArgsConstructor
@Service
public class TraineeAttendanceToExcelServiceImpl implements TraineeAttendanceToExcelService {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private final TraineeRepository traineeRepository;
    private final DynamicJsonDataRepository dynamicJsonDataRepository;
    private final TrainingProgramUserRepository trainingProgramUserRepository;

    @Override
    public void execute(Long traineeId, HttpServletResponse res) {
        Trainee trainee = traineeRepository.findById(traineeId)
                .orElseThrow(() -> new IllegalArgumentException("해당 연수자를 찾을 수 없습니다."));

        Expo expo = trainee.getExpo();

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy.MM.dd.(E)", Locale.KOREAN);
        DateTimeFormatter dayFormatter = DateTimeFormatter.ofPattern("MM.dd.(E)", Locale.KOREAN);
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

        LocalDate startedDay = LocalDate.parse(expo.getStartedDay());
        LocalDate finishedDay = LocalDate.parse(expo.getFinishedDay());

        // Trainee가 신청한 연수 프로그램 조회
        List<TrainingProgramUser> appliedPrograms = trainingProgramUserRepository.findByTrainee(trainee);

        // 신청한 프로그램에서 날짜 추출 (중복 제거 및 정렬)
        Set<LocalDate> appliedDatesSet = appliedPrograms.stream()
                .map(TrainingProgramUser::getAttendanceDate)
                .collect(Collectors.toCollection(TreeSet::new));

        List<LocalDate> dateList = new ArrayList<>(appliedDatesSet);

        // 신청한 프로그램이 없으면 예외
        if (dateList.isEmpty()) {
            throw new IllegalArgumentException("해당 연수자가 신청한 프로그램이 없습니다.");
        }

        // 연수일시 텍스트 생성
        String trainingDateText;
        if (dateList.size() == 1) {
            trainingDateText = dateList.get(0).format(dateFormatter);
        } else {
            trainingDateText = dateList.stream()
                    .map(date -> date.format(dateFormatter))
                    .collect(Collectors.joining("/"));
        }

        // 과정명(기수) 계산
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

        List<String> giNumbers = new ArrayList<>();
        if (hasKeynote) giNumbers.add((0 + idxForKeynote) + "기");
        if (hasSpecial) giNumbers.add((2 + idxForOthers) + "기");
        if (hasRelay)   giNumbers.add((6 + idxForOthers) + "기");

        String courseName = expo.getTitle() + " 직무연수" +
                (giNumbers.isEmpty() ? "" : " " + giNumbers.stream().collect(Collectors.joining(",")));

        // MongoDB에서 학교명, 이름, 연수원아이디 가져오기 (answers JSON 우선)
        String schoolName = "학교명";
        String traineeName = trainee.getName();
        String trainingId = trainee.getTrainingId();
        try {
            DynamicJsonData infoDoc = dynamicJsonDataRepository
                    .findByOwnerTypeAndOwnerId(OwnerType.TRAINEE, trainee.getId())
                    .orElse(null);
            if (infoDoc != null && infoDoc.getAnswers() != null && !infoDoc.getAnswers().isBlank()) {
                Map<String, String> parsed = OBJECT_MAPPER.readValue(
                        infoDoc.getAnswers(),
                        Map.class
                );
                // 소속(학교명 또는 소속), 이름, 연수원아이디
                schoolName = parsed.getOrDefault("학교명", parsed.getOrDefault("소속", "학교명"));
                traineeName = parsed.getOrDefault("이름", traineeName);
                trainingId = parsed.getOrDefault("연수원아이디", trainingId);
            }
        } catch (Exception e) {
            // 파싱 실패시 기본값 사용
        }

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("출석부");
            sheet.setDefaultColumnWidth(14);

            // 컬럼 폭 설정 (연번/성명 더 좁게, 소속 더 넓게, 공통/선택1~4도 옆으로 더 넓게 보이도록 조정)
            sheet.setColumnWidth(0, 1500);   // 연번 (조금 더 좁게)
            sheet.setColumnWidth(1, 5500);   // 소속 (더 넓게)
            sheet.setColumnWidth(2, 3500);   // 성명 (약간 좁게)
            sheet.setColumnWidth(3, 6500);   // 공통 (프로그램 제목/시간 더 넓게)
            sheet.setColumnWidth(4, 6500);   // 선택1
            sheet.setColumnWidth(5, 6500);   // 선택2
            sheet.setColumnWidth(6, 6500);   // 선택3
            sheet.setColumnWidth(7, 6500);   // 선택4
            sheet.setColumnWidth(8, 3800);   // 이수여부
            sheet.setColumnWidth(9, 3800);   // 비고

            XSSFFont defaultFont = (XSSFFont) workbook.createFont();
            defaultFont.setFontHeightInPoints((short) 11);

            // 헤더 스타일
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

            // 데이터 스타일
            XSSFCellStyle bodyStyle = (XSSFCellStyle) workbook.createCellStyle();
            bodyStyle.setBorderTop(BorderStyle.THIN);
            bodyStyle.setBorderBottom(BorderStyle.THIN);
            bodyStyle.setBorderLeft(BorderStyle.THIN);
            bodyStyle.setBorderRight(BorderStyle.THIN);
            bodyStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            bodyStyle.setAlignment(HorizontalAlignment.CENTER);
            bodyStyle.setFont(defaultFont);

            // 프로그램 셀 스타일 (회색 + 줄바꿈)
            XSSFCellStyle programCellStyle = (XSSFCellStyle) workbook.createCellStyle();
            programCellStyle.cloneStyleFrom(bodyStyle);
            programCellStyle.setWrapText(true);
            programCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            programCellStyle.setFillForegroundColor(new org.apache.poi.xssf.usermodel.XSSFColor(new java.awt.Color(242, 242, 242), null));

            // 상단 정보 라벨 스타일 (과정명, 연수일시, 장소, 담당자용)
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

            // 상단 정보 값 스타일
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

            // 제목 행
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

            // 상단 정보 4줄
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

                // 라벨을 0~1번 컬럼 병합
                Cell cell0 = row.createCell(0);
                cell0.setCellValue(line[0]);
                cell0.setCellStyle(infoLabelStyle);

                Cell cell1 = row.createCell(1);
                cell1.setCellStyle(infoLabelStyle);

                // 값은 2~9번 컬럼 병합
                Cell cell2 = row.createCell(2);
                cell2.setCellValue(line[1]);
                cell2.setCellStyle(infoValueStyle);

                // 라벨 병합 (0~1)
                sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(
                        row.getRowNum(), row.getRowNum(), 0, 1));

                // 값 병합 (2~9)
                sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(
                        row.getRowNum(), row.getRowNum(), 2, 9));
            }

            // 빈 줄
            rowIdx++;

            // 날짜별로 헤더 + 데이터 생성
            for (int dateIdx = 0; dateIdx < dateList.size(); dateIdx++) {
                LocalDate date = dateList.get(dateIdx);
                String dateText = date.format(dayFormatter);

                // 헤더 상단
                Row headerTopRow = sheet.createRow(rowIdx++);
                headerTopRow.setHeightInPoints(40);

                // 헤더 하단
                Row headerBottomRow = sheet.createRow(rowIdx++);
                headerBottomRow.setHeightInPoints(34);

                // 상단 헤더 생성
                for (int i = 0; i < 10; i++) {
                    Cell cell = headerTopRow.createCell(i);
                    cell.setCellStyle(headerStyle);
                }

                headerTopRow.getCell(0).setCellValue("연번");
                headerTopRow.getCell(1).setCellValue("소속");
                headerTopRow.getCell(2).setCellValue("성명");
                headerTopRow.getCell(3).setCellValue(dateText);  // 날짜 표시
                headerTopRow.getCell(8).setCellValue("이수여부\n(이수/미이수)");
                headerTopRow.getCell(9).setCellValue("비고\n(연수원 아이디)");

                // 하단 헤더 생성
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

                // 프로그램 정보 줄
                Row programRow = sheet.createRow(rowIdx++);
                programRow.setHeightInPoints(60); // 강연명+시간 두 줄을 더 여유 있게 표시

                int pCol = 0;
                // 연번 / 소속 / 성명 칸 비워두고 스타일만
                for (int i = 0; i < 3; i++) {
                    Cell emptyCell = programRow.createCell(pCol++);
                    emptyCell.setCellStyle(bodyStyle);
                }

                // 공통: 기조강연 실제 정보로 표시
                TrainingProgram keynoteProgram = keynotes.isEmpty() ? null : keynotes.get(0);
                String keynoteTitle = keynoteProgram == null ? "기조강연" : keynoteProgram.getTitle();
                String keynoteTime = keynoteProgram == null
                        ? "(시간)"
                        : "(" + formatTime(keynoteProgram.getStartedAt(), timeFormatter)
                        + "~" + formatTime(keynoteProgram.getEndedAt(), timeFormatter) + ")";
                Cell commonProgramCell = programRow.createCell(pCol++);
                commonProgramCell.setCellValue(keynoteTitle + "\n" + keynoteTime);
                commonProgramCell.setCellStyle(programCellStyle);

                // 선택1~4: 실제 선택 프로그램 정보로 표시
                for (int i = 0; i < 4; i++) {
                    Cell c = programRow.createCell(pCol++);
                    if (i < elective.size()) {
                        TrainingProgram p = elective.get(i);
                        String title = p.getTitle();
                        String time = "(" + formatTime(p.getStartedAt(), timeFormatter)
                                + "~" + formatTime(p.getEndedAt(), timeFormatter) + ")";
                        c.setCellValue(title + "\n" + time);
                    } else {
                        c.setCellValue("");
                    }
                    c.setCellStyle(programCellStyle);
                }

                // 이수여부 / 비고 칸
                for (int i = 0; i < 2; i++) {
                    Cell emptyTail = programRow.createCell(pCol++);
                    emptyTail.setCellStyle(bodyStyle);
                }

                int programRowNum = programRow.getRowNum();

                // 병합 영역
                sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(topRowNum, programRowNum, 0, 0));
                sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(topRowNum, programRowNum, 1, 1));
                sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(topRowNum, programRowNum, 2, 2));
                sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(topRowNum, topRowNum, 3, 7));
                sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(topRowNum, programRowNum, 8, 8));
                sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(topRowNum, programRowNum, 9, 9));

                // 실제 데이터 줄
                Row dataRow = sheet.createRow(rowIdx++);
                dataRow.setHeightInPoints(34);
                int col = 0;

                Cell seqCell = dataRow.createCell(col++);
                seqCell.setCellValue("1");
                seqCell.setCellStyle(bodyStyle);

                Cell schoolCell = dataRow.createCell(col++);
                schoolCell.setCellValue(schoolName);  // MongoDB에서 가져온 학교명/소속(answers JSON 우선)
                schoolCell.setCellStyle(bodyStyle);

                Cell nameCell = dataRow.createCell(col++);
                nameCell.setCellValue(traineeName);   // MongoDB에서 가져온 이름(answers JSON 우선)
                nameCell.setCellStyle(bodyStyle);

                // 공통/선택1~4 칸
                for (int i = 0; i < 5; i++) {
                    Cell emptyProgram = dataRow.createCell(col++);
                    emptyProgram.setCellStyle(bodyStyle);
                }

                // 이수여부
                Cell completeCell = dataRow.createCell(col++);
                // 이수 여부는 기본값 없이 빈 칸으로 둔다
                completeCell.setCellStyle(bodyStyle);

                // 비고(연수원아이디)
                Cell noteCell = dataRow.createCell(col++);
                noteCell.setCellValue(trainingId); // MongoDB에서 가져온 연수원아이디(answers JSON 우선)
                noteCell.setCellStyle(bodyStyle);

                // 날짜 사이에 빈 줄 추가 (마지막 날짜 제외)
                if (dateIdx < dateList.size() - 1) {
                    rowIdx++;
                }
            }

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

    private String formatTime(String dateTimeString, DateTimeFormatter formatter) {
        if (dateTimeString == null || dateTimeString.isBlank()) return "";
        return LocalDateTime.parse(dateTimeString).format(formatter);
    }

    private static boolean containsAny(String target, String... keywords) {
        if (target == null) return false;
        for (String k : keywords) {
            if (k != null && target.contains(k)) return true;
        }
        return false;
    }
}