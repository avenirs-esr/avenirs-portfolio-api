package fr.avenirsesr.portfolio.activity.domain.data;

import fr.avenirsesr.portfolio.activity.domain.model.enums.EActivityThematic;
import fr.avenirsesr.portfolio.file.domain.data.FileData;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public record ActivityPresentationData(
    UUID id,
    String title,
    EActivityThematic thematic,
    Optional<UUID> subscribedDeclaredActivity,
    String summary,
    String description,
    String executionPeriodInfo,
    FileData banner,
    List<String> links,
    Instant createdAt,
    Instant updatedAt) {}
