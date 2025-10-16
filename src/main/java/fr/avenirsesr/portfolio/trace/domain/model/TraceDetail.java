package fr.avenirsesr.portfolio.trace.domain.model;

import fr.avenirsesr.portfolio.file.domain.model.TraceAttachment;
import java.time.Instant;
import java.util.UUID;

public record TraceDetail(
    UUID id,
    String title,
    boolean isAssociated,
    String programName,
    boolean isGroup,
    String aiUseJustification,
    String personalNote,
    TraceAttachment attachment,
    TraceAssociations traceAssociations,
    Instant createdAt,
    Instant updatedAt) {}
