package fr.avenirsesr.portfolio.program.infrastructure.adapter.repository;

import fr.avenirsesr.portfolio.program.domain.model.SkillLevel;
import fr.avenirsesr.portfolio.program.domain.port.output.SkillLevelRepository;
import fr.avenirsesr.portfolio.program.infrastructure.adapter.mapper.SkillLevelMapper;
import fr.avenirsesr.portfolio.program.infrastructure.adapter.mapper.SkillMapper;
import fr.avenirsesr.portfolio.program.infrastructure.adapter.model.SkillLevelEntity;
import fr.avenirsesr.portfolio.shared.infrastructure.adapter.repository.GenericJpaRepositoryAdapter;
import org.springframework.stereotype.Component;

@Component
public class SkillLevelDatabaseRepository
    extends GenericJpaRepositoryAdapter<SkillLevel, SkillLevelEntity>
    implements SkillLevelRepository {
  public SkillLevelDatabaseRepository(SkillLevelJpaRepository jpaRepository) {
    super(
        jpaRepository,
        jpaRepository,
        skillLevel ->
            SkillLevelMapper.fromDomain(skillLevel, SkillMapper.fromDomain(skillLevel.getSkill())),
        SkillLevelMapper::toDomain);
  }
}
