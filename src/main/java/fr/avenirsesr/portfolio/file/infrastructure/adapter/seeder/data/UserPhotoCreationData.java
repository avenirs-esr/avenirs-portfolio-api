package fr.avenirsesr.portfolio.file.infrastructure.adapter.seeder.data;

import fr.avenirsesr.portfolio.common.data.domain.model.enums.EUserCategory;
import fr.avenirsesr.portfolio.file.domain.model.enums.EFileType;
import java.util.UUID;

public record UserPhotoCreationData(
    UUID userId,
    EUserCategory userCategory,
    EUserPhotoSlot photoType,
    String fileName,
    EFileType fileType,
    long fileSize) {}
