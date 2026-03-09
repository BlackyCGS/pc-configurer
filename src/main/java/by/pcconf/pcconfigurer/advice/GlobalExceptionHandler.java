package by.pcconf.pcconfigurer.advice;

import by.pcconf.pcconfigurer.exception.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
  @Value("${EXTERNAL_API_ADDRESS:localhost}")
  private String externalApiAddress;
  @Value("${REDIRECT_PORT:80}")
  private Integer redirectPort;

  @ExceptionHandler(BadRequestCustomException.class)
  public ResponseEntity<String> handleBadRequestCustomException(BadRequestCustomException ex) {
    log.info("Bad request exception caught: {}", ex.getMessage());
    return ResponseEntity.badRequest().body(ex.getMessage());
  }

  @ExceptionHandler(InvalidTokenException.class)
  public ResponseEntity<String> handleInvalidTokenException(InvalidTokenException ex) {
    log.info("Invalid token caught: {}", ex.getMessage());
    return new ResponseEntity<>(ex.getMessage(), HttpStatus.UNAUTHORIZED);
  }

  @ExceptionHandler(NotFoundCustomException.class)
  public ResponseEntity<String> handleNotFoundCustomException(NotFoundCustomException ex) {
    log.trace("Not found custom exception caught: {}", ex.getMessage());
    return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(InternalServerErrorCustomException.class)
  public ResponseEntity<String> handleInternalServerErrorCustomException(InternalServerErrorCustomException ex) {
    log.error("Internal server error caught: {}", ex.getMessage());
    return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @ExceptionHandler(UnsupportedOperationCustomException.class)
  public ResponseEntity<String> handleUnsupportedOperationCustomException(UnsupportedOperationCustomException ex) {
    log.error("UnsupportedOperation caught: {}", ex.getMessage());
    return new ResponseEntity<>(ex.getMessage(), HttpStatus.METHOD_NOT_ALLOWED);
  }

  @ExceptionHandler(NoContentCustomException.class)
  public ResponseEntity<Void> handleNoContentCustomException(NoContentCustomException ex) {
    log.info("No content custom exception caught: {}", ex.getMessage());
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  @ExceptionHandler(ExternalApiUnauthorizedException.class)
  public ResponseEntity<String> handleExternalApiUnauthorizedException(ExternalApiUnauthorizedException ex) {
    log.info("External api unauthorized caught: {}", ex.getMessage());
    String errorUrl = "http://" + externalApiAddress + ":" + redirectPort + "/register";
    HttpHeaders headers = new HttpHeaders();
    headers.setLocation(URI.create(errorUrl));
    return new ResponseEntity<>(headers, HttpStatus.FOUND);
  }
}
