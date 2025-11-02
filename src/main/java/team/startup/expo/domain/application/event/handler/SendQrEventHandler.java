package team.startup.expo.domain.application.event.handler;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.request.SingleMessageSendingRequest;
import net.nurigo.sdk.message.response.SingleMessageSentResponse;
import net.nurigo.sdk.message.service.DefaultMessageService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import team.startup.expo.domain.admin.entity.Authority;
import team.startup.expo.domain.expo.entity.Expo;
import team.startup.expo.domain.expo.exception.NotFoundExpoException;
import team.startup.expo.domain.expo.repository.ExpoRepository;
import team.startup.expo.domain.participant.entity.StandardParticipant;
import team.startup.expo.domain.participant.repository.StandardParticipantRepository;
import team.startup.expo.domain.application.event.SendQrEvent;
import team.startup.expo.domain.sms.exception.NotFoundParticipantException;
import team.startup.expo.domain.sms.exception.NotFoundTraineeException;
import team.startup.expo.domain.trainee.entity.Trainee;
import team.startup.expo.domain.trainee.repository.TraineeRepository;
import team.startup.expo.global.annotation.TransactionService;
import team.startup.expo.global.exception.ErrorCode;
import team.startup.expo.global.exception.GlobalException;
import team.startup.expo.global.sms.SmsProperties;
import team.startup.expo.global.thirdparty.aws.S3Util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
@Slf4j
@TransactionService
public class SendQrEventHandler {

    private final static int WIDTH = 200;
    private final static int HEIGHT = 200;

    private final StandardParticipantRepository standardParticipantRepository;
    private final ExpoRepository expoRepository;
    private final DefaultMessageService messageService;
    private final TraineeRepository traineeRepository;
    private final SmsProperties smsProperties;
    private final S3Util s3Util;

