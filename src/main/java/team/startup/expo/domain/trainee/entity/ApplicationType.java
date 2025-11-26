package team.startup.expo.domain.trainee.entity;

public enum ApplicationType {
    PRE("사전 등록"),
    FIELD("현장 등록");

    private final String koreanName;

    ApplicationType(String koreanName) {
        this.koreanName = koreanName;
    }

    public String getKoreanName() {
        return koreanName;
    }
}
