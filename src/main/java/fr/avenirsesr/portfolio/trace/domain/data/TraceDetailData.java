package fr.avenirsesr.portfolio.trace.domain.data;

import fr.avenirsesr.portfolio.file.domain.model.TraceAttachment;
import java.time.Instant;
import java.util.UUID;

public record TraceDetailData(
    UUID id,
    String title,
    boolean isAssociated,
    String programName,
    boolean isGroup,
    String aiUseJustification,
    String personalNote,
    String link,
    TraceAttachment attachment,
    Instant createdAt,
    Instant updatedAt) {}
