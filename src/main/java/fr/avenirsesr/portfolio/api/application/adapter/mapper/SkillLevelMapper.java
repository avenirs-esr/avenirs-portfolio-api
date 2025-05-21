package fr.avenirsesr.portfolio.api.application.adapter.mapper;

import fr.avenirsesr.portfolio.api.application.adapter.dto.SkillLevelDTO;
import fr.avenirsesr.portfolio.api.domain.model.SkillLevel;

public interface SkillLevelMapper {
  static SkillLevelDTO fromDomainToDto(SkillLevel skillLevel) {
    return SkillLevelDTO.builder()
        .id(skillLevel.getId())
        .name(skillLevel.getName())
        .status(skillLevel.getStatus().toString())
        .build();
  }
}
