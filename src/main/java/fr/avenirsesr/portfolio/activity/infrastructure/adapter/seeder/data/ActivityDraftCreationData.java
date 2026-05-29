package fr.avenirsesr.portfolio.activity.infrastructure.adapter.seeder.data;

import fr.avenirsesr.portfolio.activity.domain.model.enums.EActivityThematic;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public record ActivityDraftCreationData(
    String title,
    UUID authorStaffId,
    EActivityThematic thematic,
    Optional<String> summary,
    Optional<String> description,
    Optional<String> executionPeriodInfo,
    Optional<String> executionPeriodInfoSummary,
    Optional<Integer> traceAllowedAssociations,
    Optional<Integer> feedbackAllowedIterations,
    boolean enableReflection,
    Instant createdAt,
    Instant updatedAt) {}
