package fr.avenirsesr.portfolio.file.application.adapter.dto;

import fr.avenirsesr.portfolio.file.domain.model.shared.EFileType;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;

@Schema(requiredProperties = {"id", "fileType", "fileSize", "version"})
public record UserPhotoUploadDTO(UUID id, EFileType fileType, long fileSize, int version) {}
