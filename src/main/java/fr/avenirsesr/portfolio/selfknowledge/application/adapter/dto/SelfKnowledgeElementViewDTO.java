package fr.avenirsesr.portfolio.selfknowledge.application.adapter.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;

@Schema(requiredProperties = {"id", "title", "description"})
public record SelfKnowledgeElementViewDTO(
    UUID id, String title, String description, Integer rating) {}
