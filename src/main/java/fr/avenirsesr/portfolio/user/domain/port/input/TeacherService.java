package fr.avenirsesr.portfolio.user.domain.port.input;

import fr.avenirsesr.portfolio.common.data.domain.model.User;

public interface TeacherService {
  String getBio(User user);

  void updateProfile(User user, String bio);
}
