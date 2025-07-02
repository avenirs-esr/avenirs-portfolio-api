package fr.avenirsesr.portfolio.user.domain.utils;

import fr.avenirsesr.portfolio.user.domain.exception.UserCategoryNotRecognizedException;
import fr.avenirsesr.portfolio.user.domain.model.enums.EUserCategory;

public class UserUtils {

  public static EUserCategory getUserCategory(String profile) {
    EUserCategory userCategory = EUserCategory.fromString(profile);

    if (userCategory == null) {
      throw new UserCategoryNotRecognizedException();
    }

    return userCategory;
  }
}
