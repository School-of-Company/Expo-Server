package team.startup.expo.domain.form.presentation.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import team.startup.expo.domain.form.entity.DynamicFormType;
import team.startup.expo.domain.form.entity.FormType;
import team.startup.expo.domain.form.entity.ParticipationType;
import team.startup.expo.domain.trainee.entity.ApplicationType;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GetFormResponseDto {
    private String title;
    private String informationText;
    private ParticipationType participantType;
    private ApplicationType applicationType;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private List<DynamicFormResponseDto> dynamicForm;

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DynamicFormResponseDto {
        private String title;
        private FormType formType;
        private String jsonData;
        private Boolean requiredStatus;
        private String otherJson;
        private DynamicFormType dynamicFormType;
    }
}
