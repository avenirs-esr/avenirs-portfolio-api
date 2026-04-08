package fr.avenirsesr.portfolio.ams.application.adapter.controller;

import fr.avenirsesr.portfolio.common.language.domain.model.enums.ELanguage;
import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import fr.avenirsesr.portfolio.shared.infrastructure.ContainerConfigurationTest;
import fr.avenirsesr.portfolio.shared.infrastructure.adapter.seeder.SeederRunner;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

public class AMSControllerIT extends ContainerConfigurationTest {

  private static final String BASE_PATH = "/me/ams/view";
  private static final String VALID_STUDENT_PROGRESS_ID = "96c16cc8-4ae8-4ffe-bb37-d55d0832c36b";
  private static final String UNKNOWN_STUDENT_PROGRESS_ID = "00000000-0000-0000-0000-000000000000";

  private final ELanguage language = ELanguage.FRENCH;

  @Autowired private WebTestClient webTestClient;

  @Value("${hmac.secret-key}")
  private String secretKey;

  @Value("${user.student.payload}")
  private String studentPayload;

  @Value("${user.staff.payload}")
  private String staffPayload;

  @Value("${user.unknown.payload}")
  private String unknownUserPayload;

  @Value("${user.student.signature}")
  private String studentSignature;

  @Value("${user.staff.signature}")
  private String staffSignature;

  @Value("${user.unknown.signature}")
  private String unknownUserSignature;

  @BeforeAll
  void setup(@Autowired SeederRunner seederRunner) {
    seederRunner.run();
  }

  @Test
  void shouldReturnAmsForStudent() {
    BddLogger.given("the " + BASE_PATH + " endpoint");
    BddLogger.when("performing a GET with a valid student");
    BddLogger.then("it should return paged ams");

    webTestClient
        .get()
        .uri(
            uriBuilder ->
                uriBuilder
                    .path(BASE_PATH)
                    .queryParam("studentProgressId", VALID_STUDENT_PROGRESS_ID)
                    .queryParam("page", "0")
                    .queryParam("pageSize", "10")
                    .build())
        .header("Accept-Language", language.getCode())
        .header("X-Signed-Context", studentPayload)
        .header("X-Context-Kid", secretKey)
        .header("X-Context-Signature", studentSignature)
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody()
        .jsonPath("$.data")
        .isArray()
        .jsonPath("$.page")
        .exists();
  }

  @Test
  void shouldReturn404IfUserIsUnknown() {
    BddLogger.given("the " + BASE_PATH + " endpoint");
    BddLogger.when("performing a GET with an unknown user");
    BddLogger.then("it should return a 404");

    webTestClient
        .get()
        .uri(
            uriBuilder ->
                uriBuilder
                    .path(BASE_PATH)
                    .queryParam("studentProgressId", VALID_STUDENT_PROGRESS_ID)
                    .build())
        .header("Accept-Language", language.getCode())
        .header("X-Signed-Context", unknownUserPayload)
        .header("X-Context-Kid", secretKey)
        .header("X-Context-Signature", unknownUserSignature)
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectStatus()
        .isNotFound()
        .expectBody()
        .jsonPath("$.message")
        .isEqualTo("User not found")
        .jsonPath("$.code")
        .isEqualTo("USER_NOT_FOUND");
  }

  @Test
  void shouldReturn403IfUserIsNotStudent() {
    BddLogger.given("the " + BASE_PATH + " endpoint");
    BddLogger.when("performing a GET with a non student user");
    BddLogger.then("it should return a 403");

    webTestClient
        .get()
        .uri(
            uriBuilder ->
                uriBuilder
                    .path(BASE_PATH)
                    .queryParam("studentProgressId", VALID_STUDENT_PROGRESS_ID)
                    .build())
        .header("Accept-Language", language.getCode())
        .header("X-Signed-Context", staffPayload)
        .header("X-Context-Kid", secretKey)
        .header("X-Context-Signature", staffSignature)
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectStatus()
        .isForbidden()
        .expectBody()
        .jsonPath("$.code")
        .isEqualTo("USER_IS_NOT_STUDENT_EXCEPTION");
  }

  @Test
  void shouldReturn400IfStudentProgressIdMissing() {
    BddLogger.given("the " + BASE_PATH + " endpoint");
    BddLogger.when("performing a GET without student progress ID");
    BddLogger.then("it should return a 400");

    webTestClient
        .get()
        .uri(
            uriBuilder ->
                uriBuilder
                    .path(BASE_PATH)
                    .queryParam("page", "0")
                    .queryParam("pageSize", "10")
                    .build())
        .header("Accept-Language", language.getCode())
        .header("X-Signed-Context", studentPayload)
        .header("X-Context-Kid", secretKey)
        .header("X-Context-Signature", studentSignature)
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectStatus()
        .isBadRequest();
  }
}
