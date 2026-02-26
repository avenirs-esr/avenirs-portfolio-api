package fr.avenirsesr.portfolio.student.progress.declared.activity.application.adapter.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record SubscribeDeclaredActivityRequestDTO(
    @NotNull(message = "startDate is mandatory if a body is provided") LocalDate startDate,
    @NotNull(message = "endDate is mandatory if a body is provided") LocalDate endDate) {}
