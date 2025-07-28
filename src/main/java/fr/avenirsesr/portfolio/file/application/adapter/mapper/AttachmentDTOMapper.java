package fr.avenirsesr.portfolio.file.application.adapter.mapper;

import fr.avenirsesr.portfolio.file.application.adapter.dto.AttachmentDTO;
import fr.avenirsesr.portfolio.file.domain.model.TraceAttachment;

public interface AttachmentDTOMapper {
  static AttachmentDTO fromDomain(TraceAttachment attachment) {
    return new AttachmentDTO(
        attachment.getId(), attachment.getName(), attachment.getFileType(), attachment.getSize());
  }
}
