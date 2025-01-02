package team.startup.expo.domain.image.presentation.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UploadImageResponseDto {
    private String imageURL;
}
