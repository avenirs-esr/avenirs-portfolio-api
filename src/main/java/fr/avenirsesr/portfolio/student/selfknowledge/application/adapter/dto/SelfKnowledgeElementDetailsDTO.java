package fr.avenirsesr.portfolio.student.selfknowledge.application.adapter.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.UUID;

@Schema(requiredProperties = {"id", "title", "description", "createdAt", "updatedAt"})
public record SelfKnowledgeElementDetailsDTO(
    UUID id,
    String title,
    String description,
    Integer rating,
    boolean valorized,
    Instant createdAt,
    Instant updatedAt) {}
