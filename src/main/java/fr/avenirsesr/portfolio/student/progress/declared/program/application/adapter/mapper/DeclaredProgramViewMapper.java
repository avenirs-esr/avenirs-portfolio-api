package fr.avenirsesr.portfolio.student.progress.declared.program.application.adapter.mapper;

import fr.avenirsesr.portfolio.student.progress.declared.program.application.adapter.dto.DeclaredProgramViewDTO;
import fr.avenirsesr.portfolio.student.progress.declared.program.domain.model.DeclaredProgram;

public interface DeclaredProgramViewMapper {

  static DeclaredProgramViewDTO toDTO(DeclaredProgram declaredProgram) {
    return new DeclaredProgramViewDTO(
        declaredProgram.getId(),
        declaredProgram.getStatus(),
        declaredProgram.getTitle(),
        declaredProgram.getDescription(),
        declaredProgram.getOrganization(),
        declaredProgram.getResult(),
        declaredProgram.getSourceOfInformation(),
        declaredProgram.getLink(),
        declaredProgram.getStartDate(),
        declaredProgram.getEndDate(),
        declaredProgram.getCreatedAt(),
        declaredProgram.getUpdatedAt());
  }
}
