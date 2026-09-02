package fr.avenirsesr.portfolio.file.infrastructure.adapter.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import fr.avenirsesr.portfolio.file.domain.port.output.service.FileStorageService;
import fr.avenirsesr.portfolio.file.infrastructure.configuration.S3StorageProperties;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import software.amazon.awssdk.services.s3.S3Client;

/**
 * Guards the file.storage.type switch: the two adapters are both marked primary, so exactly one of
 * them must ever be registered.
 */
class FileStorageBackendSelectionTest {

  private final ApplicationContextRunner contextRunner =
      new ApplicationContextRunner()
          .withBean(S3Client.class, () -> mock(S3Client.class))
          .withBean(S3StorageProperties.class, S3StorageProperties::new)
          .withUserConfiguration(FileStorageServiceImpl.class, S3FileStorageService.class);

  @Test
  void shouldFallBackToLocalStorageWhenTheTypeIsNotSet() {
    BddLogger.given("no file.storage.type property");
    BddLogger.when("the context starts");
    BddLogger.then("the local adapter should be the only storage service");

    contextRunner.run(
        context -> {
          assertThat(context).hasSingleBean(FileStorageService.class);
          assertThat(context.getBean(FileStorageService.class))
              .isInstanceOf(FileStorageServiceImpl.class);
        });
  }

  @Test
  void shouldUseLocalStorageWhenExplicitlySelected() {
    BddLogger.given("file.storage.type=local");
    BddLogger.when("the context starts");
    BddLogger.then("the local adapter should be the only storage service");

    contextRunner
        .withPropertyValues("file.storage.type=local")
        .run(
            context -> {
              assertThat(context).hasSingleBean(FileStorageService.class);
              assertThat(context.getBean(FileStorageService.class))
                  .isInstanceOf(FileStorageServiceImpl.class);
            });
  }

  @Test
  void shouldUseS3WhenSelected() {
    BddLogger.given("file.storage.type=s3");
    BddLogger.when("the context starts");
    BddLogger.then("the S3 adapter should be the only storage service");

    contextRunner
        .withPropertyValues("file.storage.type=s3")
        .run(
            context -> {
              assertThat(context).hasSingleBean(FileStorageService.class);
              assertThat(context.getBean(FileStorageService.class))
                  .isInstanceOf(S3FileStorageService.class);
            });
  }
}
