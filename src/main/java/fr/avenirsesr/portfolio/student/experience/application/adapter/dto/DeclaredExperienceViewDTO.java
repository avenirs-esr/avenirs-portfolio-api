package fr.avenirsesr.portfolio.student.experience.application.adapter.dto;

import fr.avenirsesr.portfolio.student.experience.domain.model.enums.EExperienceType;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Schema(
    requiredProperties = {
      "id",
      "title",
      "organization",
      "startDate",
      "createdAt",
      "updatedAt",
      "declaredExperienceAssociationCountDTO"
    })
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
    boolean valorized,
    Instant createdAt,
    Instant updatedAt,
    DeclaredExperienceAssociationCountDTO declaredExperienceAssociationCountDTO) {}
