package fr.avenirsesr.portfolio.api.infrastructure.adapter.mapper;

import fr.avenirsesr.portfolio.api.domain.model.AMS;
import fr.avenirsesr.portfolio.api.domain.model.enums.ELanguage;
import fr.avenirsesr.portfolio.api.infrastructure.adapter.model.AMSEntity;
import fr.avenirsesr.portfolio.api.infrastructure.adapter.model.AMSTranslationEntity;
import java.util.stream.Collectors;

public interface AMSMapper {
  static AMSEntity fromDomain(AMS ams) {
    return new AMSEntity(
        ams.getId(),
        UserMapper.fromDomain(ams.getUser()),
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
            .collect(Collectors.toSet()));
  }

  static AMS toDomain(AMSEntity entity) {
    return toDomain(entity, ELanguage.FRENCH);
  }

  static AMS toDomain(AMSEntity entity, ELanguage language) {
    String title =
        entity.getTranslations().stream()
            .filter(t -> t.getLanguage().equals(language))
            .findFirst()
            .map(AMSTranslationEntity::getTitle)
            .orElse(null);
    return AMS.toDomain(
        entity.getId(),
        UserMapper.toDomain(entity.getUser()),
        title,
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
        language);
  }
}
