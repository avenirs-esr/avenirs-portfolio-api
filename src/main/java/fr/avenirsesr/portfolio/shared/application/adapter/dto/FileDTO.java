package fr.avenirsesr.portfolio.shared.application.adapter.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;

@Schema(requiredProperties = {"url"})
public record FileDTO(UUID fileId, String fileName, String url) {}
