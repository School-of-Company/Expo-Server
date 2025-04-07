package team.startup.expo.domain.form.presentation.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import team.startup.expo.domain.form.entity.FormType;
import team.startup.expo.domain.form.entity.ParticipationType;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GetFormResponseDto {
    private String informationText;
    private ParticipationType participantType;
    private List<GetFormResponseDto.DynamicFormRequestDto> dynamicForm;

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DynamicFormRequestDto {
        private String title;
        private FormType formType;
        private String jsonData;
        private Boolean requiredStatus;
        private String otherJson;
    }
}
