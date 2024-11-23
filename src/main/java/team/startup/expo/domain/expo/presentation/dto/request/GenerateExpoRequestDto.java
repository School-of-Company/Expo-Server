package team.startup.expo.domain.expo.presentation.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class GenerateExpoRequestDto {
    @NotNull
    private String title;

    @NotNull
    @Size(max = 1000)
    private String description;

    @NotNull
    @Size(max = 20)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime startedDay;

    @NotNull
    @Size(max = 20)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime finishedDay;

    @NotNull
    private String location;

    @NotNull
    private String coverImage;

    @NotNull
    private Float x;

    @NotNull
    private Float y;
}
