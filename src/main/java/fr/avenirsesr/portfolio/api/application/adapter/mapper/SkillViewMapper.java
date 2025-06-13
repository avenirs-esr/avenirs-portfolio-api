package fr.avenirsesr.portfolio.api.application.adapter.mapper;

import fr.avenirsesr.portfolio.api.application.adapter.dto.SkillViewDTO;
import fr.avenirsesr.portfolio.api.domain.model.Skill;

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
