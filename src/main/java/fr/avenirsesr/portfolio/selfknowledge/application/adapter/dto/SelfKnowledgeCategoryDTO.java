package fr.avenirsesr.portfolio.selfknowledge.application.adapter.dto;

import fr.avenirsesr.portfolio.selfknowledge.domain.model.enums.ESelfKnowledgeCategoryType;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;

@Schema(requiredProperties = {"id", "title", "description", "type"})
public record SelfKnowledgeCategoryDTO(
    UUID id,
    String title,
    String description,
    @Schema(ref = "#/components/schemas/ESelfKnowledgeCategoryType")
        ESelfKnowledgeCategoryType type) {}
