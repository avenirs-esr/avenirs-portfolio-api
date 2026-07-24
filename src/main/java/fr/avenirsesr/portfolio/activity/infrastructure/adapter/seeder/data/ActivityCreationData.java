package fr.avenirsesr.portfolio.activity.infrastructure.adapter.seeder.data;

import fr.avenirsesr.portfolio.activity.domain.model.enums.EActivityThematic;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public record ActivityCreationData(
    UUID id,
    UUID authorStaffId,
    String title,
    EActivityThematic thematic,
    String summary,
    String description,
    String executionPeriodInfo,
    Optional<LocalDate> startDate,
    Optional<LocalDate> endDate,
    ActivityBannerCreationData banner,
    boolean enableReflection,
    List<String> links,
    int traceAllowedAssociations,
    int feedbackAllowedIterations,
    Instant createdAt,
    Instant updatedAt) {}
