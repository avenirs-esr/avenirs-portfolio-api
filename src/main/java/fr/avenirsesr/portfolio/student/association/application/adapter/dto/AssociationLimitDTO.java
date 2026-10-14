package fr.avenirsesr.portfolio.student.association.application.adapter.dto;

import fr.avenirsesr.portfolio.student.association.domain.model.EAssociationContextType;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(requiredProperties = {"associatedContextType", "max", "remaining"})
public record AssociationLimitDTO(
    @Schema(ref = "#/components/schemas/EAssociationContextType")
        EAssociationContextType associatedContextType,
    int max,
    int remaining) {}
