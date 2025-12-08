package fr.avenirsesr.portfolio.student.progress.declared.program.infrastructure.adapter.mapper;

import fr.avenirsesr.portfolio.student.progress.declared.program.domain.model.DeclaredProgram;
import fr.avenirsesr.portfolio.student.progress.declared.program.infrastructure.adapter.model.DeclaredProgramEntity;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.mapper.StudentMapper;

public interface DeclaredProgramMapper {
  static DeclaredProgramEntity fromDomain(DeclaredProgram declaredProgram) {
    return DeclaredProgramEntity.of(
        declaredProgram.getId(),
        StudentMapper.fromDomain(declaredProgram.getStudent()),
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

  static DeclaredProgram toDomain(DeclaredProgramEntity declaredProgram) {
    return DeclaredProgram.of(
        declaredProgram.getId(),
        StudentMapper.toDomain(declaredProgram.getStudent()),
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
