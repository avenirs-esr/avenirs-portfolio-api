package fr.avenirsesr.portfolio.staff.activity.domain.data;

import java.time.LocalDate;
import java.util.UUID;

public record SubActivityOverviewData(
    UUID id, String title, LocalDate startDate, LocalDate endDate, boolean publishable) {}
