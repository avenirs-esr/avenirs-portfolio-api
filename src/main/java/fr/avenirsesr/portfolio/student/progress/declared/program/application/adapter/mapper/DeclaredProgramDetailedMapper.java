package fr.avenirsesr.portfolio.student.progress.declared.program.application.adapter.mapper;

import fr.avenirsesr.portfolio.student.progress.declared.program.application.adapter.dto.DeclaredProgramDetailedDTO;
import fr.avenirsesr.portfolio.student.progress.declared.program.domain.model.DeclaredProgram;

public interface DeclaredProgramDetailedMapper {

  static DeclaredProgramDetailedDTO toDTO(DeclaredProgram declaredProgram) {
    return new DeclaredProgramDetailedDTO(
        declaredProgram.getId(),
        declaredProgram.getStatus(),
        declaredProgram.getTitle(),
        declaredProgram.getDescription(),
        declaredProgram.getOrganization(),
        declaredProgram.getResult(),
        declaredProgram.getSourceOfInformation(),
        declaredProgram.getStartDate(),
        declaredProgram.getEndDate(),
        declaredProgram.getCreatedAt(),
        declaredProgram.getUpdatedAt());
  }
}
