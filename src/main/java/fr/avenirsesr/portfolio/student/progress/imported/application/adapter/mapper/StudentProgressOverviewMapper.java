package fr.avenirsesr.portfolio.student.progress.imported.application.adapter.mapper;

import fr.avenirsesr.portfolio.student.progress.imported.application.adapter.dto.StudentProgressOverviewDTO;
import fr.avenirsesr.portfolio.student.progress.imported.domain.model.SkillLevelProgress;
import fr.avenirsesr.portfolio.student.progress.imported.domain.model.StudentProgress;
import java.util.List;

public interface StudentProgressOverviewMapper {
  static StudentProgressOverviewDTO fromDomainToDto(
      StudentProgress studentProgress, List<SkillLevelProgress> skillLevelToDisplay) {
    return new StudentProgressOverviewDTO(
        studentProgress.getId(),
        studentProgress.getTrainingPath().getProgram().getName(),
        skillLevelToDisplay.stream().map(SkillOverviewMapper::fromDomainToDto).toList());
  }
}
