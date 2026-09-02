package fr.avenirsesr.portfolio.file.infrastructure.adapter.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import fr.avenirsesr.portfolio.file.domain.exception.FileNotFoundException;
import fr.avenirsesr.portfolio.file.domain.exception.FileStorageException;
import fr.avenirsesr.portfolio.file.domain.model.FileResource;
import fr.avenirsesr.portfolio.file.domain.model.enums.EFileType;
import fr.avenirsesr.portfolio.file.infrastructure.configuration.S3StorageProperties;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import software.amazon.awssdk.services.s3.model.S3Exception;

class S3FileStorageServiceTest {

  private static final String BUCKET = "avenirs-portfolio-test";

  @Mock private S3Client s3Client;

  private S3FileStorageService storageService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    var properties = new S3StorageProperties();
    properties.setBucket(BUCKET);
    storageService = new S3FileStorageService(s3Client, properties);
  }

  private FileResource aFileResource(UUID id, EFileType fileType) {
    return new FileResource(
        id, "consigne.pdf", fileType, 3L, "abc".getBytes(StandardCharsets.UTF_8));
  }

  @Nested
  class GivenAnS3StorageService {

    @BeforeEach
    void setupGiven() {
      BddLogger.given("an S3 storage service");
    }

    @Nested
    class WhenUploadingAFile {

      UUID fileId;

      @BeforeEach
      void setupWhen() {
        BddLogger.when("uploading a file");
        fileId = UUID.randomUUID();
      }

      @Test
      void thenItShouldStoreTheObjectUnderTheFileIdKey() {
        BddLogger.then("the object should be stored in the configured bucket under {id}.{type}");
        when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
            .thenReturn(PutObjectResponse.builder().build());

        String locator = storageService.upload(aFileResource(fileId, EFileType.PDF));

        assertEquals(fileId + ".pdf", locator);

        ArgumentCaptor<PutObjectRequest> captor = ArgumentCaptor.forClass(PutObjectRequest.class);
        verify(s3Client).putObject(captor.capture(), any(RequestBody.class));
        assertEquals(BUCKET, captor.getValue().bucket());
        assertEquals(fileId + ".pdf", captor.getValue().key());
      }

      @Test
      void thenItShouldTagTheObjectWithItsContentType() {
        BddLogger.then("the object content type should be the file type mime type");
        when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
            .thenReturn(PutObjectResponse.builder().build());

        storageService.upload(aFileResource(fileId, EFileType.PNG));

        ArgumentCaptor<PutObjectRequest> captor = ArgumentCaptor.forClass(PutObjectRequest.class);
        verify(s3Client).putObject(captor.capture(), any(RequestBody.class));
        assertEquals("image/png", captor.getValue().contentType());
      }

      @Test
      void thenItShouldWrapBackendErrorsIntoAFileStorageException() {
        BddLogger.and("the backend rejects the request");
        BddLogger.then("the service should throw FileStorageException");
        when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
            .thenThrow(S3Exception.builder().message("bucket is full").build());

        assertThrows(
            FileStorageException.class,
            () -> storageService.upload(aFileResource(fileId, EFileType.PDF)));
      }
    }

    @Nested
    class WhenReadingAFile {

      @BeforeEach
      void setupWhen() {
        BddLogger.when("reading a file");
      }

      @Test
      void thenItShouldReturnTheObjectContent() {
        BddLogger.then("the object bytes should be returned");
        byte[] content = "hello".getBytes(StandardCharsets.UTF_8);
        when(s3Client.getObjectAsBytes(any(GetObjectRequest.class)))
            .thenReturn(ResponseBytes.fromByteArray(GetObjectResponse.builder().build(), content));

        byte[] read = storageService.get("some-key.pdf");

        assertArrayEquals(content, read);

        ArgumentCaptor<GetObjectRequest> captor = ArgumentCaptor.forClass(GetObjectRequest.class);
        verify(s3Client).getObjectAsBytes(captor.capture());
        assertEquals(BUCKET, captor.getValue().bucket());
        assertEquals("some-key.pdf", captor.getValue().key());
      }

      @Test
      void thenItShouldTranslateAMissingKeyIntoFileNotFound() {
        BddLogger.and("the key does not exist");
        BddLogger.then("the service should throw FileNotFoundException");
        when(s3Client.getObjectAsBytes(any(GetObjectRequest.class)))
            .thenThrow(NoSuchKeyException.builder().message("no such key").build());

        assertThrows(FileNotFoundException.class, () -> storageService.get("missing.pdf"));
      }

      @Test
      void thenItShouldWrapOtherBackendErrorsIntoAFileStorageException() {
        BddLogger.and("the backend denies the request");
        BddLogger.then("the service should throw FileStorageException");
        when(s3Client.getObjectAsBytes(any(GetObjectRequest.class)))
            .thenThrow(S3Exception.builder().message("access denied").build());

        assertThrows(FileStorageException.class, () -> storageService.get("forbidden.pdf"));
      }
    }

    @Nested
    class WhenDeletingAFile {

      @BeforeEach
      void setupWhen() {
        BddLogger.when("deleting a file");
      }

      @Test
      void thenItShouldDeleteTheObjectFromTheConfiguredBucket() {
        BddLogger.then("the object should be deleted under the given key");

        storageService.delete("some-key.pdf");

        ArgumentCaptor<DeleteObjectRequest> captor =
            ArgumentCaptor.forClass(DeleteObjectRequest.class);
        verify(s3Client).deleteObject(captor.capture());
        assertEquals(BUCKET, captor.getValue().bucket());
        assertEquals("some-key.pdf", captor.getValue().key());
      }

      @Test
      void thenItShouldWrapBackendErrorsIntoAFileStorageException() {
        BddLogger.and("the backend denies the request");
        BddLogger.then("the service should throw FileStorageException");
        when(s3Client.deleteObject(any(DeleteObjectRequest.class)))
            .thenThrow(S3Exception.builder().message("access denied").build());

        assertThrows(FileStorageException.class, () -> storageService.delete("forbidden.pdf"));
      }
    }
  }
}
