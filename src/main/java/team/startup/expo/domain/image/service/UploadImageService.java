package team.startup.expo.domain.image.service;

import org.springframework.web.multipart.MultipartFile;
import team.startup.expo.domain.image.presentation.dto.response.UploadImageResponseDto;

public interface UploadImageService {
    UploadImageResponseDto execute(MultipartFile image);
}
