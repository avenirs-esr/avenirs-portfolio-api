package fr.avenirsesr.portfolio.file.application.adapter.mapper;

import fr.avenirsesr.portfolio.file.application.adapter.dto.AttachmentUploadDTO;
import fr.avenirsesr.portfolio.file.domain.model.TraceAttachment;
import java.util.Optional;

public interface AttachmentUploadDTOMapper {
  static AttachmentUploadDTO fromDomain(TraceAttachment attachment) {
    return new AttachmentUploadDTO(
        attachment.getId(),
        attachment.getName(),
        attachment.getFileType(),
        attachment.getSize(),
        attachment.getVersion(),
        attachment.getUploadedAt());
  }

  static AttachmentUploadDTO fromDomain(Optional<TraceAttachment> attachment) {
    return attachment.map(AttachmentUploadDTOMapper::fromDomain).orElse(null);
  }
}
