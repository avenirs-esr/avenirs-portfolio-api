package fr.avenirsesr.portfolio.file.infrastructure.adapter.service;

import static org.junit.jupiter.api.Assertions.*;

import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import fr.avenirsesr.portfolio.file.domain.exception.FileNotFoundException;
import fr.avenirsesr.portfolio.file.domain.model.FileResource;
import fr.avenirsesr.portfolio.file.domain.model.enums.EFileType;
import fr.avenirsesr.portfolio.file.infrastructure.configuration.S3StorageProperties;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.UUID;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.DockerImageName;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;

/**
 * Exercises the adapter against a real S3 implementation.
 *
 * <p>Unlike the controller integration tests, this one does not extend {@code
 * ContainerConfigurationTest}: the adapter is instantiated directly, so booting the application
 * context and seeding the database would only slow the test down without covering anything more.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class S3FileStorageServiceIT {

  private static final String BUCKET = "avenirs-portfolio-it";
  private static final String ACCESS_KEY = "avenirs-it-access-key";
  private static final String SECRET_KEY = "avenirs-it-secret-key";
  private static final int MINIO_PORT = 9000;

  private GenericContainer<?> minio;
  private S3Client s3Client;
  private S3FileStorageService storageService;

  @BeforeAll
  void startBackend() {
    minio =
        new GenericContainer<>(DockerImageName.parse("minio/minio:RELEASE.2025-04-22T22-12-26Z"))
            .withEnv("MINIO_ROOT_USER", ACCESS_KEY)
            .withEnv("MINIO_ROOT_PASSWORD", SECRET_KEY)
            .withCommand("server", "/data")
            .withExposedPorts(MINIO_PORT)
            .waitingFor(
                Wait.forHttp("/minio/health/ready")
                    .forPort(MINIO_PORT)
                    .withStartupTimeout(Duration.ofMinutes(2)));
    minio.start();

    var properties = new S3StorageProperties();
    properties.setBucket(BUCKET);
    properties.setRegion("us-east-1");
    properties.setAccessKey(ACCESS_KEY);
    properties.setSecretKey(SECRET_KEY);

    s3Client =
        S3Client.builder()
            .endpointOverride(
                URI.create(
                    "http://%s:%d".formatted(minio.getHost(), minio.getMappedPort(MINIO_PORT))))
            .region(Region.of(properties.getRegion()))
            .credentialsProvider(
                StaticCredentialsProvider.create(
                    AwsBasicCredentials.create(ACCESS_KEY, SECRET_KEY)))
            .forcePathStyle(true)
            .build();
    s3Client.createBucket(CreateBucketRequest.builder().bucket(BUCKET).build());

    storageService = new S3FileStorageService(s3Client, properties);
  }

  @AfterAll
  void stopBackend() {
    if (s3Client != null) {
      s3Client.close();
    }
    if (minio != null) {
      minio.stop();
    }
  }

  @Test
  void shouldRoundTripAFileThroughTheBucket() {
    BddLogger.given("a file resource");
    var id = UUID.randomUUID();
    byte[] content = "Contenu du fichier de test".getBytes(StandardCharsets.UTF_8);
    var resource = new FileResource(id, "retour.pdf", EFileType.PDF, content.length, content);

    BddLogger.when("uploading it and reading it back");
    String locator = storageService.upload(resource);
    byte[] read = storageService.get(locator);

    BddLogger.then("the stored bytes should be the ones that were uploaded");
    assertEquals(id + ".pdf", locator);
    assertArrayEquals(content, read);
  }

  @Test
  void shouldReportAMissingKeyAsFileNotFound() {
    BddLogger.given("a key that was never uploaded");
    String locator = UUID.randomUUID() + ".pdf";

    BddLogger.when("reading it");
    BddLogger.then("the service should throw FileNotFoundException");
    assertThrows(FileNotFoundException.class, () -> storageService.get(locator));
  }

  @Test
  void shouldMakeAFileUnreadableOnceDeleted() {
    BddLogger.given("an uploaded file");
    var id = UUID.randomUUID();
    byte[] content = "a supprimer".getBytes(StandardCharsets.UTF_8);
    String locator =
        storageService.upload(
            new FileResource(id, "trace.pdf", EFileType.PDF, content.length, content));

    BddLogger.when("deleting it");
    storageService.delete(locator);

    BddLogger.then("reading it back should report a missing file");
    assertThrows(FileNotFoundException.class, () -> storageService.get(locator));
  }

  @Test
  void shouldSucceedWhenDeletingAKeyThatDoesNotExist() {
    BddLogger.given("a key that was never uploaded");
    String locator = UUID.randomUUID() + ".pdf";

    BddLogger.when("deleting it");
    BddLogger.then("the backend should report success, unlike the local adapter");
    assertDoesNotThrow(() -> storageService.delete(locator));
  }
}
