package fr.avenirsesr.portfolio.api.infrastructure.adapter.mapper;

import fr.avenirsesr.portfolio.api.domain.model.Program;
import fr.avenirsesr.portfolio.api.infrastructure.adapter.model.ProgramEntity;

public interface ProgramMapper {
  static ProgramEntity fromModelToEntity(String name, String description) {
    return new ProgramEntity();
  }

  static Program fromEntityToModel(ProgramEntity programEntity) {
    return Program.toDomain(
        programEntity.getId(),
        InstitutionMapper.fromEntityToModel(programEntity.getInstitution()),
        programEntity.getName());
  }
}
