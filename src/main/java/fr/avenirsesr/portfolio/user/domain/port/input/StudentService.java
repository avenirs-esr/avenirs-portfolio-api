package fr.avenirsesr.portfolio.user.domain.port.input;

import fr.avenirsesr.portfolio.common.data.domain.model.User;
import fr.avenirsesr.portfolio.user.domain.model.Student;
import java.util.UUID;

public interface StudentService {
  Student getStudentById(UUID studentId);

  String getBio(User user);

  void updateProfile(User user, String bio);
}
