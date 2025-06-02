package fr.avenirsesr.portfolio.api.domain.utils;

import fr.avenirsesr.portfolio.api.domain.exception.UserCategoryNotRecognizedException;
import fr.avenirsesr.portfolio.api.domain.model.enums.EUserCategory;

public class UserUtils {

  public static EUserCategory getUserCategory(String profile) {
    EUserCategory userCategory = EUserCategory.fromString(profile);

    if (userCategory == null) {
      throw new UserCategoryNotRecognizedException();
    }

    return userCategory;
  }
}
