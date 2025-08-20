package com.parkingapp.backendapi.auth.service;

import com.parkingapp.backendapi.auth.dto.JwtResponseDto;
import com.parkingapp.backendapi.auth.dto.LoginRequestDto;
import com.parkingapp.backendapi.auth.dto.RegisterRequestDto;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AuthService {

  private final RegisterService registerService;
  private final LoginService loginService;

  public JwtResponseDto handleRegisterRequest(RegisterRequestDto registerRequest) {
    return registerService.registerUser(registerRequest);
  }

  public JwtResponseDto HandleLoginRequest(LoginRequestDto loginRequest) {
    return loginService.loginUser(loginRequest);
  }
}
