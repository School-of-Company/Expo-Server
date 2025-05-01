package team.startup.expo.domain.image.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.web.multipart.MultipartFile;
import team.startup.expo.domain.image.presentation.dto.response.UploadImageResponseDto;
import team.startup.expo.domain.image.service.UploadImageService;
import team.startup.expo.global.annotation.TransactionService;
import team.startup.expo.global.thirdparty.aws.S3Util;

@TransactionService
@RequiredArgsConstructor
public class UploadImageServiceImpl implements UploadImageService {

    private final S3Util s3Util;

    public UploadImageResponseDto execute(MultipartFile image) {
        String imageURL = s3Util.upload(image);
        return UploadImageResponseDto.builder()
                .imageURL(imageURL)
                .build();

    }
}
