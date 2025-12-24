package fr.avenirsesr.portfolio.program.infrastructure.adapter.mapper;

import fr.avenirsesr.portfolio.common.data.infrastructure.adapter.EntityGrapher;
import fr.avenirsesr.portfolio.common.data.infrastructure.adapter.mapper.Mapper;
import fr.avenirsesr.portfolio.common.language.infrastructure.adapter.utils.TranslationUtil;
import fr.avenirsesr.portfolio.program.domain.model.SkillLevel;
import fr.avenirsesr.portfolio.program.infrastructure.adapter.model.SkillLevelEntity;
import fr.avenirsesr.portfolio.program.infrastructure.adapter.model.SkillLevelTranslationEntity;

public class SkillLevelMapper implements Mapper<SkillLevelEntity, SkillLevel> {
  public static final SkillLevelMapper INSTANCE = new SkillLevelMapper();

  @Override
  public SkillLevelEntity fromDomain(SkillLevel skillLevel) {
    return SkillLevelEntity.of(
        skillLevel.getId(), SkillMapper.INSTANCE.fromDomain(skillLevel.getSkill()));
  }

  @Override
  public SkillLevel toDomain(SkillLevelEntity skillLevelEntity) {
    SkillLevelTranslationEntity skillLevelTranslationEntity =
        TranslationUtil.getTranslation(skillLevelEntity.getTranslations());

    return SkillLevel.toDomain(
        skillLevelEntity.getId(),
        skillLevelTranslationEntity.getName(),
        skillLevelTranslationEntity.getDescription(),
        SkillMapper.INSTANCE.toDomain(skillLevelEntity.getSkill()),
        skillLevelEntity.getCreatedAt(),
        skillLevelEntity.getUpdatedAt());
  }

  @Override
  public SkillLevel toDomain(SkillLevelEntity skillLevelEntity, EntityGrapher<?> graph) {
    SkillLevelTranslationEntity skillLevelTranslationEntity =
        TranslationUtil.getTranslation(skillLevelEntity.getTranslations());
    var attributes = graph.attributes();

    return SkillLevel.toDomain(
        skillLevelEntity.getId(),
        skillLevelTranslationEntity.getName(),
        skillLevelTranslationEntity.getDescription(),
        attributes.contains("skill")
            ? SkillMapper.INSTANCE.toDomain(skillLevelEntity.getSkill())
            : null,
        skillLevelEntity.getCreatedAt(),
        skillLevelEntity.getUpdatedAt());
  }
}
