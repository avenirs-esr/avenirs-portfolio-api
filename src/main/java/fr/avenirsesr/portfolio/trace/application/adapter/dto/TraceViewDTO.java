package fr.avenirsesr.portfolio.trace.application.adapter.dto;

import fr.avenirsesr.portfolio.file.application.adapter.dto.FileDTO;
import fr.avenirsesr.portfolio.trace.domain.model.enums.ETraceAuthorType;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Schema(
    requiredProperties = {
      "id",
      "title",
      "isAssociated",
      "createdAt",
      "updatedAt",
      "authorType",
    })
public record TraceViewDTO(
    UUID id,
    String title,
    boolean isAssociated,
    Instant createdAt,
    Instant updatedAt,
    LocalDate willBeDeletedAt,
    FileDTO attachment,
    @Schema(ref = "#/components/schemas/ETraceAuthorType") ETraceAuthorType authorType,
    String personalNote,
    String aiUseJustification) {}
