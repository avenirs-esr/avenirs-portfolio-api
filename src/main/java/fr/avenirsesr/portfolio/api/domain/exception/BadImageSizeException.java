package fr.avenirsesr.portfolio.api.domain.exception;

public class BadImageSizeException extends BusinessException {

  public BadImageSizeException() {
    super("");
  }

  public BadImageSizeException(String message) {
    super(message);
  }
}
