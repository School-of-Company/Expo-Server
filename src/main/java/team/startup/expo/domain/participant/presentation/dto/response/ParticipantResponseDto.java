package team.startup.expo.domain.participant.presentation.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
@Builder
public class ParticipantResponseDto {
    private final Info info;
    private final List<GetParticipantInfoResponseDto> participants;

    @Getter
    @AllArgsConstructor
    @Builder
    public static class Info {
        private final int totalPage;
        private final int totalElement;
    }
}
