package fr.avenirsesr.portfolio.program.infrastructure.adapter.mapper;

import fr.avenirsesr.portfolio.common.data.infrastructure.adapter.mapper.Mapper;
import fr.avenirsesr.portfolio.common.language.infrastructure.adapter.utils.TranslationUtil;
import fr.avenirsesr.portfolio.program.domain.model.Skill;
import fr.avenirsesr.portfolio.program.infrastructure.adapter.model.SkillEntity;
import fr.avenirsesr.portfolio.program.infrastructure.adapter.model.SkillTranslationEntity;

public class SkillMapper implements Mapper<SkillEntity, Skill> {
  public static SkillMapper INSTANCE = new SkillMapper();

  @Override
  public SkillEntity fromDomain(Skill skill) {
    return SkillEntity.of(skill.getId(), skill.getCreatedAt(), skill.getUpdatedAt());
  }

  @Override
  public Skill toDomain(SkillEntity skillEntity) {
    SkillTranslationEntity skillTranslationEntity =
        TranslationUtil.getTranslation(skillEntity.getTranslations());

    return Skill.toDomain(
        skillEntity.getId(),
        skillTranslationEntity.getName(),
        skillEntity.getCreatedAt(),
        skillEntity.getUpdatedAt());
  }
}
