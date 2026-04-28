package fr.avenirsesr.portfolio.student.progress.declared.program.application.adapter.mapper;

import fr.avenirsesr.portfolio.student.progress.declared.program.application.adapter.dto.DeclaredProgramDetailedDTO;
import fr.avenirsesr.portfolio.student.progress.declared.program.domain.model.DeclaredProgram;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface DeclaredProgramDetailedMapper {
  DeclaredProgramDetailedDTO toDTO(DeclaredProgram declaredProgram);
}
