package fr.avenirsesr.portfolio.trace.infrastructure.adapter.seeder.data;

import fr.avenirsesr.portfolio.file.domain.model.enums.EFileType;
import java.time.Instant;

public record TraceAttachementCreationData(
    String title, EFileType fileType, long size, Instant uploadedAt) {}
