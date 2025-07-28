package fr.avenirsesr.portfolio.file.application.adapter.dto;

import fr.avenirsesr.portfolio.file.domain.model.EFileType;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;

@Schema(requiredProperties = {"id", "fileName", "fileType", "fileSize"})
public record AttachmentDTO(UUID id, String fileName, EFileType fileType, long fileSize) {}
