package fr.avenirsesr.portfolio.api.infrastructure.adapter.mapper;

import fr.avenirsesr.portfolio.api.domain.model.SkillLevel;
import fr.avenirsesr.portfolio.api.infrastructure.adapter.model.SkillLevelEntity;

public interface SkillLevelMapper {
  static SkillLevelEntity fromDomain(SkillLevel skillLevel) {
    return new SkillLevelEntity(
        skillLevel.getId(),
        skillLevel.getName(),
        skillLevel.getStatus(),
        skillLevel.getTracks().stream().map(TrackMapper::fromDomain).toList(),
        skillLevel.getAmses().stream().map(AMSMapper::fromDomain).toList());
  }

  static SkillLevel toDomain(SkillLevelEntity skillLevelEntity) {
    return SkillLevel.toDomain(
        skillLevelEntity.getId(),
        skillLevelEntity.getName(),
        skillLevelEntity.getStatus(),
        skillLevelEntity.getTracks().stream().map(TrackMapper::toDomain).toList(),
        skillLevelEntity.getAmses().stream().map(AMSMapper::toDomain).toList());
  }
}
