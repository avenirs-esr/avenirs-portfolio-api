package fr.avenirsesr.portfolio.user.domain.data;

import java.time.Instant;
import java.util.UUID;

public record CguAcceptanceData(UUID versionId, Instant acceptedAt, boolean isLastVersion) {}
