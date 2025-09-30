package fr.avenirsesr.portfolio.trace.application.adapter.dto;

import fr.avenirsesr.portfolio.ams.domain.model.enums.EAmsStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;

@Schema(requiredProperties = {"id", "title", "status"})
public record AmsAssociationDTO(
    UUID id, String title, @Schema(ref = "#/components/schemas/EAmsStatus") EAmsStatus status) {}
