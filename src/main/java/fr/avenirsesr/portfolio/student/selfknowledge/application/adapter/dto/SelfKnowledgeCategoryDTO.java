package fr.avenirsesr.portfolio.student.selfknowledge.application.adapter.dto;

import fr.avenirsesr.portfolio.student.selfknowledge.domain.model.enums.ESelfKnowledgeCategory;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(requiredProperties = {"type", "mandatory"})
public record SelfKnowledgeCategoryDTO(
    @Schema(ref = "#/components/schemas/ESelfKnowledgeCategory") ESelfKnowledgeCategory type,
    boolean mandatory) {}
