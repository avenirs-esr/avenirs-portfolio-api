package fr.avenirsesr.portfolio.api.infrastructure.adapter.repository;

import fr.avenirsesr.portfolio.api.domain.model.ProgramProgress;
import fr.avenirsesr.portfolio.api.domain.port.output.repository.ProgramProgressRepository;
import fr.avenirsesr.portfolio.api.infrastructure.adapter.model.ProgramProgressEntity;
import org.springframework.stereotype.Component;

@Component
public class ProgramProgressDatabaseRepository
    extends GenericJpaRepositoryAdapter<ProgramProgress, ProgramProgressEntity>
    implements ProgramProgressRepository {
  public ProgramProgressDatabaseRepository(ProgramProgressJpaRepository jpaRepository) {
    super(jpaRepository, ProgramProgressEntity::fromDomain, ProgramProgressEntity::toDomain);
  }
}
