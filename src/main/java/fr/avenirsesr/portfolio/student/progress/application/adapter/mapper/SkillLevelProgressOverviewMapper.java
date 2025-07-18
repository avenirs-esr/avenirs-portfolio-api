package fr.avenirsesr.portfolio.student.progress.application.adapter.mapper;

import fr.avenirsesr.portfolio.student.progress.application.adapter.dto.SkillLevelProgressOverviewDTO;
import fr.avenirsesr.portfolio.student.progress.domain.model.SkillLevelProgress;

public interface SkillLevelProgressOverviewMapper {
  static SkillLevelProgressOverviewDTO fromDomainToDto(SkillLevelProgress skillLevelProgress) {
    return new SkillLevelProgressOverviewDTO(
        skillLevelProgress.getSkillLevel().getId(),
        skillLevelProgress.getSkillLevel().getName(),
        skillLevelProgress.getStatus());
  }
}
