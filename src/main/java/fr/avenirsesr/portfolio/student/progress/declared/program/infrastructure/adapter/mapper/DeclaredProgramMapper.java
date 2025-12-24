package fr.avenirsesr.portfolio.student.progress.declared.program.infrastructure.adapter.mapper;

import fr.avenirsesr.portfolio.common.data.infrastructure.adapter.EntityGrapher;
import fr.avenirsesr.portfolio.common.data.infrastructure.adapter.mapper.Mapper;
import fr.avenirsesr.portfolio.student.progress.declared.program.domain.model.DeclaredProgram;
import fr.avenirsesr.portfolio.student.progress.declared.program.infrastructure.adapter.model.DeclaredProgramEntity;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.mapper.StudentMapper;

public class DeclaredProgramMapper implements Mapper<DeclaredProgramEntity, DeclaredProgram> {
  public static final DeclaredProgramMapper INSTANCE = new DeclaredProgramMapper();

  @Override
  public DeclaredProgramEntity fromDomain(DeclaredProgram declaredProgram) {
    return DeclaredProgramEntity.of(
        declaredProgram.getId(),
        StudentMapper.INSTANCE.fromDomain(declaredProgram.getStudent()),
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

  @Override
  public DeclaredProgram toDomain(DeclaredProgramEntity declaredProgram) {
    return DeclaredProgram.of(
        declaredProgram.getId(),
        StudentMapper.INSTANCE.toDomain(declaredProgram.getStudent()),
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

  @Override
  public DeclaredProgram toDomain(DeclaredProgramEntity declaredProgram, EntityGrapher<?> graph) {
    var attributes = graph.attributes();

    return DeclaredProgram.of(
        declaredProgram.getId(),
        attributes.contains("student")
            ? StudentMapper.INSTANCE.toDomain(declaredProgram.getStudent())
            : null,
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
