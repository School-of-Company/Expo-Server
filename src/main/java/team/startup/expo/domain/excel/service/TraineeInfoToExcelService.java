package team.startup.expo.domain.excel.service;

import jakarta.servlet.http.HttpServletResponse;

public interface TraineeInfoToExcelService {
    void execute(String expoId, HttpServletResponse res);
}
