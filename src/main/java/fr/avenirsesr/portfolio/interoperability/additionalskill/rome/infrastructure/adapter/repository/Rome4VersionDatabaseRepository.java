package fr.avenirsesr.portfolio.interoperability.additionalskill.rome.infrastructure.adapter.repository;

import fr.avenirsesr.portfolio.common.data.infrastructure.adapter.repository.GenericJpaRepositoryAdapter;
import fr.avenirsesr.portfolio.interoperability.additionalskill.rome.domain.model.Rome4Version;
import fr.avenirsesr.portfolio.interoperability.additionalskill.rome.domain.port.output.repository.Rome4VersionRepository;
import fr.avenirsesr.portfolio.interoperability.additionalskill.rome.infrastructure.adapter.mapper.Rome4VersionMapper;
import fr.avenirsesr.portfolio.interoperability.additionalskill.rome.infrastructure.adapter.model.Rome4VersionEntity;
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
