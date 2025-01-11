package team.startup.expo.domain.sms.service.impl;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.model.StorageType;
import net.nurigo.sdk.message.request.SingleMessageSendingRequest;
import net.nurigo.sdk.message.response.SingleMessageSentResponse;
import net.nurigo.sdk.message.service.DefaultMessageService;
import team.startup.expo.domain.admin.entity.Authority;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import team.startup.expo.domain.expo.entity.Expo;
import team.startup.expo.domain.expo.exception.NotFoundExpoException;
import team.startup.expo.domain.expo.repository.ExpoRepository;
import team.startup.expo.domain.participant.entity.StandardParticipant;
import team.startup.expo.domain.participant.repository.ParticipantRepository;
import team.startup.expo.domain.sms.exception.NotFoundParticipantException;
import team.startup.expo.domain.sms.exception.NotFoundTraineeException;
import team.startup.expo.domain.sms.presentation.dto.request.SendQrRequestDto;
import team.startup.expo.domain.sms.service.SendQrService;
import team.startup.expo.domain.trainee.entity.Trainee;
import team.startup.expo.domain.trainee.repository.TraineeRepository;
import team.startup.expo.global.annotation.TransactionService;
import team.startup.expo.global.sms.SmsProperties;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@TransactionService
@RequiredArgsConstructor
@Slf4j
public class SendQrServiceImpl implements SendQrService {

    private final static int WIDTH = 200;
    private final static int HEIGHT = 200;

    private final ParticipantRepository participantRepository;
    private final ExpoRepository expoRepository;
    private final DefaultMessageService messageService;
    private final TraineeRepository traineeRepository;
    private final SmsProperties smsProperties;

    public SingleMessageSentResponse execute(String expoId, SendQrRequestDto dto) {
        SingleMessageSentResponse response = null;

        Expo expo = expoRepository.findById(expoId)
                .orElseThrow(NotFoundExpoException::new);

        if (dto.getAuthority() == Authority.ROLE_STANDARD) {
            StandardParticipant participant = participantRepository.findByPhoneNumberAndExpo(dto.getPhoneNumber(), expo)
                    .orElseThrow(NotFoundParticipantException::new);

            String information = "{\"participantId\": " + participant.getId() + ", \"phoneNumber\": \"" + participant.getPhoneNumber() + "\"}";
            byte[] qrBytes = createQr(information);

            participant.addQrCode(qrBytes);

            Message message = createMessage(qrBytes, dto);

            response = messageService.sendOne(new SingleMessageSendingRequest(message));
        } else if (dto.getAuthority() == Authority.ROLE_TRAINEE) {
            Trainee trainee = traineeRepository.findByPhoneNumberAndExpo(dto.getPhoneNumber(), expo)
                    .orElseThrow(NotFoundTraineeException::new);

            String information = "{\"traineeId\": " + trainee.getId() + ", \"phoneNumber\": \"" + trainee.getPhoneNumber() + "\"}";

            byte[] qrBytes = createQr(information);

            trainee.addQrCode(qrBytes);

            Message message = createMessage(qrBytes, dto);

            response = messageService.sendOne(new SingleMessageSendingRequest(message));
        }

        return response;
    }

    private byte[] createQr(String information) {
        byte[] bytes = null;
        try {
            BitMatrix encode = new MultiFormatWriter().encode(information, BarcodeFormat.QR_CODE, WIDTH, HEIGHT);

            ByteArrayOutputStream out = new ByteArrayOutputStream();

            MatrixToImageWriter.writeToStream(encode, "JPG", out);

            bytes = out.toByteArray();
        } catch (IOException | WriterException e) {
            log.error(e.getMessage(), e);
        }

        return bytes;
    }

    private Message createMessage(byte[] qrBytes, SendQrRequestDto dto) {
        try {
            Path tempFilePath = Files.createTempFile("temp-qr", ".jpg");
            Files.write(tempFilePath, qrBytes);
            File tempFile = tempFilePath.toFile();

            String imageId;
            try {
                imageId = messageService.uploadFile(tempFile, StorageType.MMS, null);
            } finally {
                tempFile.delete();
            }

            Message message = new Message();
            message.setFrom(smsProperties.getFromNumber());
            message.setTo(dto.getPhoneNumber());
            message.setText("QR 코드가 포함된 메시지입니다.");
            message.setImageId(imageId);

            return message;
        } catch (IOException e) {}

        return null;
    }
}
