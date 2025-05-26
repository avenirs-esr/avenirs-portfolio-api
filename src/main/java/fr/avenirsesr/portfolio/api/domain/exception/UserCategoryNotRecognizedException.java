package fr.avenirsesr.portfolio.api.domain.exception;

public class UserCategoryNotRecognizedException extends BusinessException {

  public UserCategoryNotRecognizedException() {
    super("");
  }

  public UserCategoryNotRecognizedException(String message) {
    super(message);
  }
}
