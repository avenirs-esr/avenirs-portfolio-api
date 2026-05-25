package fr.avenirsesr.portfolio.file.application.adapter.dto;

import fr.avenirsesr.portfolio.file.domain.model.EFileType;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.UUID;

@Schema(requiredProperties = {"id", "fileName", "fileType", "fileSize", "version", "uploadedAt"})
public record FileDTO(
    UUID id,
    String fileName,
    @Schema(ref = "#/components/schemas/EFileType") EFileType fileType,
    long fileSize,
    int version,
    Instant uploadedAt) {}
