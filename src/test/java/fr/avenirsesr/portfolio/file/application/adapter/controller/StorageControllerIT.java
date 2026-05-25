package fr.avenirsesr.portfolio.file.application.adapter.controller;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import fr.avenirsesr.portfolio.file.domain.model.EUserPhotoType;
import fr.avenirsesr.portfolio.file.domain.port.output.repository.ActivityBannerRepository;
import fr.avenirsesr.portfolio.file.domain.port.output.repository.FileRepository;
import fr.avenirsesr.portfolio.file.domain.port.output.service.FileStorageService;
import fr.avenirsesr.portfolio.file.domain.service.ActivityResourceServiceImpl;
import fr.avenirsesr.portfolio.file.domain.service.FileResourceServiceImpl;
import fr.avenirsesr.portfolio.shared.domain.port.input.LoggedInUserService;
import fr.avenirsesr.portfolio.shared.infrastructure.ContainerConfigurationTest;
import fr.avenirsesr.portfolio.shared.infrastructure.adapter.seeder.SeederRunner;
import fr.avenirsesr.portfolio.trace.domain.port.input.TraceService;
import fr.avenirsesr.portfolio.trace.domain.port.output.repository.TraceRepository;
import fr.avenirsesr.portfolio.user.domain.port.output.repository.StaffRepository;
import fr.avenirsesr.portfolio.user.domain.port.output.repository.StudentRepository;
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

  @Autowired private FileRepository fileRepository;
  @Autowired private TraceRepository traceRepository;
  @Autowired private StudentRepository studentRepository;
  @Autowired private StaffRepository staffRepository;
  @Autowired private TraceService traceService;
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
            new FileResourceServiceImpl(
                fileStorageService,
                fileRepository,
                traceRepository,
                staffRepository,
                studentRepository,
                loggedInUserService,
                traceService),
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
