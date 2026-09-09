package fr.avenirsesr.portfolio.staff.activity.domain.data;

import fr.avenirsesr.portfolio.file.domain.data.FileData;
import fr.avenirsesr.portfolio.staff.activity.domain.model.enums.EActivityThematic;
import fr.avenirsesr.portfolio.student.activity.domain.model.enums.EDeclaredActivityStatus;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public record ActivityPresentationData(
    UUID id,
    String title,
    EActivityThematic thematic,
    Optional<UUID> subscribedDeclaredActivity,
    Optional<EDeclaredActivityStatus> subscribedDeclaredActivityStatus,
    String summary,
    String description,
    String recommendedCompletionContexts,
    FileData banner,
    Instant createdAt,
    Instant updatedAt) {}
