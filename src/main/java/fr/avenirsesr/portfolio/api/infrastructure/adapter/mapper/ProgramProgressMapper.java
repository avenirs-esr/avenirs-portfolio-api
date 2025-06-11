package fr.avenirsesr.portfolio.api.infrastructure.adapter.mapper;

import fr.avenirsesr.portfolio.api.domain.model.ProgramProgress;
import fr.avenirsesr.portfolio.api.domain.model.enums.ELanguage;
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
    return toDomain(programProgressEntity, ELanguage.FRENCH);
  }

  static ProgramProgress toDomain(ProgramProgressEntity programProgressEntity, ELanguage language) {
    var programProgress =
        ProgramProgress.toDomain(
            programProgressEntity.getId(),
            ProgramMapper.toDomain(programProgressEntity.getProgram(), language),
            StudentMapper.toDomain(
                programProgressEntity.getStudent().getStudent(),
                programProgressEntity.getStudent()),
            Set.of());

    programProgress.setSkills(
        programProgressEntity.getSkills().stream()
            .map(skill -> SkillMapper.toDomain(skill, programProgress, language))
            .collect(Collectors.toSet()));

    return programProgress;
  }
}
