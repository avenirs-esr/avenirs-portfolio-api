package fr.avenirsesr.portfolio.student.trace.domain.data;

import fr.avenirsesr.portfolio.file.domain.model.File;
import fr.avenirsesr.portfolio.student.trace.domain.model.enums.ETraceAuthorType;
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
    Optional<LocalDate> willBeDeletedAt,
    Optional<File> attachment,
    ETraceAuthorType authorType,
    String personalNote,
    String aiUseJustification) {}
