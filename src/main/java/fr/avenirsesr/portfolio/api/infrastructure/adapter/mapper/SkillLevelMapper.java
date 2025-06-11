package fr.avenirsesr.portfolio.api.infrastructure.adapter.mapper;

import fr.avenirsesr.portfolio.api.domain.model.Skill;
import fr.avenirsesr.portfolio.api.domain.model.SkillLevel;
import fr.avenirsesr.portfolio.api.domain.model.enums.ELanguage;
import fr.avenirsesr.portfolio.api.infrastructure.adapter.model.*;
import java.util.List;

public interface SkillLevelMapper {
  static SkillLevelEntity fromDomain(
      SkillLevel skillLevel, SkillEntity skillEntity, List<TraceEntity> tracesEntities) {
    SkillLevelEntity entity =
        new SkillLevelEntity(
            skillLevel.getId(),
            skillLevel.getStatus(),
            tracesEntities,
            skillLevel.getAmses().stream().map(AMSMapper::fromDomain).toList(),
            skillEntity);
    return entity;
  }

  static SkillLevel toDomain(SkillLevelEntity skillLevelEntity, Skill skill, ELanguage language) {
    SkillLevelTranslationEntity skillLevelTranslationEntity =
        skillLevelEntity.getTranslations().stream()
            .filter(t -> t.getLanguage().equals(language))
            .findFirst()
            .orElseThrow(
                () ->
                    new IllegalArgumentException("No translation found for language: " + language));
    return SkillLevel.toDomain(
        skillLevelEntity.getId(),
        skillLevelTranslationEntity.getName(),
        skillLevelTranslationEntity.getDescription(),
        skillLevelEntity.getStatus(),
        skillLevelEntity.getTraces().stream().map(TraceMapper::toDomain).toList(),
        skillLevelEntity.getAmses().stream().map(AMSMapper::toDomain).toList(),
        skill,
        language);
  }
}
