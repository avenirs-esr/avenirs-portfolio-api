package fr.avenirsesr.portfolio.student.association.application.adapter.request;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.UUID;

@Schema(requiredProperties = "idsToAssociate")
public record AssociationsCreationRequest(List<UUID> idsToAssociate) {}
