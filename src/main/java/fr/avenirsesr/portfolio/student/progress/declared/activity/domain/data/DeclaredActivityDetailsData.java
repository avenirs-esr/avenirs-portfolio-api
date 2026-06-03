package fr.avenirsesr.portfolio.student.progress.declared.activity.domain.data;

import fr.avenirsesr.portfolio.activity.domain.data.ActivityContentData;
import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.model.enums.EDeclaredActivityStatus;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record DeclaredActivityDetailsData(
    UUID id,
    ActivityContentData activity,
    EDeclaredActivityStatus status,
    String reflection,
    LocalDate startDate,
    LocalDate endDate,
    Instant finishedAt,
    Instant createdAt,
    Instant updatedAt) {}
