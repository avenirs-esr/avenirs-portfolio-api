package fr.avenirsesr.portfolio.student.progress.declared.experience.application.adapter.mapper;

import fr.avenirsesr.portfolio.student.progress.declared.experience.application.adapter.dto.DeclaredExperienceAssociationsDTO;
import fr.avenirsesr.portfolio.student.progress.declared.experience.application.adapter.dto.DeclaredExperienceViewDTO;
import fr.avenirsesr.portfolio.student.progress.declared.experience.domain.data.DeclaredExperienceAssociationsData;
import fr.avenirsesr.portfolio.student.progress.declared.experience.domain.model.DeclaredExperience;
import fr.avenirsesr.portfolio.trace.application.adapter.dto.TraceAssociationDTO;
import fr.avenirsesr.portfolio.trace.application.adapter.mapper.TraceOverviewMapper;

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

  public static DeclaredExperienceAssociationsDTO toAssociationsDTO(
      DeclaredExperienceAssociationsData declaredExperienceAssociations) {
    return new DeclaredExperienceAssociationsDTO(
        declaredExperienceAssociations.traceAssociations().stream()
            .map(
                a ->
                    new TraceAssociationDTO(
                        a.associationId(), TraceOverviewMapper.toDTO(a.trace(), null)))
            .toList());
  }
}
