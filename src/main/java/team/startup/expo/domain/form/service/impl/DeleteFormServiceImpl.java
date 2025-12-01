package team.startup.expo.domain.form.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import team.startup.expo.domain.expo.entity.Expo;
import team.startup.expo.domain.expo.exception.NotFoundExpoException;
import team.startup.expo.domain.expo.repository.ExpoRepository;
import team.startup.expo.domain.form.entity.Form;
import team.startup.expo.domain.form.entity.ParticipationType;
import team.startup.expo.domain.form.exception.NotFoundFormException;
import team.startup.expo.domain.form.repository.DynamicFormRepository;
import team.startup.expo.domain.form.repository.FormRepository;
import team.startup.expo.domain.form.service.DeleteFormService;
import team.startup.expo.domain.json.repository.DynamicJsonRepository;
import team.startup.expo.domain.trainee.entity.ApplicationType;
import team.startup.expo.global.annotation.TransactionService;

@TransactionService
@RequiredArgsConstructor
@CacheConfig(cacheNames = "Form")
public class DeleteFormServiceImpl implements DeleteFormService {

    private final ExpoRepository expoRepository;
    private final FormRepository formRepository;
    private final DynamicFormRepository dynamicFormRepository;
    private final DynamicJsonRepository dynamicJsonRepository;

    @CacheEvict(key = "#expoId + '_' + #participationType + '_' + #applicationType", cacheNames = "cacheManager")
    public void execute(String expoId, ParticipationType participationType, ApplicationType applicationType) {
        Expo expo = expoRepository.findById(expoId)
                .orElseThrow(NotFoundExpoException::new);

        Form form = formRepository.findByExpoAndParticipationTypeAndApplicationType(expo, participationType, applicationType)
                .orElseThrow(NotFoundFormException::new);

        dynamicJsonRepository.deleteAllByFormId(form.getId());

        dynamicFormRepository.deleteByFormId(form.getId());

        formRepository.delete(form);
    }
}
