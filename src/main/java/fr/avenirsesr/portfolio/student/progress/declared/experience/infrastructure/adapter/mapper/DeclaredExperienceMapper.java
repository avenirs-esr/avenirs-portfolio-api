package fr.avenirsesr.portfolio.student.progress.declared.experience.infrastructure.adapter.mapper;

import fr.avenirsesr.portfolio.student.progress.declared.experience.domain.model.DeclaredExperience;
import fr.avenirsesr.portfolio.student.progress.declared.experience.infrastructure.adapter.model.DeclaredExperienceEntity;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.mapper.StudentMapper;

public interface DeclaredExperienceMapper {
  static DeclaredExperienceEntity fromDomain(DeclaredExperience declaredExperience) {
    return DeclaredExperienceEntity.of(
        declaredExperience.getId(),
        declaredExperience.getCreatedAt(),
        declaredExperience.getUpdatedAt(),
        StudentMapper.fromDomain(declaredExperience.getStudent()),
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
        declaredExperience.getEndDate());
  }

  static DeclaredExperience toDomain(DeclaredExperienceEntity entity) {
    return DeclaredExperience.toDomain(
        entity.getId(),
        entity.getCreatedAt(),
        entity.getUpdatedAt(),
        StudentMapper.toDomain(entity.getStudent()),
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
        entity.getEndDate());
  }
}
