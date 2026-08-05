package fr.avenirsesr.portfolio.student.activity.application.adapter.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;

@Schema(requiredProperties = {"associationId", "declaredActivity"})
public record DeclaredActivityAssociationDTO(
    UUID associationId, DeclaredActivityViewDTO declaredActivity) {}
