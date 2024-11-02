package team.startup.expo.domain.image.presentation;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import team.startup.expo.domain.image.presentation.dto.response.UploadImageResponseDto;
import team.startup.expo.domain.image.service.UploadImageService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/image")
public class ImageController {

    private final UploadImageService uploadImageService;

    @PostMapping
    public ResponseEntity<UploadImageResponseDto> uploadImage(@RequestPart MultipartFile image) {
        UploadImageResponseDto result = uploadImageService.execute(image);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }
}
