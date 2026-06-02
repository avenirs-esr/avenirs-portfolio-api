package fr.avenirsesr.portfolio.activity.domain.data;

import fr.avenirsesr.portfolio.activity.domain.model.enums.EActivityThematic;
import fr.avenirsesr.portfolio.file.domain.data.FileData;
import java.time.Instant;
import java.util.UUID;

public record ActivityContentData(
    UUID id,
    String title,
    EActivityThematic thematic,
    FileData banner,
    String summary,
    String description,
    String executionPeriodInfo,
    boolean enableReflection,
    int traceAllowedAssociations,
    int feedbackAllowedIterations,
    Instant createdAt,
    Instant updatedAt) {}
