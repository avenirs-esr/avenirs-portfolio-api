package fr.avenirsesr.portfolio.file.application.adapter.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.avenirsesr.portfolio.common.data.domain.model.enums.EUserCategory;
import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import fr.avenirsesr.portfolio.file.domain.model.EUserPhotoType;
import fr.avenirsesr.portfolio.shared.infrastructure.ContainerConfigurationTest;
import fr.avenirsesr.portfolio.shared.infrastructure.adapter.seeder.SeederRunner;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.test.web.reactive.server.WebTestClient;

class UserResourceControllerIT extends ContainerConfigurationTest {

  @Autowired private WebTestClient webTestClient;

  @Autowired private ObjectMapper objectMapper;

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

  @Test
  void shouldUploadUserProfilePhotoSuccessfully() {
    BddLogger.given("the /me/storage/users/{userCategory}/{photoType} endpoint");

    byte[] fileContent = "FakeImageContent".getBytes(StandardCharsets.UTF_8);

    MultipartBodyBuilder builder = new MultipartBodyBuilder();
    builder
        .part(
            "file",
            new ByteArrayResource(fileContent) {
              @Override
              public String getFilename() {
                return "profile-photo.jpg";
              }
            })
        .contentType(MediaType.IMAGE_JPEG);

    BddLogger.when("performing a MULTIPART PUT");
    BddLogger.then("it should upload user profile photo successfully");

    webTestClient
        .put()
        .uri(
            "/me/storage/users/{userCategory}/{photoType}",
            EUserCategory.STUDENT,
            EUserPhotoType.PROFILE)
        .header("X-Signed-Context", studentPayload)
        .header("X-Context-Kid", secretKey)
        .header("X-Context-Signature", studentSignature)
        .contentType(MediaType.MULTIPART_FORM_DATA)
        .bodyValue(builder.build())
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody()
        .jsonPath("$.id")
        .exists()
        .jsonPath("$.fileSize")
        .isEqualTo(fileContent.length)
        .jsonPath("$.version")
        .isEqualTo(2);
  }

  @Test
  void shouldReturnNotFoundWhenDeletingNonExistingPhoto() {
    BddLogger.given("the /me/storage/users/{fileId} endpoint");

    UUID fileId = UUID.randomUUID();

    BddLogger.when("performing a DELETE on non existing photo");
    BddLogger.then("it should return the not found status");

    webTestClient
        .delete()
        .uri("/me/storage/users/{fileId}", fileId)
        .header("X-Signed-Context", studentPayload)
        .header("X-Context-Kid", secretKey)
        .header("X-Context-Signature", studentSignature)
        .exchange()
        .expectStatus()
        .isNotFound();
  }
}
