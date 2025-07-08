package fr.avenirsesr.portfolio.program.application.adapter.mapper;

import fr.avenirsesr.portfolio.program.application.adapter.dto.TrainingPathDTO;
import fr.avenirsesr.portfolio.program.domain.model.TrainingPath;

public interface TrainingPathMapper {
  static TrainingPathDTO fromDomainToDto(TrainingPath trainingPath) {
    return new TrainingPathDTO(
        trainingPath.getId(),
        trainingPath.getProgram().getName(),
        trainingPath.getProgram().getDurationUnit().orElse(null),
        trainingPath.getProgram().getDurationCount().orElse(null));
  }
}
