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
        SingleMessageSentResponse response = null;
        try {
            log.info("[SendQr] Start: expoId={}, authority={}, phone={}",
                    sendQrEvent.getExpoId(), sendQrEvent.getAuthority(), sendQrEvent.getPhoneNumber());
            Expo expo = expoRepository.findById(sendQrEvent.getExpoId())
                    .orElseThrow(NotFoundExpoException::new);

            if (sendQrEvent.getAuthority() == Authority.ROLE_STANDARD) {
                StandardParticipant participant = standardParticipantRepository.findByPhoneNumberAndExpo(sendQrEvent.getPhoneNumber(), expo)
                        .orElseThrow(NotFoundParticipantException::new);
                log.info("[SendQr] STANDARD participant found: id={}, phone={}",
                        participant.getId(), participant.getPhoneNumber());
                String information = "{\"participantId\": " + participant.getId() + ", \"phoneNumber\": \"" + participant.getPhoneNumber() + "\"}";
                byte[] qrBytes = createQr(information);
                log.info("[SendQr] QR created for STANDARD: bytes={}",
                        qrBytes != null ? qrBytes.length : 0);
                Message message = createMessage(qrBytes, sendQrEvent, smsProperties.getFromStandardNumber());
                participant.plusSmsTryTime();
                standardParticipantRepository.save(participant);
                log.info("[SendQr] STANDARD smsTryTime updated: id={}, tryTime={}",
                        participant.getId(), participant.getSmsTryTime());
                response = messageService.sendOne(new SingleMessageSendingRequest(message));
                log.info("[SendQr] SMS sent (STANDARD): response={}", response);
            } else if (sendQrEvent.getAuthority() == Authority.ROLE_TRAINEE) {
                Trainee trainee = traineeRepository.findByPhoneNumberAndExpo(sendQrEvent.getPhoneNumber(), expo)
                        .orElseThrow(NotFoundTraineeException::new);
                log.info("[SendQr] TRAINEE found: id={}, phone={}",
                        trainee.getId(), trainee.getPhoneNumber());
                String information = "{\"traineeId\": " + trainee.getId() + ", \"phoneNumber\": \"" + trainee.getPhoneNumber() + "\"}";
                byte[] qrBytes = createQr(information);
                log.info("[SendQr] QR created for TRAINEE: bytes={}",
                        qrBytes != null ? qrBytes.length : 0);
                Message message = createMessage(qrBytes, sendQrEvent, smsProperties.getFromTraineeNumber());
                response = messageService.sendOne(new SingleMessageSendingRequest(message));
                log.info("[SendQr] SMS sent (TRAINEE): response={}", response);
            }
            log.info("[SendQr] Completed: expoId={}, authority={}, phone={}, response={}",
                    sendQrEvent.getExpoId(), sendQrEvent.getAuthority(), sendQrEvent.getPhoneNumber(), response);
        } catch (Exception e) {
            log.error("[SendQr] Error during sendQrHandler: expoId={}, authority={}, phone={}",
                    sendQrEvent.getExpoId(), sendQrEvent.getAuthority(), sendQrEvent.getPhoneNumber(), e);
            throw new GlobalException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
        return CompletableFuture.completedFuture(response);
    }

    private byte[] createQr(String information) {
        byte[] bytes = null;
        try {
            BitMatrix encode = new MultiFormatWriter().encode(information, BarcodeFormat.QR_CODE, WIDTH, HEIGHT);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(encode, "JPG", out);
            bytes = out.toByteArray();
        } catch (IOException | WriterException e) {
            log.error("[SendQr] QR generation failed: payload={}", information, e);
            throw new GlobalException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
        return bytes;
    }

    private Message createMessage(byte[] qrBytes, SendQrEvent sendQrEvent, String phoneNumber) {
        try {
            log.info("[SendQr] Preparing SMS message for phone={}", sendQrEvent.getPhoneNumber());
            Path tempFilePath = Files.createTempFile("temp-qr", ".jpg");
            Files.write(tempFilePath, qrBytes);
            File tempFile = tempFilePath.toFile();
            String objectUrl = s3Util.qrUpload(tempFile);
            log.info("[SendQr] Uploaded QR to S3: objectUrl={}", objectUrl);
            String contactNumber = phoneNumber.equals(smsProperties.getFromStandardNumber()) ? "062-380-4504" : "062-380-4587";
            Message message = new Message();
            message.setFrom(phoneNumber);
            message.setTo(sendQrEvent.getPhoneNumber());

            message.setText(
                    "2025 광주광역시교육청 AI광주미래교육 박람회 현장 등록이 완료되었습니다.\n" +
                    "출입 QR코드 링크: " + "https://s3.startup-expo.kr/" + objectUrl + "\n" +
                    "(문의) ☎" + contactNumber);
            return message;
        } catch (IOException e) {
            log.error("[SendQr] Failed to build SMS message for phone={}", sendQrEvent.getPhoneNumber(), e);
            throw new GlobalException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }
}
