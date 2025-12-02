package fr.avenirsesr.portfolio.program.infrastructure.adapter.repository;

import fr.avenirsesr.portfolio.common.repository.GenericTranslatedJpaRepositoryAdapter;
import fr.avenirsesr.portfolio.program.domain.model.Institution;
import fr.avenirsesr.portfolio.program.domain.port.output.repository.InstitutionRepository;
import fr.avenirsesr.portfolio.program.infrastructure.adapter.mapper.InstitutionMapper;
import fr.avenirsesr.portfolio.program.infrastructure.adapter.model.InstitutionEntity;
import fr.avenirsesr.portfolio.program.infrastructure.adapter.model.InstitutionTranslationEntity;
import org.springframework.stereotype.Component;

@Component
public class InstitutionDatabaseRepository
    extends GenericTranslatedJpaRepositoryAdapter<
        Institution, InstitutionEntity, InstitutionTranslationEntity>
    implements InstitutionRepository {

  public InstitutionDatabaseRepository(
      InstitutionJpaRepository jpaRepository,
      InstitutionTranslationJpaRepository translationJpaRepository) {
    super(
        jpaRepository,
        jpaRepository,
        translationJpaRepository,
        translationJpaRepository,
        InstitutionMapper::fromDomain,
        InstitutionMapper::toDomain,
        InstitutionMapper::toDomain);
  }
}
