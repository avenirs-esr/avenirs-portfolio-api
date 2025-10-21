package fr.avenirsesr.portfolio.student.progress.application.adapter.mapper;

import fr.avenirsesr.portfolio.student.progress.application.adapter.dto.StudentProgressOverviewDTO;
import fr.avenirsesr.portfolio.student.progress.domain.data.SkillLevelProgressWithTraceCountData;
import fr.avenirsesr.portfolio.student.progress.domain.model.StudentProgress;
import java.util.List;

public interface StudentProgressOverviewMapper {
  static StudentProgressOverviewDTO fromDomainToDto(
      StudentProgress studentProgress,
      List<SkillLevelProgressWithTraceCountData> skillLevelToDisplay) {
    return new StudentProgressOverviewDTO(
        studentProgress.getId(),
        studentProgress.getTrainingPath().getProgram().getName(),
        skillLevelToDisplay.stream().map(SkillOverviewMapper::fromDomainToDto).toList());
  }
}
