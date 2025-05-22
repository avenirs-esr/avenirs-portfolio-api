package fr.avenirsesr.portfolio.api.domain.port.output.repository;

import fr.avenirsesr.portfolio.api.domain.model.User;

import java.util.UUID;

public interface UserRepository extends GenericRepositoryPort<User> {

    User findById(UUID id);

}
