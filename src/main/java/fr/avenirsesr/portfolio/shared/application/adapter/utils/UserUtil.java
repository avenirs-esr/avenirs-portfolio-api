package fr.avenirsesr.portfolio.shared.application.adapter.utils;

import fr.avenirsesr.portfolio.user.domain.model.Student;
import fr.avenirsesr.portfolio.user.domain.model.User;
import fr.avenirsesr.portfolio.user.domain.port.input.StudentService;
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
  private final StudentService studentService;

  public Student getStudent(Principal principal) {
    return studentService.getStudentById(UUID.fromString(principal.getName()));
  }

  public User getUser(Principal principal) {
    return userService.getUser(UUID.fromString(principal.getName()));
  }
}
