package fr.avenirsesr.portfolio.file.infrastructure.adapter.service;

import fr.avenirsesr.portfolio.file.domain.exception.FileNotFoundException;
import fr.avenirsesr.portfolio.file.domain.model.shared.FileResource;
import fr.avenirsesr.portfolio.file.domain.port.output.service.FileStorageService;
import fr.avenirsesr.portfolio.file.infrastructure.configuration.FileStorageConstants;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
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
}
