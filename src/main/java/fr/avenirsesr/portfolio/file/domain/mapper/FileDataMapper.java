package fr.avenirsesr.portfolio.file.domain.mapper;

import fr.avenirsesr.portfolio.file.domain.data.FileData;
import fr.avenirsesr.portfolio.file.domain.model.File;
import java.util.Optional;

public interface FileDataMapper {
  static FileData mapFileData(File file, String defaultUrl) {
    if (file == null) return new FileData(Optional.empty(), Optional.empty(), defaultUrl);

    return new FileData(
        Optional.ofNullable(file.getId()), Optional.ofNullable(file.getFileName()), file.getUri());
  }
}
