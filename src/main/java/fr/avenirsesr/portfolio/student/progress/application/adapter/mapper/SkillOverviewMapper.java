package fr.avenirsesr.portfolio.student.progress.application.adapter.mapper;

import fr.avenirsesr.portfolio.student.progress.application.adapter.dto.SkillOverviewDTO;
import fr.avenirsesr.portfolio.student.progress.domain.model.StudentProgress;

public interface SkillOverviewMapper {
  static SkillOverviewDTO fromDomainToDto(StudentProgress studentProgress) {
    return new SkillOverviewDTO(
        studentProgress.getSkillLevel().getSkill().getId(),
        studentProgress.getSkillLevel().getSkill().getName(),
        studentProgress.getSkillLevel().getTraces().size(),
        studentProgress.getSkillLevel().getAmses().size(),
        SkillLevelOverviewMapper.fromDomainToDto(studentProgress));
  }
}
