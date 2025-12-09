package fr.avenirsesr.portfolio.user.domain.port.input;

import fr.avenirsesr.portfolio.common.data.domain.model.enums.EUserCategory;
import fr.avenirsesr.portfolio.common.user.domain.port.output.BaseUserService;
import fr.avenirsesr.portfolio.user.domain.data.UserPhotosData;
import fr.avenirsesr.portfolio.user.domain.data.UserProfileOverviewData;
import java.util.UUID;

public interface UserService extends BaseUserService {

  UserPhotosData getUserPhotos(UUID userId, EUserCategory userCategory);

  UserProfileOverviewData getUserProfileOverviewDTO(UUID userId, EUserCategory userCategory);

  void updateProfile(
      EUserCategory userCategory, String firstname, String lastname, String email, String bio);
}
