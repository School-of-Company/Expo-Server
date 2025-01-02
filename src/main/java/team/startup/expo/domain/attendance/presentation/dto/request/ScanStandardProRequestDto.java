package team.startup.expo.domain.attendance.presentation.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class ScanStandardProRequestDto {
    @NotNull
    private Long participantId;
}
