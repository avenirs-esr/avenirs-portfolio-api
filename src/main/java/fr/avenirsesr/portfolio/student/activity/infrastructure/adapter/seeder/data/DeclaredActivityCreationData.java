package fr.avenirsesr.portfolio.student.activity.infrastructure.adapter.seeder.data;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

public record DeclaredActivityCreationData(
    UUID id,
    UUID studentId,
    UUID activityId,
    Optional<String> reflection,
    Optional<LocalDate> startDate,
    Optional<LocalDate> endDate,
    Optional<Instant> finishedAt) {}
