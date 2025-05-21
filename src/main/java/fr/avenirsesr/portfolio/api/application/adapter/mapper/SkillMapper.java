package fr.avenirsesr.portfolio.api.application.adapter.mapper;

import fr.avenirsesr.portfolio.api.application.adapter.dto.SkillDTO;
import fr.avenirsesr.portfolio.api.domain.model.Skill;

public interface SkillMapper {
  static SkillDTO fromDomainToDto(Skill skill) {
    return SkillDTO.builder()
        .id(skill.getId())
        .name(skill.getName())
        .trackCount(
            skill.getSkillLevels().stream().mapToInt(level -> level.getTraces().size()).sum())
        .activityCount(
            skill.getSkillLevels().stream().mapToInt(level -> level.getAmses().size()).sum())
        .levels(skill.getSkillLevels().stream().map(SkillLevelMapper::fromDomainToDto).toList())
        .build();
  }
}
