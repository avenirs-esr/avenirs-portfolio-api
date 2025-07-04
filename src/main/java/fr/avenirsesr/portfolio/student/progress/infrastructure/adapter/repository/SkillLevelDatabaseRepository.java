package fr.avenirsesr.portfolio.student.progress.infrastructure.adapter.repository;

import fr.avenirsesr.portfolio.student.progress.domain.model.SkillLevel;
import fr.avenirsesr.portfolio.student.progress.domain.port.output.repository.SkillLevelRepository;
import fr.avenirsesr.portfolio.student.progress.infrastructure.adapter.mapper.TrainingPathMapper;
import fr.avenirsesr.portfolio.student.progress.infrastructure.adapter.mapper.SkillLevelMapper;
import fr.avenirsesr.portfolio.student.progress.infrastructure.adapter.mapper.SkillMapper;
import fr.avenirsesr.portfolio.student.progress.infrastructure.adapter.model.SkillLevelEntity;
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
                    TrainingPathMapper.fromDomain(skillLevel.getSkill().getTrainingPath())),
                skillLevel.getTraces().stream().map(TraceMapper::fromDomain).toList()),
        skillLevelEntity ->
            SkillLevelMapper.toDomain(
                skillLevelEntity,
                SkillMapper.toDomain(
                    skillLevelEntity.getSkill(),
                    TrainingPathMapper.toDomain(
                        skillLevelEntity.getSkill().getProgramProgress()))));
  }
}
