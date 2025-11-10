package fr.avenirsesr.portfolio.additionalskill.infrastructure.adapter.repository;

import fr.avenirsesr.portfolio.additionalskill.domain.model.AdditionalSkillCategory;
import fr.avenirsesr.portfolio.additionalskill.domain.port.output.repository.AdditionalSkillCategoryRepository;
import fr.avenirsesr.portfolio.additionalskill.infrastructure.adapter.mapper.AdditionalSkillCategoryMapper;
import fr.avenirsesr.portfolio.additionalskill.infrastructure.adapter.model.AdditionalSkillCategoryEntity;
import fr.avenirsesr.portfolio.common.data.infrastructure.adapter.repository.GenericJpaRepositoryAdapter;
import org.springframework.stereotype.Component;

@Component
public class AdditionalSkillCategoryDatabaseRepository
    extends GenericJpaRepositoryAdapter<AdditionalSkillCategory, AdditionalSkillCategoryEntity>
    implements AdditionalSkillCategoryRepository {
  private final AdditionalSkillCategoryJpaRepository jpaRepository;

  public AdditionalSkillCategoryDatabaseRepository(
      AdditionalSkillCategoryJpaRepository jpaRepository) {
    super(
        jpaRepository,
        jpaRepository,
        AdditionalSkillCategoryMapper::fromDomain,
        AdditionalSkillCategoryMapper::toDomain);
    this.jpaRepository = jpaRepository;
  }
}
