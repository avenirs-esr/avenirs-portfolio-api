package fr.avenirsesr.portfolio.student.progress.declared.program.application.adapter.mapper;

import fr.avenirsesr.portfolio.student.progress.declared.program.application.adapter.dto.DeclaredProgramViewDTO;
import fr.avenirsesr.portfolio.student.progress.declared.program.domain.model.DeclaredProgram;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface DeclaredProgramViewMapper {
  DeclaredProgramViewDTO toDTO(DeclaredProgram declaredProgram);
}
