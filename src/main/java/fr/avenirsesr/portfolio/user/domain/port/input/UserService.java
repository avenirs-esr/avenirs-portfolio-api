package fr.avenirsesr.portfolio.user.domain.port.input;

import fr.avenirsesr.portfolio.common.data.domain.model.User;
import fr.avenirsesr.portfolio.common.data.domain.model.enums.EUserCategory;
import fr.avenirsesr.portfolio.user.domain.data.UserPhotosData;
import fr.avenirsesr.portfolio.user.domain.data.UserProfileOverviewData;
import java.util.UUID;

public interface UserService {
  User getUser(UUID id);

  UserPhotosData getUserPhotos(UUID userId, EUserCategory userCategory);

  UserProfileOverviewData getUserProfileOverviewDTO(UUID userId, EUserCategory userCategory);

  void updateProfile(
      EUserCategory userCategory, String firstname, String lastname, String email, String bio);
}
