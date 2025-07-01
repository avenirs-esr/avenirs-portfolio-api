package fr.avenirsesr.portfolio.api.infrastructure.adapter.mapper;

import fr.avenirsesr.portfolio.api.domain.model.ProgramProgress;
import fr.avenirsesr.portfolio.api.domain.model.Student;
import fr.avenirsesr.portfolio.api.infrastructure.adapter.dto.ProgramProgressSummaryDTO;
import fr.avenirsesr.portfolio.api.infrastructure.adapter.model.ProgramProgressEntity;
import java.util.Set;
import java.util.stream.Collectors;

public interface ProgramProgressMapper {
  static ProgramProgressEntity fromDomain(ProgramProgress programProgress) {
    var entity =
        new ProgramProgressEntity(
            programProgress.getId(),
            ProgramMapper.fromDomain(programProgress.getProgram()),
            UserMapper.fromDomain(programProgress.getStudent().getUser()),
            Set.of());

    entity.setSkills(
        programProgress.getSkills().stream()
            .map(skill -> SkillMapper.fromDomain(skill, entity))
            .collect(Collectors.toSet()));

    return entity;
  }

  static ProgramProgress toDomain(ProgramProgressEntity programProgressEntity) {
    var programProgress =
        ProgramProgress.toDomain(
            programProgressEntity.getId(),
            ProgramMapper.toDomain(programProgressEntity.getProgram()),
            StudentMapper.toDomain(
                programProgressEntity.getStudent().getStudent(),
                programProgressEntity.getStudent()),
            Set.of());

    programProgress.setSkills(
        programProgressEntity.getSkills().stream()
            .map(skill -> SkillMapper.toDomain(skill, programProgress))
            .collect(Collectors.toSet()));

    return programProgress;
  }

  static ProgramProgress toDomainWithoutRecursion(
      ProgramProgressSummaryDTO programProgressSummaryDTO, Student student) {
    return ProgramProgress.toDomain(
        programProgressSummaryDTO.id(),
        ProgramMapper.toDomain(programProgressSummaryDTO.program()),
        student,
        Set.of());
  }
}
