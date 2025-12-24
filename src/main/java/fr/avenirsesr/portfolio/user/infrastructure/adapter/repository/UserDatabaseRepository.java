package fr.avenirsesr.portfolio.user.infrastructure.adapter.repository;

import fr.avenirsesr.portfolio.common.data.domain.model.User;
import fr.avenirsesr.portfolio.common.data.infrastructure.adapter.repository.GenericJpaRepositoryAdapter;
import fr.avenirsesr.portfolio.user.domain.port.output.repository.UserRepository;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.mapper.UserMapper;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.model.UserEntity;
import org.springframework.stereotype.Repository;

@Repository
public class UserDatabaseRepository extends GenericJpaRepositoryAdapter<User, UserEntity>
    implements UserRepository {
  public UserDatabaseRepository(UserJpaRepository jpaRepository) {
    super(jpaRepository, jpaRepository, UserEntity.class, UserMapper.INSTANCE);
  }

  @Override
  public long countAll() {
    return jpaRepository.count();
  }
}
