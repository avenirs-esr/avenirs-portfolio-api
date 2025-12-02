package fr.avenirsesr.portfolio.program.infrastructure.adapter.repository;

import fr.avenirsesr.portfolio.common.repository.GenericTranslatedJpaRepositoryAdapter;
import fr.avenirsesr.portfolio.program.domain.model.Skill;
import fr.avenirsesr.portfolio.program.domain.port.output.repository.SkillRepository;
import fr.avenirsesr.portfolio.program.infrastructure.adapter.mapper.SkillMapper;
import fr.avenirsesr.portfolio.program.infrastructure.adapter.model.SkillEntity;
import fr.avenirsesr.portfolio.program.infrastructure.adapter.model.SkillTranslationEntity;
import org.springframework.stereotype.Component;

@Component
public class SkillDatabaseRepository
    extends GenericTranslatedJpaRepositoryAdapter<Skill, SkillEntity, SkillTranslationEntity>
    implements SkillRepository {

  public SkillDatabaseRepository(
      SkillJpaRepository jpaRepository, SkillTranslationJpaRepository translationJpaRepository) {
    super(
        jpaRepository,
        jpaRepository,
        translationJpaRepository,
        translationJpaRepository,
        SkillMapper::fromDomain,
        SkillMapper::toDomain,
        SkillMapper::toDomain);
  }
}
