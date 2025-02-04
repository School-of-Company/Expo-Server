package team.startup.expo.domain.excel.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.servlet.http.HttpServletResponse;

public interface TraineeInfoToExcelService {
    void execute(String expoId, HttpServletResponse res) throws JsonProcessingException;
}
