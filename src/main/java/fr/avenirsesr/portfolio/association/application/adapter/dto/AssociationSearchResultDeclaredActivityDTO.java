package fr.avenirsesr.portfolio.association.application.adapter.dto;

import fr.avenirsesr.portfolio.activity.domain.model.enums.EActivityThematic;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;

@Schema(requiredProperties = {"id", "title", "thematic", "disabled"})
public record AssociationSearchResultDeclaredActivityDTO(
    UUID id,
    String title,
    @Schema(ref = "#/components/schemas/EActivityThematic") EActivityThematic thematic,
    boolean disabled) {}
