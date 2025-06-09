package fr.avenirsesr.portfolio.api.infrastructure.adapter.mapper;

import fr.avenirsesr.portfolio.api.domain.model.Trace;
import fr.avenirsesr.portfolio.api.infrastructure.adapter.model.TraceEntity;
import java.util.List;

public interface TraceMapper {
  static TraceEntity fromDomain(Trace trace) {
    return new TraceEntity(
        trace.getId(),
        UserMapper.fromDomain(trace.getUser()),
        trace.getTitle(),
        trace.getSkillLevels().stream()
            .map(
                skillLevel ->
                    SkillLevelMapper.fromDomain(
                        skillLevel,
                        SkillMapper.fromDomain(
                            skillLevel.getSkill(),
                            ProgramProgressMapper.fromDomain(
                                skillLevel.getSkill().getProgramProgress())),
                        List.of()))
            .toList(),
        trace.getAmses().stream().map(AMSMapper::fromDomain).toList(),
        trace.isGroup(),
        trace.getCreatedAt());
  }

  static Trace toDomain(TraceEntity traceEntity) {
    return Trace.toDomain(
        traceEntity.getId(),
        UserMapper.toDomain(traceEntity.getUser()),
        traceEntity.getTitle(),
        traceEntity.getSkillLevels().stream()
            .map(
                skillLevelEntity ->
                    SkillLevelMapper.toDomain(
                        skillLevelEntity,
                        SkillMapper.toDomain(
                            skillLevelEntity.getSkill(),
                            ProgramProgressMapper.toDomain(
                                skillLevelEntity.getSkill().getProgramProgress()))))
            .toList(),
        traceEntity.getAmses().stream().map(AMSMapper::toDomain).toList(),
        traceEntity.isGroup(),
        traceEntity.getCreatedAt());
  }
}
