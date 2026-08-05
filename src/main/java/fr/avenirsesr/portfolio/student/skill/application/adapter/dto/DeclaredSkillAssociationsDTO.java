package fr.avenirsesr.portfolio.student.skill.application.adapter.dto;

import fr.avenirsesr.portfolio.student.activity.application.adapter.dto.DeclaredActivityAssociationDTO;
import fr.avenirsesr.portfolio.student.experience.application.adapter.dto.DeclaredExperienceAssociationDTO;
import fr.avenirsesr.portfolio.student.trace.application.adapter.dto.TraceAssociationDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(
    requiredProperties = {
      "traceAssociations",
      "declaredActivityAssociations",
      "declaredExperienceAssociations"
    })
public record DeclaredSkillAssociationsDTO(
    List<TraceAssociationDTO> traceAssociations,
    List<DeclaredActivityAssociationDTO> declaredActivityAssociations,
    List<DeclaredExperienceAssociationDTO> declaredExperienceAssociations) {}
