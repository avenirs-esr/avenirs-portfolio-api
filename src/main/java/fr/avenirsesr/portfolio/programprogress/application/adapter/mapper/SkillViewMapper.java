package fr.avenirsesr.portfolio.programprogress.application.adapter.mapper;

import fr.avenirsesr.portfolio.programprogress.application.adapter.dto.SkillViewDTO;
import fr.avenirsesr.portfolio.programprogress.domain.model.Skill;

public interface SkillViewMapper {
  static SkillViewDTO fromDomainToDto(Skill skill, int levelCount) {
    return new SkillViewDTO(
        skill.getId(),
        skill.getName(),
        skill.getSkillLevels().stream().mapToInt(level -> level.getTraces().size()).sum(),
        skill.getSkillLevels().stream().mapToInt(level -> level.getAmses().size()).sum(),
        levelCount,
        skill.getSkillLevels().stream()
            .map(SkillLevelViewMapper::fromDomainToDto)
            .toList()
            .getFirst());
  }
}
