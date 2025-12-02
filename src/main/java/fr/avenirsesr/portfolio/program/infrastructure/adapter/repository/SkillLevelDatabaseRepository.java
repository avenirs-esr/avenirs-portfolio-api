package fr.avenirsesr.portfolio.program.infrastructure.adapter.repository;

import fr.avenirsesr.portfolio.common.repository.GenericTranslatedJpaRepositoryAdapter;
import fr.avenirsesr.portfolio.program.domain.model.SkillLevel;
import fr.avenirsesr.portfolio.program.domain.port.output.repository.SkillLevelRepository;
import fr.avenirsesr.portfolio.program.infrastructure.adapter.mapper.SkillLevelMapper;
import fr.avenirsesr.portfolio.program.infrastructure.adapter.mapper.SkillMapper;
import fr.avenirsesr.portfolio.program.infrastructure.adapter.model.SkillLevelEntity;
import fr.avenirsesr.portfolio.program.infrastructure.adapter.model.SkillLevelTranslationEntity;
import org.springframework.stereotype.Component;

@Component
public class SkillLevelDatabaseRepository
    extends GenericTranslatedJpaRepositoryAdapter<
        SkillLevel, SkillLevelEntity, SkillLevelTranslationEntity>
    implements SkillLevelRepository {

  public SkillLevelDatabaseRepository(
      SkillLevelJpaRepository jpaRepository,
      SkillLevelTranslationJpaRepository translationJpaRepository) {
    super(
        jpaRepository,
        jpaRepository,
        translationJpaRepository,
        translationJpaRepository,
        skillLevel ->
            SkillLevelMapper.fromDomain(skillLevel, SkillMapper.fromDomain(skillLevel.getSkill())),
        SkillLevelMapper::toDomain,
        SkillLevelMapper::toDomain);
  }
}
