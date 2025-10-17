package fr.avenirsesr.portfolio.user.domain.port.output.repository;

import fr.avenirsesr.portfolio.common.data.domain.port.output.repository.GenericRepositoryPort;
import fr.avenirsesr.portfolio.user.domain.model.User;

public interface UserRepository extends GenericRepositoryPort<User> {
  long countAll();
}
