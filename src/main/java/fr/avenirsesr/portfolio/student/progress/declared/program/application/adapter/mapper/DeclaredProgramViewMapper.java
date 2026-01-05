package fr.avenirsesr.portfolio.student.progress.declared.program.application.adapter.mapper;

import fr.avenirsesr.portfolio.student.progress.declared.program.application.adapter.dto.DeclaredProgramViewDTO;
import fr.avenirsesr.portfolio.student.progress.declared.program.domain.model.DeclaredProgram;

public interface DeclaredProgramViewMapper {

  static DeclaredProgramViewDTO toDTO(DeclaredProgram declaredProgram) {
    return new DeclaredProgramViewDTO(
        declaredProgram.getId(),
        declaredProgram.getStatus(),
        declaredProgram.getTitle(),
        declaredProgram.getOrganization(),
        declaredProgram.getResult());
  }
}
