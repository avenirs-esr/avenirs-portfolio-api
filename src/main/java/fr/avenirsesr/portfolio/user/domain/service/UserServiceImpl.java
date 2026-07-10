package fr.avenirsesr.portfolio.user.domain.service;

import fr.avenirsesr.portfolio.common.data.domain.model.User;
import fr.avenirsesr.portfolio.common.data.domain.model.enums.EUserCategory;
import fr.avenirsesr.portfolio.common.error.domain.exception.UserNotFoundException;
import fr.avenirsesr.portfolio.common.error.domain.model.enums.EErrorCode;
import fr.avenirsesr.portfolio.common.security.domain.exception.UserNotAuthorizedException;
import fr.avenirsesr.portfolio.common.user.domain.exceptions.ExternalUserNotFoundException;
import fr.avenirsesr.portfolio.common.user.domain.model.enums.EUserStatus;
import fr.avenirsesr.portfolio.file.domain.model.File;
import fr.avenirsesr.portfolio.notification.domain.port.output.repository.NotificationRepository;
import fr.avenirsesr.portfolio.shared.domain.port.input.LoggedInUserService;
import fr.avenirsesr.portfolio.user.domain.data.UserQuickLinksData;
import fr.avenirsesr.portfolio.user.domain.port.input.StaffService;
import fr.avenirsesr.portfolio.user.domain.port.input.StudentService;
import fr.avenirsesr.portfolio.user.domain.port.input.UserService;
import fr.avenirsesr.portfolio.user.domain.port.output.client.ExternalUserClient;
import fr.avenirsesr.portfolio.user.domain.port.output.repository.UserPrincipalRepository;
import fr.avenirsesr.portfolio.user.domain.port.output.repository.UserRepository;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
public class UserServiceImpl implements UserService {
  private final UserRepository userRepository;
  private final UserPrincipalRepository userPrincipalRepository;
  private final StaffService staffService;
  private final StudentService studentService;
  private final ExternalUserClient externalUserClient;
  private final LoggedInUserService loggedInUserService;
  private final NotificationRepository notificationRepository;

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
  public User getUserByEppn(String eppn) {
    return userPrincipalRepository
        .findByEppn(eppn)
        .orElseGet(() -> createUserFromExternalUser(eppn));
  }

  @Override
  public void updateProfile(EUserCategory userCategory, String email, String bio) {
    var user = loggedInUserService.getLoggedInUser();
    if (email != null) {
      switch (userCategory) {
        case STUDENT -> user.setEmail(email);
        case STAFF -> throw new UserNotAuthorizedException();
      }
    }
    userRepository.save(user);

    switch (userCategory) {
      case STUDENT -> studentService.updateProfile(user, bio);
      case STAFF -> staffService.updateProfile(user, bio);
      default -> throw new UserNotAuthorizedException();
    }
  }

  @Override
  public void updateNotificationPreferences(boolean notificationEnabled) {
    var user = loggedInUserService.getLoggedInUser();
    user.setNotificationEnabled(notificationEnabled);
    userRepository.save(user);
  }

  @Override
  public User createUser(UUID id, String firstname, String lastname, String email, String eppn) {
    var user = User.create(id, firstname, lastname, email);
    userRepository.save(user);
    userPrincipalRepository.saveOrUpdate(user, eppn);
    return user;
  }

  @Override
  public UserQuickLinksData getQuickLinks(EUserCategory userCategory) {
    User user;
    boolean hasUnseenNotification =
        switch (userCategory) {
          case STUDENT -> {
            var student = loggedInUserService.getLoggedInStudent();
            user = student.getUser();
            yield student.isHasUnseenNotification();
          }
          case STAFF -> {
            var staff = loggedInUserService.getLoggedInStaff();
            user = staff.getUser();
            yield staff.isHasUnseenNotification();
          }
        };
    int unreadNotifications =
        (int) notificationRepository.countUnreadByUserAndCategory(user.getId(), userCategory);
    return new UserQuickLinksData(
        user.getId(),
        user.getFirstName(),
        user.getLastName(),
        hasUnseenNotification,
        unreadNotifications,
        user.isNotificationEnabled());
  }

  @Override
  public File uploadProfilePicture(
      EUserCategory userCategory, String fileName, String mimeType, long size, byte[] content) {
    return switch (userCategory) {
      case STUDENT -> studentService.uploadProfilePicture(fileName, mimeType, size, content);
      case STAFF -> staffService.uploadProfilePicture(fileName, mimeType, size, content);
    };
  }

  @Override
  public void deleteProfilePicture(EUserCategory userCategory) {
    switch (userCategory) {
      case STUDENT -> studentService.deleteProfilePicture();
      case STAFF -> staffService.deleteProfilePicture();
    }
  }

  @Override
  public File uploadCoverPicture(
      EUserCategory userCategory, String fileName, String mimeType, long size, byte[] content) {
    return switch (userCategory) {
      case STUDENT -> studentService.uploadCoverPicture(fileName, mimeType, size, content);
      case STAFF -> staffService.uploadCoverPicture(fileName, mimeType, size, content);
    };
  }

  @Override
  public void deleteCoverPicture(EUserCategory userCategory) {
    switch (userCategory) {
      case STUDENT -> studentService.deleteCoverPicture();
      case STAFF -> staffService.deleteCoverPicture();
    }
  }

  private User createUserFromExternalUser(String eppn) {
    var externalUser =
        externalUserClient.getByEppn(eppn).orElseThrow(ExternalUserNotFoundException::new);

    if (externalUser.status() == EUserStatus.BLOCKED) {
      throw new ExternalUserNotFoundException(EErrorCode.EXTERNAL_USER_BLOCKED);
    }

    if (externalUser.status() == EUserStatus.REMOVED) {
      throw new ExternalUserNotFoundException(EErrorCode.EXTERNAL_USER_REMOVED);
    }

    var user =
        createUser(
            UUID.randomUUID(),
            externalUser.firstName(),
            externalUser.lastName(),
            externalUser.email(),
            externalUser.eppn());

    switch (externalUser.category()) {
      case STUDENT -> studentService.createStudent(user.getId(), externalUser.email(), null);
      case STAFF -> staffService.createStaff(user.getId(), externalUser.email(), null);
    }

    externalUserClient.activateByEppn(externalUser.eppn());

    return user;
  }
}
