package fr.avenirsesr.portfolio.programprogress.infrastructure.adapter.mapper;

import fr.avenirsesr.portfolio.programprogress.domain.model.ProgramProgress;
import fr.avenirsesr.portfolio.programprogress.domain.model.Skill;
import fr.avenirsesr.portfolio.programprogress.infrastructure.adapter.model.ProgramProgressEntity;
import fr.avenirsesr.portfolio.programprogress.infrastructure.adapter.model.SkillEntity;
import fr.avenirsesr.portfolio.programprogress.infrastructure.adapter.model.SkillTranslationEntity;
import fr.avenirsesr.portfolio.shared.infrastructure.adapter.utils.TranslationUtil;
import fr.avenirsesr.portfolio.trace.infrastructure.adapter.mapper.TraceMapper;
import java.util.Set;
import java.util.stream.Collectors;

public interface SkillMapper {
  static SkillEntity fromDomain(Skill skill, ProgramProgressEntity programProgressEntity) {
    var skillEntity = SkillEntity.of(skill.getId(), Set.of(), programProgressEntity);
    var levels =
        skill.getSkillLevels().stream()
            .map(
                skillLevel ->
                    SkillLevelMapper.fromDomain(
                        skillLevel,
                        skillEntity,
                        skillLevel.getTraces().stream().map(TraceMapper::fromDomain).toList()))
            .collect(Collectors.toSet());
    skillEntity.setSkillLevels(levels);
    return skillEntity;
  }

  static Skill toDomain(SkillEntity skillEntity, ProgramProgress programProgress) {

    SkillTranslationEntity skillTranslationEntity =
        TranslationUtil.getTranslation(skillEntity.getTranslations());
    var skill = Skill.toDomain(skillEntity.getId(), skillTranslationEntity.getName(), Set.of());

    skill.setSkillLevels(
        skillEntity.getSkillLevels().stream()
            .map(skillLevelEntity -> SkillLevelMapper.toDomain(skillLevelEntity, skill))
            .collect(Collectors.toSet()));

    skill.setProgramProgress(programProgress);

    return skill;
  }

  static Skill toDomainWithoutRecursion(SkillEntity skillEntity, ProgramProgress programProgress) {
    SkillTranslationEntity skillTranslationEntity =
        TranslationUtil.getTranslation(skillEntity.getTranslations());
    var skill = Skill.toDomain(skillEntity.getId(), skillTranslationEntity.getName(), Set.of());
    skill.setProgramProgress(programProgress);
    return skill;
  }

  static Skill toDomainWithoutRecursion(SkillEntity skillEntity) {
    return toDomainWithoutRecursion(skillEntity, null);
  }
}
