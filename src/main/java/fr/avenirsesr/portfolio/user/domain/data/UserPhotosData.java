package fr.avenirsesr.portfolio.user.domain.data;

import java.util.Optional;
import java.util.UUID;

public record UserPhotosData(
    Optional<UUID> profileFileId,
    Optional<String> profileFileName,
    String profileFileUrl,
    Optional<UUID> coverFileId,
    Optional<String> coverFileName,
    String coverFileUrl) {}
