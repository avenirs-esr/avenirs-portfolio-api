package fr.avenirsesr.portfolio.user.domain.model;

import java.util.Optional;
import java.util.UUID;

public record UserPhotos(
    Optional<UUID> profileFileId,
    Optional<String> profileFileName,
    String profileFileUrl,
    Optional<UUID> coverFileId,
    Optional<String> coverFileName,
    String coverFileUrl) {}
