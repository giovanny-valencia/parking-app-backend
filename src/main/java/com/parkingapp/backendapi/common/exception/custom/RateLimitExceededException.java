package com.parkingapp.backendapi.common.exception.custom;

public class RateLimitExceededException extends RuntimeException {
  public RateLimitExceededException(String message) {
    super(message);
  }
}
