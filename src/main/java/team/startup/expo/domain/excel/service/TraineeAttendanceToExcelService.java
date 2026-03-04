package team.startup.expo.domain.excel.service;

import jakarta.servlet.http.HttpServletResponse;

public interface TraineeAttendanceToExcelService {
    void execute(Long traineeId, HttpServletResponse res);
}
