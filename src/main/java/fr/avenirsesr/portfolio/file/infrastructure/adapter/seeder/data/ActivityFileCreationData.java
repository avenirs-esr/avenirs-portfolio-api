package fr.avenirsesr.portfolio.file.infrastructure.adapter.seeder.data;

import fr.avenirsesr.portfolio.common.file.domain.model.enums.EFileType;
import java.util.UUID;

public record ActivityFileCreationData(
    UUID activityId, String fileName, EFileType fileType, long fileSize) {}
