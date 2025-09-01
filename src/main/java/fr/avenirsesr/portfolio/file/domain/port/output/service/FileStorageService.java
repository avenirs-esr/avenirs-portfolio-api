package fr.avenirsesr.portfolio.file.domain.port.output.service;

import fr.avenirsesr.portfolio.file.domain.model.shared.FileResource;
import java.io.IOException;
import java.util.UUID;

public interface FileStorageService {
  byte[] get(String path) throws IOException;

  String upload(FileResource fileResource) throws IOException;

  void delete(UUID fileId) throws IOException;
}
