package fr.avenirsesr.portfolio.student.progress.declared.experience.application.adapter.dto;

import fr.avenirsesr.portfolio.student.progress.declared.experience.domain.model.enums.EExperienceType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

@Schema(
    requiredProperties = {
      "title",
      "organization",
      "startDate",
    })
public record DeclaredExperienceRequest(
    @NotBlank @Size(max = 80) String title,
    @Schema(ref = "#/components/schemas/EExperienceType") EExperienceType experienceType,
    @NotBlank @Size(max = 80) String organization,
    @Size(max = 50) String activitySector,
    @Size(max = 50) String location,
    @Size(max = 400) String description,
    @Size(max = 200) String sourceOfInformation,
    @Size(max = 400) String summary,
    String externalLink,
    LocalDate startDate,
    LocalDate endDate) {}
