package fr.avenirsesr.portfolio.student.association.application.adapter.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;

@Schema(requiredProperties = {"id", "title", "disabled", "limitReached"})
public record AssociationSearchResultDTO(
    UUID id,
    String title,
    String category,
    boolean disabled,
    boolean limitReached,
    String description) {}
