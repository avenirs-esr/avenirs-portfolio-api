package fr.avenirsesr.portfolio.user.infrastructure.adapter.mapper;

import fr.avenirsesr.portfolio.user.domain.model.FileUploadLink;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.model.FileUploadLinkEntity;

public interface FileUploadLinkMapper {
  static FileUploadLinkEntity fromDomain(FileUploadLink fileUploadLink) {
    return FileUploadLinkEntity.of(
        fileUploadLink.getUploadId(),
        fileUploadLink.getContextType(),
        fileUploadLink.getContextId());
  }

  static FileUploadLink toDomain(FileUploadLinkEntity fileUploadLinkEntity) {
    return FileUploadLink.toDomain(
        fileUploadLinkEntity.getUploadId(),
        fileUploadLinkEntity.getContextType(),
        fileUploadLinkEntity.getContextId());
  }
}
