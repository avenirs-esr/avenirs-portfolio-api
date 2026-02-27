package fr.avenirsesr.portfolio.activity.application.adapter.dto;

import java.time.LocalDate;

public record ActivityPeriodRequest(LocalDate startDate, LocalDate endDate) {}
