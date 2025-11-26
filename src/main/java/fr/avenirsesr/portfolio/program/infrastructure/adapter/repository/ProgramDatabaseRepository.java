package fr.avenirsesr.portfolio.program.infrastructure.adapter.repository;

import fr.avenirsesr.portfolio.common.data.infrastructure.adapter.repository.GenericJpaRepositoryAdapter;
import fr.avenirsesr.portfolio.program.domain.model.Program;
import fr.avenirsesr.portfolio.program.domain.port.output.repository.ProgramRepository;
import fr.avenirsesr.portfolio.program.infrastructure.adapter.mapper.ProgramMapper;
import fr.avenirsesr.portfolio.program.infrastructure.adapter.model.ProgramEntity;
import org.springframework.stereotype.Component;

@Component
public class ProgramDatabaseRepository extends GenericJpaRepositoryAdapter<Program, ProgramEntity>
    implements ProgramRepository {
  private final ProgramJpaRepository jpaRepository;

  public ProgramDatabaseRepository(ProgramJpaRepository jpaRepository) {
    super(jpaRepository, jpaRepository, ProgramMapper::fromDomain, ProgramMapper::toDomain);
    this.jpaRepository = jpaRepository;
  }
}
