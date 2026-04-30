package fr.avenirsesr.portfolio.shared.application.adapter.exception;

import fr.avenirsesr.portfolio.common.error.application.adapter.exception.BaseRestExceptionHandler;
import fr.avenirsesr.portfolio.common.error.application.adapter.response.ErrorResponse;
import fr.avenirsesr.portfolio.common.error.domain.exception.InvalidDateFormatException;
import java.time.format.DateTimeParseException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class RestExceptionHandler extends BaseRestExceptionHandler {

  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<ErrorResponse> handleNotReadable(HttpMessageNotReadableException ex) {
    Throwable cause = ex.getCause();

    if (cause != null
        && (cause.getCause() instanceof InvalidDateFormatException
            || cause.getCause() instanceof DateTimeParseException)) {
      InvalidDateFormatException invalidDateFormatException = new InvalidDateFormatException();
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
          .body(
              new ErrorResponse(
                  invalidDateFormatException.getErrorCode().name(),
                  invalidDateFormatException.getMessage()));
    }

    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(new ErrorResponse("MESSAGE_NOT_READABLE", ex.getMessage()));
  }
}
