package fr.avenirsesr.portfolio.student.activity.application.adapter.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;

@Schema(requiredProperties = {"startDate", "endDate"})
public record DeclaredActivityPeriodDTO(LocalDate startDate, LocalDate endDate) {}
