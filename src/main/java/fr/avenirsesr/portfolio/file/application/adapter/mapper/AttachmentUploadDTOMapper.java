package fr.avenirsesr.portfolio.file.application.adapter.mapper;

import fr.avenirsesr.portfolio.file.application.adapter.dto.AttachmentUploadDTO;
import fr.avenirsesr.portfolio.file.domain.model.TraceAttachment;
import java.util.Optional;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AttachmentUploadDTOMapper {

  @Mapping(source = "name", target = "fileName")
  @Mapping(source = "size", target = "fileSize")
  AttachmentUploadDTO fromDomain(TraceAttachment attachment);

  default AttachmentUploadDTO fromDomain(Optional<TraceAttachment> attachment) {
    return attachment.map(this::fromDomain).orElse(null);
  }
}
