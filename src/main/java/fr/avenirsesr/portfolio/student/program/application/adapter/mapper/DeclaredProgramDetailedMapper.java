package fr.avenirsesr.portfolio.student.program.application.adapter.mapper;

import fr.avenirsesr.portfolio.student.program.application.adapter.dto.DeclaredProgramDetailedDTO;
import fr.avenirsesr.portfolio.student.program.domain.model.DeclaredProgram;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface DeclaredProgramDetailedMapper {
  DeclaredProgramDetailedDTO toDTO(DeclaredProgram declaredProgram);
}
