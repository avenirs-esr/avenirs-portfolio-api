package fr.avenirsesr.portfolio.api.infrastructure.adapter.mapper;

import fr.avenirsesr.portfolio.api.domain.model.Program;
import fr.avenirsesr.portfolio.api.infrastructure.adapter.model.ProgramEntity;

public interface ProgramMapper {
  static ProgramEntity fromDomain(Program program) {
    return new ProgramEntity(
        program.getId(),
        program.getName(),
        program.getLearningMethod(),
        InstitutionMapper.fromDomain(program.getInstitution()));
  }

  static Program toDomain(ProgramEntity programEntity) {
    return Program.toDomain(
        programEntity.getId(),
        InstitutionMapper.toDomain(programEntity.getInstitution()),
        programEntity.getName(),
        programEntity.getLearningMethod());
  }
}
