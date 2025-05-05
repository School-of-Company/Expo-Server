package team.startup.expo.global.thirdparty.aws;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import team.startup.expo.global.thirdparty.aws.exception.FileExtensionInvalidException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.UUID;

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
            List<String> list = List.of("jpg", "jpeg", "png");

            String[] splitFile = image.getOriginalFilename().split("\\.");

            if (splitFile.length < 2)
                throw new FileExtensionInvalidException();

            String extension = splitFile[1].toLowerCase();

            if (list.stream().noneMatch(it -> it.equals(extension)))
                throw new FileExtensionInvalidException();

            String profileName = imageBucket + "/" + UUID.randomUUID() + image.getOriginalFilename();
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(image.getInputStream().available());
            amazonS3.putObject(imageBucket, profileName, image.getInputStream(), metadata);
            return amazonS3.getUrl(imageBucket, profileName).toString();

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
