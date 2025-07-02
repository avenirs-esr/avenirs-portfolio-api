package fr.avenirsesr.portfolio.programprogress.infrastructure.adapter.repository;

import fr.avenirsesr.portfolio.programprogress.domain.model.Institution;
import fr.avenirsesr.portfolio.programprogress.domain.port.output.repository.InstitutionRepository;
import fr.avenirsesr.portfolio.programprogress.infrastructure.adapter.mapper.InstitutionMapper;
import fr.avenirsesr.portfolio.programprogress.infrastructure.adapter.model.InstitutionEntity;
import fr.avenirsesr.portfolio.shared.infrastructure.adapter.repository.GenericJpaRepositoryAdapter;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class InstitutionDatabaseRepository
    extends GenericJpaRepositoryAdapter<Institution, InstitutionEntity>
    implements InstitutionRepository {
  private final InstitutionJpaRepository jpaRepository;

  public InstitutionDatabaseRepository(InstitutionJpaRepository jpaRepository) {
    super(jpaRepository, jpaRepository, InstitutionMapper::fromDomain, InstitutionMapper::toDomain);
    this.jpaRepository = jpaRepository;
  }

  public void saveAllEntities(List<InstitutionEntity> entities) {
    if (entities != null && !entities.isEmpty()) {
      jpaRepository.saveAll(entities);
    }
  }
}
