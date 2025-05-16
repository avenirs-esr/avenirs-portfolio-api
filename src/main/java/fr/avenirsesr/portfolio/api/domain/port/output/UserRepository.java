package fr.avenirsesr.portfolio.api.domain.port.output;

import fr.avenirsesr.portfolio.api.domain.model.User;

import java.util.UUID;

public interface UserRepository {

    User findById(UUID id);

    User save(User user);

}