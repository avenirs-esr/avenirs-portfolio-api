package fr.avenirsesr.portfolio.user.infrastructure.adapter.repository;

import fr.avenirsesr.portfolio.common.data.infrastructure.adapter.repository.GenericJpaRepositoryAdapter;
import fr.avenirsesr.portfolio.user.domain.model.Cgu;
import fr.avenirsesr.portfolio.user.domain.port.output.repository.CguRepository;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.mapper.CguMapper;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.model.CguEntity;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;

@Repository
public class CguDatabaseRepository extends GenericJpaRepositoryAdapter<Cgu, CguEntity>
    implements CguRepository {

  private final CguJpaRepository cguJpaRepository;

  public CguDatabaseRepository(CguJpaRepository cguJpaRepository) {
    super(cguJpaRepository, cguJpaRepository, CguEntity.class, CguMapper.INSTANCE);
    this.cguJpaRepository = cguJpaRepository;
  }

  @Override
  public Optional<Cgu> findLastAcceptedByUser(UUID userId) {
    return cguJpaRepository
        .findFirstByUserIdOrderByAcceptedAtDesc(userId)
        .map(CguMapper.INSTANCE::toDomain);
  }

  @Override
  public Optional<Cgu> findByUserAndVersion(UUID userId, UUID versionId) {
    return cguJpaRepository
        .findByUserIdAndVersionId(userId, versionId)
        .map(CguMapper.INSTANCE::toDomain);
  }
}
