package fr.avenirsesr.portfolio.api.domain.exception;

import fr.avenirsesr.portfolio.api.domain.model.enums.EErrorCode;

public class UserCategoryNotRecognizedException extends BusinessException {

  public UserCategoryNotRecognizedException() {
    super(EErrorCode.USER_CATEGORY_NOT_FOUND);
  }

  public UserCategoryNotRecognizedException(String customMessage) {
    super(EErrorCode.USER_CATEGORY_NOT_FOUND, customMessage);
  }
}
