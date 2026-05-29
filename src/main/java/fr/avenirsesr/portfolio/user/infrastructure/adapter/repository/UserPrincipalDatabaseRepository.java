package fr.avenirsesr.portfolio.user.infrastructure.adapter.repository;

import fr.avenirsesr.portfolio.common.data.domain.model.User;
import fr.avenirsesr.portfolio.user.domain.port.output.repository.UserPrincipalRepository;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.mapper.UserMapper;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.model.UserPrincipalEntity;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;

@Repository
public class UserPrincipalDatabaseRepository implements UserPrincipalRepository {
  private final UserPrincipalJpaRepository jpaRepository;

  public UserPrincipalDatabaseRepository(UserPrincipalJpaRepository jpaRepository) {
    this.jpaRepository = jpaRepository;
  }

  @Override
  public Optional<User> findByEppn(String eppn) {
    return jpaRepository
        .findByEppn(eppn)
        .map(UserPrincipalEntity::getUser)
        .map(UserMapper.INSTANCE::toDomain);
  }

  @Override
  public void saveOrUpdate(User user, String eppn) {
    var existing = jpaRepository.findByUserId(user.getId());

    var now = Instant.now();

    if (existing.isPresent()) {
      var entity = existing.get();

      entity.setEppn(eppn);
      entity.setUpdatedAt(now);

      jpaRepository.save(entity);
      return;
    }

    jpaRepository.save(
        UserPrincipalEntity.of(
            UUID.randomUUID(), UserMapper.INSTANCE.fromDomain(user), eppn, now, now));
  }

  @Override
  public long countAll() {
    return jpaRepository.count();
  }
}
