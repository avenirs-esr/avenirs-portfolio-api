package fr.avenirsesr.portfolio.student.experience.application.adapter.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;

@Schema(requiredProperties = {"associationId", "declaredExperience"})
public record DeclaredExperienceAssociationDTO(
    UUID associationId, DeclaredExperienceViewDTO declaredExperience) {}
