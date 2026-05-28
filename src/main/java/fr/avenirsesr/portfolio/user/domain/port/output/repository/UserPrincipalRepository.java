package fr.avenirsesr.portfolio.user.domain.port.output.repository;

import fr.avenirsesr.portfolio.common.data.domain.model.User;
import java.util.Optional;

public interface UserPrincipalRepository {
  Optional<User> findByEppn(String eppn);

  void saveOrUpdate(User user, String eppn);

  long countAll();
}
