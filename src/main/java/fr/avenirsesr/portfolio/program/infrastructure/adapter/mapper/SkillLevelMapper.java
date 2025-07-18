package fr.avenirsesr.portfolio.program.infrastructure.adapter.mapper;

import fr.avenirsesr.portfolio.program.domain.model.SkillLevel;
import fr.avenirsesr.portfolio.program.infrastructure.adapter.model.SkillEntity;
import fr.avenirsesr.portfolio.program.infrastructure.adapter.model.SkillLevelEntity;
import fr.avenirsesr.portfolio.program.infrastructure.adapter.model.SkillLevelTranslationEntity;
import fr.avenirsesr.portfolio.shared.infrastructure.adapter.utils.TranslationUtil;

public interface SkillLevelMapper {
  static SkillLevelEntity fromDomain(SkillLevel skillLevel) {
    return SkillLevelEntity.of(skillLevel.getId(), SkillMapper.fromDomain(skillLevel.getSkill()));
  }

  static SkillLevelEntity fromDomain(SkillLevel skillLevel, SkillEntity skillEntity) {
    return SkillLevelEntity.of(skillLevel.getId(), skillEntity);
  }

  static SkillLevel toDomain(SkillLevelEntity skillLevelEntity) {
    SkillLevelTranslationEntity skillLevelTranslationEntity =
        TranslationUtil.getTranslation(skillLevelEntity.getTranslations());

    return SkillLevel.toDomain(
        skillLevelEntity.getId(),
        skillLevelTranslationEntity.getName(),
        skillLevelTranslationEntity.getDescription(),
        SkillMapper.toDomain(skillLevelEntity.getSkill()));
  }
}
