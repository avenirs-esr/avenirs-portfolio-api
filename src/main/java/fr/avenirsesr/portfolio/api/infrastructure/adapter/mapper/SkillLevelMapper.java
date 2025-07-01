package fr.avenirsesr.portfolio.api.infrastructure.adapter.mapper;

import fr.avenirsesr.portfolio.api.domain.model.Skill;
import fr.avenirsesr.portfolio.api.domain.model.SkillLevel;
import fr.avenirsesr.portfolio.api.infrastructure.adapter.model.*;
import fr.avenirsesr.portfolio.api.infrastructure.adapter.util.TranslationUtil;
import java.util.List;

public interface SkillLevelMapper {
  static SkillLevelEntity fromDomain(
      SkillLevel skillLevel, SkillEntity skillEntity, List<TraceEntity> tracesEntities) {
    return new SkillLevelEntity(
        skillLevel.getId(),
        skillLevel.getStatus(),
        tracesEntities,
        skillLevel.getAmses().stream().map(AMSMapper::fromDomain).toList(),
        skillEntity,
        skillLevel.getStartDate(),
        skillLevel.getEndDate());
  }

  static SkillLevel toDomain(SkillLevelEntity skillLevelEntity, Skill skill) {
    SkillLevel skillLevel = toDomainWithoutRecursion(skillLevelEntity, skill);
    skillLevel.setAmses(
        skillLevelEntity.getAmses().stream().map(AMSMapper::toDomainWithoutRecursion).toList());
    return skillLevel;
  }

  static SkillLevel toDomainWithoutRecursion(SkillLevelEntity skillLevelEntity, Skill skill) {
    SkillLevelTranslationEntity skillLevelTranslationEntity =
        TranslationUtil.getTranslation(skillLevelEntity.getTranslations());
    return SkillLevel.toDomain(
        skillLevelEntity.getId(),
        skillLevelTranslationEntity.getName(),
        skillLevelTranslationEntity.getDescription(),
        skillLevelEntity.getStatus(),
        skillLevelEntity.getTraces().stream().map(TraceMapper::toDomainWithoutRecursion).toList(),
        List.of(),
        skill,
        skillLevelEntity.getStartDate(),
        skillLevelEntity.getEndDate());
  }

  static SkillLevel toDomainWithoutRecursion(SkillLevelEntity skillLevelEntity) {
    return toDomainWithoutRecursion(skillLevelEntity, null);
  }
}
