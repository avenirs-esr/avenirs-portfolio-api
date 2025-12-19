package fr.avenirsesr.portfolio.student.progress.declared.experience.application.adapter.dto;

import static fr.avenirsesr.portfolio.common.validation.domain.constraints.FieldMaxLengths.*;

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
    @NotBlank @Size(max = TITLE_LENGTH) String title,
    @Schema(ref = "#/components/schemas/EExperienceType") EExperienceType experienceType,
    @NotBlank @Size(max = ORGANIZATION_LENGTH) String organization,
    @Size(max = ACTIVITY_SECTOR_LENGTH) String activitySector,
    @Size(max = LOCATION_LENGTH) String location,
    @Size(max = DESCRIPTION_LENGTH) String description,
    @Size(max = SOURCE_OF_INFORMATION_LENGTH) String sourceOfInformation,
    @Size(max = SUMMARY_LENGTH) String summary,
    String externalLink,
    LocalDate startDate,
    LocalDate endDate) {}
