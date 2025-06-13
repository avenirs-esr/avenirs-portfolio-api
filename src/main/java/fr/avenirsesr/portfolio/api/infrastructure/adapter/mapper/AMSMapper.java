package fr.avenirsesr.portfolio.api.infrastructure.adapter.mapper;

import fr.avenirsesr.portfolio.api.domain.model.AMS;
import fr.avenirsesr.portfolio.api.domain.model.enums.ELanguage;
import fr.avenirsesr.portfolio.api.infrastructure.adapter.model.AMSEntity;
import fr.avenirsesr.portfolio.api.infrastructure.adapter.model.AMSTranslationEntity;
import fr.avenirsesr.portfolio.api.infrastructure.adapter.util.TranslationUtil;
import java.util.stream.Collectors;

public interface AMSMapper {
  static AMSEntity fromDomain(AMS ams) {
    return new AMSEntity(
        ams.getId(),
        UserMapper.fromDomain(ams.getUser()),
        ams.getStartDate(),
        ams.getEndDate(),
        ams.getSkillLevels().stream()
            .map(
                skillLevel ->
                    SkillLevelMapper.fromDomain(
                        skillLevel,
                        SkillMapper.fromDomain(
                            skillLevel.getSkill(),
                            ProgramProgressMapper.fromDomain(
                                skillLevel.getSkill().getProgramProgress())),
                        skillLevel.getTraces().stream().map(TraceMapper::fromDomain).toList()))
            .collect(Collectors.toSet()),
        ams.getCohorts().stream().map(CohortMapper::fromDomain).collect(Collectors.toSet()));
  }

  static AMS toDomain(AMSEntity entity) {
    return toDomain(entity, ELanguage.FRENCH);
  }

  static AMS toDomain(AMSEntity entity, ELanguage language) {
    ELanguage fallbackLanguage = ELanguage.FRENCH;
    AMSTranslationEntity translationEntity =
        TranslationUtil.getTranslation(entity.getTranslations(), language, fallbackLanguage);
    return AMS.toDomain(
        entity.getId(),
        UserMapper.toDomain(entity.getUser()),
        translationEntity.getTitle(),
        entity.getStartDate(),
        entity.getEndDate(),
        entity.getSkillLevels().stream()
            .map(
                skillLevelEntity ->
                    SkillLevelMapper.toDomain(
                        skillLevelEntity,
                        SkillMapper.toDomain(
                            skillLevelEntity.getSkill(),
                            ProgramProgressMapper.toDomain(
                                skillLevelEntity.getSkill().getProgramProgress(), ELanguage.FRENCH),
                            ELanguage.FRENCH),
                        ELanguage.FRENCH))
            .toList(),
        entity.getCohorts().stream().map(CohortMapper::toDomain).collect(Collectors.toSet()),
        language);
  }
}
