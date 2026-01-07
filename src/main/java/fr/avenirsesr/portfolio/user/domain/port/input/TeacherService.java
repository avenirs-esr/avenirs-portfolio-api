package fr.avenirsesr.portfolio.user.domain.port.input;

import fr.avenirsesr.portfolio.common.data.domain.model.User;
import fr.avenirsesr.portfolio.user.domain.model.Teacher;
import java.util.UUID;

public interface TeacherService {
  String getBio(User user);

  void updateProfile(User user, String bio);

  Teacher createTeacher(UUID userId, String bio);
}
