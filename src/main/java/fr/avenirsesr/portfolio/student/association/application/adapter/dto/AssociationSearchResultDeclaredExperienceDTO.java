package fr.avenirsesr.portfolio.student.association.application.adapter.dto;

import fr.avenirsesr.portfolio.student.experience.domain.model.enums.EExperienceType;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;

@Schema(requiredProperties = {"id", "title", "experienceType", "disabled"})
public record AssociationSearchResultDeclaredExperienceDTO(
    UUID id,
    String title,
    @Schema(ref = "#/components/schemas/EExperienceType") EExperienceType experienceType,
    boolean disabled) {}
