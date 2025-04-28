package team.startup.expo.domain.application.presentation.dto.response;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ApplicationTemporaryQrResponseDto {
    private Long participantId;
    private String phoneNumber;
    private String expoId;
}
