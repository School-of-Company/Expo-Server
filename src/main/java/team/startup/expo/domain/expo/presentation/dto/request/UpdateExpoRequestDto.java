package team.startup.expo.domain.expo.presentation.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import team.startup.expo.domain.expo.entity.Expo;
import team.startup.expo.domain.standard.presentation.dto.request.AddStandardProRequestDto;
import team.startup.expo.domain.standard.presentation.dto.request.UpdateStandardProRequestDto;
import team.startup.expo.domain.training.presentation.dto.request.AddTrainingProRequestDto;
import team.startup.expo.domain.training.presentation.dto.request.UpdateTrainingProRequestDto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
public class UpdateExpoRequestDto {
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
    private Float x;

    @NotNull
    private Float y;

    @NotNull
    private List<UpdateStandardProRequestDto> updateStandardProRequestDto;

    @NotNull
    private List<UpdateTrainingProRequestDto> updateTrainingProRequestDto;

    public Expo toEntity(Expo expo) {
        return Expo.builder()
                .id(expo.getId())
                .title(title)
                .description(description)
                .startedDay(String.valueOf(startedDay))
                .finishedDay(String.valueOf(finishedDay))
                .location(location)
                .coverImage(coverImage)
                .x(x)
                .y(y)
                .admin(expo.getAdmin())
                .build();
    }
}
