package fr.avenirsesr.portfolio.student.progress.application.adapter.mapper;

import fr.avenirsesr.portfolio.student.progress.application.adapter.dto.StudentProgressOverviewDTO;
import fr.avenirsesr.portfolio.student.progress.domain.model.StudentProgress;
import fr.avenirsesr.portfolio.student.progress.domain.model.TrainingPath;

import java.util.Set;

public interface StudentProgressOverviewMapper {
  static StudentProgressOverviewDTO fromDomainToDto(
          TrainingPath trainingPath, Set<StudentProgress> studentProgresses) {
    return new StudentProgressOverviewDTO(
        trainingPath.getId(),
        trainingPath.getProgram().getName(),
        studentProgresses.stream().map(SkillOverviewMapper::fromDomainToDto).toList());
  }
}
