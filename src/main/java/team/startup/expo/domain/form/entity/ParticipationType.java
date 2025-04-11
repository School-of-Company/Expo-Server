package team.startup.expo.domain.form.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum ParticipationType {
    @JsonProperty("TRAINEE")
    TRAINEE,
    @JsonProperty("STANDARD")
    STANDARD
}
