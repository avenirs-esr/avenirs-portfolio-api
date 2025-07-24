package fr.avenirsesr.portfolio.shared.application.adapter.exception;

import fr.avenirsesr.portfolio.additionalskill.domain.exception.DuplicateAdditionalSkillException;
import fr.avenirsesr.portfolio.shared.application.adapter.response.ErrorResponse;
import fr.avenirsesr.portfolio.shared.domain.exception.BadImageSizeException;
import fr.avenirsesr.portfolio.shared.domain.exception.BadImageTypeException;
import fr.avenirsesr.portfolio.shared.domain.exception.BusinessException;
import fr.avenirsesr.portfolio.shared.domain.model.enums.EErrorCode;
import fr.avenirsesr.portfolio.user.domain.exception.UserCategoryNotRecognizedException;
import fr.avenirsesr.portfolio.user.domain.exception.UserIsNotStudentException;
import fr.avenirsesr.portfolio.user.domain.exception.UserNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

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
        .body(new ErrorResponse(ex.getErrorCode().name(), ex.getErrorCode().getMessage()));
  }

  @ExceptionHandler(UserNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleUserNotFound(UserNotFoundException ex) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(new ErrorResponse(ex.getErrorCode().name(), ex.getErrorCode().getMessage()));
  }

  @ExceptionHandler(BadImageTypeException.class)
  public ResponseEntity<ErrorResponse> handleUserNotFound(BadImageTypeException ex) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(new ErrorResponse(ex.getErrorCode().name(), ex.getErrorCode().getMessage()));
  }

  @ExceptionHandler(BadImageSizeException.class)
  public ResponseEntity<ErrorResponse> handleUserNotFound(BadImageSizeException ex) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(new ErrorResponse(ex.getErrorCode().name(), ex.getErrorCode().getMessage()));
  }

  @ExceptionHandler(UserCategoryNotRecognizedException.class)
  public ResponseEntity<ErrorResponse> handleUserCategoryNotRecognized(
      UserCategoryNotRecognizedException ex) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(new ErrorResponse(ex.getErrorCode().name(), ex.getErrorCode().getMessage()));
  }

  @ExceptionHandler(UserIsNotStudentException.class)
  public ResponseEntity<ErrorResponse> handleUserIsNotStudent(UserIsNotStudentException ex) {
    return ResponseEntity.status(HttpStatus.FORBIDDEN)
        .body(new ErrorResponse(ex.getErrorCode().name(), ex.getErrorCode().getMessage()));
  }

  @ExceptionHandler(DuplicateAdditionalSkillException.class)
  public ResponseEntity<ErrorResponse> handleStudentAdditionalSkillConflict(
      DuplicateAdditionalSkillException ex) {
    return ResponseEntity.status(HttpStatus.CONFLICT)
        .body(new ErrorResponse(ex.getErrorCode().name(), ex.getErrorCode().getMessage()));
  }

  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  public ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatch(
      MethodArgumentTypeMismatchException ex) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(
            new ErrorResponse(
                EErrorCode.INVALID_ARGUMENT_TYPE.name(),
                EErrorCode.INVALID_ARGUMENT_TYPE.getMessage()));
  }
}
