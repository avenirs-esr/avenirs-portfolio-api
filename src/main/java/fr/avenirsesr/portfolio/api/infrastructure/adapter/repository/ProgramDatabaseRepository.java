package fr.avenirsesr.portfolio.api.infrastructure.adapter.repository;

import fr.avenirsesr.portfolio.api.domain.model.Program;
import fr.avenirsesr.portfolio.api.domain.port.output.repository.ProgramRepository;
import fr.avenirsesr.portfolio.api.infrastructure.adapter.mapper.ProgramMapper;
import fr.avenirsesr.portfolio.api.infrastructure.adapter.model.ProgramEntity;
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
