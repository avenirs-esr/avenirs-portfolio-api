package fr.avenirsesr.portfolio.student.progress.imported.application.adapter.mapper;

import fr.avenirsesr.portfolio.student.progress.imported.application.adapter.dto.SkillLevelViewDTO;
import fr.avenirsesr.portfolio.student.progress.imported.domain.model.SkillLevelProgress;

public interface SkillLevelViewMapper {
  static SkillLevelViewDTO fromDomainToDto(SkillLevelProgress skillLevelProgress) {
    return new SkillLevelViewDTO(
        skillLevelProgress.getSkillLevel().getId(),
        skillLevelProgress.getSkillLevel().getName(),
        skillLevelProgress.getSkillLevel().getDescription().orElse(null),
        skillLevelProgress.getStatus());
  }
}
