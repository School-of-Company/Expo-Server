package team.startup.expo.domain.expo.presentation.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import team.startup.expo.domain.expo.Expo;

@Getter
@NoArgsConstructor
public class UpdateExpoRequestDto {
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

    public Expo toEntity(Expo expo) {
        return Expo.builder()
                .id(expo.getId())
                .title(title)
                .description(description)
                .startedDay(startedDay)
                .finishedDay(finishedDay)
                .location(location)
                .coverImage(coverImage)
                .admin(expo.getAdmin())
                .build();
    }
}
