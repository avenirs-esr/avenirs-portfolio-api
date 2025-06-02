package fr.avenirsesr.portfolio.api.infrastructure.adapter.mapper;

import fr.avenirsesr.portfolio.api.domain.model.Skill;
import fr.avenirsesr.portfolio.api.domain.model.SkillLevel;
import fr.avenirsesr.portfolio.api.infrastructure.adapter.model.SkillEntity;
import fr.avenirsesr.portfolio.api.infrastructure.adapter.model.SkillLevelEntity;
import fr.avenirsesr.portfolio.api.infrastructure.adapter.model.TrackEntity;
import java.util.List;

public interface SkillLevelMapper {
  static SkillLevelEntity fromDomain(
      SkillLevel skillLevel, SkillEntity skillEntity, List<TrackEntity> tracksEntities) {
    SkillLevelEntity entity =
        new SkillLevelEntity(
            skillLevel.getId(),
            skillLevel.getName(),
            skillLevel.getStatus(),
            tracksEntities,
            skillLevel.getAmses().stream().map(AMSMapper::fromDomain).toList(),
            skillEntity);
    return entity;
  }

  static SkillLevel toDomain(SkillLevelEntity skillLevelEntity, Skill skill) {
    SkillLevel skillLevel =
        SkillLevel.toDomain(
            skillLevelEntity.getId(),
            skillLevelEntity.getName(),
            skillLevelEntity.getStatus(),
            skillLevelEntity.getTracks().stream()
                .map(trackEntity -> TrackMapper.toDomain(trackEntity))
                .toList(),
            skillLevelEntity.getAmses().stream().map(AMSMapper::toDomain).toList());
    skillLevel.setSkill(skill);
    return skillLevel;
  }
}
