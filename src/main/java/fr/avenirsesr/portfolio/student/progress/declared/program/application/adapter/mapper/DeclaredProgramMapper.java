package fr.avenirsesr.portfolio.student.progress.declared.program.application.adapter.mapper;

import fr.avenirsesr.portfolio.student.progress.declared.program.application.adapter.dto.DeclaredProgramDTO;
import fr.avenirsesr.portfolio.student.progress.declared.program.domain.model.DeclaredProgram;

public interface DeclaredProgramMapper {

  static DeclaredProgramDTO toDTO(DeclaredProgram declaredProgram) {
    return new DeclaredProgramDTO(
        declaredProgram.getId(),
        declaredProgram.getTitle(),
        declaredProgram.getOrganization(),
        declaredProgram.getStatus());
  }
}
