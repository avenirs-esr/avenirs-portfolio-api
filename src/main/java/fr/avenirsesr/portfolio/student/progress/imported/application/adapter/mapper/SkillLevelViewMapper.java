package fr.avenirsesr.portfolio.student.progress.imported.application.adapter.mapper;

import fr.avenirsesr.portfolio.student.progress.imported.application.adapter.dto.SkillLevelViewDTO;
import fr.avenirsesr.portfolio.student.progress.imported.domain.data.SkillLevelProgressWithTraceCountData;

public interface SkillLevelViewMapper {
  static SkillLevelViewDTO fromDomainToDto(SkillLevelProgressWithTraceCountData dto) {
    return new SkillLevelViewDTO(
        dto.skillLevelProgress().getSkillLevel().getId(),
        dto.skillLevelProgress().getSkillLevel().getName(),
        dto.skillLevelProgress().getSkillLevel().getDescription().orElse(null),
        dto.traceCount(),
        dto.skillLevelProgress().getAmses().size(),
        dto.skillLevelProgress().getStatus());
  }
}
