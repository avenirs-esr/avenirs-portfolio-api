package fr.avenirsesr.portfolio.api.application.adapter.mapper;

import fr.avenirsesr.portfolio.api.application.adapter.dto.SkillLevelOverviewDTO;
import fr.avenirsesr.portfolio.api.domain.model.SkillLevel;

public interface SkillLevelOverviewMapper {
  static SkillLevelOverviewDTO fromDomainToDto(SkillLevel skillLevel) {
    return new SkillLevelOverviewDTO(
        skillLevel.getId(), skillLevel.getName(), skillLevel.getStatus().toString());
  }
}
