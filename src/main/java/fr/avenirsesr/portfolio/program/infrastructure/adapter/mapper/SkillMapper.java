package fr.avenirsesr.portfolio.program.infrastructure.adapter.mapper;

import fr.avenirsesr.portfolio.program.domain.model.Skill;
import fr.avenirsesr.portfolio.program.infrastructure.adapter.model.SkillEntity;
import fr.avenirsesr.portfolio.program.infrastructure.adapter.model.SkillTranslationEntity;
import fr.avenirsesr.portfolio.shared.infrastructure.adapter.utils.TranslationUtil;

public interface SkillMapper {
  static SkillEntity fromDomain(Skill skill) {
    return SkillEntity.of(skill.getId());
  }

  static Skill toDomain(SkillEntity skillEntity) {
    SkillTranslationEntity skillTranslationEntity =
        TranslationUtil.getTranslation(skillEntity.getTranslations());

    return Skill.toDomain(skillEntity.getId(), skillTranslationEntity.getName());
  }
}
