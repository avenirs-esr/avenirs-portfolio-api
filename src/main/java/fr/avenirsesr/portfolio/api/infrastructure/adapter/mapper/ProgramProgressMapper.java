package fr.avenirsesr.portfolio.api.infrastructure.adapter.mapper;

import fr.avenirsesr.portfolio.api.domain.model.ProgramProgress;
import fr.avenirsesr.portfolio.api.infrastructure.adapter.model.ProgramEntity;
import fr.avenirsesr.portfolio.api.infrastructure.adapter.model.ProgramProgressEntity;
import fr.avenirsesr.portfolio.api.infrastructure.adapter.model.UserEntity;
import java.util.stream.Collectors;

public interface ProgramProgressMapper {
  static ProgramProgressEntity fromModelToEntity(ProgramProgress programProgress) {
    return new ProgramProgressEntity(
        programProgress.getId(),
        ProgramEntity.fromDomain(programProgress.getProgram()),
        UserEntity.fromDomain(programProgress.getStudent().getUser()),
        programProgress.getSkills().stream()
            .map(SkillMapper::fromModelToEntity)
            .collect(Collectors.toSet()));
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
