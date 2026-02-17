package fr.avenirsesr.portfolio.activity.domain.data;

import fr.avenirsesr.portfolio.activity.domain.model.enums.EActivityThematic;
import fr.avenirsesr.portfolio.file.domain.data.FileData;
import java.time.Instant;
import java.util.UUID;

public record ActivityDetailData(
    UUID id,
    String title,
    EActivityThematic thematic,
    FileData activityBanner,
    String summary,
    String executionPeriodInfo,
    Instant createdAt,
    Instant updatedAt) {}
