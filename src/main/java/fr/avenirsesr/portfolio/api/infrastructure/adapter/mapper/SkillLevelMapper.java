package fr.avenirsesr.portfolio.api.infrastructure.adapter.mapper;

import fr.avenirsesr.portfolio.api.domain.model.Skill;
import fr.avenirsesr.portfolio.api.domain.model.SkillLevel;
import fr.avenirsesr.portfolio.api.domain.model.enums.ELanguage;
import fr.avenirsesr.portfolio.api.infrastructure.adapter.model.*;
import fr.avenirsesr.portfolio.api.infrastructure.adapter.util.TranslationUtil;
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
            skillEntity,
            skillLevel.getStartDate(),
            skillLevel.getEndDate());
    return entity;
  }

  static SkillLevel toDomain(SkillLevelEntity skillLevelEntity, Skill skill, ELanguage language) {
    ELanguage fallbackLanguage = ELanguage.FRENCH;
    SkillLevelTranslationEntity skillLevelTranslationEntity =
        TranslationUtil.getTranslation(
            skillLevelEntity.getTranslations(), language, fallbackLanguage);
    return SkillLevel.toDomain(
        skillLevelEntity.getId(),
        skillLevelTranslationEntity.getName(),
        skillLevelTranslationEntity.getDescription(),
        skillLevelEntity.getStatus(),
        skillLevelEntity.getTraces().stream().map(TraceMapper::toDomain).toList(),
        skillLevelEntity.getAmses().stream().map(ams -> AMSMapper.toDomain(ams, language)).toList(),
        skill,
        language,
        skillLevelEntity.getStartDate(),
        skillLevelEntity.getEndDate());
  }
}
