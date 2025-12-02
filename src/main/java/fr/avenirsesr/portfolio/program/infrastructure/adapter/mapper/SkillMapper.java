package fr.avenirsesr.portfolio.program.infrastructure.adapter.mapper;

import fr.avenirsesr.portfolio.common.language.infrastructure.adapter.utils.TranslationUtil;
import fr.avenirsesr.portfolio.program.domain.model.Skill;
import fr.avenirsesr.portfolio.program.infrastructure.adapter.model.SkillEntity;
import fr.avenirsesr.portfolio.program.infrastructure.adapter.model.SkillTranslationEntity;

public interface SkillMapper {
  static SkillEntity fromDomain(Skill skill) {
    return SkillEntity.of(skill.getId());
  }

  static Skill toDomain(SkillEntity skillEntity) {
    SkillTranslationEntity skillTranslationEntity =
        TranslationUtil.getTranslation(skillEntity.getTranslations());

    return Skill.toDomain(
        skillEntity.getId(),
        skillTranslationEntity.getName(),
        skillEntity.getCreatedAt(),
        skillEntity.getUpdatedAt());
  }

  static Skill toDomain(SkillTranslationEntity skillTranslationEntity) {
    SkillEntity skillEntity = skillTranslationEntity.getSkill();
    return Skill.toDomain(
        skillEntity.getId(),
        skillTranslationEntity.getName(),
        skillEntity.getCreatedAt(),
        skillEntity.getUpdatedAt());
  }
}
