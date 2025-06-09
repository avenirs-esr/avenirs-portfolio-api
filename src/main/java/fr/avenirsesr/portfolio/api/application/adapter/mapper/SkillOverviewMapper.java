package fr.avenirsesr.portfolio.api.application.adapter.mapper;

import fr.avenirsesr.portfolio.api.application.adapter.dto.SkillOverviewDTO;
import fr.avenirsesr.portfolio.api.domain.model.Skill;

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
