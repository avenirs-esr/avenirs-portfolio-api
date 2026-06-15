package fr.avenirsesr.portfolio.student.progress.declared.activity.application.adapter.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import fr.avenirsesr.portfolio.shared.infrastructure.ContainerConfigurationTest;
import fr.avenirsesr.portfolio.shared.infrastructure.adapter.seeder.SeederRunner;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.transaction.annotation.Transactional;

public class DeclaredActivityControllerIT extends ContainerConfigurationTest {

  private static final String BASE_PATH = "/me/activity-progress";

  @Autowired private WebTestClient webTestClient;
  @Autowired private ObjectMapper objectMapper;

  @Value("${hmac.secret-key}")
  private String secretKey;

  @Value("${user.student.payload}")
  private String studentPayload;

  @Value("${user.student.signature}")
  private String studentSignature;

  @Value("${user.second.student.payload}")
  private String otherStudentPayload;

  @Value("${user.second.student.signature}")
  private String otherStudentSignature;

  private final String notFoundId = "00000000-0000-0000-0000-000000000000";
  private final String activityId = "3f7c9a2e-5d44-4b7a-9c6f-2a6e8e91b1a1";
  private String declaredActivityId;

  @BeforeAll
  void setup(@Autowired SeederRunner seederRunner) throws Exception {
    seederRunner.run();
    declaredActivityId = createActivityAndGetId();
  }

  private String createActivityAndGetId() throws Exception {
    String response =
        webTestClient
            .post()
            .uri(BASE_PATH + "/subscribe/" + activityId)
            .header("X-Signed-Context", studentPayload)
            .header("X-Context-Kid", secretKey)
            .header("X-Context-Signature", studentSignature)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue("{}")
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(String.class)
            .returnResult()
            .getResponseBody();

    JsonNode jsonNode = objectMapper.readTree(response);
    return jsonNode.get("createdItemId").asText();
  }

  @Test
  @Transactional
  void shouldGetDeclaredActivities() {
    BddLogger.given("declared activities exist");

    BddLogger.when("getting declared activities");

    BddLogger.then("it should return a list");

    webTestClient
        .get()
        .uri(BASE_PATH)
        .header("X-Signed-Context", studentPayload)
        .header("X-Context-Kid", secretKey)
        .header("X-Context-Signature", studentSignature)
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody()
        .jsonPath("$.data")
        .isArray();
  }

  @Test
  @Transactional
  void shouldGetDeclaredActivityDetails() throws Exception {
    String id = declaredActivityId;

    BddLogger.given("a declared activity");

    BddLogger.when("getting details");

    BddLogger.then("it should return the activity");

    webTestClient
        .get()
        .uri(BASE_PATH + "/" + id)
        .header("X-Signed-Context", studentPayload)
        .header("X-Context-Kid", secretKey)
        .header("X-Context-Signature", studentSignature)
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody()
        .jsonPath("$.id")
        .isEqualTo(id);
  }

  @Test
  void shouldReturnNotFoundWhenActivityDoesNotExist() {
    BddLogger.given("unknown declared activity id");

    BddLogger.when("getting activity");

    BddLogger.then("it should return not found");

    webTestClient
        .get()
        .uri(BASE_PATH + "/" + notFoundId)
        .header("X-Signed-Context", studentPayload)
        .header("X-Context-Kid", secretKey)
        .header("X-Context-Signature", studentSignature)
        .exchange()
        .expectStatus()
        .isNotFound();
  }

  @Test
  @Transactional
  void shouldDeleteAssociations() throws Exception {
    String id = declaredActivityId;

    webTestClient
        .method(HttpMethod.DELETE)
        .uri(BASE_PATH + "/" + id + "/associations")
        .header("X-Signed-Context", studentPayload)
        .header("X-Context-Kid", secretKey)
        .header("X-Context-Signature", studentSignature)
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue("{\"idsToDelete\":[]}")
        .exchange()
        .expectStatus()
        .isOk();
  }

  @Test
  @Transactional
  void shouldAssociateDeclaredSkillsToDeclaredActivity() throws Exception {
    String id = declaredActivityId;

    BddLogger.given("a declared activity and declared skills to associate");

    String requestBody =
        """
        {
          "idsToAssociate": [
            "0b6cdaac-0b71-4102-b342-fee525a42989",
            "d2cfae1d-cd31-43f0-9723-2ad762c52fb1"
          ]
        }
        """;

    BddLogger.when("associating declared skills to the activity");

    BddLogger.then("it should return updated associations");

    webTestClient
        .post()
        .uri(BASE_PATH + "/" + id + "/associate/declared-skills")
        .header("X-Signed-Context", studentPayload)
        .header("X-Context-Kid", secretKey)
        .header("X-Context-Signature", studentSignature)
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(requestBody)
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody()
        .jsonPath("$.declaredSkillAssociations")
        .isArray();
  }

  @Test
  @Transactional
  void shouldReturnNotFoundWhenAssociatingDeclaredSkillsToUnknownActivity() {
    BddLogger.given("unknown declared activity id");

    String requestBody =
        """
        {
          "idsToAssociate": [
            "11111111-1111-1111-1111-111111111111"
          ]
        }
        """;

    BddLogger.when("associating declared skills");

    BddLogger.then("it should return not found");

    webTestClient
        .post()
        .uri(BASE_PATH + "/" + notFoundId + "/associate/declared-skills")
        .header("X-Signed-Context", studentPayload)
        .header("X-Context-Kid", secretKey)
        .header("X-Context-Signature", studentSignature)
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(requestBody)
        .exchange()
        .expectStatus()
        .isNotFound();
  }

  @Test
  void shouldReturn404WhenSearchingTracesForNonExistentActivity() {
    BddLogger.given("a non-existent declared activity");

    BddLogger.when("searching traces for association");
    webTestClient
        .get()
        .uri(
            uriBuilder ->
                uriBuilder
                    .path(BASE_PATH + "/" + notFoundId + "/search-for-association/traces")
                    .queryParam("page", "0")
                    .queryParam("pageSize", "8")
                    .build())
        .header("X-Signed-Context", studentPayload)
        .header("X-Context-Kid", secretKey)
        .header("X-Context-Signature", studentSignature)
        .exchange()
        .expectStatus()
        .isNotFound();

    BddLogger.then("it should return 404");
  }

  @Test
  @Transactional
  void shouldReturn403WhenSearchingTracesForOtherStudentActivity() throws Exception {
    BddLogger.given("a declared activity belonging to another student");
    String id = declaredActivityId;

    BddLogger.when("another student searches traces for association");
    webTestClient
        .get()
        .uri(
            uriBuilder ->
                uriBuilder
                    .path(BASE_PATH + "/" + id + "/search-for-association/traces")
                    .queryParam("page", "0")
                    .queryParam("pageSize", "8")
                    .build())
        .header("X-Signed-Context", otherStudentPayload)
        .header("X-Context-Kid", secretKey)
        .header("X-Context-Signature", otherStudentSignature)
        .exchange()
        .expectStatus()
        .isForbidden();

    BddLogger.then("it should return 403");
  }
}
