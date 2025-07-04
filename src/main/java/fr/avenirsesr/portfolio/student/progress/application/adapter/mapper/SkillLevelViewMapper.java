package fr.avenirsesr.portfolio.student.progress.application.adapter.mapper;

import fr.avenirsesr.portfolio.student.progress.application.adapter.dto.SkillLevelViewDTO;
import fr.avenirsesr.portfolio.student.progress.domain.model.SkillLevel;

public interface SkillLevelViewMapper {
  static SkillLevelViewDTO fromDomainToDto(SkillLevel skillLevel) {
    return new SkillLevelViewDTO(
        skillLevel.getId(),
        skillLevel.getName(),
        skillLevel.getDescription().orElse(null),
        skillLevel.getStatus());
  }
}
