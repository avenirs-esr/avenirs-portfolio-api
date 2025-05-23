package fr.avenirsesr.portfolio.api.application.adapter.mapper;

import fr.avenirsesr.portfolio.api.application.adapter.dto.SkillDTO;
import fr.avenirsesr.portfolio.api.domain.model.Skill;

public interface SkillMapper {
  static SkillDTO fromDomainToDto(Skill skill) {
    return new SkillDTO(
        skill.getId(),
        skill.getName(),
        skill.getSkillLevels().stream().mapToInt(level -> level.getTracks().size()).sum(),
        skill.getSkillLevels().stream().mapToInt(level -> level.getAmses().size()).sum(),
        skill.getSkillLevels().stream().map(SkillLevelMapper::fromDomainToDto).toList());
  }
}
