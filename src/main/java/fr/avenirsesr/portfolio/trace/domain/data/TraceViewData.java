package fr.avenirsesr.portfolio.trace.domain.data;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

public record TraceViewData(
    UUID id,
    String title,
    boolean isAssociated,
    Instant createdAt,
    Instant updatedAt,
    Optional<LocalDate> willBeDeletedAt) {}
