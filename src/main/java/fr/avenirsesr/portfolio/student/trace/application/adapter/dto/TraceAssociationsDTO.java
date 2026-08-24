package fr.avenirsesr.portfolio.student.trace.application.adapter.dto;

import fr.avenirsesr.portfolio.student.activity.application.adapter.dto.DeclaredActivityAssociationDTO;
import fr.avenirsesr.portfolio.student.experience.application.adapter.dto.DeclaredExperienceAssociationDTO;
import fr.avenirsesr.portfolio.student.skill.application.adapter.dto.DeclaredSkillAssociationDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(
    requiredProperties = {
      "declaredActivityAssociations",
      "declaredSkillAssociations",
      "declaredExperienceAssociations"
    })
public record TraceAssociationsDTO(
    List<DeclaredActivityAssociationDTO> declaredActivityAssociations,
    List<DeclaredSkillAssociationDTO> declaredSkillAssociations,
    List<DeclaredExperienceAssociationDTO> declaredExperienceAssociations) {}
