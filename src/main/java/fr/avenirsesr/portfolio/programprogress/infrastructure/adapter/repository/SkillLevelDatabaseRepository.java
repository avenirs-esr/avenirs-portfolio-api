package fr.avenirsesr.portfolio.programprogress.infrastructure.adapter.repository;

import fr.avenirsesr.portfolio.programprogress.domain.model.SkillLevel;
import fr.avenirsesr.portfolio.programprogress.domain.port.output.repository.SkillLevelRepository;
import fr.avenirsesr.portfolio.programprogress.infrastructure.adapter.mapper.ProgramProgressMapper;
import fr.avenirsesr.portfolio.programprogress.infrastructure.adapter.mapper.SkillLevelMapper;
import fr.avenirsesr.portfolio.programprogress.infrastructure.adapter.mapper.SkillMapper;
import fr.avenirsesr.portfolio.programprogress.infrastructure.adapter.model.SkillLevelEntity;
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
