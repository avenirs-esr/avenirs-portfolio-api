package fr.avenirsesr.portfolio.student.experience.infrastructure.adapter.mapper;

import fr.avenirsesr.portfolio.common.data.infrastructure.adapter.EntityGrapher;
import fr.avenirsesr.portfolio.common.data.infrastructure.adapter.mapper.Mapper;
import fr.avenirsesr.portfolio.student.experience.domain.model.DeclaredExperience;
import fr.avenirsesr.portfolio.student.experience.infrastructure.adapter.model.DeclaredExperienceEntity;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.mapper.StudentMapper;

public class DeclaredExperienceMapper
    implements Mapper<DeclaredExperienceEntity, DeclaredExperience> {
  public static final DeclaredExperienceMapper INSTANCE = new DeclaredExperienceMapper();

  @Override
  public DeclaredExperienceEntity fromDomain(DeclaredExperience declaredExperience) {
    return DeclaredExperienceEntity.of(
        declaredExperience.getId(),
        declaredExperience.getCreatedAt(),
        declaredExperience.getUpdatedAt(),
        StudentMapper.INSTANCE.fromDomain(declaredExperience.getStudent()),
        declaredExperience.getTitle(),
        declaredExperience.getExperienceType(),
        declaredExperience.getOrganization(),
        declaredExperience.getActivitySector(),
        declaredExperience.getLocation(),
        declaredExperience.getDescription(),
        declaredExperience.getSourceOfInformation(),
        declaredExperience.getSummary(),
        declaredExperience.getExternalLink(),
        declaredExperience.getStartDate(),
        declaredExperience.getEndDate(),
        declaredExperience.isValorized());
  }

  @Override
  public DeclaredExperience toDomain(DeclaredExperienceEntity entity) {
    return DeclaredExperience.toDomain(
        entity.getId(),
        entity.getCreatedAt(),
        entity.getUpdatedAt(),
        StudentMapper.INSTANCE.toDomain(entity.getStudent()),
        entity.getTitle(),
        entity.getExperienceType(),
        entity.getOrganization(),
        entity.getActivitySector(),
        entity.getLocation(),
        entity.getDescription(),
        entity.getSourceOfInformation(),
        entity.getSummary(),
        entity.getExternalLink(),
        entity.getStartDate(),
        entity.getEndDate(),
        entity.isValorized());
  }

  @Override
  public DeclaredExperience toDomain(DeclaredExperienceEntity entity, EntityGrapher<?> graph) {
    var attributes = graph.attributes();

    return DeclaredExperience.toDomain(
        entity.getId(),
        entity.getCreatedAt(),
        entity.getUpdatedAt(),
        attributes.contains("student")
            ? StudentMapper.INSTANCE.toDomain(entity.getStudent())
            : null,
        entity.getTitle(),
        entity.getExperienceType(),
        entity.getOrganization(),
        entity.getActivitySector(),
        entity.getLocation(),
        entity.getDescription(),
        entity.getSourceOfInformation(),
        entity.getSummary(),
        entity.getExternalLink(),
        entity.getStartDate(),
        entity.getEndDate(),
        entity.isValorized());
  }
}
