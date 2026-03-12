package fr.avenirsesr.portfolio.shared.application.adapter.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.UUID;

@Schema(requiredProperties = "idsToAssociate")
public record AssociationsCreationRequest(List<UUID> idsToAssociate) {}
