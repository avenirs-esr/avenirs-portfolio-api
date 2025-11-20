package fr.avenirsesr.portfolio.selfknowledge.application.adapter.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

@Schema(requiredProperties = {"title", "description"})
public record SelfKnowledgeElementRequest(
    @Size(max = 80) String title,
    @Size(max = 400) String description,
    @Min(1) @Max(5) Integer rating) {}
