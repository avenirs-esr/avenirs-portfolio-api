package fr.avenirsesr.portfolio.shared.domain.service;

import fr.avenirsesr.portfolio.common.data.domain.model.User;
import fr.avenirsesr.portfolio.common.error.domain.exception.UserNotFoundException;
import fr.avenirsesr.portfolio.common.web.infrastructure.context.RequestContext;
import fr.avenirsesr.portfolio.shared.domain.port.input.LoggedInUserService;
import fr.avenirsesr.portfolio.user.domain.exception.UserIsNotStudentException;
import fr.avenirsesr.portfolio.user.domain.model.Student;
import fr.avenirsesr.portfolio.user.domain.port.output.repository.StudentRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
public class LoggedInUserServiceImpl implements LoggedInUserService {
  private final StudentRepository studentRepository;

  @Override
  public Student getLoggedInStudent() {
    return studentRepository
        .findById(getLoggedInUser().getId())
        .orElseThrow(UserIsNotStudentException::new);
  }

  @Override
  public User getLoggedInUser() {
    return RequestContext.get().userLoggedIn().orElseThrow(UserNotFoundException::new);
  }
}
