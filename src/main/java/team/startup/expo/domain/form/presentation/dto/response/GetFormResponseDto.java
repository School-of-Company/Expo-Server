package team.startup.expo.domain.form.presentation.dto.response;

import lombok.Builder;
import lombok.Getter;
import team.startup.expo.domain.form.entity.FormType;
import team.startup.expo.domain.form.entity.ParticipationType;

import java.util.List;

@Getter
@Builder
public class GetFormResponseDto {
    private String informationImage;
    private ParticipationType participantType;
    private List<GetFormResponseDto.DynamicFormRequestDto> dynamicForm;

    @Getter
    @Builder
    public static class DynamicFormRequestDto {
        private String title;
        private FormType formType;
        private String jsonData;
        private Boolean requiredStatus;
        private String otherJson;
    }
}
