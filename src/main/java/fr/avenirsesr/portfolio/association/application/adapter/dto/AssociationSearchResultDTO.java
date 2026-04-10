package fr.avenirsesr.portfolio.association.application.adapter.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;

@Schema(requiredProperties = {"id", "title", "disabled"})
public record AssociationSearchResultDTO(
    UUID id, String title, String category, boolean disabled) {}
