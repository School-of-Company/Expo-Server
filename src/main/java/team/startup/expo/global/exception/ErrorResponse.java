package team.startup.expo.global.exception;

import lombok.Builder;

@Builder
public record ErrorResponse(int status, String message) {}
