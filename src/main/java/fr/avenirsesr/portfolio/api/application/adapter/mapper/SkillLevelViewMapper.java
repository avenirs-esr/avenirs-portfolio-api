package fr.avenirsesr.portfolio.api.application.adapter.mapper;

import fr.avenirsesr.portfolio.api.application.adapter.dto.SkillLevelViewDTO;
import fr.avenirsesr.portfolio.api.domain.model.SkillLevel;

public interface SkillLevelViewMapper {
  static SkillLevelViewDTO fromDomainToDto(SkillLevel skillLevel) {
    return new SkillLevelViewDTO(
        skillLevel.getId(),
        skillLevel.getName(),
        skillLevel.getDescription(),
        skillLevel.getStatus());
  }
}
