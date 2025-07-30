package fr.avenirsesr.portfolio.file.domain.model.shared;

import java.util.UUID;

public record FileResource(
    UUID id, String fileName, EFileType fileType, long size, byte[] content) {}
