package fr.avenirsesr.portfolio.selfknowledge.application.adapter.dto;

import fr.avenirsesr.portfolio.selfknowledge.domain.model.enums.ESelfKnowledgeCategory;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(requiredProperties = {"type", "mandatory"})
public record SelfKnowledgeCategoryDTO(
    @Schema(ref = "#/components/schemas/ESelfKnowledgeCategory") ESelfKnowledgeCategory type,
    boolean mandatory) {}
