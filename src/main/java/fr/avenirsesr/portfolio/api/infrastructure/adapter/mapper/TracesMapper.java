package fr.avenirsesr.portfolio.api.infrastructure.adapter.mapper;

import fr.avenirsesr.portfolio.api.domain.model.Trace;
import fr.avenirsesr.portfolio.api.infrastructure.adapter.model.TraceEntity;

public interface TracesMapper {
  static TraceEntity fromModelToEntity(Trace trace) {
    return new TraceEntity();
  }

  static Trace fromEntityToModel(TraceEntity traceEntity) {
    return Trace.toDomain(
        traceEntity.getId(),
        UserMapper.fromEntityToModel(traceEntity.getUser()),
        traceEntity.getSkillLevels().stream().map(SkillLevelMapper::fromEntityToModel).toList(),
        traceEntity.getAmses().stream().map(AMSMapper::fromEntityToModel).toList());
  }
}
