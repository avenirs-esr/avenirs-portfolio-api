package fr.avenirsesr.portfolio.file.infrastructure.adapter.service;

import fr.avenirsesr.portfolio.file.domain.exception.FileNotFoundException;
import fr.avenirsesr.portfolio.file.domain.exception.FileStorageException;
import fr.avenirsesr.portfolio.file.domain.model.FileResource;
import fr.avenirsesr.portfolio.file.domain.port.output.service.FileStorageService;
import fr.avenirsesr.portfolio.file.infrastructure.configuration.FileStorageConstants;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

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
  public byte[] get(String path) {
    File file = new File(path);

    if (!file.exists()) {
      throw new FileNotFoundException();
    }

    try {
      return java.nio.file.Files.readAllBytes(file.toPath());
    } catch (IOException e) {
      throw new FileStorageException("Failed to read file at path " + path, e);
    }
  }

  @Override
  public void delete(UUID id) {
    String uploadDir = System.getProperty("user.dir") + FileStorageConstants.STORAGE_PATH;

    File dir = new File(uploadDir);
    if (!dir.exists()) {
      throw new IllegalStateException("File storage directory does not exist");
    }

    File[] matchingFiles = dir.listFiles((d, name) -> name.startsWith(id.toString() + "."));
    if (matchingFiles == null || matchingFiles.length == 0) {
      log.error("No file with id {} found", id);
      throw new FileNotFoundException();
    }

    File fileToDelete = matchingFiles[0];
    if (!fileToDelete.delete()) {
      throw new FileStorageException("Failed to delete file with id " + id, null);
    }
    log.info("File with id {} has been deleted", id);
  }

  @Override
  public void deleteByPath(String path) {
    File file = new File(path);

    if (!file.exists()) {
      log.error("No file found at path {}", path);
      throw new FileNotFoundException();
    }

    if (!file.delete()) {
      throw new FileStorageException("Failed to delete file at path " + path, null);
    }

    log.info("File at path {} has been deleted", path);
  }
}
