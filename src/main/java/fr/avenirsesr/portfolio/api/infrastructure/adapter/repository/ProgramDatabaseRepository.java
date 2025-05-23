package fr.avenirsesr.portfolio.api.infrastructure.adapter.repository;

import fr.avenirsesr.portfolio.api.domain.model.Program;
import fr.avenirsesr.portfolio.api.domain.port.output.repository.ProgramRepository;
import fr.avenirsesr.portfolio.api.infrastructure.adapter.mapper.ProgramMapper;
import fr.avenirsesr.portfolio.api.infrastructure.adapter.model.ProgramEntity;
import org.springframework.stereotype.Component;

@Component
public class ProgramDatabaseRepository extends GenericJpaRepositoryAdapter<Program, ProgramEntity>
    implements ProgramRepository {
  public ProgramDatabaseRepository(ProgramJpaRepository jpaRepository) {
    super(jpaRepository, jpaRepository, ProgramMapper::fromDomain, ProgramMapper::toDomain);
  }
}
