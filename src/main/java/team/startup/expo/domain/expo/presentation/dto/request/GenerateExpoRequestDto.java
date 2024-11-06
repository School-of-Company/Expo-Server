package team.startup.expo.domain.expo.presentation.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class GenerateExpoRequestDto {
    @NotNull
    private String title;

    @NotNull
    @Size(max = 1000)
    private String description;

    @NotNull
    private String startedDay;

    @NotNull
    private String finishedDay;

    @NotNull
    private String location;

    @NotNull
    private String coverImage;

    @NotNull
    private Float x;

    @NotNull
    private Float y;
}
