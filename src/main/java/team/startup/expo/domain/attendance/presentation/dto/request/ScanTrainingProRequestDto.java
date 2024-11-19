package team.startup.expo.domain.attendance.presentation.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ScanTrainingProRequestDto {
    @NotNull
    private Long traineeId;
}
