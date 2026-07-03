package fr.avenirsesr.portfolio.activity.infrastructure.adapter.seeder.data;

import fr.avenirsesr.portfolio.activity.domain.model.enums.EActivityThematic;
import java.time.Instant;
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
    Optional<String> executionPeriodInfoSummary,
    ActivityBannerCreationData banner,
    boolean enableReflection,
    List<String> links,
    int traceAllowedAssociations,
    int feedbackAllowedIterations,
    Instant createdAt,
    Instant updatedAt) {}
