package fr.avenirsesr.portfolio.user.domain.port.input;

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

  String uploadStudentProfilePicture(User user, MultipartFile photoFile) throws IOException;

  String uploadStudentCoverPicture(User user, MultipartFile coverFile) throws IOException;

  String uploadTeacherProfilePicture(User user, MultipartFile photoFile) throws IOException;

  String uploadTeacherCoverPicture(User user, MultipartFile coverFile) throws IOException;
}
