package fr.avenirsesr.portfolio.api.domain.service;

import fr.avenirsesr.portfolio.api.domain.exception.BadImageSizeException;
import fr.avenirsesr.portfolio.api.domain.exception.BadImageTypeException;
import fr.avenirsesr.portfolio.api.domain.exception.UserNotFoundException;
import fr.avenirsesr.portfolio.api.domain.model.Student;
import fr.avenirsesr.portfolio.api.domain.model.User;
import fr.avenirsesr.portfolio.api.domain.port.input.UserService;
import fr.avenirsesr.portfolio.api.domain.port.output.repository.RessourceRepository;
import fr.avenirsesr.portfolio.api.domain.port.output.repository.UserRepository;
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
  private final RessourceRepository ressourceRepository;

  @Override
  public User getProfile(UUID id) {
    return getUser(id);
  }

  @Override
  public void updateProfile(UUID id, String firstname, String lastname, String email, String bio) {
    User user = getUser(id);

    if (firstname != null) {
      user.setFirstName(firstname);
    }

    if (lastname != null) {
      user.setLastName(lastname);
    }

    if (email != null) {
      user.setEmail(email);
    }

    userRepository.save(user);

    if (bio != null) {
      Student student = user.toStudent();
      student.setBio(bio);
      userRepository.save(student);
    }
  }

  @Override
  public void updateProfilePicture(UUID id, MultipartFile photoFile) throws IOException {
    User user = getUser(id);

    checkImageFormat(photoFile);
    String profilePicturePath = ressourceRepository.storeStudentProfilePicture(id, photoFile);
    Student student = user.toStudent();
    student.setProfilePicture(profilePicturePath);

    userRepository.save(student);
  }

  @Override
  public void updateCoverPicture(UUID id, MultipartFile coverFile) throws IOException {
    User user = getUser(id);

    checkImageFormat(coverFile);
    String coverPicturePath = ressourceRepository.storeStudentCoverPicture(id, coverFile);
    Student student = user.toStudent();
    student.setCoverPicture(coverPicturePath);

    userRepository.save(student);
  }

  private User getUser(UUID id) {
    return userRepository.findById(id).orElseThrow(UserNotFoundException::new);
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
