package team.startup.expo.domain.standard.presentation.dto.response;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class GetStandardProParticipantResponseDto {
    private String name;
    private String affiliation;
    private String position;
    private String programName;
    private Boolean status;
    private String entryTime;
    private String leaveTime;
}
