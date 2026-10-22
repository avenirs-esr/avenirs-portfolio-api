package fr.avenirsesr.portfolio.user.domain.port.output.repository;

import fr.avenirsesr.portfolio.common.data.domain.model.User;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public interface UserPrincipalRepository {
  Optional<User> findByEppn(String eppn);

  /** Resolves the users owning the given eppns, keyed by eppn. Unknown eppns are absent. */
  Map<String, User> findAllByEppns(Collection<String> eppns);

  Optional<String> findEppnByUserId(UUID userId);

  void saveOrUpdate(User user, String eppn);

  long countAll();
}
