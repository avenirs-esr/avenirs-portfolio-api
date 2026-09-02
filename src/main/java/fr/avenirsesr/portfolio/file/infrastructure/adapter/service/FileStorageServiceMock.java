package fr.avenirsesr.portfolio.file.infrastructure.adapter.service;

import fr.avenirsesr.portfolio.file.domain.exception.FileNotFoundException;
import fr.avenirsesr.portfolio.file.domain.exception.FileStorageException;
import fr.avenirsesr.portfolio.file.domain.model.FileResource;
import fr.avenirsesr.portfolio.file.domain.model.enums.EFileType;
import fr.avenirsesr.portfolio.file.domain.port.output.service.FileStorageService;
import fr.avenirsesr.portfolio.file.infrastructure.configuration.FileStorageConstants;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Qualifier("seederFileStorageService")
public class FileStorageServiceMock implements FileStorageService {
  private String placeholderPath(String root, EFileType fileType) {
    return "%s%s/%s.%s"
        .formatted(
            root,
            FileStorageConstants.STORAGE_PATH,
            FileStorageConstants.PLACEHOLDER_FILE_UUID,
            fileType.name().toLowerCase());
  }

  @Override
  public String upload(FileResource fileResource) {
    log.debug(
        "Mocking upload of file resource {} and return placeholder file", fileResource.fileName());
    return resolvePlaceholderPath(fileResource.fileType());
  }

  private String resolvePlaceholderPath(EFileType fileType) {
    String dockerPath = placeholderPath("/workspace/app", fileType);
    if (ensurePlaceholderExists(dockerPath)) {
      return dockerPath;
    }
    String localPath = placeholderPath(System.getProperty("user.dir"), fileType);
    if (ensurePlaceholderExists(localPath)) {
      return localPath;
    }
    throw new FileStorageException("Failed to create placeholder file for type " + fileType, null);
  }

  private boolean ensurePlaceholderExists(String path) {
    File file = new File(path);
    if (file.exists()) {
      return true;
    }
    File parent = file.getParentFile();
    if (parent == null || (!parent.exists() && !parent.mkdirs() && !parent.exists())) {
      return false;
    }
    try (FileOutputStream fos = new FileOutputStream(file)) {
      fos.write("placeholder".getBytes());
      return true;
    } catch (IOException e) {
      log.debug("Could not create placeholder file at path {}", path, e);
      return false;
    }
  }

  @Override
  public byte[] get(String locator) {
    log.debug("Mocking get file resource {} return placeholder file", locator);
    File file = new File(locator);

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
  public void delete(String locator) {
    log.debug("Mocking delete file resource {}", locator);
  }
}
