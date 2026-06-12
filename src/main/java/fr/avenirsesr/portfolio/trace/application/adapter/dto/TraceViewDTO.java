package fr.avenirsesr.portfolio.trace.application.adapter.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Schema(
    requiredProperties = {
      "id",
      "title",
      "isAssociated",
      "isDeletable",
      "createdAt",
      "updatedAt",
    })
public record TraceViewDTO(
    UUID id,
    String title,
    boolean isAssociated,
    boolean isDeletable,
    Instant createdAt,
    Instant updatedAt,
    LocalDate willBeDeletedAt) {}
