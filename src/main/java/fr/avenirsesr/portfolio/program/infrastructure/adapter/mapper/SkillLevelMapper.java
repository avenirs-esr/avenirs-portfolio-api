package fr.avenirsesr.portfolio.program.infrastructure.adapter.mapper;

import fr.avenirsesr.portfolio.ams.infrastructure.adapter.mapper.AMSMapper;
import fr.avenirsesr.portfolio.program.domain.model.Skill;
import fr.avenirsesr.portfolio.program.domain.model.SkillLevel;
import fr.avenirsesr.portfolio.program.infrastructure.adapter.model.SkillEntity;
import fr.avenirsesr.portfolio.program.infrastructure.adapter.model.SkillLevelEntity;
import fr.avenirsesr.portfolio.program.infrastructure.adapter.model.SkillLevelTranslationEntity;
import fr.avenirsesr.portfolio.shared.infrastructure.adapter.utils.TranslationUtil;
import fr.avenirsesr.portfolio.trace.infrastructure.adapter.mapper.TraceMapper;
import fr.avenirsesr.portfolio.trace.infrastructure.adapter.model.TraceEntity;
import java.util.List;

public interface SkillLevelMapper {
  static SkillLevelEntity fromDomain(SkillLevel skillLevel) {
    return SkillLevelEntity.of(
        skillLevel.getId(),
        skillLevel.getStatus(),
        null,
        skillLevel.getAmses().stream().map(AMSMapper::fromDomain).toList(),
        SkillMapper.fromDomain(skillLevel.getSkill()),
        skillLevel.getStartDate(),
        skillLevel.getEndDate());
  }

  static SkillLevelEntity fromDomain(
      SkillLevel skillLevel, SkillEntity skillEntity, List<TraceEntity> tracesEntities) {
    return SkillLevelEntity.of(
        skillLevel.getId(),
        skillLevel.getStatus(),
        tracesEntities,
        skillLevel.getAmses().stream().map(AMSMapper::fromDomain).toList(),
        skillEntity,
        skillLevel.getStartDate(),
        skillLevel.getEndDate());
  }

  static SkillLevel toDomain(SkillLevelEntity skillLevelEntity, Skill skill) {
    SkillLevel skillLevel = toDomainWithoutRecursion(skillLevelEntity, skill);
    skillLevel.setAmses(
        skillLevelEntity.getAmses().stream().map(AMSMapper::toDomainWithoutRecursion).toList());
    return skillLevel;
  }

  static SkillLevel toDomainWithoutRecursion(SkillLevelEntity skillLevelEntity, Skill skill) {
    SkillLevelTranslationEntity skillLevelTranslationEntity =
        TranslationUtil.getTranslation(skillLevelEntity.getTranslations());
    return SkillLevel.toDomain(
        skillLevelEntity.getId(),
        skillLevelTranslationEntity.getName(),
        skillLevelTranslationEntity.getDescription(),
        skillLevelEntity.getStatus(),
        skillLevelEntity.getTraces().stream().map(TraceMapper::toDomainWithoutRecursion).toList(),
        List.of(),
        skill,
        skillLevelEntity.getStartDate(),
        skillLevelEntity.getEndDate());
  }

  static SkillLevel toDomainWithoutRecursion(SkillLevelEntity skillLevelEntity) {
    return toDomainWithoutRecursion(skillLevelEntity, null);
  }
}
