package fr.avenirsesr.portfolio.student.progress.application.adapter.mapper;

import fr.avenirsesr.portfolio.student.progress.application.adapter.dto.SkillLevelViewDTO;
import fr.avenirsesr.portfolio.student.progress.domain.dto.SkillLevelProgressWithTraceCountDTO;

public interface SkillLevelViewMapper {
  static SkillLevelViewDTO fromDomainToDto(SkillLevelProgressWithTraceCountDTO dto) {
    return new SkillLevelViewDTO(
        dto.skillLevelProgress().getSkillLevel().getId(),
        dto.skillLevelProgress().getSkillLevel().getName(),
        dto.skillLevelProgress().getSkillLevel().getDescription().orElse(null),
        dto.traceCount(),
        dto.skillLevelProgress().getAmses().size(),
        dto.skillLevelProgress().getStatus());
  }
}
