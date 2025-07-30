package fr.avenirsesr.portfolio.user.domain.port.input;

import fr.avenirsesr.portfolio.user.domain.model.User;
import fr.avenirsesr.portfolio.user.domain.model.UserPhotos;
import fr.avenirsesr.portfolio.user.domain.model.enums.EUserCategory;
import java.util.UUID;

public interface UserService {
  User getUser(UUID id);

  UserPhotos getUserPhotos(UUID userId, EUserCategory userCategory);

  void updateProfile(
      EUserCategory userCategory,
      User user,
      String firstname,
      String lastname,
      String email,
      String bio);
}
