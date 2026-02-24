package fr.avenirsesr.portfolio.student.progress.declared.activity.infrastructure.adapter.seeder.data;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record DeclaredActivityCreationData(
    UUID studentId,
    UUID activityId,
    String reflection,
    LocalDate startDate,
    LocalDate endDate,
    Instant finishedAt) {}
