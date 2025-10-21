package fr.avenirsesr.portfolio.file.domain.data;

import java.util.Optional;
import java.util.UUID;

public record UserPhotoData(Optional<UUID> id, Optional<String> name, String url) {}
