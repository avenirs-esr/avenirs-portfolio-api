package fr.avenirsesr.portfolio.program.infrastructure.adapter.repository;

import fr.avenirsesr.portfolio.program.domain.model.SkillLevel;
import fr.avenirsesr.portfolio.program.domain.port.output.SkillLevelRepository;
import fr.avenirsesr.portfolio.program.infrastructure.adapter.mapper.SkillLevelMapper;
import fr.avenirsesr.portfolio.program.infrastructure.adapter.mapper.SkillMapper;
import fr.avenirsesr.portfolio.program.infrastructure.adapter.model.SkillLevelEntity;
import fr.avenirsesr.portfolio.shared.infrastructure.adapter.repository.GenericJpaRepositoryAdapter;
import fr.avenirsesr.portfolio.trace.infrastructure.adapter.mapper.TraceMapper;
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
            SkillLevelMapper.fromDomain(
                skillLevel,
                SkillMapper.fromDomain(skillLevel.getSkill()),
                skillLevel.getTraces().stream().map(TraceMapper::fromDomain).toList()),
        skillLevelEntity ->
            SkillLevelMapper.toDomain(
                skillLevelEntity, SkillMapper.toDomain(skillLevelEntity.getSkill())));
  }
}
