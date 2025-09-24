package fr.avenirsesr.portfolio.student.progress.application.adapter.mapper;

import fr.avenirsesr.portfolio.student.progress.application.adapter.dto.SkillLevelProgressOverviewDTO;
import fr.avenirsesr.portfolio.student.progress.domain.dto.SkillLevelProgressWithTraceCountDTO;

public interface SkillLevelProgressOverviewMapper {
  static SkillLevelProgressOverviewDTO fromDomainToDto(SkillLevelProgressWithTraceCountDTO dto) {
    return new SkillLevelProgressOverviewDTO(
        dto.skillLevelProgress().getSkillLevel().getId(),
        dto.skillLevelProgress().getSkillLevel().getName(),
        dto.traceCount(),
        dto.skillLevelProgress().getAmses().size(),
        dto.skillLevelProgress().getStatus());
  }
}
