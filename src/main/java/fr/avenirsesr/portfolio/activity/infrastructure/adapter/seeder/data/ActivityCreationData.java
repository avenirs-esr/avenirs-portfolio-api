package fr.avenirsesr.portfolio.activity.infrastructure.adapter.seeder.data;

import fr.avenirsesr.portfolio.activity.domain.model.enums.EActivityThematic;
import java.util.UUID;

public record ActivityCreationData(
    UUID id,
    String title,
    EActivityThematic thematic,
    String summary,
    String executionPeriodInfo,
    ActivityBannerCreationData banner) {}
