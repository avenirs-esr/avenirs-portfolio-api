package fr.avenirsesr.portfolio.api.domain.port.output.repository;

import fr.avenirsesr.portfolio.api.domain.model.User;
import java.util.List;

public interface UserRepository {
  void save(User user);

  void saveAll(List<User> users);
}
