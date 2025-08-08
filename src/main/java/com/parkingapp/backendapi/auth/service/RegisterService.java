package com.parkingapp.backendapi.auth.service;

import com.parkingapp.backendapi.auth.dto.JwtResponseDto;
import com.parkingapp.backendapi.auth.dto.RegisterRequestDto;
import com.parkingapp.backendapi.auth.utils.PasswordValidator;
import com.parkingapp.backendapi.common.exception.EmailAlreadyExistsException;
import com.parkingapp.backendapi.security.jwt.JwtTokenProvider;
import com.parkingapp.backendapi.user.entity.AccountType;
import com.parkingapp.backendapi.user.entity.User;
import com.parkingapp.backendapi.user.repository.UserRepository;
import java.time.Instant;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Service responsible for new user registers
 *
 * <ul>
 *   <li>password satisfies security requirements
 *   <li>firstname and lastname are considered valid, no white spaces, symbols, etc. -- annotations
 *   <li>Valid DOB -- annotation
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
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtTokenProvider jwtTokenProvider;

  public JwtResponseDto registerUser(RegisterRequestDto registerRequestDto) {
    if (!PasswordValidator.isPasswordSecure(
        registerRequestDto.password(), registerRequestDto.confirmPassword())) {
      return null;
    }

    // Annotations from the Dto class should handle firstname, lastname, and valid DOB checks right?
    if (userRepository.findByEmail(registerRequestDto.email()).isPresent()) {
      throw new EmailAlreadyExistsException("That email is taken. Try another.");
    }

    User newUser = createNewUser(registerRequestDto);
    userRepository.save(newUser);

    UserDetails userDetails = newUser; // errr...?

    String token = jwtTokenProvider.generateToken(userDetails);

    JwtResponseDto jwtResponseDto =
        new JwtResponseDto(
            token,
            "Bearer",
            newUser.getEmail(),
            newUser.getAccountType().toString(),
            Instant.now().toEpochMilli()); // unsure if this is correct

    return jwtResponseDto;
  }

  private User createNewUser(RegisterRequestDto registerRequestDto) {
    User newUser = new User();

    newUser.setFirstName(registerRequestDto.firstName());
    newUser.setLastName(registerRequestDto.firstName());
    newUser.setDateOfBirth(registerRequestDto.dateOfBirth());
    newUser.setEmail(registerRequestDto.email());
    newUser.setHashedPassword(passwordEncoder.encode(registerRequestDto.password()));
    newUser.setAccountType(AccountType.USER);
    newUser.setCreatedOn(Instant.now());

    return newUser;
  }
}
