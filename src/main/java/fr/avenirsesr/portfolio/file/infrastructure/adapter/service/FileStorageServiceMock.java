package fr.avenirsesr.portfolio.file.infrastructure.adapter.service;

import fr.avenirsesr.portfolio.file.domain.exception.FileNotFoundException;
import fr.avenirsesr.portfolio.file.domain.exception.FileStorageException;
import fr.avenirsesr.portfolio.file.domain.model.FileResource;
import fr.avenirsesr.portfolio.file.domain.model.enums.EFileType;
import fr.avenirsesr.portfolio.file.domain.port.output.service.FileStorageService;
import fr.avenirsesr.portfolio.file.infrastructure.configuration.FileStorageConstants;
import java.io.File;
import java.io.IOException;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Qualifier("seederFileStorageService")
public class FileStorageServiceMock implements FileStorageService {
  private String placeholderPath(EFileType fileType) {
    return "/workspace/app%s/%s.%s"
        .formatted(
            FileStorageConstants.STORAGE_PATH,
            FileStorageConstants.PLACEHOLDER_FILE_UUID,
            fileType.name().toLowerCase());
  }

  @Override
  public String upload(FileResource fileResource) {
    log.debug(
        "Mocking upload of file resource {} and return placeholder file", fileResource.fileName());
    return placeholderPath(fileResource.fileType());
  }

  @Override
  public byte[] get(String path) {
    log.debug("Mocking get file resource {} return placeholder file", path);
    File file = new File(placeholderPath(EFileType.PNG));

    if (!file.exists()) {
      throw new FileNotFoundException();
    }

    try {
      return java.nio.file.Files.readAllBytes(file.toPath());
    } catch (IOException e) {
      throw new FileStorageException("Failed to read placeholder file", e);
    }
  }

  @Override
  public void delete(UUID id) {
    log.debug("Mocking delete file resource {}", id);
  }

  @Override
  public void deleteByPath(String path) throws FileStorageException {
    log.debug("Mocking delete file resource by path {}", path);
  }
}
