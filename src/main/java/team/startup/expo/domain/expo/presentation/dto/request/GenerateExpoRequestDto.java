package team.startup.expo.domain.expo.presentation.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import team.startup.expo.domain.standard.presentation.dto.request.AddStandardProRequestDto;
import team.startup.expo.domain.training.presentation.dto.request.AddTrainingProRequestDto;

import java.time.LocalDate;
import java.util.List;

@Getter
@NoArgsConstructor
public class GenerateExpoRequestDto {
    @NotNull
    private String title;

    @NotNull
    @Size(max = 1000)
    private String description;

    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate startedDay;

    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate finishedDay;

    @NotNull
    private String location;

    @NotNull
    private String coverImage;

    @NotNull
    private String x;

    @NotNull
    private String y;

    @NotNull
    private List<AddStandardProRequestDto> addStandardProRequestDto;

    @NotNull
    private List<AddTrainingProRequestDto> addTrainingProRequestDto;
}
