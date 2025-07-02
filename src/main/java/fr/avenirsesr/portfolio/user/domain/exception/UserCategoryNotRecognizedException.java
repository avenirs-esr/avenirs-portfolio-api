package fr.avenirsesr.portfolio.user.domain.exception;

import fr.avenirsesr.portfolio.shared.domain.exception.BusinessException;
import fr.avenirsesr.portfolio.shared.domain.model.enums.EErrorCode;

public class UserCategoryNotRecognizedException extends BusinessException {

  public UserCategoryNotRecognizedException() {
    super(EErrorCode.USER_CATEGORY_NOT_FOUND);
  }

  public UserCategoryNotRecognizedException(String customMessage) {
    super(EErrorCode.USER_CATEGORY_NOT_FOUND, customMessage);
  }
}
