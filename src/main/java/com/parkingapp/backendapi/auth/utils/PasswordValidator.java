package com.parkingapp.backendapi.auth.utils;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Ensures password satisfies security requirements
 *
 * <ul>
 *   <li>Password and confirmPassword are equal
 *   <li>Min length 8 characters
 *   <li>At least one special character
 *   <li>At least one uppercase letter
 *   <li>At least one digit
 * </ul>
 */
public final class PasswordValidator {
  private static final int PASSWORD_MINIMUM_LENGTH = 8;
  private static final Pattern SPECIAL_CHAR_PATTERN =
      Pattern.compile(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?].*");
  private static final Pattern CAPITAL_LETTERS = Pattern.compile(".*[A-Z].*");
  private static final Pattern DIGIT_CHAR_PATTERN = Pattern.compile(".*\\d.*");

  // Private constructor to prevent instantiation of this utility class
  private PasswordValidator() {}

  /**
   * Checks if a password is secure according to all defined rules.
   *
   * @param password The password string to validate.
   * @param confirmPassword The confirmation password string to compare.
   * @return true if the password is secure, false otherwise.
   */
  public static boolean isPasswordSecure(String password, String confirmPassword) {
    if (password == null || confirmPassword == null) {
      return false;
    }

    return isPasswordConfirmed(password, confirmPassword)
        && isMinLengthMet(password)
        && hasSpecialChar(password)
        && hasUppercase(password)
        && hasDigit(password);
  }

  // confirm password check will run everytime this is run. Makes sense to store it here
  private static boolean isPasswordConfirmed(String password, String confirmPassword) {
    return Objects.equals(password, confirmPassword);
  }

  private static boolean isMinLengthMet(String password) {
    return password.length() >= PASSWORD_MINIMUM_LENGTH;
  }

  private static boolean hasSpecialChar(String password) {
    return SPECIAL_CHAR_PATTERN.matcher(password).matches();
  }

  private static boolean hasUppercase(String password) {
    return CAPITAL_LETTERS.matcher(password).matches();
  }

  private static boolean hasDigit(String password) {
    return DIGIT_CHAR_PATTERN.matcher(password).matches();
  }
}
