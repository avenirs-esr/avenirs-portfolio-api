package fr.avenirsesr.portfolio.student.program.application.adapter.dto;

import static fr.avenirsesr.portfolio.common.validation.domain.constraints.FieldMaxLengths.*;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

@Schema(requiredProperties = {"title", "organization", "startDate"})
public record DeclaredProgramRequestDTO(
    @NotBlank @Size(max = TITLE_LENGTH) String title,
    @Size(max = DESCRIPTION_LENGTH) String description,
    @NotBlank @Size(max = ORGANIZATION_LENGTH) String organization,
    @Size(max = RESULT_LENGTH) String result,
    @Size(max = SOURCE_OF_INFORMATION_LENGTH) String sourceOfInformation,
    @NotNull LocalDate startDate,
    LocalDate endDate,
    Boolean valorized) {}
