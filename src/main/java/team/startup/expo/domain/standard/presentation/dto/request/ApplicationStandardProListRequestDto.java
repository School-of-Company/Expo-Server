package team.startup.expo.domain.standard.presentation.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class ApplicationStandardProListRequestDto {

    @NotNull
    private String phoneNumber;

    @NotNull
    private List<Long> standardProIds;
}
