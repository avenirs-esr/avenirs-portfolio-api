package fr.avenirsesr.portfolio.user.infrastructure.adapter.mapper;

import fr.avenirsesr.portfolio.user.domain.model.UploadLink;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.model.UploadLinkEntity;

public interface UploadLinkMapper {
  static UploadLinkEntity fromDomain(UploadLink uploadLink) {
    return UploadLinkEntity.of(
        uploadLink.getUploadId(), uploadLink.getContextType(), uploadLink.getContextId());
  }

  static UploadLink toDomain(UploadLinkEntity uploadLinkEntity) {
    return UploadLink.toDomain(
        uploadLinkEntity.getUploadId(),
        uploadLinkEntity.getContextType(),
        uploadLinkEntity.getContextId());
  }
}
