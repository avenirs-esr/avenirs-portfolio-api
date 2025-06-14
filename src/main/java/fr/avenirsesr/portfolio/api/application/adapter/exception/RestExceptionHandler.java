package fr.avenirsesr.portfolio.api.application.adapter.exception;

import fr.avenirsesr.portfolio.api.application.adapter.response.ErrorResponse;
import fr.avenirsesr.portfolio.api.domain.exception.BadImageSizeException;
import fr.avenirsesr.portfolio.api.domain.exception.BadImageTypeException;
import fr.avenirsesr.portfolio.api.domain.exception.BusinessException;
import fr.avenirsesr.portfolio.api.domain.exception.UserCategoryNotRecognizedException;
import fr.avenirsesr.portfolio.api.domain.exception.UserIsNotStudentException;
import fr.avenirsesr.portfolio.api.domain.exception.UserNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class RestExceptionHandler {

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
    log.error("Erreur technique inattendue", ex);

    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(new ErrorResponse("INTERNAL_ERROR", "Une erreur technique est survenue."));
  }

  @ExceptionHandler(BusinessException.class)
  public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException ex) {
    log.error("Erreur métier inattendue", ex);

    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(new ErrorResponse(ex.getErrorCode().getCode(), ex.getErrorCode().getMessage()));
  }

  @ExceptionHandler(UserNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleUserNotFound(UserNotFoundException ex) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(new ErrorResponse(ex.getErrorCode().getCode(), ex.getErrorCode().getMessage()));
  }

  @ExceptionHandler(BadImageTypeException.class)
  public ResponseEntity<ErrorResponse> handleUserNotFound(BadImageTypeException ex) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(new ErrorResponse(ex.getErrorCode().getCode(), ex.getErrorCode().getMessage()));
  }

  @ExceptionHandler(BadImageSizeException.class)
  public ResponseEntity<ErrorResponse> handleUserNotFound(BadImageSizeException ex) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(new ErrorResponse(ex.getErrorCode().getCode(), ex.getErrorCode().getMessage()));
  }

  @ExceptionHandler(UserCategoryNotRecognizedException.class)
  public ResponseEntity<ErrorResponse> handleUserCategoryNotRecognized(
      UserCategoryNotRecognizedException ex) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(new ErrorResponse(ex.getErrorCode().getCode(), ex.getErrorCode().getMessage()));
  }

  @ExceptionHandler(UserIsNotStudentException.class)
  public ResponseEntity<ErrorResponse> handleUserIsNotStudent(UserIsNotStudentException ex) {
    return ResponseEntity.status(HttpStatus.FORBIDDEN)
        .body(new ErrorResponse(ex.getErrorCode().getCode(), ex.getErrorCode().getMessage()));
  }
}
