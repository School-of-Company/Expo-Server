package team.startup.expo.domain.stat.presentation.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class StandardParticipantStatsResponse {
    private ElementaryDto elementary;
    private MiddleDto middle;
    private HighDto high;
    private KindergartenDto kindergarten;
    private OtherDto other;

    @Getter
    @Builder
    public static class ElementaryDto {
        private Integer number;
        private Integer percent;
    }

    @Getter
    @Builder
    public static class MiddleDto {
        private Integer number;
        private Integer percent;
    }

    @Getter
    @Builder
    public static class HighDto {
        private Integer number;
        private Integer percent;
    }

    @Getter
    @Builder
    public static class KindergartenDto {
        private Integer number;
        private Integer percent;
    }

    @Getter
    @Builder
    public static class OtherDto {
        private Integer number;
        private Integer percent;
    }
}