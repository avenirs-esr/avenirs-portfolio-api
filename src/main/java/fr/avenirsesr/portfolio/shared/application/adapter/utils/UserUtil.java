package fr.avenirsesr.portfolio.shared.application.adapter.utils;

import fr.avenirsesr.portfolio.user.domain.exception.UserIsNotStudentException;
import fr.avenirsesr.portfolio.user.domain.model.Student;
import fr.avenirsesr.portfolio.user.domain.model.User;
import fr.avenirsesr.portfolio.user.domain.port.input.UserService;
import java.security.Principal;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserUtil {

  private final UserService userService;

  public Student getStudent(Principal principal) {
    User user = userService.getProfile(UUID.fromString(principal.getName()));

    if (!user.isStudent()) {
      log.error("User {} is not a student", principal.getName());
      throw new UserIsNotStudentException();
    }

    return user.toStudent();
  }

  public User getUser(Principal principal) {
    return userService.getProfile(UUID.fromString(principal.getName()));
  }
}