    @Async("asyncExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public CompletableFuture<SingleMessageSentResponse> sendQrHandler(SendQrEvent sendQrEvent) {
        log.info("[SendQrEvent] start: expoId={}, authority={}, to={}", sendQrEvent.getExpoId(), sendQrEvent.getAuthority(), sendQrEvent.getPhoneNumber());
        SingleMessageSentResponse response = null;

        try {
            Expo expo = expoRepository.findById(sendQrEvent.getExpoId())
                    .orElseThrow(NotFoundExpoException::new);
            log.debug("[SendQrEvent] loaded expo: {}", expo.getId());

            if (sendQrEvent.getAuthority() == Authority.ROLE_STANDARD) {
                StandardParticipant participant = standardParticipantRepository.findByPhoneNumberAndExpo(sendQrEvent.getPhoneNumber(), expo)
                        .orElseThrow(NotFoundParticipantException::new);
                log.debug("[SendQrEvent] standard participant loaded: id={}, phone={}", participant.getId(), participant.getPhoneNumber());
                String information = "{\"participantId\": " + participant.getId() + ", \"phoneNumber\": \"" + participant.getPhoneNumber() + "\"}";
                byte[] qrBytes = createQr(information);
                log.debug("[SendQrEvent] QR generated (STANDARD): bytes={}", (qrBytes == null ? 0 : qrBytes.length));
                Message message = createMessage(qrBytes, sendQrEvent, smsProperties.getFromStandardNumber());
                log.info("[SendQrEvent] message created (STANDARD): to={}, from={}", sendQrEvent.getPhoneNumber(), smsProperties.getFromStandardNumber());
                participant.plusSmsTryTime();
                standardParticipantRepository.save(participant);
                log.debug("[SendQrEvent] participant smsTryTime incremented and saved: id={}, tryTime={}", participant.getId(), participant.getSmsTryTime());
                response = messageService.sendOne(new SingleMessageSendingRequest(message));
                log.info("[SendQrEvent] SMS sent (STANDARD): messageId={}, status={}", (response == null ? null : response.getMessageId()), (response == null ? null : response.getStatusCode()));
            } else if (sendQrEvent.getAuthority() == Authority.ROLE_TRAINEE) {
                Trainee trainee = traineeRepository.findByPhoneNumberAndExpo(sendQrEvent.getPhoneNumber(), expo)
                        .orElseThrow(NotFoundTraineeException::new);
                log.debug("[SendQrEvent] trainee loaded: id={}, phone={}", trainee.getId(), trainee.getPhoneNumber());
                String information = "{\"traineeId\": " + trainee.getId() + ", \"phoneNumber\": \"" + trainee.getPhoneNumber() + "\"}";
                byte[] qrBytes = createQr(information);
                log.debug("[SendQrEvent] QR generated (TRAINEE): bytes={}", (qrBytes == null ? 0 : qrBytes.length));
                Message message = createMessage(qrBytes, sendQrEvent, smsProperties.getFromTraineeNumber());
                log.info("[SendQrEvent] message created (TRAINEE): to={}, from={}", sendQrEvent.getPhoneNumber(), smsProperties.getFromTraineeNumber());
                response = messageService.sendOne(new SingleMessageSendingRequest(message));
                log.info("[SendQrEvent] SMS sent (TRAINEE): messageId={}, status={}", (response == null ? null : response.getMessageId()), (response == null ? null : response.getStatusCode()));
            }
        } catch (Exception e) {
            log.error("[SendQrEvent] failed: expoId={}, authority={}, to={}", sendQrEvent.getExpoId(), sendQrEvent.getAuthority(), sendQrEvent.getPhoneNumber(), e);
            throw new GlobalException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
        log.info("[SendQrEvent] done: expoId={}, authority={}, to={}, status={}", sendQrEvent.getExpoId(), sendQrEvent.getAuthority(), sendQrEvent.getPhoneNumber(), (response == null ? null : response.getStatusCode()));
        return CompletableFuture.completedFuture(response);
    }

    private byte[] createQr(String information) {
        log.debug("[QR] create start: payloadSize={}", (information == null ? 0 : information.length()));
        byte[] bytes = null;
        try {
            BitMatrix encode = new MultiFormatWriter().encode(information, BarcodeFormat.QR_CODE, WIDTH, HEIGHT);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(encode, "JPG", out);
            bytes = out.toByteArray();
            log.debug("[QR] create done: bytes={}", (bytes == null ? 0 : bytes.length));
        } catch (IOException | WriterException e) {
            log.error("[QR] create failed: payloadSize={}", (information == null ? 0 : information.length()), e);
        }
        return bytes;
    }

    private Message createMessage(byte[] qrBytes, SendQrEvent sendQrEvent, String phoneNumber) {
        log.debug("[MSG] create start: to={}, from={}, qrBytes={}", sendQrEvent.getPhoneNumber(), phoneNumber, (qrBytes == null ? 0 : qrBytes.length));
        try {
            Path tempFilePath = Files.createTempFile("temp-qr", ".jpg");
            log.debug("[MSG] temp file created: {}", tempFilePath);
            Files.write(tempFilePath, qrBytes);
            log.debug("[MSG] temp file written: size={} bytes", (qrBytes == null ? 0 : qrBytes.length));
            File tempFile = tempFilePath.toFile();
            String objectUrl = s3Util.qrUpload(tempFile);
            log.info("[MSG] uploaded to S3: objectUrl={}", objectUrl);
            Message message = new Message();
            message.setFrom(phoneNumber);
            message.setTo(sendQrEvent.getPhoneNumber());
            message.setText("2025 광주광역시교육청 AI광주미래교육 박람회 사전 등록 완료\n" +
                    "2025 광주광역시교육청 AI광주미래교육 박람회 사전 등록이 완료되었습니다.\n" +
                    "출입 QR코드 링크: " + "https://qr.startup-expo.kr/" + objectUrl + "\n" +
                    "(문의) ☎062-380-4504");
            log.debug("[MSG] message built: to={}, from={}", sendQrEvent.getPhoneNumber(), phoneNumber);
            return message;
        } catch (IOException e) {
            log.error("[MSG] create failed: to={}, from={}, reason=IOException", sendQrEvent.getPhoneNumber(), phoneNumber, e);
        }
        return null;
    }
}
