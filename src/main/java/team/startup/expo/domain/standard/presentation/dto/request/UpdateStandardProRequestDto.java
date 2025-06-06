package team.startup.expo.domain.standard.presentation.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import team.startup.expo.domain.standard.entity.StandardProgram;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class UpdateStandardProRequestDto {
    @NotNull
    private Long id;

    @NotNull
    private String title;

    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime startedAt;

    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime endedAt;

    public StandardProgram toEntity(StandardProgram standardProgram) {
        return StandardProgram.builder()
                .id(standardProgram.getId())
                .title(title)
                .startedAt(String.valueOf(startedAt))
                .endedAt(String.valueOf(endedAt))
                .expo(standardProgram.getExpo())
                .build();
    }
}
