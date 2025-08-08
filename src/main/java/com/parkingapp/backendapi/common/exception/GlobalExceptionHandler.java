package com.parkingapp.backendapi.common.exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

/**
 * Global exception handler file
 *
 * <p>Note: Do NOT add handlers exceptions handled by Spring Security
 *
 * <p>When the backend encounters an error and throws it, this handler will automatically catch and
 * handle it by logging it for us to see it and returns a message to the user's client.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

  private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

  @ExceptionHandler(EntityNotFoundException.class)
  public ResponseEntity<Object> handleEntityNotFoundException(
      EntityNotFoundException ex, WebRequest request) {

    log.error(
        "Data inconsistency: Authenticated user not found after successful JWT validation: {}",
        ex.getMessage(),
        ex);

    Map<String, Object> body = new LinkedHashMap<>();
    body.put("timestamp", LocalDateTime.now());
    body.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
    // Provide a generic, safe message to the client for this internal issue
    body.put("error", "Internal Server Error");
    body.put("message", "An unexpected data issue occurred. Please try again or contact support.");

    // This particular EntityNotFoundException indicates a server-side data problem
    // because the user was already authenticated via JWT.
    // Therefore, HttpStatus.INTERNAL_SERVER_ERROR is appropriate.
    return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
  }

  /**
   * Handles exceptions related to a duplicate report found in the database.
   *
   * <p>When a user submits a report, the vehicle will be searched against the database. If there's
   * a current active report {@link com.parkingapp.backendapi.report.entity.Status } with an 'OPEN'
   * or 'ASSIGNED' status then it's currently active.
   *
   * @param ex the exception
   * @return the response entity
   */
  @ExceptionHandler(DuplicateReportException.class)
  public ResponseEntity<ErrorResponse> handleDuplicateReportException(DuplicateReportException ex) {
    log.warn("Duplicate Report Error: {}", ex.getMessage());
    return new ResponseEntity<>(
        new ErrorResponse(ex.getMessage()),
        HttpStatus.CONFLICT // Recommended for duplicate resources
        );
  }

  /**
   * Handles exception when a user tries to register with an already existing email address found in
   * the database.
   *
   * <p>When a user registers, the provided email is searched for availability in the database. If
   * found, throws this error. Intention is to gracefully fail and let the client know.
   *
   * <p>This is standard, but allows email scraping
   *
   * @param ex the exception
   * @return the response entity
   */
  @ExceptionHandler(EmailAlreadyExistsException.class)
  public ResponseEntity<ErrorResponse> handleEmailAlreadyExistsException(
      EmailAlreadyExistsException ex) {
    return new ResponseEntity<>(new ErrorResponse(ex.getMessage()), HttpStatus.CONFLICT);
  }

  /**
   * Handles exceptions when a requested resource is not found (e.g., report by ID). Logs specific
   * message, sends "Not Found" (404) to client.
   *
   * @param ex The exception
   * @return The response entity
   */
  @ExceptionHandler(NoSuchElementException.class) // <-- New handler
  public ResponseEntity<ErrorResponse> handleNoSuchElementException(NoSuchElementException ex) {
    log.warn("Resource Not Found Error: {}", ex.getMessage()); // Use warn level for not found
    return new ResponseEntity<>(
        new ErrorResponse(ex.getMessage()), // Return the specific message about what wasn't found
        HttpStatus.NOT_FOUND); // Appropriate 404 status
  }

  /**
   * Handles exceptions related to validation or business logic errors (e.g., unsupported
   * jurisdiction). Logs detailed message for developer, sends generic "Bad Request" to client.
   */
  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
    log.error(
        "Validation/Business Logic Error: {}",
        ex.getMessage(),
        ex); // Log full details for developer
    return new ResponseEntity<>(
        new ErrorResponse("Invalid data provided. Please check your request and try again."),
        HttpStatus.BAD_REQUEST);
  }

  /**
   * Handles exceptions from Jakarta Bean Validation (@NotNull, @Size, etc. on DTOs). Logs specific
   * field violations for developer, sends generic "Bad Request" to client.
   */
  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<ErrorResponse> handleConstraintViolationException(
      ConstraintViolationException ex) {
    // Collect specific constraint violations for developer logging
    String violations =
        ex.getConstraintViolations().stream()
            .map(
                ConstraintViolation
                    ::getMessage) // Or violation.getPropertyPath() + ": " + violation.getMessage()
            .collect(Collectors.joining(", "));
    log.error(
        "DTO Constraint Violation Error: {}", violations, ex); // Log full details for developer

    return new ResponseEntity<>(
        new ErrorResponse("One or more required fields are missing or improperly formatted."),
        HttpStatus.BAD_REQUEST);
  }

  /**
   * Handles errors during JSON parsing (e.g., malformed JSON in request body). Logs parsing error
   * for developer, sends generic "Bad Request" to client.
   */
  @ExceptionHandler(JsonProcessingException.class)
  public ResponseEntity<ErrorResponse> handleJsonProcessingException(JsonProcessingException ex) {
    log.error("JSON Parsing Error: {}", ex.getMessage(), ex); // Log full details for developer
    return new ResponseEntity<>(
        new ErrorResponse("Malformed JSON in request body. Please check your data."),
        HttpStatus.BAD_REQUEST);
  }

  /**
   * Catches any other unexpected exceptions. Logs full stack trace for developer, sends generic
   * "Internal Server Error" to client.
   */
  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
    log.error(
        "An unexpected internal server error occurred.", ex); // Log full stack trace for developer
    return new ResponseEntity<>(
        new ErrorResponse("An unexpected error occurred. Please try again later."),
        HttpStatus.INTERNAL_SERVER_ERROR);
  }
}
