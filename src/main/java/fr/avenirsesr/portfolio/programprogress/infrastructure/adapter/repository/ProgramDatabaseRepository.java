package fr.avenirsesr.portfolio.programprogress.infrastructure.adapter.repository;

import fr.avenirsesr.portfolio.programprogress.domain.model.Program;
import fr.avenirsesr.portfolio.programprogress.domain.port.output.repository.ProgramRepository;
import fr.avenirsesr.portfolio.programprogress.infrastructure.adapter.mapper.ProgramMapper;
import fr.avenirsesr.portfolio.programprogress.infrastructure.adapter.model.ProgramEntity;
import fr.avenirsesr.portfolio.shared.infrastructure.adapter.repository.GenericJpaRepositoryAdapter;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class ProgramDatabaseRepository extends GenericJpaRepositoryAdapter<Program, ProgramEntity>
    implements ProgramRepository {
  private final ProgramJpaRepository jpaRepository;

  public ProgramDatabaseRepository(ProgramJpaRepository jpaRepository) {
    super(jpaRepository, jpaRepository, ProgramMapper::fromDomain, ProgramMapper::toDomain);
    this.jpaRepository = jpaRepository;
  }

  public void saveAllEntities(List<ProgramEntity> entities) {
    if (entities != null) {
      jpaRepository.saveAll(entities);
    }
  }
}
