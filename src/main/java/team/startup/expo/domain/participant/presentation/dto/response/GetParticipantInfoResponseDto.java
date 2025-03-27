package team.startup.expo.domain.participant.presentation.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class GetParticipantInfoResponseDto {
    private Long id;
    private String name;
    private String phoneNumber;
    private Boolean informationStatus;
}
