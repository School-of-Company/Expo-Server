package team.startup.expo.domain.training.service.impl;

import lombok.RequiredArgsConstructor;
import team.startup.expo.domain.admin.Admin;
import team.startup.expo.domain.admin.util.UserUtil;
import team.startup.expo.domain.expo.Expo;
import team.startup.expo.domain.expo.exception.NotFoundExpoException;
import team.startup.expo.domain.expo.exception.NotMatchAdminException;
import team.startup.expo.domain.expo.repository.ExpoRepository;
import team.startup.expo.domain.training.TrainingProgram;
import team.startup.expo.domain.training.presentation.dto.request.AddTrainingProRequestDto;
import team.startup.expo.domain.training.repository.TrainingProgramRepository;
import team.startup.expo.domain.training.service.AddTrainingProService;
import team.startup.expo.global.annotation.TransactionService;

@RequiredArgsConstructor
@TransactionService
public class AddTrainingProServiceImpl implements AddTrainingProService {

    private final ExpoRepository expoRepository;
    private final TrainingProgramRepository trainingProgramRepository;
    private final UserUtil userUtil;

    public void execute(Long expoId, AddTrainingProRequestDto dto) {
        Admin admin = userUtil.getCurrentUser();

        Expo expo = expoRepository.findById(expoId)
                .orElseThrow(NotFoundExpoException::new);

        if (expo.getAdmin() != admin)
            throw new NotMatchAdminException();

        saveTrainingPro(dto, expo);
    }

    private void saveTrainingPro(AddTrainingProRequestDto dto, Expo expo) {
        TrainingProgram trainingProgram = TrainingProgram.builder()
                .title(dto.getTitle())
                .startedAt(dto.getStartedAt())
                .endedAt(dto.getEndedAt())
                .category(dto.getCategory())
                .expo(expo)
                .build();

        trainingProgramRepository.save(trainingProgram);
    }
}