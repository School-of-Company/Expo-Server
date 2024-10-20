package team.startup.expo.domain.auth.service;


import team.startup.expo.domain.auth.presentation.dto.request.SignUpRequestDto;

public interface SignUpService {
    void execute(SignUpRequestDto dto);
}
