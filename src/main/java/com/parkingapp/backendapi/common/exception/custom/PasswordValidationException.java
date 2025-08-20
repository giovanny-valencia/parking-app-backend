package com.parkingapp.backendapi.common.exception.custom;

/** custom exception when a provided password fails the security requirements. */
public class PasswordValidationException extends RuntimeException {
  public PasswordValidationException(String message) {
    super(message);
  }
}
