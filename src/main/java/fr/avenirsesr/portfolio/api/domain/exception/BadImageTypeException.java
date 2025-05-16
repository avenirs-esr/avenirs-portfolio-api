package fr.avenirsesr.portfolio.api.domain.exception;

public class BadImageTypeException extends BusinessException {

  public BadImageTypeException() {
    super("");
  }

  public BadImageTypeException(String message) {
    super(message);
  }
}
