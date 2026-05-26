package fr.avenirsesr.portfolio.file.domain.model;

import fr.avenirsesr.portfolio.file.domain.model.enums.EFileType;
import java.util.UUID;

public record FileResource(
    UUID id, String fileName, EFileType fileType, long size, byte[] content) {}
