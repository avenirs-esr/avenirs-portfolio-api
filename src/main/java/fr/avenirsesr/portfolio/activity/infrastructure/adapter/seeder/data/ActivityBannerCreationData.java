package fr.avenirsesr.portfolio.activity.infrastructure.adapter.seeder.data;

import fr.avenirsesr.portfolio.file.domain.model.shared.EFileType;

public record ActivityBannerCreationData(String fileName, EFileType fileType, long fileSize) {}
