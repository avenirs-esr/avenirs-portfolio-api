package fr.avenirsesr.portfolio.trace.application.adapter.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(requiredProperties = {"skillLevelAssociations", "declaredSkillAssociations"})
public record TraceAssociationsDTO(
    List<SkillLevelAssociationDTO> skillLevelAssociations,
    List<DeclaredSkillAssociationDTO> declaredSkillAssociations) {}
