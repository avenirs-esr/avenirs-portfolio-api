package fr.avenirsesr.portfolio.file.application.adapter.mapper;

import fr.avenirsesr.portfolio.file.application.adapter.dto.AttachmentUploadDTO;
import fr.avenirsesr.portfolio.file.domain.model.TraceAttachment;

public interface AttachmentUploadDTOMapper {
  static AttachmentUploadDTO fromDomain(TraceAttachment attachment) {
    return new AttachmentUploadDTO(
        attachment.getId(),
        attachment.getName(),
        attachment.getFileType(),
        attachment.getSize(),
        attachment.getVersion());
  }
}
