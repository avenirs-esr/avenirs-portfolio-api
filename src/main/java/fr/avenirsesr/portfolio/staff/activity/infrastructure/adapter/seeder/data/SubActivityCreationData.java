package fr.avenirsesr.portfolio.staff.activity.infrastructure.adapter.seeder.data;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public record SubActivityCreationData(
    UUID id,
    UUID previousActivityId,
    String title,
    String description,
    String recommendedCompletionContexts,
    Optional<LocalDate> startDate,
    Optional<LocalDate> endDate,
    List<String> links,
    Instant updatedAt) {}
