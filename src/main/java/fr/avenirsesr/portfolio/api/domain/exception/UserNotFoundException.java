package fr.avenirsesr.portfolio.api.domain.exception;

public class UserNotFoundException extends BusinessException {

  public UserNotFoundException() {
    super("");
  }

  public UserNotFoundException(String message) {
    super(message);
  }
}
