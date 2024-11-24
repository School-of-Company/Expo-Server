package team.startup.expo.domain.standard.presentation.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import team.startup.expo.domain.standard.StandardProgram;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class UpdateStandardProRequestDto {
    @NotNull
    @Size(max = 100)
    private String title;

    @NotNull
    @Size(max = 20)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime startedAt;

    @NotNull
    @Size(max = 20)
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
