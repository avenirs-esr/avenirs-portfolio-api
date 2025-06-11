package fr.avenirsesr.portfolio.api.infrastructure.adapter.mapper;

import fr.avenirsesr.portfolio.api.domain.model.ProgramProgress;
import fr.avenirsesr.portfolio.api.domain.model.Skill;
import fr.avenirsesr.portfolio.api.domain.model.enums.ELanguage;
import fr.avenirsesr.portfolio.api.infrastructure.adapter.model.ProgramProgressEntity;
import fr.avenirsesr.portfolio.api.infrastructure.adapter.model.SkillEntity;
import fr.avenirsesr.portfolio.api.infrastructure.adapter.model.SkillTranslationEntity;
import java.util.Set;
import java.util.stream.Collectors;

public interface SkillMapper {
  static SkillEntity fromDomain(Skill skill, ProgramProgressEntity programProgressEntity) {
    var skillEntity = new SkillEntity(skill.getId(), Set.of(), programProgressEntity);
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

  static Skill toDomain(
      SkillEntity skillEntity, ProgramProgress programProgress, ELanguage language) {
    String name =
        skillEntity.getTranslations().stream()
            .filter(t -> t.getLanguage().equals(language))
            .findFirst()
            .map(SkillTranslationEntity::getName)
            .orElse(null);
    var skill = Skill.toDomain(skillEntity.getId(), name, Set.of(), language);

    skill.setSkillLevels(
        skillEntity.getSkillLevels().stream()
            .map(skillLevelEntity -> SkillLevelMapper.toDomain(skillLevelEntity, skill, language))
            .collect(Collectors.toSet()));

    skill.setProgramProgress(programProgress);

    return skill;
  }
}
