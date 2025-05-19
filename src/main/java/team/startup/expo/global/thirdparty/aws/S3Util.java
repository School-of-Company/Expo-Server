package team.startup.expo.global.thirdparty.aws;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import team.startup.expo.global.thirdparty.aws.exception.FileExtensionInvalidException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class S3Util {

    @Value("${cloud.aws.s3.image-bucket}")
    private String imageBucket;

    @Value("${cloud.aws.s3.qr-bucket}")
    private String qrBucket;

    private final AmazonS3 amazonS3;

    public String upload(MultipartFile image) {
        try {
            List<String> allowedExtensions = List.of("jpg", "jpeg", "png");

            String originalFilename = image.getOriginalFilename();
            if (originalFilename == null || originalFilename.isBlank()) {
                throw new FileExtensionInvalidException();
            }

            String extension = "";
            int lastDotIndex = originalFilename.lastIndexOf('.');
            if (lastDotIndex != -1 && lastDotIndex < originalFilename.length() - 1) {
                extension = originalFilename.substring(lastDotIndex + 1).toLowerCase();
            }

            if (!allowedExtensions.contains(extension)) {
                throw new FileExtensionInvalidException();
            }

            String savedFileName = UUID.randomUUID() + "_" + originalFilename;
            String objectKey = imageBucket + "/" + savedFileName;

            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(image.getInputStream().available());

            amazonS3.putObject(imageBucket, objectKey, image.getInputStream(), metadata);

            String encodedFileName = URLEncoder.encode(savedFileName, StandardCharsets.UTF_8);

            return "https://api.startup-expo.kr/expo-image-bucket-9881/" + encodedFileName;

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String qrUpload(File image) throws IOException {
        String filename = image.getName();
        String[] splitFile = filename.split("\\.");

        if (splitFile.length != 2)
            throw new FileExtensionInvalidException();

        String extension = splitFile[1].toLowerCase();
        List<String> allowedExtensions = List.of("jpg", "jpeg", "png");

        if (allowedExtensions.stream().noneMatch(it -> it.equals(extension)))
            throw new FileExtensionInvalidException();

        String savedFileName = UUID.randomUUID() + filename;
        String objectKey = qrBucket + "/" + savedFileName;

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(Files.size(image.toPath()));
        metadata.setContentType("image/" + extension);

        amazonS3.putObject(qrBucket, objectKey, new FileInputStream(image), metadata);

        return savedFileName;

    }


    public void deleteImage(String url) {
        String key = url.split("com/")[1];
        amazonS3.deleteObject(imageBucket, key);
    }
}
