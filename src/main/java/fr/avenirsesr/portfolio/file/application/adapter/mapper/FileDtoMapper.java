package fr.avenirsesr.portfolio.file.application.adapter.mapper;

import fr.avenirsesr.portfolio.common.file.application.adapter.dto.FileDTO;
import fr.avenirsesr.portfolio.common.file.domain.model.File;
import java.util.Optional;
import org.mapstruct.Mapper;

/**
 * Lets the MapStruct mappers of this service reuse the file representation built by the shared
 * registry, through their {@code uses} attribute.
 */
@Mapper(componentModel = "spring")
public interface FileDtoMapper {
  default FileDTO fromDomain(File file) {
    return FileDTO.fromDomain(file);
  }

  default FileDTO fromDomain(Optional<File> file) {
    return FileDTO.fromDomain(file);
  }
}
