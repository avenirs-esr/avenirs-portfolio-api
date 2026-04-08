package fr.avenirsesr.portfolio.trace.application.adapter.dto;

import fr.avenirsesr.portfolio.student.progress.declared.activity.application.adapter.dto.DeclaredActivityAssociationDTO;
import fr.avenirsesr.portfolio.student.progress.declared.skill.application.adapter.dto.DeclaredSkillAssociationDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(requiredProperties = {"declaredActivityAssociations", "declaredSkillAssociations"})
public record TraceAssociationsDTO(
    List<DeclaredActivityAssociationDTO> declaredActivityAssociations,
    List<DeclaredSkillAssociationDTO> declaredSkillAssociations) {}
