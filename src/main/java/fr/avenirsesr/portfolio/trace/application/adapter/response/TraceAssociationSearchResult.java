package fr.avenirsesr.portfolio.trace.application.adapter.response;

import fr.avenirsesr.portfolio.trace.application.adapter.dto.ETraceAssociationType;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;

@Schema(requiredProperties = {"type", "title"})
public record TraceAssociationSearchResult(
    ETraceAssociationType type, UUID id, String title, String description) {}
