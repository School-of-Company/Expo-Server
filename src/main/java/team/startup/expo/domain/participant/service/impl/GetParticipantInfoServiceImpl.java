package team.startup.expo.domain.participant.service.impl;

import lombok.RequiredArgsConstructor;
import team.startup.expo.domain.expo.entity.Expo;
import team.startup.expo.domain.expo.exception.NotFoundExpoException;
import team.startup.expo.domain.expo.repository.ExpoRepository;
import team.startup.expo.domain.participant.entity.StandardParticipant;
import team.startup.expo.domain.participant.presentation.dto.response.GetParticipantInfoResponseDto;
import team.startup.expo.domain.participant.repository.ParticipantRepository;
import team.startup.expo.domain.participant.service.GetParticipantInfoService;
import team.startup.expo.domain.trainee.entity.ApplicationType;
import team.startup.expo.global.annotation.ReadOnlyTransactionService;

import java.util.List;

@ReadOnlyTransactionService
@RequiredArgsConstructor
public class GetParticipantInfoServiceImpl implements GetParticipantInfoService {

    private final ParticipantRepository participantRepository;
    private final ExpoRepository expoRepository;

    public List<GetParticipantInfoResponseDto> execute(String expoId, ApplicationType type, String name) {
        Expo expo = expoRepository.findById(expoId)
                .orElseThrow(NotFoundExpoException::new);

        List<StandardParticipant> participantList;

        if (name == null) {
            participantList = participantRepository.findByExpoAndApplicationType(expo, type);
        } else {
            participantList = participantRepository.findByExpoAndApplicationTypeAndName(expo, type, name);
        }

        return participantList.stream()
                .map(standardParticipant -> GetParticipantInfoResponseDto.builder()
                        .id(standardParticipant.getId())
                        .name(standardParticipant.getName())
                        .phoneNumber(standardParticipant.getPhoneNumber())
                        .informationStatus(standardParticipant.getPersonalInformationStatus())
                        .build()
                ).toList();

    }
}
