package fr.avenirsesr.portfolio.api.infrastructure.adapter.mapper;

import fr.avenirsesr.portfolio.api.domain.model.ProgramProgress;
import fr.avenirsesr.portfolio.api.infrastructure.adapter.model.ProgramProgressEntity;
import java.util.stream.Collectors;

public interface ProgramProgressMapper {
  static ProgramProgressEntity fromModelToEntity(ProgramProgress programProgress) {
    return new ProgramProgressEntity();
  }

  static ProgramProgress fromEntityToModel(ProgramProgressEntity programProgressEntity) {
    return ProgramProgress.toDomain(
        programProgressEntity.getId(),
        ProgramMapper.fromEntityToModel(programProgressEntity.getProgram()),
        StudentMapper.fromEntityToModel(programProgressEntity.getStudent()),
        programProgressEntity.getSkills().stream()
            .map(SkillMapper::fromEntityToModel)
            .collect(Collectors.toSet()));
  }
}
