package fr.avenirsesr.portfolio.program.infrastructure.adapter.repository;

import fr.avenirsesr.portfolio.common.data.infrastructure.adapter.repository.GenericTranslatableJpaRepositoryAdapter;
import fr.avenirsesr.portfolio.program.domain.model.Institution;
import fr.avenirsesr.portfolio.program.domain.port.output.repository.InstitutionRepository;
import fr.avenirsesr.portfolio.program.infrastructure.adapter.mapper.InstitutionMapper;
import fr.avenirsesr.portfolio.program.infrastructure.adapter.model.InstitutionEntity;
import fr.avenirsesr.portfolio.program.infrastructure.adapter.model.InstitutionTranslationEntity;
import org.springframework.stereotype.Component;

@Component
public class InstitutionDatabaseRepository
    extends GenericTranslatableJpaRepositoryAdapter<
        Institution, InstitutionTranslationEntity, InstitutionEntity>
    implements InstitutionRepository {
  private final InstitutionJpaRepository jpaRepository;

  public InstitutionDatabaseRepository(InstitutionJpaRepository jpaRepository) {
    super(
        jpaRepository,
        jpaRepository,
        InstitutionEntity.class,
        InstitutionMapper.INSTANCE,
        InstitutionTranslationEntity::create,
        InstitutionTranslationEntity::update);
    this.jpaRepository = jpaRepository;
  }
}
