package fr.avenirsesr.portfolio.file.domain.port.output.service;

import fr.avenirsesr.portfolio.file.domain.exception.FileStorageException;
import fr.avenirsesr.portfolio.file.domain.model.FileResource;

/**
 * Storage backend abstraction.
 *
 * <p>Uploading returns a locator that is opaque to the domain: it is persisted as {@code File.uri}
 * and handed back to the very same adapter to read or delete the content. Each adapter picks its
 * own format — an absolute path for local storage, an object key for S3 — so the domain never has
 * to interpret it.
 */
public interface FileStorageService {
  String upload(FileResource fileResource) throws FileStorageException;

  byte[] get(String locator) throws FileStorageException;

  void delete(String locator) throws FileStorageException;
}
