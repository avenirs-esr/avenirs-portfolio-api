package fr.avenirsesr.portfolio.api.infrastructure.adapter.repository;

import fr.avenirsesr.portfolio.api.domain.model.Institution;
import fr.avenirsesr.portfolio.api.domain.port.output.repository.InstitutionRepository;
import fr.avenirsesr.portfolio.api.infrastructure.adapter.mapper.InstitutionMapper;
import fr.avenirsesr.portfolio.api.infrastructure.adapter.model.InstitutionEntity;
import org.springframework.stereotype.Component;

@Component
public class InstitutionDatabaseRepository
    extends GenericJpaRepositoryAdapter<Institution, InstitutionEntity>
    implements InstitutionRepository {
  public InstitutionDatabaseRepository(InstitutionJpaRepository jpaRepository) {
    super(jpaRepository, jpaRepository, InstitutionMapper::fromDomain, InstitutionMapper::toDomain);
  }
}
