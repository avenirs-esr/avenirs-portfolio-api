package fr.avenirsesr.portfolio.user.domain.service;

import fr.avenirsesr.portfolio.shared.domain.exception.BadImageSizeException;
import fr.avenirsesr.portfolio.shared.domain.exception.BadImageTypeException;
import fr.avenirsesr.portfolio.user.domain.exception.UserNotFoundException;
import fr.avenirsesr.portfolio.user.domain.model.Student;
import fr.avenirsesr.portfolio.user.domain.model.User;
import fr.avenirsesr.portfolio.user.domain.port.input.RessourceService;
import fr.avenirsesr.portfolio.user.domain.port.input.UserService;
import fr.avenirsesr.portfolio.user.domain.port.output.repository.UserRepository;
import jakarta.validation.constraints.NotNull;
import java.io.IOException;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@AllArgsConstructor
@Service
public class UserServiceImpl implements UserService {

  public static final long MAX_SIZE = 10 * 1024 * 1024; // 10 Mo

  private final UserRepository userRepository;
  private final RessourceService ressourceService;

  @Override
  public User getProfile(UUID id) {
    return getUser(id);
  }

  @Override
  public void updateProfile(
      User user,
      @NotNull String firstname,
      @NotNull String lastname,
      String email,
      String bio,
      String profilePictureUrl,
      String coverPictureUrl) {
    user.setFirstName(firstname);
    user.setLastName(lastname);

    if (email != null) {
      user.setEmail(email);
    }

    userRepository.save(user);

    Student student = user.toStudent();

    if (bio != null) {
      student.setBio(bio);
    }

    if (profilePictureUrl != null) {
      student.setProfilePicture(profilePictureUrl);
    }

    if (coverPictureUrl != null) {
      student.setCoverPicture(coverPictureUrl);
    }

    userRepository.save(student);
  }

  @Override
  public String uploadStudentProfilePicture(User user, MultipartFile photoFile) throws IOException {
    checkImageFormat(photoFile);
    return ressourceService.uploadStudentProfilePicture(user, photoFile);
  }

  @Override
  public String uploadStudentCoverPicture(User user, MultipartFile coverFile) throws IOException {
    checkImageFormat(coverFile);
    return ressourceService.uploadStudentCoverPicture(user, coverFile);
  }

  @Override
  public String uploadTeacherProfilePicture(User user, MultipartFile photoFile) throws IOException {
    checkImageFormat(photoFile);
    return ressourceService.uploadTeacherProfilePicture(user, photoFile);
  }

  @Override
  public String uploadTeacherCoverPicture(User user, MultipartFile coverFile) throws IOException {
    checkImageFormat(coverFile);
    return ressourceService.uploadTeacherCoverPicture(user, coverFile);
  }

  private User getUser(UUID id) {
    var user = userRepository.findById(id);

    if (user.isEmpty()) {
      log.error("User {} not found", id);
      throw new UserNotFoundException();
    }
    return user.get();
  }

  private void checkImageFormat(MultipartFile file) {
    String contentType = file.getContentType();
    long contentSize = file.getSize();

    if (!contentType.equals(MediaType.IMAGE_JPEG_VALUE)
        && !contentType.equals(MediaType.IMAGE_PNG_VALUE)) {
      throw new BadImageTypeException();
    }

    if (contentSize > MAX_SIZE) {
      throw new BadImageSizeException();
    }
  }
}
