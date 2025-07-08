package fr.avenirsesr.portfolio.student.progress.application.adapter.mapper;

import fr.avenirsesr.portfolio.student.progress.application.adapter.dto.SkillLevelViewDTO;
import fr.avenirsesr.portfolio.student.progress.domain.model.StudentProgress;

public interface SkillLevelViewMapper {
  static SkillLevelViewDTO fromDomainToDto(StudentProgress studentProgress) {
    return new SkillLevelViewDTO(
        studentProgress.getSkillLevel().getId(),
        studentProgress.getSkillLevel().getName(),
        studentProgress.getSkillLevel().getDescription().orElse(null),
        studentProgress.getStatus());
  }
}
