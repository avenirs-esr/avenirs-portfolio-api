package fr.avenirsesr.portfolio.file.domain.model;

import java.util.Optional;
import java.util.UUID;

public record UserPhotoUrlAndId(Optional<UUID> id, String url) {}
