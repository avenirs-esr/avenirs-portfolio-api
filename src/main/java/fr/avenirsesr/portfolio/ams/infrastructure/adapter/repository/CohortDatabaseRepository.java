package fr.avenirsesr.portfolio.ams.infrastructure.adapter.repository;

import fr.avenirsesr.portfolio.ams.domain.model.Cohort;
import fr.avenirsesr.portfolio.ams.domain.port.output.repository.CohortRepository;
import fr.avenirsesr.portfolio.ams.infrastructure.adapter.mapper.CohortMapper;
import fr.avenirsesr.portfolio.ams.infrastructure.adapter.model.CohortEntity;
import fr.avenirsesr.portfolio.shared.infrastructure.adapter.repository.GenericJpaRepositoryAdapter;
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
