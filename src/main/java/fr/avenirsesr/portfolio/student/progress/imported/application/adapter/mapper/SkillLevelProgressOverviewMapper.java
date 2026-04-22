package fr.avenirsesr.portfolio.student.progress.imported.application.adapter.mapper;

import fr.avenirsesr.portfolio.student.progress.imported.application.adapter.dto.SkillLevelProgressOverviewDTO;
import fr.avenirsesr.portfolio.student.progress.imported.domain.model.SkillLevelProgress;

public interface SkillLevelProgressOverviewMapper {
  static SkillLevelProgressOverviewDTO fromDomainToDto(SkillLevelProgress skillLevelProgress) {
    return new SkillLevelProgressOverviewDTO(
        skillLevelProgress.getSkillLevel().getId(),
        skillLevelProgress.getSkillLevel().getName(),
        skillLevelProgress.getStatus());
  }
}
