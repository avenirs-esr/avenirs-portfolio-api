package fr.avenirsesr.portfolio.trace.domain.data;

import fr.avenirsesr.portfolio.file.domain.model.File;
import fr.avenirsesr.portfolio.trace.domain.model.enums.ETraceAuthorType;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public record TraceDetailData(
    UUID id,
    String title,
    boolean isAssociated,
    boolean isDeletable,
    String programName,
    ETraceAuthorType authorType,
    String aiUseJustification,
    String personalNote,
    Optional<String> link,
    Optional<File> attachment,
    Instant createdAt,
    Instant updatedAt) {}
