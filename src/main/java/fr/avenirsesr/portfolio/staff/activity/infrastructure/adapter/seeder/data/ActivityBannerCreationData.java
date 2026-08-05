package fr.avenirsesr.portfolio.staff.activity.infrastructure.adapter.seeder.data;

import fr.avenirsesr.portfolio.file.domain.model.enums.EFileType;

public record ActivityBannerCreationData(String fileName, EFileType fileType, long fileSize) {}
