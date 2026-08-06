package fr.avenirsesr.portfolio.student.trace.domain.data;

import fr.avenirsesr.portfolio.file.domain.model.File;
import fr.avenirsesr.portfolio.student.trace.domain.model.enums.ETraceAuthorType;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public record TraceDetailData(
    UUID id,
    String title,
    boolean isAssociated,
    ETraceAuthorType authorType,
    String aiUseJustification,
    String personalNote,
    Optional<String> link,
    Optional<File> attachment,
    boolean valorized,
    Instant createdAt,
    Instant updatedAt) {}
