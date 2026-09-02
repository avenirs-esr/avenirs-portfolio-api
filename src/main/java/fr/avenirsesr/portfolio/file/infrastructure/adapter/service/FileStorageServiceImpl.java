package fr.avenirsesr.portfolio.file.infrastructure.adapter.service;

import fr.avenirsesr.portfolio.file.domain.exception.FileNotFoundException;
import fr.avenirsesr.portfolio.file.domain.exception.FileStorageException;
import fr.avenirsesr.portfolio.file.domain.model.FileResource;
import fr.avenirsesr.portfolio.file.domain.port.output.service.FileStorageService;
import fr.avenirsesr.portfolio.file.infrastructure.configuration.FileStorageConstants;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

/**
 * Stores files on the local filesystem. The locator handed back to the domain is the absolute path
 * of the written file.
 */
@Slf4j
@Component
@Primary
public class FileStorageServiceImpl implements FileStorageService {

  @Override
  public String upload(FileResource fileResource) {
    String uploadDir = System.getProperty("user.dir") + FileStorageConstants.STORAGE_PATH;

    File dir = new File(uploadDir);
    if (!dir.exists()) dir.mkdirs();

    var fileName = fileResource.id() + "." + fileResource.fileType().name().toLowerCase();

    String filePath = String.join("", uploadDir, "/", fileName);
    try (FileOutputStream fos = new FileOutputStream(filePath)) {
      fos.write(fileResource.content());
    } catch (IOException e) {
      throw new FileStorageException("Failed to upload file " + fileResource.fileName(), e);
    }

    log.info("File {} has been uploaded as {}", fileResource.fileName(), fileName);
    return filePath;
  }

  @Override
  public byte[] get(String locator) {
    File file = new File(locator);

    if (!file.exists()) {
      throw new FileNotFoundException();
    }

    try {
      return java.nio.file.Files.readAllBytes(file.toPath());
    } catch (IOException e) {
      throw new FileStorageException("Failed to read file at path " + locator, e);
    }
  }

  @Override
  public void delete(String locator) {
    File file = new File(locator);

    if (!file.exists()) {
      log.error("No file found at path {}", locator);
      throw new FileNotFoundException();
    }

    if (!file.delete()) {
      throw new FileStorageException("Failed to delete file at path " + locator, null);
    }

    log.info("File at path {} has been deleted", locator);
  }
}
