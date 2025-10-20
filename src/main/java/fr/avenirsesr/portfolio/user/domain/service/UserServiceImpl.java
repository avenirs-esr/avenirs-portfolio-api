package fr.avenirsesr.portfolio.user.domain.service;

import fr.avenirsesr.portfolio.common.data.domain.model.User;
import fr.avenirsesr.portfolio.common.data.domain.model.enums.EUserCategory;
import fr.avenirsesr.portfolio.common.error.domain.exception.UserNotFoundException;
import fr.avenirsesr.portfolio.file.domain.model.EUserPhotoType;
import fr.avenirsesr.portfolio.file.domain.port.input.UserResourceService;
import fr.avenirsesr.portfolio.user.domain.model.*;
import fr.avenirsesr.portfolio.user.domain.port.input.StudentService;
import fr.avenirsesr.portfolio.user.domain.port.input.TeacherService;
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
  private final TeacherService teacherService;
  private final StudentService studentService;

  @Override
  public User getUser(UUID id) {
    return userRepository
        .findById(id)
        .orElseThrow(
            () -> {
              log.error("User {} not found", id);
              return new UserNotFoundException();
            });
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
  public UserProfileOverviewDTO getUserProfileOverviewDTO(UUID userId, EUserCategory userCategory) {
    var user = getUser(userId);
    var bio =
        switch (userCategory) {
          case STUDENT -> studentService.getBio(user);
          case TEACHER -> teacherService.getBio(user);
        };

    return new UserProfileOverviewDTO(
        user.getFirstName(), user.getLastName(), user.getEmail(), bio);
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
      throw new IllegalArgumentException("Lastname is null");
    }

    if (email != null) {
      user.setEmail(email);
    }
    userRepository.save(user);

    switch (userCategory) {
      case STUDENT:
        studentService.updateProfile(user, bio);
      case TEACHER:
        teacherService.updateProfile(user, bio);
    }
  }
}
