package fr.avenirsesr.portfolio.shared.domain.service;

import fr.avenirsesr.portfolio.common.data.domain.model.User;
import fr.avenirsesr.portfolio.common.error.domain.exception.UserNotFoundException;
import fr.avenirsesr.portfolio.common.web.infrastructure.context.RequestContext;
import fr.avenirsesr.portfolio.shared.application.adapter.exception.RequestContextNotDefinedException;
import fr.avenirsesr.portfolio.shared.domain.port.input.LoggedInUserService;
import fr.avenirsesr.portfolio.user.domain.model.Staff;
import fr.avenirsesr.portfolio.user.domain.model.Student;
import fr.avenirsesr.portfolio.user.domain.port.input.StaffService;
import fr.avenirsesr.portfolio.user.domain.port.input.StudentService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
public class LoggedInUserServiceImpl implements LoggedInUserService {
  private final StudentService studentService;
  private final StaffService staffService;

  @Override
  public Student getLoggedInStudent() {
    return studentService.getStudentById(getLoggedInUser().getId());
  }

  @Override
  public Staff getLoggedInStaff() {
    return staffService.getStaffById(getLoggedInUser().getId());
  }

  @Override
  public User getLoggedInUser() {
    var context = RequestContext.get();
    if (context == null) {
      throw new RequestContextNotDefinedException();
    }
    return RequestContext.get().userLoggedIn().orElseThrow(UserNotFoundException::new);
  }
}
