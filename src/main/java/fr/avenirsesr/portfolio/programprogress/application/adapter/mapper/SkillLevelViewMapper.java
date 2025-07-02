package fr.avenirsesr.portfolio.programprogress.application.adapter.mapper;

import fr.avenirsesr.portfolio.programprogress.application.adapter.dto.SkillLevelViewDTO;
import fr.avenirsesr.portfolio.programprogress.domain.model.SkillLevel;

public interface SkillLevelViewMapper {
  static SkillLevelViewDTO fromDomainToDto(SkillLevel skillLevel) {
    return new SkillLevelViewDTO(
        skillLevel.getId(),
        skillLevel.getName(),
        skillLevel.getDescription(),
        skillLevel.getStatus());
  }
}
