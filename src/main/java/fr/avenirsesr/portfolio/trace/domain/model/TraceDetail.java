package fr.avenirsesr.portfolio.trace.domain.model;

import fr.avenirsesr.portfolio.file.domain.model.TraceAttachment;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record TraceDetail(
    UUID id,
    String title,
    ETraceStatus status,
    String programName,
    boolean isGroup,
    List<TraceAttachment> attachments,
    Instant createdAt,
    Instant updatedAt) {}
