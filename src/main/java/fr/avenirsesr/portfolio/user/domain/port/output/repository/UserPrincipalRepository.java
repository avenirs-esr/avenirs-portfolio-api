package fr.avenirsesr.portfolio.user.domain.port.output.repository;

import fr.avenirsesr.portfolio.common.data.domain.model.User;
import java.util.Optional;
import java.util.UUID;

public interface UserPrincipalRepository {
  Optional<User> findByEppn(String eppn);

  Optional<String> findEppnByUserId(UUID userId);

  void saveOrUpdate(User user, String eppn);

  long countAll();
}
