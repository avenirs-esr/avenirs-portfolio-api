package fr.avenirsesr.portfolio.trace.application.adapter.dto;

import fr.avenirsesr.portfolio.activity.domain.model.enums.EActivityThematic;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;

@Schema(requiredProperties = {"id", "title", "thematic", "disabled"})
public record TraceAssociationDeclaredActivityInfoDTO(
    UUID id, String title, EActivityThematic thematic, boolean disabled) {}
