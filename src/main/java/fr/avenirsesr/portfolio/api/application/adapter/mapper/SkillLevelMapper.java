package fr.avenirsesr.portfolio.api.application.adapter.mapper;

import fr.avenirsesr.portfolio.api.application.adapter.dto.SkillLevelDTO;
import fr.avenirsesr.portfolio.api.domain.model.SkillLevel;

public interface SkillLevelMapper {
  static SkillLevelDTO fromDomainToDto(SkillLevel skillLevel) {
    return new SkillLevelDTO(
        skillLevel.getId(), skillLevel.getName(), skillLevel.getStatus().toString());
  }
}
