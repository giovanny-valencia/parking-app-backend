package com.parkingapp.backendapi.common.exception;

public class DuplicateReportException extends RuntimeException {
  public DuplicateReportException(String message) {
    super(message);
  }

  // You can add constructors for cause if needed, but a single message is often enough
  public DuplicateReportException(String message, Throwable cause) {
    super(message, cause);
  }
}
