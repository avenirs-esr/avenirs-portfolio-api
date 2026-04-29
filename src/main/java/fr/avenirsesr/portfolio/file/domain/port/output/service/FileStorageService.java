package fr.avenirsesr.portfolio.file.domain.port.output.service;

import fr.avenirsesr.portfolio.file.domain.exception.FileStorageException;
import fr.avenirsesr.portfolio.file.domain.model.shared.FileResource;
import java.util.UUID;

public interface FileStorageService {
  byte[] get(String path) throws FileStorageException;

  String upload(FileResource fileResource) throws FileStorageException;

  void delete(UUID fileId) throws FileStorageException;
}
