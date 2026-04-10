package fr.avenirsesr.portfolio.association.application.adapter.dto;

import fr.avenirsesr.portfolio.common.externalskill.domain.model.enums.EExternalSkillType;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;

@Schema(requiredProperties = {"id", "title", "type", "disabled"})
public record AssociationSearchResultDeclaredSkillIDTO(
    UUID id,
    String title,
    @Schema(ref = "#/components/schemas/EExternalSkillType") EExternalSkillType type,
    boolean disabled) {}
