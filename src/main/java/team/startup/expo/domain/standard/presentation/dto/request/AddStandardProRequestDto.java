package team.startup.expo.domain.standard.presentation.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class AddStandardProRequestDto {

    @NotNull
    @Size(max = 100)
    private String title;

    @NotNull
    @Size(max = 20)
    private String startedAt;

    @NotNull
    @Size(max = 20)
    private String endedAt;
}
