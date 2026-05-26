package fr.avenirsesr.portfolio.file.application.adapter.mapper;

import fr.avenirsesr.portfolio.file.application.adapter.dto.FileDTO;
import fr.avenirsesr.portfolio.file.domain.model.File;
import java.util.Optional;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface FileDtoMapper {
  @Mapping(source = "size", target = "fileSize")
  @Mapping(source = "uri", target = "url")
  FileDTO fromDomain(File file);

  default FileDTO fromDomain(Optional<File> file) {
    return file.map(this::fromDomain).orElse(null);
  }
}
