package fr.avenirsesr.portfolio.api.infrastructure.adapter.repository;

import fr.avenirsesr.portfolio.api.domain.model.SkillLevel;
import fr.avenirsesr.portfolio.api.domain.port.output.repository.SkillLevelRepository;
import fr.avenirsesr.portfolio.api.infrastructure.adapter.mapper.ProgramProgressMapper;
import fr.avenirsesr.portfolio.api.infrastructure.adapter.mapper.SkillLevelMapper;
import fr.avenirsesr.portfolio.api.infrastructure.adapter.mapper.SkillMapper;
import fr.avenirsesr.portfolio.api.infrastructure.adapter.mapper.TraceMapper;
import fr.avenirsesr.portfolio.api.infrastructure.adapter.model.SkillLevelEntity;
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
                SkillMapper.fromDomain(
                    skillLevel.getSkill(),
                    ProgramProgressMapper.fromDomain(skillLevel.getSkill().getProgramProgress())),
                skillLevel.getTraces().stream().map(TraceMapper::fromDomain).toList()),
        skillLevelEntity ->
            SkillLevelMapper.toDomain(
                skillLevelEntity,
                SkillMapper.toDomain(
                    skillLevelEntity.getSkill(),
                    ProgramProgressMapper.toDomain(
                        skillLevelEntity.getSkill().getProgramProgress()))));
  }
}
