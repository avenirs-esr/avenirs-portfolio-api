package fr.avenirsesr.portfolio.user.domain.model;

import java.util.Optional;
import java.util.UUID;

public record UserPhotos(
    Optional<UUID> profileFileId, String profileUrl, Optional<UUID> coverFileId, String coverUrl) {}
