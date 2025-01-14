package team.startup.expo.domain.standard.presentation.dto.response;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class GetStandardProParticipantResponseDto {
    private Long id;
    private String name;
    private String programName;
    private Boolean status;
    private String entryTime;
    private String leaveTime;
}
