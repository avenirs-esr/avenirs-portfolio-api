package fr.avenirsesr.portfolio.api.infrastructure.adapter.mapper;

import fr.avenirsesr.portfolio.api.domain.model.AMS;
import fr.avenirsesr.portfolio.api.domain.model.enums.ELanguage;
import fr.avenirsesr.portfolio.api.infrastructure.adapter.model.AMSEntity;
import fr.avenirsesr.portfolio.api.infrastructure.adapter.model.AMSTranslationEntity;
import fr.avenirsesr.portfolio.api.infrastructure.adapter.util.TranslationUtil;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public interface AMSMapper {
  static AMSEntity fromDomain(AMS ams) {
    AMSEntity entity =
        new AMSEntity(
            ams.getId(),
            UserMapper.fromDomain(ams.getUser()),
            ams.getStatus(),
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
            ams.getCohorts().stream().map(CohortMapper::fromDomain).collect(Collectors.toSet()),
            ams.getTraces().stream().map(TraceMapper::fromDomain).collect(Collectors.toSet()));
    return entity;
  }

  static AMS toDomain(AMSEntity entity) {
    return toDomain(entity, ELanguage.FRENCH);
  }

  static AMS toDomain(AMSEntity entity, ELanguage language) {
    AMS ams = toDomainWithoutRecursion(entity, language);
    ams.setTraces(entity.getTraces().stream().map(TraceMapper::toDomainRecursion).toList());
    ams.setSkillLevels(
        entity.getSkillLevels().stream()
            .map(skillLevel -> SkillLevelMapper.toDomainWithoutRecursion(skillLevel, language))
            .collect(Collectors.toList()));
    ams.setCohorts(
        entity.getCohorts().stream()
            .map(cohort -> CohortMapper.toDomainWithoutRecursion(cohort, language))
            .collect(Collectors.toSet()));
    return ams;
  }

  static AMS toDomainWithoutRecursion(AMSEntity entity) {
    return toDomainWithoutRecursion(entity, ELanguage.FRENCH);
  }

  static AMS toDomainWithoutRecursion(AMSEntity entity, ELanguage language) {
    AMSTranslationEntity translationEntity =
        TranslationUtil.getTranslation(entity.getTranslations());
    return AMS.toDomain(
        entity.getId(),
        UserMapper.toDomain(entity.getUser()),
        translationEntity.getTitle(),
        entity.getStartDate(),
        entity.getEndDate(),
        List.of(),
        List.of(),
        Set.of(),
        language,
        entity.getStatus());
  }
}
