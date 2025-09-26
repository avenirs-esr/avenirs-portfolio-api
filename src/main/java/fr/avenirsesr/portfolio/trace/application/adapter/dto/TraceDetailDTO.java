package fr.avenirsesr.portfolio.trace.application.adapter.dto;

import fr.avenirsesr.portfolio.file.application.adapter.dto.AttachmentUploadDTO;
import fr.avenirsesr.portfolio.trace.domain.model.ETraceStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Schema(
    requiredProperties = {
      "id",
      "title",
      "status",
      "programName",
      "isGroup",
      "attachments",
      "createdAt",
      "updatedAt"
    })
public record TraceDetailDTO(
    UUID id,
    String title,
    @Schema(ref = "#/components/schemas/ETraceStatus") ETraceStatus status,
    String programName,
    boolean isGroup,
    List<AttachmentUploadDTO> attachments,
    Instant createdAt,
    Instant updatedAt) {}
