package fr.avenirsesr.portfolio.shared.application.adapter.exception;

import fr.avenirsesr.portfolio.additionalskill.domain.exception.AdditionalSkillNotFoundException;
import fr.avenirsesr.portfolio.additionalskill.domain.exception.DuplicateAdditionalSkillException;
import fr.avenirsesr.portfolio.file.domain.exception.FileNotFoundException;
import fr.avenirsesr.portfolio.file.domain.exception.FileSizeTooBigException;
import fr.avenirsesr.portfolio.file.domain.exception.FileTypeNotSupportedException;
import fr.avenirsesr.portfolio.shared.application.adapter.response.ErrorResponse;
import fr.avenirsesr.portfolio.shared.domain.exception.BusinessException;
import fr.avenirsesr.portfolio.shared.domain.model.enums.EErrorCode;
import fr.avenirsesr.portfolio.student.progress.domain.exception.StudentProgressNotFoundException;
import fr.avenirsesr.portfolio.trace.domain.exception.TraceNotFoundException;
import fr.avenirsesr.portfolio.user.domain.exception.UserCategoryNotRecognizedException;
import fr.avenirsesr.portfolio.user.domain.exception.UserIsNotStudentException;
import fr.avenirsesr.portfolio.user.domain.exception.UserNotAuthorizedException;
import fr.avenirsesr.portfolio.user.domain.exception.UserNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

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
  public ResponseEntity<ErrorResponse> handle(UserNotFoundException ex) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(new ErrorResponse(ex.getErrorCode().name(), ex.getErrorCode().getMessage()));
  }

  @ExceptionHandler(FileTypeNotSupportedException.class)
  public ResponseEntity<ErrorResponse> handle(FileTypeNotSupportedException ex) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(new ErrorResponse(ex.getErrorCode().name(), ex.getErrorCode().getMessage()));
  }

  @ExceptionHandler(FileNotFoundException.class)
  public ResponseEntity<ErrorResponse> handle(FileNotFoundException ex) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(new ErrorResponse(ex.getErrorCode().name(), ex.getErrorCode().getMessage()));
  }

  @ExceptionHandler(FileSizeTooBigException.class)
  public ResponseEntity<ErrorResponse> handle(FileSizeTooBigException ex) {
    return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE)
        .body(new ErrorResponse(ex.getErrorCode().name(), ex.getErrorCode().getMessage()));
  }

  @ExceptionHandler(MaxUploadSizeExceededException.class)
  public ResponseEntity<ErrorResponse> handle(MaxUploadSizeExceededException ex) {
    return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE)
        .body(
            new ErrorResponse(
                EErrorCode.MAX_FILE_SIZE_EXCEEDED.name(),
                EErrorCode.MAX_FILE_SIZE_EXCEEDED.getMessage()));
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

  @ExceptionHandler(AdditionalSkillNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleStudentAdditionalSkillNotFound(
      AdditionalSkillNotFoundException ex) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(new ErrorResponse(ex.getErrorCode().name(), ex.getErrorCode().getMessage()));
  }

  @ExceptionHandler(TraceNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleTraceNotFound(TraceNotFoundException ex) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(new ErrorResponse(ex.getErrorCode().name(), ex.getErrorCode().getMessage()));
  }

  @ExceptionHandler(UserNotAuthorizedException.class)
  public ResponseEntity<ErrorResponse> handleTraceNotFound(UserNotAuthorizedException ex) {
    return ResponseEntity.status(HttpStatus.FORBIDDEN)
        .body(new ErrorResponse(ex.getErrorCode().name(), ex.getErrorCode().getMessage()));
  }

  @ExceptionHandler(StudentProgressNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleStudentProgressNotFound(
      StudentProgressNotFoundException ex) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(new ErrorResponse(ex.getErrorCode().name(), ex.getErrorCode().getMessage()));
  }

  @ExceptionHandler(MissingServletRequestParameterException.class)
  public ResponseEntity<String> handleMissingParams(MissingServletRequestParameterException ex) {
    String name = ex.getParameterName();
    String message = String.format("Missing required parameter: %s", name);
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
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
