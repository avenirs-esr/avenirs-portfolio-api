package fr.avenirsesr.portfolio.student.progress.imported.application.adapter.mapper;

import fr.avenirsesr.portfolio.student.progress.imported.application.adapter.dto.SkillLevelProgressOverviewDTO;
import fr.avenirsesr.portfolio.student.progress.imported.domain.data.SkillLevelProgressWithTraceCountData;

public interface SkillLevelProgressOverviewMapper {
  static SkillLevelProgressOverviewDTO fromDomainToDto(SkillLevelProgressWithTraceCountData dto) {
    return new SkillLevelProgressOverviewDTO(
        dto.skillLevelProgress().getSkillLevel().getId(),
        dto.skillLevelProgress().getSkillLevel().getName(),
        dto.traceCount(),
        dto.skillLevelProgress().getAmses().size(),
        dto.skillLevelProgress().getStatus());
  }
}
