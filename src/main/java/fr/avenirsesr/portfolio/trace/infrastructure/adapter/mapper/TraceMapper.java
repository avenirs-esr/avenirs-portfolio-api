package fr.avenirsesr.portfolio.trace.infrastructure.adapter.mapper;

import fr.avenirsesr.portfolio.ams.infrastructure.adapter.mapper.AMSMapper;
import fr.avenirsesr.portfolio.program.infrastructure.adapter.mapper.SkillLevelMapper;
import fr.avenirsesr.portfolio.program.infrastructure.adapter.mapper.SkillMapper;
import fr.avenirsesr.portfolio.trace.domain.model.Trace;
import fr.avenirsesr.portfolio.trace.infrastructure.adapter.model.TraceEntity;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.mapper.UserMapper;
import java.util.List;

public interface TraceMapper {
  static TraceEntity fromDomain(Trace trace) {
    return TraceEntity.of(
        trace.getId(),
        UserMapper.fromDomain(trace.getUser()),
        trace.getTitle(),
        trace.getLanguage(),
        trace.getSkillLevels().stream()
            .map(
                skillLevel ->
                    SkillLevelMapper.fromDomain(
                        skillLevel, SkillMapper.fromDomain(skillLevel.getSkill()), List.of()))
            .toList(),
        trace.getAmses().stream().map(AMSMapper::fromDomain).toList(),
        trace.isGroup(),
        trace.getAiUseJustification().orElse(null),
        trace.getPersonalNote().orElse(null),
        trace.getCreatedAt(),
        trace.getUpdatedAt(),
        trace.getDeletedAt().orElse(null));
  }

  static Trace toDomain(TraceEntity traceEntity) {
    Trace trace = toDomainWithoutRecursion(traceEntity);
    trace.setAmses(
        traceEntity.getAmses().stream().map(AMSMapper::toDomainWithoutRecursion).toList());
    return trace;
  }

  static Trace toDomainWithoutRecursion(TraceEntity traceEntity) {
    return Trace.toDomain(
        traceEntity.getId(),
        UserMapper.toDomain(traceEntity.getUser()),
        traceEntity.getTitle(),
        List.of(),
        List.of(),
        traceEntity.isGroup(),
        traceEntity.getAiUseJustification(),
        traceEntity.getPersonalNote(),
        traceEntity.getCreatedAt(),
        traceEntity.getUpdatedAt(),
        traceEntity.getDeletedAt(),
        traceEntity.getLanguage());
  }
}
