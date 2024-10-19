package team.startup.expo.domain.auth.service;


import team.startup.expo.domain.auth.presentation.dto.request.SignUpRequest;

public interface SignUpService {
    void execute(SignUpRequest dto);
}
