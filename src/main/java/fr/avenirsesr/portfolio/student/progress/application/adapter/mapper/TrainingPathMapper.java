package fr.avenirsesr.portfolio.student.progress.application.adapter.mapper;

import fr.avenirsesr.portfolio.student.progress.application.adapter.dto.TrainingPathDTO;
import fr.avenirsesr.portfolio.student.progress.domain.model.TrainingPath;

public interface TrainingPathMapper {
  static TrainingPathDTO fromDomainToDto(TrainingPath trainingPath) {
    return new TrainingPathDTO(
        trainingPath.getId(),
        trainingPath.getProgram().getName(),
        trainingPath.getProgram().getDurationUnit(),
        trainingPath.getProgram().getDurationCount());
  }
}
