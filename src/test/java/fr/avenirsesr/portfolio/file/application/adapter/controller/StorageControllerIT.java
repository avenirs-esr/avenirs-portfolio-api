package fr.avenirsesr.portfolio.file.application.adapter.controller;

import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import fr.avenirsesr.portfolio.file.domain.port.output.repository.FileRepository;
import fr.avenirsesr.portfolio.file.domain.port.output.service.FileStorageService;
import fr.avenirsesr.portfolio.file.domain.service.FileResourceServiceImpl;
import fr.avenirsesr.portfolio.shared.domain.port.input.LoggedInUserService;
import fr.avenirsesr.portfolio.shared.infrastructure.ContainerConfigurationTest;
import fr.avenirsesr.portfolio.shared.infrastructure.adapter.seeder.SeederRunner;
import java.io.IOException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

class StorageControllerIT extends ContainerConfigurationTest {

  private static final String DEFAULT_BASE_PATH = "/storage/default";
  private static final String DEFAULT_COVER = DEFAULT_BASE_PATH + "/cover-picture";
  private static final String DEFAULT_PROFILE = DEFAULT_BASE_PATH + "/profile-picture";

  private WebTestClient webTestClient;

  @Mock private FileStorageService fileStorageService;

  @Autowired private FileRepository fileRepository;
  @Autowired private LoggedInUserService loggedInUserService;

  @Value("${user.student.payload}")
  private String studentPayload;

  @Value("${user.student.signature}")
  private String studentSignature;

  @BeforeAll
  void setup(@Autowired SeederRunner seederRunner) {
    seederRunner.run();
  }

  @BeforeEach
  void init() {
    MockitoAnnotations.openMocks(this);

    StorageController storageController =
        new StorageController(
            new FileResourceServiceImpl(fileStorageService, fileRepository, loggedInUserService),
            new DefaultResourceLoader());

    webTestClient =
        WebTestClient.bindToController(storageController).configureClient().baseUrl("").build();
  }

  @Test
  void shouldGetDefaultUserProfilePhoto() {
    BddLogger.given("the " + DEFAULT_PROFILE + " endpoint");

    BddLogger.when("performing a GET with a PROFILE photo type");
    BddLogger.then("it should return the default user profile photo");

    webTestClient
        .get()
        .uri(DEFAULT_PROFILE)
        .header("X-Signed-Context", studentPayload)
        .header("X-Context-Signature", studentSignature)
        .exchange()
        .expectStatus()
        .isOk()
        .expectHeader()
        .contentTypeCompatibleWith(MediaType.IMAGE_PNG);
  }

  @Test
  void shouldGetDefaultUserCoverPhoto() throws IOException {
    BddLogger.given("the " + DEFAULT_COVER + " endpoint");

    BddLogger.when("performing a GET with a COVER photo type");
    BddLogger.then("it should return the default user cover photo");

    webTestClient
        .get()
        .uri(DEFAULT_COVER)
        .header("X-Signed-Context", studentPayload)
        .header("X-Context-Signature", studentSignature)
        .exchange()
        .expectStatus()
        .isOk()
        .expectHeader()
        .contentTypeCompatibleWith(MediaType.IMAGE_PNG);
  }
}
