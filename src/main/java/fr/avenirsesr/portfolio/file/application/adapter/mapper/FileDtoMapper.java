package fr.avenirsesr.portfolio.file.application.adapter.mapper;

import fr.avenirsesr.portfolio.file.application.adapter.dto.FileDTO;
import fr.avenirsesr.portfolio.file.domain.model.File;
import fr.avenirsesr.portfolio.file.infrastructure.configuration.FileStorageConstants;
import java.util.Optional;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface FileDtoMapper {
  default FileDTO fromDomain(File file) {
    if (file == null) {
      return null;
    }

    return new FileDTO(
        file.getId(),
        file.getFileName(),
        file.getFileType(),
        file.getSize(),
        FileStorageConstants.publicUrlOf(file.getId()),
        file.getUploadedAt());
  }

  default FileDTO fromDomain(Optional<File> file) {
    return file.map(this::fromDomain).orElse(null);
  }
}
