package edu.final_year.backendwebm.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> fieldErrors = ex.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        fieldError -> fieldError.getDefaultMessage() == null ? "Invalid value" : fieldError.getDefaultMessage(),
                        (existing, replacement) -> existing
                ));
        return ResponseEntity.badRequest().body(new ErrorResponse("Validation failed", fieldErrors));
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleEmailAlreadyExists(EmailAlreadyExistsException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ErrorResponse.of(ex.getMessage()));
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentials(BadCredentialsException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ErrorResponse.of("Invalid email or password"));
    }

    @ExceptionHandler(NifAlreadyRegisteredException.class)
    public ResponseEntity<ErrorResponse> handleNifAlreadyRegistered(NifAlreadyRegisteredException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ErrorResponse.of(ex.getMessage()));
    }

    @ExceptionHandler(InvalidNifException.class)
    public ResponseEntity<ErrorResponse> handleInvalidNif(InvalidNifException ex) {
        return ResponseEntity.badRequest().body(ErrorResponse.of(ex.getMessage()));
    }

    @ExceptionHandler(ObrUnavailableException.class)
    public ResponseEntity<ErrorResponse> handleObrUnavailable(ObrUnavailableException ex) {
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(ErrorResponse.of(ex.getMessage()));
    }

    @ExceptionHandler(InvalidRefreshTokenException.class)
    public ResponseEntity<ErrorResponse> handleInvalidRefreshToken(InvalidRefreshTokenException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ErrorResponse.of(ex.getMessage()));
    }
}
