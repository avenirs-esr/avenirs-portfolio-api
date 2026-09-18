package fr.avenirsesr.portfolio.student.association.application.adapter.dto;

import fr.avenirsesr.portfolio.student.activity.application.adapter.dto.DeclaredActivityAssociationDTO;
import fr.avenirsesr.portfolio.student.experience.application.adapter.dto.DeclaredExperienceAssociationDTO;
import fr.avenirsesr.portfolio.student.program.application.adapter.dto.DeclaredProgramAssociationDTO;
import fr.avenirsesr.portfolio.student.skill.application.adapter.dto.DeclaredSkillAssociationDTO;
import fr.avenirsesr.portfolio.student.trace.application.adapter.dto.TraceAssociationDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(
    requiredProperties = {
      "traceAssociations",
      "declaredActivityAssociations",
      "declaredSkillAssociations",
      "declaredExperienceAssociations",
      "declaredProgramAssociations"
    })
public record AssociationsDTO(
    List<TraceAssociationDTO> traceAssociations,
    List<DeclaredActivityAssociationDTO> declaredActivityAssociations,
    List<DeclaredSkillAssociationDTO> declaredSkillAssociations,
    List<DeclaredExperienceAssociationDTO> declaredExperienceAssociations,
    List<DeclaredProgramAssociationDTO> declaredProgramAssociations) {}
