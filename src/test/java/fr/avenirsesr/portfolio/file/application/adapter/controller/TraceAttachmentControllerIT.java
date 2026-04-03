package fr.avenirsesr.portfolio.file.application.adapter.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import fr.avenirsesr.portfolio.shared.infrastructure.ContainerConfigurationTest;
import fr.avenirsesr.portfolio.shared.infrastructure.adapter.seeder.SeederRunner;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.test.web.reactive.server.WebTestClient;

class TraceAttachmentControllerIT extends ContainerConfigurationTest {

  private static final String BASE_PATH = "/me/storage/traces/{traceId}";

  @Autowired private WebTestClient webTestClient;

  @Autowired private ObjectMapper objectMapper;

  @Value("${hmac.secret-key}")
  private String secretKey;

  @Value("${user.student.payload}")
  private String studentPayload;

  @Value("${user.student.signature}")
  private String studentSignature;

  @Value("${file.storage.local-path}")
  private String storagePath;

  @BeforeAll
  void setup(@Autowired SeederRunner seederRunner) {
    seederRunner.run();
  }

  @AfterEach
  void cleanupStorageFolder() throws Exception {
    var folder = Path.of(storagePath);
    if (Files.exists(folder)) {
      Files.list(folder).forEach(path -> path.toFile().delete());
    }
  }

  @Test
  void shouldUploadAttachmentSuccessfully() {
    BddLogger.given("the " + BASE_PATH + " endpoint");

    UUID existingTraceId = UUID.fromString("efb1f0ce-e531-49af-8031-949f3d68b354");

    byte[] fileContent = "Contenu du fichier de test".getBytes(StandardCharsets.UTF_8);

    MultipartBodyBuilder builder = new MultipartBodyBuilder();
    builder
        .part(
            "file",
            new ByteArrayResource(fileContent) {
              @Override
              public String getFilename() {
                return "test-file.txt";
              }
            })
        .contentType(MediaType.TEXT_PLAIN);

    BddLogger.when("performing a MULTIPART with a correct attachment");
    BddLogger.then("it should successfully upload the attachment");

    webTestClient
        .post()
        .uri(BASE_PATH, existingTraceId)
        .header("X-Signed-Context", studentPayload)
        .header("X-Context-Kid", secretKey)
        .header("X-Context-Signature", studentSignature)
        .contentType(MediaType.MULTIPART_FORM_DATA)
        .bodyValue(builder.build())
        .exchange()
        .expectStatus()
        .isCreated()
        .expectBody()
        .jsonPath("$.id")
        .exists()
        .jsonPath("$.fileName")
        .isEqualTo("test-file.txt")
        .jsonPath("$.fileSize")
        .isEqualTo(fileContent.length);
  }

  @Test
  void shouldReturn404WhenTraceNotFound() {
    BddLogger.given("the " + BASE_PATH + " endpoint");

    UUID unknownTraceId = UUID.randomUUID();

    MultipartBodyBuilder builder = new MultipartBodyBuilder();
    builder
        .part(
            "file",
            new ByteArrayResource("Contenu".getBytes()) {
              @Override
              public String getFilename() {
                return "test-file.txt";
              }
            })
        .contentType(MediaType.TEXT_PLAIN);

    BddLogger.when("performing a MULTIPART with an unknown trace ID");
    BddLogger.then("it should return 404");

    webTestClient
        .post()
        .uri(BASE_PATH, unknownTraceId)
        .header("X-Signed-Context", studentPayload)
        .header("X-Context-Kid", secretKey)
        .header("X-Context-Signature", studentSignature)
        .contentType(MediaType.MULTIPART_FORM_DATA)
        .bodyValue(builder.build())
        .exchange()
        .expectStatus()
        .isNotFound()
        .expectBody()
        .jsonPath("$.code")
        .isEqualTo("TRACE_NOT_FOUND");
  }

  @Test
  void shouldReturn403WhenUserNotAuthorized() {
    BddLogger.given("the " + BASE_PATH + " endpoint");

    UUID traceIdNotOwnedByUser = UUID.fromString("4b02b225-998a-4996-be52-8d9b2a5ab327");

    MultipartBodyBuilder builder = new MultipartBodyBuilder();
    builder
        .part(
            "file",
            new ByteArrayResource("Contenu".getBytes()) {
              @Override
              public String getFilename() {
                return "test-file.txt";
              }
            })
        .contentType(MediaType.TEXT_PLAIN);

    BddLogger.when("performing a MULTIPART with a not authorized user");
    BddLogger.then("it should return 403");

    webTestClient
        .post()
        .uri(BASE_PATH, traceIdNotOwnedByUser)
        .header("X-Signed-Context", studentPayload)
        .header("X-Context-Kid", secretKey)
        .header("X-Context-Signature", studentSignature)
        .contentType(MediaType.MULTIPART_FORM_DATA)
        .bodyValue(builder.build())
        .exchange()
        .expectStatus()
        .isForbidden()
        .expectBody()
        .jsonPath("$.code")
        .isEqualTo("USER_NOT_AUTHORIZED");
  }
}
