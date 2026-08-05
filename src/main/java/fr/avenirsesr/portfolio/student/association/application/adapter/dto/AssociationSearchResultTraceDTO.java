package fr.avenirsesr.portfolio.student.association.application.adapter.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;

@Schema(requiredProperties = {"id", "title", "disabled"})
public record AssociationSearchResultTraceDTO(UUID id, String title, boolean disabled) {}
