package fr.avenirsesr.portfolio.programprogress.application.adapter.mapper;

import fr.avenirsesr.portfolio.programprogress.application.adapter.dto.SkillOverviewDTO;
import fr.avenirsesr.portfolio.programprogress.domain.model.Skill;

public interface SkillOverviewMapper {
  static SkillOverviewDTO fromDomainToDto(Skill skill) {
    return new SkillOverviewDTO(
        skill.getId(),
        skill.getName(),
        skill.getSkillLevels().stream().mapToInt(level -> level.getTraces().size()).sum(),
        skill.getSkillLevels().stream().mapToInt(level -> level.getAmses().size()).sum(),
        skill.getSkillLevels().stream()
            .map(SkillLevelOverviewMapper::fromDomainToDto)
            .toList()
            .getFirst());
  }
}
