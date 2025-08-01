package fr.avenirsesr.portfolio.additionalskill.infrastructure.adapter.repository;

import fr.avenirsesr.portfolio.additionalskill.domain.model.Rome4Version;
import fr.avenirsesr.portfolio.additionalskill.domain.port.output.repository.Rome4VersionRepository;
import fr.avenirsesr.portfolio.additionalskill.infrastructure.adapter.mapper.Rome4VersionMapper;
import fr.avenirsesr.portfolio.additionalskill.infrastructure.adapter.model.Rome4VersionEntity;
import fr.avenirsesr.portfolio.shared.infrastructure.adapter.repository.GenericJpaRepositoryAdapter;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class Rome4VersionDatabaseRepository
    extends GenericJpaRepositoryAdapter<Rome4Version, Rome4VersionEntity>
    implements Rome4VersionRepository {
  private final Rome4VersionJpaRepository jpaRepository;

  public Rome4VersionDatabaseRepository(Rome4VersionJpaRepository jpaRepository) {
    super(
        jpaRepository, jpaRepository, Rome4VersionMapper::fromDomain, Rome4VersionMapper::toDomain);
    this.jpaRepository = jpaRepository;
  }

  @Override
  public Optional<Rome4Version> findFirstByOrderByVersionDesc() {
    return jpaRepository.findFirstByOrderByVersionDesc();
  }
}
