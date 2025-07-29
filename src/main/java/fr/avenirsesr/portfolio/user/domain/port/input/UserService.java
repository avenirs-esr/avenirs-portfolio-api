package fr.avenirsesr.portfolio.user.domain.port.input;

import fr.avenirsesr.portfolio.user.domain.model.Student;
import fr.avenirsesr.portfolio.user.domain.model.Teacher;
import fr.avenirsesr.portfolio.user.domain.model.User;
import java.io.IOException;
import java.util.UUID;
import org.springframework.web.multipart.MultipartFile;

public interface UserService {
  User getProfile(UUID id);

  void updateProfile(
      User user,
      String firstname,
      String lastname,
      String email,
      String bio,
      String profilePictureUrl,
      String coverPictureUrl);

  String uploadProfilePicture(Student student, MultipartFile photoFile) throws IOException;

  String uploadProfilePicture(Teacher teacher, MultipartFile photoFile) throws IOException;

  String uploadCoverPicture(Student student, MultipartFile coverFile) throws IOException;

  String uploadCoverPicture(Teacher teacher, MultipartFile coverFile) throws IOException;
}
