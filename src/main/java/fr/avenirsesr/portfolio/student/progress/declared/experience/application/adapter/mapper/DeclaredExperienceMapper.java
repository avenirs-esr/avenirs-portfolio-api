package fr.avenirsesr.portfolio.student.progress.declared.experience.application.adapter.mapper;

import fr.avenirsesr.portfolio.student.progress.declared.experience.application.adapter.dto.DeclaredExperienceViewDTO;
import fr.avenirsesr.portfolio.student.progress.declared.experience.domain.model.DeclaredExperience;

public class DeclaredExperienceMapper {

  public static DeclaredExperienceViewDTO toDTO(DeclaredExperience experience) {
    return new DeclaredExperienceViewDTO(
        experience.getId(),
        experience.getTitle(),
        experience.getExperienceType(),
        experience.getOrganization(),
        experience.getActivitySector(),
        experience.getLocation(),
        experience.getDescription(),
        experience.getSourceOfInformation(),
        experience.getSummary(),
        experience.getExternalLink(),
        experience.getStartDate(),
        experience.getEndDate(),
        experience.getCreatedAt(),
        experience.getUpdatedAt());
  }
}
