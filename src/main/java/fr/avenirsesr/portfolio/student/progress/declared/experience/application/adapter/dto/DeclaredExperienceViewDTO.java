package fr.avenirsesr.portfolio.student.progress.declared.experience.application.adapter.dto;

import fr.avenirsesr.portfolio.student.progress.declared.experience.domain.model.enums.EExperienceType;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Schema(requiredProperties = {"id", "title", "organization", "startDate", "createdAt", "updatedAt"})
public record DeclaredExperienceViewDTO(
    UUID id,
    String title,
    @Schema(ref = "#/components/schemas/EExperienceType") EExperienceType experienceType,
    String organization,
    String activitySector,
    String location,
    String description,
    String sourceOfInformation,
    String summary,
    String externalLink,
    LocalDate startDate,
    LocalDate endDate,
    Instant createdAt,
    Instant updatedAt) {}
