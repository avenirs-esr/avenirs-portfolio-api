package fr.avenirsesr.portfolio.api.infrastructure.adapter.mapper;

import fr.avenirsesr.portfolio.api.domain.model.SkillLevel;
import fr.avenirsesr.portfolio.api.infrastructure.adapter.model.SkillLevelEntity;
import java.util.ArrayList;

public interface SkillLevelMapper {
  static SkillLevelEntity fromModelToEntity(SkillLevel skillLevel) {
    return new SkillLevelEntity(
        skillLevel.getId(),
        skillLevel.getName(),
        skillLevel.getStatus(),
        skillLevel.getTraces().stream().map(TracesMapper::fromModelToEntity).toList(),
        skillLevel.getAmses().stream().map(AMSMapper::fromModelToEntity).toList());
  }

  static SkillLevel fromEntityToModel(SkillLevelEntity skillLevelEntity) {
    return SkillLevel.toDomain(
        skillLevelEntity.getId(),
        skillLevelEntity.getName(),
        skillLevelEntity.getStatus(),
        new ArrayList<>(),
        new ArrayList<>());
  }
}
