package fr.avenirsesr.portfolio.file.infrastructure.adapter.service;

import fr.avenirsesr.portfolio.file.domain.exception.FileNotFoundException;
import fr.avenirsesr.portfolio.file.domain.model.shared.FileResource;
import fr.avenirsesr.portfolio.file.domain.port.output.service.FileStorageService;
import fr.avenirsesr.portfolio.file.infrastructure.configuration.FileStorageConstants;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class FileStorageServiceImpl implements FileStorageService {

  @Override
  public String upload(FileResource fileResource) throws IOException {
    String uploadDir = System.getProperty("user.dir") + FileStorageConstants.STORAGE_PATH;

    File dir = new File(uploadDir);
    if (!dir.exists()) dir.mkdirs();

    var fileName = fileResource.id() + "." + fileResource.fileType().name().toLowerCase();

    String filePath = String.join("", uploadDir, "/", fileName);
    try (FileOutputStream fos = new FileOutputStream(filePath)) {
      fos.write(fileResource.content());
    }

    log.info("File {} has been uploaded as {}", fileResource.fileName(), fileName);
    return filePath;
  }

  @Override
  public byte[] get(String path) throws IOException {
    File file = new File(path);

    if (!file.exists()) {
      throw new FileNotFoundException();
    }

    return java.nio.file.Files.readAllBytes(file.toPath());
  }

  @Override
  public void delete(UUID id) throws IOException {
    String uploadDir = System.getProperty("user.dir") + FileStorageConstants.STORAGE_PATH;

    File dir = new File(uploadDir);
    if (!dir.exists()) {
      throw new FileNotFoundException();
    }

    // Search for the file with the given id (extension is unknown, so we check all)
    File[] matchingFiles = dir.listFiles((d, name) -> name.startsWith(id.toString() + "."));
    if (matchingFiles == null || matchingFiles.length == 0) {
      throw new FileNotFoundException();
    }

    File fileToDelete = matchingFiles[0];
    if (fileToDelete.delete()) {
      log.info("File with id {} has been deleted", id);
    } else {
      throw new IOException("Failed to delete file with id " + id);
    }
  }
}
