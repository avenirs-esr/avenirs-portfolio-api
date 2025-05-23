package fr.avenirsesr.portfolio.api.infrastructure.adapter.mapper;

import fr.avenirsesr.portfolio.api.domain.model.ProgramProgress;
import fr.avenirsesr.portfolio.api.infrastructure.adapter.model.ProgramProgressEntity;
import java.util.stream.Collectors;

public interface ProgramProgressMapper {
  static ProgramProgressEntity fromDomain(ProgramProgress programProgress) {
    return new ProgramProgressEntity(
        programProgress.getId(),
        ProgramMapper.fromDomain(programProgress.getProgram()),
        UserMapper.fromDomain(programProgress.getStudent().getUser()),
        programProgress.getSkills().stream()
            .map(SkillMapper::fromDomain)
            .collect(Collectors.toSet()));
  }

  static ProgramProgress toDomain(ProgramProgressEntity programProgressEntity) {
    return ProgramProgress.toDomain(
        programProgressEntity.getId(),
        ProgramMapper.toDomain(programProgressEntity.getProgram()),
        StudentMapper.toDomain(
            programProgressEntity.getStudent().getStudent(), programProgressEntity.getStudent()),
        programProgressEntity.getSkills().stream()
            .map(SkillMapper::toDomain)
            .collect(Collectors.toSet()));
  }
}
