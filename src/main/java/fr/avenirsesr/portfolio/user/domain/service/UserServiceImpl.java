package fr.avenirsesr.portfolio.user.domain.service;

import fr.avenirsesr.portfolio.file.domain.model.EUserPhotoType;
import fr.avenirsesr.portfolio.file.domain.port.input.UserResourceService;
import fr.avenirsesr.portfolio.user.domain.exception.UserNotFoundException;
import fr.avenirsesr.portfolio.user.domain.model.Student;
import fr.avenirsesr.portfolio.user.domain.model.Teacher;
import fr.avenirsesr.portfolio.user.domain.model.User;
import fr.avenirsesr.portfolio.user.domain.model.UserPhotos;
import fr.avenirsesr.portfolio.user.domain.model.enums.EUserCategory;
import fr.avenirsesr.portfolio.user.domain.port.input.UserService;
import fr.avenirsesr.portfolio.user.domain.port.output.repository.UserRepository;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
public class UserServiceImpl implements UserService {
  private final UserRepository userRepository;
  private final UserResourceService userResourceService;

  @Override
  public User getUser(UUID id) {
    var user = userRepository.findById(id);

    if (user.isEmpty()) {
      log.error("User {} not found", id);
      throw new UserNotFoundException();
    }

    return user.get();
  }

  @Override
  public UserPhotos getUserPhotos(UUID userId, EUserCategory userCategory) {
    var user = getUser(userId);
    var profile = userResourceService.getUserPhotoUrl(user, userCategory, EUserPhotoType.PROFILE);
    var cover = userResourceService.getUserPhotoUrl(user, userCategory, EUserPhotoType.COVER);

    return new UserPhotos(
        profile.id(), profile.name(), profile.url(), cover.id(), cover.name(), cover.url());
  }

  @Override
  public void updateProfile(
      EUserCategory userCategory,
      User user,
      String firstname,
      String lastname,
      String email,
      String bio) {
    user.setFirstName(firstname);
    user.setLastName(lastname);
    if (firstname == null) {
      throw new IllegalArgumentException("Firstname is null");
    }
    if (lastname == null) {
      throw new IllegalArgumentException("Firstname is null");
    }

    if (email != null) {
      user.setEmail(email);
    }
    userRepository.save(user);

    switch (userCategory) {
      case STUDENT:
        {
          Student student = user.toStudent();
          if (bio != null) {
            student.setBio(bio);
          }
          userRepository.save(student);
        }
      case TEACHER:
        {
          Teacher teacher = user.toTeacher();
          if (bio != null) {
            teacher.setBio(bio);
          }
          userRepository.save(teacher);
        }
    }
  }
}
