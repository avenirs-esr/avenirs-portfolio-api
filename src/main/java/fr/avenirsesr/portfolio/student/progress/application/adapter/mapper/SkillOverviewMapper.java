package fr.avenirsesr.portfolio.student.progress.application.adapter.mapper;

import fr.avenirsesr.portfolio.student.progress.application.adapter.dto.SkillOverviewDTO;
import fr.avenirsesr.portfolio.student.progress.domain.data.SkillLevelProgressWithTraceCountData;

public interface SkillOverviewMapper {
  static SkillOverviewDTO fromDomainToDto(SkillLevelProgressWithTraceCountData dto) {
    return new SkillOverviewDTO(
        dto.skillLevelProgress().getSkillLevel().getSkill().getId(),
        dto.skillLevelProgress().getSkillLevel().getSkill().getName(),
        SkillLevelProgressOverviewMapper.fromDomainToDto(dto));
  }
}
