package team.startup.expo.domain.excel.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.servlet.http.HttpServletResponse;

public interface ProgramParticipantInfoToExcelService {
    void execute(String expoId, Long programId, HttpServletResponse res) throws JsonProcessingException;
}
