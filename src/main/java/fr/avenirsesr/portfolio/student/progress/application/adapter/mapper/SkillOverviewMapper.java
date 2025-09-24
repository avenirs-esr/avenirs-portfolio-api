package fr.avenirsesr.portfolio.student.progress.application.adapter.mapper;

import fr.avenirsesr.portfolio.student.progress.application.adapter.dto.SkillOverviewDTO;
import fr.avenirsesr.portfolio.student.progress.domain.dto.SkillLevelProgressWithTraceCountDTO;

public interface SkillOverviewMapper {
  static SkillOverviewDTO fromDomainToDto(SkillLevelProgressWithTraceCountDTO dto) {
    return new SkillOverviewDTO(
        dto.skillLevelProgress().getSkillLevel().getSkill().getId(),
        dto.skillLevelProgress().getSkillLevel().getSkill().getName(),
        SkillLevelProgressOverviewMapper.fromDomainToDto(dto));
  }
}
