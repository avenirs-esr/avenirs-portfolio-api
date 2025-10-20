package fr.avenirsesr.portfolio.user.domain.port.output.repository;

import fr.avenirsesr.portfolio.common.data.domain.model.User;
import fr.avenirsesr.portfolio.common.data.domain.port.output.repository.GenericRepositoryPort;

public interface UserRepository extends GenericRepositoryPort<User> {
  long countAll();
}
