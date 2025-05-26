package fr.avenirsesr.portfolio.api.infrastructure.adapter.mapper;

import fr.avenirsesr.portfolio.api.domain.model.ProgramProgress;
import fr.avenirsesr.portfolio.api.domain.model.Skill;
import fr.avenirsesr.portfolio.api.infrastructure.adapter.model.ProgramProgressEntity;
import fr.avenirsesr.portfolio.api.infrastructure.adapter.model.SkillEntity;
import java.util.Set;
import java.util.stream.Collectors;

public interface SkillMapper {
  static SkillEntity fromDomain(Skill skill, ProgramProgressEntity programProgressEntity) {
    var skillEntity =
        new SkillEntity(skill.getId(), skill.getName(), Set.of(), programProgressEntity);
    var levels =
        skill.getSkillLevels().stream()
            .map(
                skillLevel ->
                    SkillLevelMapper.fromDomain(
                        skillLevel,
                        skillEntity,
                        skillLevel.getTracks().stream().map(TrackMapper::fromDomain).toList()))
            .collect(Collectors.toSet());
    skillEntity.setSkillLevels(levels);
    return skillEntity;
  }

  static Skill toDomain(SkillEntity skillEntity, ProgramProgress programProgress) {
    var skill = Skill.toDomain(skillEntity.getId(), skillEntity.getName(), Set.of());

    skill.setSkillLevels(
        skillEntity.getSkillLevels().stream()
            .map(skillLevelEntity -> SkillLevelMapper.toDomain(skillLevelEntity, skill))
            .collect(Collectors.toSet()));

    skill.setProgramProgress(programProgress);

    return skill;
  }
}
