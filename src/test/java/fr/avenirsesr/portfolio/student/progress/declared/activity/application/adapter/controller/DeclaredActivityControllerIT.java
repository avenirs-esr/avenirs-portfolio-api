package fr.avenirsesr.portfolio.student.progress.declared.activity.application.adapter.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import fr.avenirsesr.portfolio.shared.infrastructure.ContainerConfigurationTest;
import fr.avenirsesr.portfolio.shared.infrastructure.adapter.seeder.SeederRunner;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.transaction.annotation.Transactional;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DeclaredActivityControllerIT extends ContainerConfigurationTest {

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

  @Value("${user.second.staff.payload}")
  private String staffPayload;

  @Value("${user.second.staff.signature}")
  private String staffSignature;

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

  private WebTestClient.RequestHeadersSpec<?> withStudentHeaders(
      WebTestClient.RequestHeadersSpec<?> request) {
    return request
        .header("X-Signed-Context", studentPayload)
        .header("X-Context-Kid", secretKey)
        .header("X-Context-Signature", studentSignature);
  }

  private WebTestClient.RequestHeadersSpec<?> withOtherStudentHeaders(
      WebTestClient.RequestHeadersSpec<?> request) {
    return request
        .header("X-Signed-Context", otherStudentPayload)
        .header("X-Context-Kid", secretKey)
        .header("X-Context-Signature", otherStudentSignature);
  }

  private WebTestClient.RequestHeadersSpec<?> withStaffHeaders(
      WebTestClient.RequestHeadersSpec<?> request) {
    return request
        .header("X-Signed-Context", staffPayload)
        .header("X-Context-Kid", secretKey)
        .header("X-Context-Signature", staffSignature);
  }

  @Nested
  class GetDeclaredActivities {

    @Test
    @Transactional
    void shouldGetDeclaredActivities() {
      BddLogger.given("declared activities exist");

      BddLogger.when("getting declared activities");

      BddLogger.then("it should return a list");

      withStudentHeaders(webTestClient.get().uri(BASE_PATH))
          .exchange()
          .expectStatus()
          .isOk()
          .expectBody()
          .jsonPath("$.data")
          .isArray();
    }
  }

  @Nested
  class GetDeclaredActivityDetails {

    @Test
    @Transactional
    void shouldGetDeclaredActivityDetailsWhenLoggedInStudentOwnsActivity() {
      String id = declaredActivityId;

      BddLogger.given("a declared activity owned by the logged-in student");

      BddLogger.when("the student gets declared activity details");

      BddLogger.then("it should return the activity details");

      withStudentHeaders(webTestClient.get().uri(BASE_PATH + "/" + id))
          .exchange()
          .expectStatus()
          .isOk()
          .expectBody()
          .jsonPath("$.id")
          .isEqualTo(id);
    }

    @Test
    @Transactional
    void shouldGetDeclaredActivityDetailsWhenLoggedInStaffIsActivityAuthor() {
      String id = "d4c9f5a5-6c2b-4a5e-9c4f-8e2a6b1d3f04";

      BddLogger.given(
          "a declared activity created from an activity authored by the logged-in staff");

      BddLogger.when("the staff gets declared activity details");

      BddLogger.then("it should return the activity details");

      withStaffHeaders(webTestClient.get().uri(BASE_PATH + "/" + id))
          .exchange()
          .expectStatus()
          .isOk()
          .expectBody()
          .jsonPath("$.id")
          .isEqualTo(id);
    }

    @Test
    void shouldReturnNotFoundWhenDeclaredActivityDoesNotExist() {
      BddLogger.given("unknown declared activity id");

      BddLogger.when("getting declared activity details");

      BddLogger.then("it should return not found");

      withStudentHeaders(webTestClient.get().uri(BASE_PATH + "/" + notFoundId))
          .exchange()
          .expectStatus()
          .isNotFound();
    }
  }

  @Nested
  class DeleteAssociations {

    @Test
    @Transactional
    void shouldDeleteAssociations() {
      String id = declaredActivityId;

      BddLogger.given("a declared activity owned by the logged-in student");

      BddLogger.when("deleting no associations");

      BddLogger.then("it should return ok");

      withStudentHeaders(
              webTestClient
                  .method(HttpMethod.DELETE)
                  .uri(BASE_PATH + "/" + id + "/associations")
                  .contentType(MediaType.APPLICATION_JSON)
                  .bodyValue("{\"idsToDelete\":[]}"))
          .exchange()
          .expectStatus()
          .isOk();
    }
  }

  @Nested
  class AssociateDeclaredSkills {

    @Test
    @Transactional
    void shouldAssociateDeclaredSkillsToDeclaredActivity() {
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

      withStudentHeaders(
              webTestClient
                  .post()
                  .uri(BASE_PATH + "/" + id + "/associate/declared-skills")
                  .contentType(MediaType.APPLICATION_JSON)
                  .bodyValue(requestBody))
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

      withStudentHeaders(
              webTestClient
                  .post()
                  .uri(BASE_PATH + "/" + notFoundId + "/associate/declared-skills")
                  .contentType(MediaType.APPLICATION_JSON)
                  .bodyValue(requestBody))
          .exchange()
          .expectStatus()
          .isNotFound();
    }
  }

  @Nested
  class SearchTracesForAssociation {

    @Test
    void shouldReturn404WhenSearchingTracesForNonExistentActivity() {
      BddLogger.given("a non-existent declared activity");

      BddLogger.when("searching traces for association");

      withStudentHeaders(
              webTestClient
                  .get()
                  .uri(
                      uriBuilder ->
                          uriBuilder
                              .path(BASE_PATH + "/" + notFoundId + "/search-for-association/traces")
                              .queryParam("page", "0")
                              .queryParam("pageSize", "8")
                              .build()))
          .exchange()
          .expectStatus()
          .isNotFound();

      BddLogger.then("it should return 404");
    }

    @Test
    @Transactional
    void shouldReturn403WhenSearchingTracesForOtherStudentActivity() {
      String id = declaredActivityId;

      BddLogger.given("a declared activity belonging to another student");

      BddLogger.when("another student searches traces for association");

      withOtherStudentHeaders(
              webTestClient
                  .get()
                  .uri(
                      uriBuilder ->
                          uriBuilder
                              .path(BASE_PATH + "/" + id + "/search-for-association/traces")
                              .queryParam("page", "0")
                              .queryParam("pageSize", "8")
                              .build()))
          .exchange()
          .expectStatus()
          .isForbidden();

      BddLogger.then("it should return 403");
    }
  }
}
