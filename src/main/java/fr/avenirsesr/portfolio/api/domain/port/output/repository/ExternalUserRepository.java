package fr.avenirsesr.portfolio.api.domain.port.output.repository;

import fr.avenirsesr.portfolio.api.domain.model.ExternalUser;
import java.util.List;

public interface ExternalUserRepository {
  void save(ExternalUser externalUser);

  void saveAll(List<ExternalUser> externalUsers);
}
