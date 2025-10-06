package com.parkingapp.backendapi.auth.service;

import com.parkingapp.backendapi.auth.dto.JwtResponseDto;
import com.parkingapp.backendapi.auth.dto.RegisterRequestDto;
import com.parkingapp.backendapi.auth.mapper.NewUserMapper;
import com.parkingapp.backendapi.auth.utils.PasswordValidator;
import com.parkingapp.backendapi.common.exception.custom.EmailAlreadyExistsException;
import com.parkingapp.backendapi.common.exception.custom.PasswordValidationException;
import com.parkingapp.backendapi.infrastructure.security.jwt.JwtTokenProvider;
import com.parkingapp.backendapi.user.entity.AccountType;
import com.parkingapp.backendapi.user.entity.User;
import com.parkingapp.backendapi.user.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Service responsible for new user registers
 *
 * <ul>
 *   <li>password satisfies security requirements
 *   <li>firstname, lastname, and DOB are verified in the DTO via annotations
 *   <li>email must be available
 *   <li>hashes password
 *   <li>creates the new user
 *   <li>todo: send an email verification to confirm account -- need domain (business) for this and
 *       a dedicated email service provider which isn't free
 * </ul>
 */
@Service
@AllArgsConstructor
public class RegisterService {
  private final PasswordEncoder passwordEncoder;
  private final JwtTokenProvider jwtTokenProvider;
  private final UserRepository userRepository;
  private final NewUserMapper newUserMapper;

  public JwtResponseDto registerUser(RegisterRequestDto registerRequestDto) {
    System.out.println("made it to registerUser");
    // server side checks for password security policy and verifying password & confirm password
    if (!PasswordValidator.isPasswordSecure(
        registerRequestDto.password(), registerRequestDto.confirmPassword())) {
      System.out.println("failed passwordCheck");
      // todo: while client should catch this, specify the requirements that failed
      throw new PasswordValidationException("Password does not meet security requirements.");
    }
    System.out.println("password ok");
    // Annotations from the Dto class should handle firstname, lastname, and valid DOB checks

    if (userRepository.findByEmail(registerRequestDto.email()).isPresent()) {
      throw new EmailAlreadyExistsException("That email is taken. Try another.");
    }

    System.out.println("before save reached");

    User newUser = createNewUser(registerRequestDto);
    userRepository.save(newUser);

    String token = jwtTokenProvider.generateToken(newUser);

    return new JwtResponseDto(token, "Bearer");
  }

  /**
   * Uses mapper class to convert DTO -> User entity, then hashes and sets password.
   *
   * @param registerRequestDto user provided register DTO
   * @return newUser User
   */
  private User createNewUser(RegisterRequestDto registerRequestDto) {
    User newUser = newUserMapper.toEntity(registerRequestDto);

    newUser.setHashedPassword(passwordEncoder.encode(registerRequestDto.password()));

    // for now, all user types will resort to standard USER permissions
    // officers will be manually converted from standard users to officers
    newUser.setAccountType(AccountType.USER);

    return newUser;
  }
}
