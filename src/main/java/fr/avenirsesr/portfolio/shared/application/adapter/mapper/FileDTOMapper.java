package fr.avenirsesr.portfolio.shared.application.adapter.mapper;

import fr.avenirsesr.portfolio.file.domain.model.File;
import fr.avenirsesr.portfolio.file.infrastructure.configuration.FileStorageConstants;
import fr.avenirsesr.portfolio.shared.application.adapter.dto.FileDTO;
import java.util.List;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface FileDTOMapper {

  default FileDTO toFileDTO(File file, String baseUrl) {
    return new FileDTO(
        file.getId(), file.getFileName(), baseUrl + FileStorageConstants.publicUrlOf(file.getId()));
  }

  default List<FileDTO> toFileDTOs(List<File> files, String baseUrl) {
    return files.stream().map(file -> toFileDTO(file, baseUrl)).toList();
  }
}
