package fr.avenirsesr.portfolio.student.progress.declared.program.application.adapter.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

@Schema(requiredProperties = {"title", "organization", "startDate"})
public record AddDeclaredProgramDTO(
    @NotBlank @Size(max = 80) String title,
    @Size(max = 400) String description,
    @NotBlank @Size(max = 50) String organization,
    @Size(max = 50) String result,
    @Size(max = 200) String sourceOfInformation,
    String link,
    @NotNull LocalDate startDate,
    LocalDate endDate) {}
