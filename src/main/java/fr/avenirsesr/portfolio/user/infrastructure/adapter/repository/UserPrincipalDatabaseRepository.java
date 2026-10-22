package fr.avenirsesr.portfolio.user.infrastructure.adapter.repository;

import fr.avenirsesr.portfolio.common.data.domain.model.User;
import fr.avenirsesr.portfolio.user.domain.port.output.repository.UserPrincipalRepository;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.mapper.UserMapper;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.model.UserPrincipalEntity;
import java.time.Instant;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
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
  public Map<String, User> findAllByEppns(Collection<String> eppns) {
    if (eppns == null || eppns.isEmpty()) {
      return Map.of();
    }

    Map<String, User> usersByEppn = new LinkedHashMap<>();
    for (UserPrincipalEntity principal : jpaRepository.findAllByEppnIn(eppns)) {
      usersByEppn.put(principal.getEppn(), UserMapper.INSTANCE.toDomain(principal.getUser()));
    }

    return usersByEppn;
  }

  @Override
  public Optional<String> findEppnByUserId(UUID userId) {
    return jpaRepository.findByUserId(userId).map(UserPrincipalEntity::getEppn);
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
