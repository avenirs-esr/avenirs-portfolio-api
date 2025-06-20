package fr.avenirsesr.portfolio.api.infrastructure.adapter.repository;

import fr.avenirsesr.portfolio.api.domain.model.Cohort;
import fr.avenirsesr.portfolio.api.domain.port.output.repository.CohortRepository;
import fr.avenirsesr.portfolio.api.infrastructure.adapter.mapper.CohortMapper;
import fr.avenirsesr.portfolio.api.infrastructure.adapter.model.CohortEntity;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class CohortDatabaseRepository extends GenericJpaRepositoryAdapter<Cohort, CohortEntity>
    implements CohortRepository {
  private final CohortJpaRepository jpaRepository;

  public CohortDatabaseRepository(CohortJpaRepository repository) {
    super(repository, repository, CohortMapper::fromDomain, CohortMapper::toDomain);
    this.jpaRepository = repository;
  }

  public void saveAllEntities(List<CohortEntity> entities) {
    if (entities != null) {
      jpaRepository.saveAll(entities);
    }
  }
}
