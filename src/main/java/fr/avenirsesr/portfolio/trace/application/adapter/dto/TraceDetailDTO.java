package fr.avenirsesr.portfolio.trace.application.adapter.dto;

import fr.avenirsesr.portfolio.file.application.adapter.dto.AttachmentUploadDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.UUID;

@Schema(
    requiredProperties = {
      "id",
      "title",
      "status",
      "programName",
      "isGroup",
      "aiUseJustification",
      "personalNote",
      "attachment",
      "createdAt",
      "updatedAt"
    })
public record TraceDetailDTO(
    UUID id,
    String title,
    boolean isAssociated,
    String programName,
    boolean isGroup,
    String aiUseJustification,
    String personalNote,
    AttachmentUploadDTO attachment,
    Instant createdAt,
    Instant updatedAt) {}
