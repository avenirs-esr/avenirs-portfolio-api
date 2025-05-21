package fr.avenirsesr.portfolio.api.infrastructure.adapter.repository;

import fr.avenirsesr.portfolio.api.domain.model.User;
import fr.avenirsesr.portfolio.api.domain.port.output.repository.UserRepository;
import fr.avenirsesr.portfolio.api.infrastructure.adapter.mapper.UserMapper;
import fr.avenirsesr.portfolio.api.infrastructure.adapter.model.UserEntity;
import org.springframework.stereotype.Component;

@Component
public class UserDatabaseRepository extends GenericJpaRepositoryAdapter<User, UserEntity>
    implements UserRepository {
  public UserDatabaseRepository(UserJpaRepository jpaRepository) {
    super(jpaRepository, UserMapper::fromDomain, UserMapper::toDomain);
  }
}
