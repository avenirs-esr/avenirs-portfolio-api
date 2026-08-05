package fr.avenirsesr.portfolio.staff.activity.infrastructure.adapter.seeder.data;

import fr.avenirsesr.portfolio.staff.activity.domain.model.enums.EActivityThematic;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public record ActivityDraftCreationData(
    String title,
    UUID authorStaffId,
    EActivityThematic thematic,
    Optional<String> summary,
    Optional<String> description,
    Optional<String> recommendedCompletionContexts,
    Optional<LocalDate> startDate,
    Optional<LocalDate> endDate,
    Optional<Integer> traceAllowedAssociations,
    Optional<Integer> feedbackAllowedIterations,
    boolean enableReflection,
    List<String> links,
    Instant createdAt,
    Instant updatedAt) {}
