package fr.avenirsesr.portfolio.file.infrastructure.adapter.mapper;

import fr.avenirsesr.portfolio.common.data.infrastructure.adapter.mapper.Mapper;
import fr.avenirsesr.portfolio.file.domain.model.File;
import fr.avenirsesr.portfolio.file.infrastructure.adapter.model.FileEntity;

public class FileMapper implements Mapper<FileEntity, File> {
  public static final FileMapper INSTANCE = new FileMapper();

  @Override
  public FileEntity fromDomain(File domain) {
    return FileEntity.of(
        domain.getId(),
        domain.getFileType(),
        domain.getFileName(),
        domain.getSize(),
        domain.getUri(),
        domain.getUploadedById(),
        domain.getUploadedAt(),
        domain.isRestricted(),
        domain.getCreatedAt(),
        domain.getUpdatedAt());
  }

  @Override
  public File toDomain(FileEntity entity) {
    return File.toDomain(
        entity.getId(),
        entity.getFileType(),
        entity.getFileName(),
        entity.getSize(),
        entity.getUri(),
        entity.getUploadedById(),
        entity.getUploadedAt(),
        entity.isRestricted(),
        entity.getCreatedAt(),
        entity.getUpdatedAt());
  }
}
