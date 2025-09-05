package fr.avenirsesr.portfolio.file.domain.model;

import java.util.Optional;
import java.util.UUID;

public record UserPhotoUrlAndId(Optional<UUID> id, Optional<String> name, String url) {}
