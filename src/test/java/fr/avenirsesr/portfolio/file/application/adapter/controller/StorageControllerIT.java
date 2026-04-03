package fr.avenirsesr.portfolio.file.application.adapter.controller;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import fr.avenirsesr.portfolio.file.domain.model.EUserPhotoType;
import fr.avenirsesr.portfolio.file.domain.port.output.repository.ActivityBannerRepository;
import fr.avenirsesr.portfolio.file.domain.port.output.service.FileStorageService;
import fr.avenirsesr.portfolio.file.domain.service.ActivityResourceServiceImpl;
import fr.avenirsesr.portfolio.file.domain.service.UserResourceServiceImpl;
import fr.avenirsesr.portfolio.file.infrastructure.adapter.repository.UserPhotoDatabaseRepository;
import fr.avenirsesr.portfolio.shared.domain.port.input.LoggedInUserService;
import fr.avenirsesr.portfolio.shared.infrastructure.ContainerConfigurationTest;
import fr.avenirsesr.portfolio.shared.infrastructure.adapter.seeder.SeederRunner;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

class StorageControllerIT extends ContainerConfigurationTest {

  private static final String DEFAULT_BASE_PATH = "/storage/users/default/{photoType}";

  private WebTestClient webTestClient;

  @Mock private FileStorageService fileStorageService;

  @Autowired private UserPhotoDatabaseRepository userPhotoDatabaseRepository;
  @Autowired private ActivityBannerRepository activityBannerRepository;
  @Autowired private LoggedInUserService loggedInUserService;

  @Value("${hmac.secret-key}")
  private String secretKey;

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
            new UserResourceServiceImpl(
                fileStorageService, userPhotoDatabaseRepository, loggedInUserService),
            new ActivityResourceServiceImpl(
                fileStorageService, loggedInUserService, activityBannerRepository),
            fileStorageService);

    webTestClient =
        WebTestClient.bindToController(storageController).configureClient().baseUrl("").build();
  }

  @Test
  void shouldGetDefaultUserProfilePhoto() throws IOException {
    BddLogger.given("the " + DEFAULT_BASE_PATH + " endpoint");

    when(fileStorageService.get(anyString()))
        .thenReturn("Contenu du fichier de test".getBytes(StandardCharsets.UTF_8));

    BddLogger.when("performing a GET with a PROFILE photo type");
    BddLogger.then("it should return the default user profile photo");

    webTestClient
        .get()
        .uri(DEFAULT_BASE_PATH, EUserPhotoType.PROFILE)
        .header("X-Signed-Context", studentPayload)
        .header("X-Context-Kid", secretKey)
        .header("X-Context-Signature", studentSignature)
        .exchange()
        .expectStatus()
        .isOk()
        .expectHeader()
        .contentTypeCompatibleWith(MediaType.IMAGE_PNG);
  }

  @Test
  void shouldGetDefaultUserCoverPhoto() throws IOException {
    BddLogger.given("the " + DEFAULT_BASE_PATH + " endpoint");

    when(fileStorageService.get(anyString()))
        .thenReturn("Contenu du fichier de test".getBytes(StandardCharsets.UTF_8));

    BddLogger.when("performing a GET with a COVER photo type");
    BddLogger.then("it should return the default user cover photo");

    webTestClient
        .get()
        .uri(DEFAULT_BASE_PATH, EUserPhotoType.COVER)
        .header("X-Signed-Context", studentPayload)
        .header("X-Context-Kid", secretKey)
        .header("X-Context-Signature", studentSignature)
        .exchange()
        .expectStatus()
        .isOk()
        .expectHeader()
        .contentTypeCompatibleWith(MediaType.IMAGE_PNG);
  }
}
