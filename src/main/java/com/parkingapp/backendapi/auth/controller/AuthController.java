package com.parkingapp.backendapi.auth.controller;

import com.parkingapp.backendapi.auth.dto.JwtResponseDto;
import com.parkingapp.backendapi.auth.dto.LoginRequestDto;
import com.parkingapp.backendapi.auth.dto.RegisterRequestDto;
import com.parkingapp.backendapi.auth.service.AuthService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

  private AuthService authService;

  @PostMapping("/register")
  public ResponseEntity<JwtResponseDto> RegisterRequest(
      @RequestBody RegisterRequestDto registerRequest) {

    return ResponseEntity.ok(authService.handleRegisterRequest(registerRequest));
  }

  @PostMapping("/login")
  public ResponseEntity<JwtResponseDto> LoginRequest(@RequestBody LoginRequestDto loginRequest) {

    return ResponseEntity.ok(authService.HandleLoginRequest(loginRequest));
  }

  // todo: v2, add logout for token rotation
}
