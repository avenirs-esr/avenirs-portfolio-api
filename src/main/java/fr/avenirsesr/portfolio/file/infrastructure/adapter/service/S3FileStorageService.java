package fr.avenirsesr.portfolio.file.infrastructure.adapter.service;

import fr.avenirsesr.portfolio.file.domain.exception.FileNotFoundException;
import fr.avenirsesr.portfolio.file.domain.exception.FileStorageException;
import fr.avenirsesr.portfolio.file.domain.model.FileResource;
import fr.avenirsesr.portfolio.file.domain.port.output.service.FileStorageService;
import fr.avenirsesr.portfolio.file.infrastructure.configuration.S3StorageProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

/**
 * Stores files in an S3-compatible bucket. The locator handed back to the domain is the object key.
 */
@Slf4j
@Component
@Primary
@RequiredArgsConstructor
@ConditionalOnProperty(name = "file.storage.type", havingValue = "s3")
public class S3FileStorageService implements FileStorageService {
  private final S3Client s3Client;
  private final S3StorageProperties properties;

  @Override
  public String upload(FileResource fileResource) {
    var key = fileResource.id() + "." + fileResource.fileType().name().toLowerCase();

    try {
      s3Client.putObject(
          PutObjectRequest.builder()
              .bucket(properties.getBucket())
              .key(key)
              // Stored alongside the object so a client reading it back is told what it is.
              .contentType(fileResource.fileType().getMimeType())
              .build(),
          RequestBody.fromBytes(fileResource.content()));
    } catch (S3Exception e) {
      throw new FileStorageException("Failed to upload file " + fileResource.fileName(), e);
    }

    log.info("File {} has been uploaded as {}", fileResource.fileName(), key);
    return key;
  }

  @Override
  public byte[] get(String locator) {
    try {
      ResponseBytes<?> object =
          s3Client.getObjectAsBytes(
              GetObjectRequest.builder().bucket(properties.getBucket()).key(locator).build());
      return object.asByteArray();
    } catch (NoSuchKeyException e) {
      log.error("No object found for key {}", locator);
      throw new FileNotFoundException();
    } catch (S3Exception e) {
      throw new FileStorageException("Failed to read object with key " + locator, e);
    }
  }

  /**
   * Deletes the object. S3 answers successfully for a key that does not exist, so unlike the local
   * adapter this never reports a missing file.
   */
  @Override
  public void delete(String locator) {
    try {
      s3Client.deleteObject(
          DeleteObjectRequest.builder().bucket(properties.getBucket()).key(locator).build());
    } catch (S3Exception e) {
      throw new FileStorageException("Failed to delete object with key " + locator, e);
    }

    log.info("Object with key {} has been deleted", locator);
  }
}
