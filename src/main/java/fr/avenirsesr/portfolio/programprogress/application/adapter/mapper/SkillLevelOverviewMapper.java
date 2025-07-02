package fr.avenirsesr.portfolio.programprogress.application.adapter.mapper;

import fr.avenirsesr.portfolio.programprogress.application.adapter.dto.SkillLevelOverviewDTO;
import fr.avenirsesr.portfolio.programprogress.domain.model.SkillLevel;

public interface SkillLevelOverviewMapper {
  static SkillLevelOverviewDTO fromDomainToDto(SkillLevel skillLevel) {
    return new SkillLevelOverviewDTO(
        skillLevel.getId(), skillLevel.getName(), skillLevel.getStatus());
  }
}
