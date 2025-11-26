package fr.avenirsesr.portfolio.shared.domain.port.input;

import fr.avenirsesr.portfolio.common.data.domain.model.User;
import fr.avenirsesr.portfolio.user.domain.model.Student;

public interface LoggedInUserService {
  Student getLoggedInStudent();

  User getLoggedInUser();
}
