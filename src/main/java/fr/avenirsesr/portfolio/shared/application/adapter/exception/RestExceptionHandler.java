package fr.avenirsesr.portfolio.shared.application.adapter.exception;

import fr.avenirsesr.portfolio.additionalskill.domain.exception.AdditionalSkillNotFoundException;
import fr.avenirsesr.portfolio.additionalskill.domain.exception.DuplicateAdditionalSkillException;
import fr.avenirsesr.portfolio.additionalskill.domain.exception.InvalidDescriptionException;
import fr.avenirsesr.portfolio.common.error.application.adapter.exception.BaseRestExceptionHandler;
import fr.avenirsesr.portfolio.common.error.application.adapter.response.ErrorResponse;
import fr.avenirsesr.portfolio.common.error.domain.exception.UserNotFoundException;
import fr.avenirsesr.portfolio.common.security.domain.exception.UserNotAuthorizedException;
import fr.avenirsesr.portfolio.file.domain.exception.FileNotFoundException;
import fr.avenirsesr.portfolio.file.domain.exception.FileSizeTooBigException;
import fr.avenirsesr.portfolio.file.domain.exception.FileTypeNotSupportedException;
import fr.avenirsesr.portfolio.selfknowledge.domain.exception.SelfKnowledgeInvalidDescriptionException;
import fr.avenirsesr.portfolio.selfknowledge.domain.exception.SelfKnowledgeInvalidRatingException;
import fr.avenirsesr.portfolio.selfknowledge.domain.exception.SelfKnowledgeInvalidTitleException;
import fr.avenirsesr.portfolio.student.progress.domain.exception.SkillLevelNotFoundException;
import fr.avenirsesr.portfolio.student.progress.domain.exception.StudentProgressNotFoundException;
import fr.avenirsesr.portfolio.trace.domain.exception.TraceNotFoundException;
import fr.avenirsesr.portfolio.user.domain.exception.UserCategoryNotRecognizedException;
import fr.avenirsesr.portfolio.user.domain.exception.UserIsNotStudentException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class RestExceptionHandler extends BaseRestExceptionHandler {

  @ExceptionHandler(UserNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleUserNotFoundException(UserNotFoundException ex) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(new ErrorResponse(ex.getErrorCode().name(), ex.getMessage()));
  }

  @ExceptionHandler(FileTypeNotSupportedException.class)
  public ResponseEntity<ErrorResponse> handleFileTypeNotSupportedException(
      FileTypeNotSupportedException ex) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(new ErrorResponse(ex.getErrorCode().name(), ex.getMessage()));
  }

  @ExceptionHandler(FileNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleFileNotFoundException(FileNotFoundException ex) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(new ErrorResponse(ex.getErrorCode().name(), ex.getMessage()));
  }

  @ExceptionHandler(FileSizeTooBigException.class)
  public ResponseEntity<ErrorResponse> handleFileSizeTooBigException(FileSizeTooBigException ex) {
    return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE)
        .body(new ErrorResponse(ex.getErrorCode().name(), ex.getMessage()));
  }

  @ExceptionHandler(UserCategoryNotRecognizedException.class)
  public ResponseEntity<ErrorResponse> handleUserCategoryNotRecognizedException(
      UserCategoryNotRecognizedException ex) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(new ErrorResponse(ex.getErrorCode().name(), ex.getMessage()));
  }

  @ExceptionHandler(UserIsNotStudentException.class)
  public ResponseEntity<ErrorResponse> handleUserIsNotStudentException(
      UserIsNotStudentException ex) {
    return ResponseEntity.status(HttpStatus.FORBIDDEN)
        .body(new ErrorResponse(ex.getErrorCode().name(), ex.getMessage()));
  }

  @ExceptionHandler(DuplicateAdditionalSkillException.class)
  public ResponseEntity<ErrorResponse> handleDuplicateAdditionalSkillException(
      DuplicateAdditionalSkillException ex) {
    return ResponseEntity.status(HttpStatus.CONFLICT)
        .body(new ErrorResponse(ex.getErrorCode().name(), ex.getMessage()));
  }

  @ExceptionHandler(AdditionalSkillNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleAdditionalSkillNotFoundException(
      AdditionalSkillNotFoundException ex) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(new ErrorResponse(ex.getErrorCode().name(), ex.getMessage()));
  }

  @ExceptionHandler(TraceNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleTraceNotFoundException(TraceNotFoundException ex) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(new ErrorResponse(ex.getErrorCode().name(), ex.getMessage()));
  }

  @ExceptionHandler(UserNotAuthorizedException.class)
  public ResponseEntity<ErrorResponse> handleUserNotAuthorizedException(
      UserNotAuthorizedException ex) {
    return ResponseEntity.status(HttpStatus.FORBIDDEN)
        .body(new ErrorResponse(ex.getErrorCode().name(), ex.getMessage()));
  }

  @ExceptionHandler(StudentProgressNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleStudentProgressNotFoundException(
      StudentProgressNotFoundException ex) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(new ErrorResponse(ex.getErrorCode().name(), ex.getMessage()));
  }

  @ExceptionHandler(SkillLevelNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleSkillLevelNotFoundException(
      SkillLevelNotFoundException ex) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(new ErrorResponse(ex.getErrorCode().name(), ex.getMessage()));
  }

  @ExceptionHandler(InvalidDescriptionException.class)
  public ResponseEntity<ErrorResponse> handleInvalidDescriptionException(
      InvalidDescriptionException ex) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(new ErrorResponse(ex.getErrorCode().name(), ex.getMessage()));
  }

  @ExceptionHandler(SelfKnowledgeInvalidTitleException.class)
  public ResponseEntity<ErrorResponse> handleSelfKnowledgeInvalidTitleException(
      SelfKnowledgeInvalidTitleException ex) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(new ErrorResponse(ex.getErrorCode().name(), ex.getMessage()));
  }

  @ExceptionHandler(SelfKnowledgeInvalidDescriptionException.class)
  public ResponseEntity<ErrorResponse> handleSelfKnowledgeInvalidDescriptionException(
      SelfKnowledgeInvalidDescriptionException ex) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(new ErrorResponse(ex.getErrorCode().name(), ex.getMessage()));
  }

  @ExceptionHandler(SelfKnowledgeInvalidRatingException.class)
  public ResponseEntity<ErrorResponse> handleSelfKnowledgeInvalidRatingException(
      SelfKnowledgeInvalidRatingException ex) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(new ErrorResponse(ex.getErrorCode().name(), ex.getMessage()));
  }
}
