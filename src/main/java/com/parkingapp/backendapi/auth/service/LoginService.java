package com.parkingapp.backendapi.auth.service;

import com.parkingapp.backendapi.auth.dto.JwtResponseDto;
import com.parkingapp.backendapi.auth.dto.LoginRequestDto;
import com.parkingapp.backendapi.security.jwt.JwtTokenProvider;
import com.parkingapp.backendapi.user.entity.User;
import com.parkingapp.backendapi.user.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

/**
 * Service responsible for verifying a login request and logging in a user.
 *
 * <ul>
 *   <li>Uses Authentication Manager to authenticate and load the user (UserDetailsServiceImpl)
 *   <li>generate and return JWT via Jwt Token Provider
 * </ul>
 */
@Service
@AllArgsConstructor
public class LoginService {

  private final AuthenticationManager authenticationManager;
  private final JwtTokenProvider jwtTokenProvider;
  private final UserRepository userRepository;

  /**
   * Authenticates a user based on their email and password, and generates a JWT.
   *
   * @param loginRequestDto The DTO containing the user's email and plain-text password.
   * @return A JwtResponseDto containing the generated JWT.
   * @throws BadCredentialsException if the email or password is incorrect.
   */
  public JwtResponseDto loginUser(LoginRequestDto loginRequestDto) {

    // authenticate the user
    Authentication authentication =
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                loginRequestDto.email(), loginRequestDto.password()));

    User user = (User) authentication.getPrincipal();

    String token = jwtTokenProvider.generateToken(user);

    return new JwtResponseDto(token, "Bearer");
  }
}
