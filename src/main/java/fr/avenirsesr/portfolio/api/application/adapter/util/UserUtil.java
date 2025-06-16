package fr.avenirsesr.portfolio.api.application.adapter.util;

import fr.avenirsesr.portfolio.api.domain.exception.UserIsNotStudentException;
import fr.avenirsesr.portfolio.api.domain.exception.UserNotFoundException;
import fr.avenirsesr.portfolio.api.domain.model.Student;
import fr.avenirsesr.portfolio.api.domain.model.User;
import fr.avenirsesr.portfolio.api.domain.port.output.repository.UserRepository;
import java.security.Principal;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserUtil {

  private final UserRepository userRepository;

  public Student getStudent(Principal principal) {
    User user =
        userRepository
            .findById(UUID.fromString(principal.getName()))
            .orElseThrow(UserNotFoundException::new);

    if (!user.isStudent()) {
      log.error("User {} is not a student", principal.getName());
      throw new UserIsNotStudentException();
    }

    return user.toStudent();
  }
}
