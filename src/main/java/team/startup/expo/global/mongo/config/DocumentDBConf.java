package team.startup.expo.global.mongo.config;

import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Configuration
public class DocumentDBConf {

    @Value("${spring.data.mongodb.uri}")
    private String mongoUri;

    @Bean
    public MongoClient mongoClient() {
        try {
            log.info("MongoDB 연결 URI: {}", mongoUri);

            MongoClientSettings mongoClientSettings = getMongodbBuilder()
                    .applyConnectionString(new com.mongodb.ConnectionString(mongoUri))
                    .build();

            log.info("MongoDB Client 생성 완료");
            return MongoClients.create(mongoClientSettings);
        } catch (Exception e) {
            log.error("MongoDB Client 생성 중 오류 발생", e);
            throw new RuntimeException("MongoDB Client 초기화 실패", e);
        }
    }

    private MongoClientSettings.Builder getMongodbBuilder() throws Exception {
        MongoClientSettings.Builder builder = MongoClientSettings.builder();
        String endOfCertificateDelimiter = "-----END CERTIFICATE-----";
        File file = null;

        try {
            ClassPathResource classPathResource = new ClassPathResource("certification/global-bundle.pem");
            InputStream inputStream = classPathResource.getInputStream();

            file = File.createTempFile("global-bundle", ".pem");
            FileUtils.copyInputStreamToFile(inputStream, file);
            inputStream.close();

            log.info("CA 파일 임시 생성 완료: {}", file.getAbsolutePath());

            String pemContents = new String(Files.readAllBytes(file.toPath()));

            List<String> allCertificates = Arrays.stream(pemContents
                            .split(endOfCertificateDelimiter))
                    .filter(line -> !line.isBlank())
                    .map(line -> line + endOfCertificateDelimiter)
                    .collect(Collectors.toUnmodifiableList());

            log.info("총 {} 개의 인증서 발견", allCertificates.size());

            CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");

            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(null);

            for (int i = 0; i < allCertificates.size(); i++) {
                String certString = allCertificates.get(i);
                Certificate caCert = certificateFactory.generateCertificate(
                        new ByteArrayInputStream(certString.getBytes())
                );
                keyStore.setCertificateEntry(String.format("AWS-certificate-%d", i), caCert);
                log.info("인증서 {} 추가 완료", i);
            }

            TrustManagerFactory trustManagerFactory =
                    TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(keyStore);

            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustManagerFactory.getTrustManagers(), null);

            builder.applyToSslSettings(ssl -> {
                ssl.enabled(true).context(sslContext);
            });

            log.info("=== SSL 설정 완료 ===");

            return builder;

        } catch (Exception e) {
            log.error("MongoDB CA 파일 조회 중 오류 발생: {}", e.getMessage());
            throw new RuntimeException("MongoDB SSL 설정 실패", e);
        } finally {
            if (file != null && file.exists()) {
                file.deleteOnExit();
                log.info("임시 CA 파일 삭제 예약");
            }
        }
    }
}