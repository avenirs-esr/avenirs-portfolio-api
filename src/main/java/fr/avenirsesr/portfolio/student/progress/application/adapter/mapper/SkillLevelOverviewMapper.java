package fr.avenirsesr.portfolio.student.progress.application.adapter.mapper;

import fr.avenirsesr.portfolio.student.progress.application.adapter.dto.SkillLevelOverviewDTO;
import fr.avenirsesr.portfolio.student.progress.domain.model.StudentProgress;

public interface SkillLevelOverviewMapper {
  static SkillLevelOverviewDTO fromDomainToDto(StudentProgress studentProgress) {
    return new SkillLevelOverviewDTO(
        studentProgress.getSkillLevel().getId(),
        studentProgress.getSkillLevel().getName(),
        studentProgress.getStatus());
  }
}
