package fr.avenirsesr.portfolio.student.activity.application.adapter.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.transaction.annotation.Transactional;

public class DeclaredActivityControllerIT extends ContainerConfigurationTest {

  private static final String BASE_PATH = "/me/activity-progress";
  private static final String FEEDBACK_BASE_PATH = BASE_PATH + "/feedbacks";

  @Autowired private WebTestClient webTestClient;
  @Autowired private ObjectMapper objectMapper;

  @Value("${user.student.payload}")
  private String studentPayload;

  @Value("${user.student.signature}")
  private String studentSignature;

  @Value("${user.second.student.payload}")
  private String otherStudentPayload;

  @Value("${user.second.student.signature}")
  private String otherStudentSignature;

  @Value("${user.no-permission.payload}")
  private String noPermissionPayload;

  @Value("${user.no-permission.signature}")
  private String noPermissionSignature;

  private final String activityId = "3f7c9a2e-5d44-4b7a-9c6f-2a6e8e91b1a1";

  /** Activities owned by this class only, so that unsubscribing does not disturb other tests. */
  private final String unsubscriptionActivityId = "c1e8b9a7-2d55-4f1a-8b5f-3c7e4a9d6f20";

  private final String lockedInteractionsActivityId = "5c8f2a91-3d7b-4e66-9b2f-1a4c8e7d9f55";

  private final String notFoundId = "00000000-0000-0000-0000-000000000000";
  private String declaredActivityId;

  @BeforeAll
  void setup(@Autowired SeederRunner seederRunner) throws Exception {
    seederRunner.run();
    declaredActivityId = createActivityAndGetId();
  }

  private String createActivityAndGetId() throws Exception {
    return subscribeAndGetId(activityId);
  }

  private String subscribeAndGetId(String subscribedActivityId) throws Exception {
    String response =
        webTestClient
            .post()
            .uri(BASE_PATH + "/subscribe/" + subscribedActivityId)
            .header("X-Signed-Context", studentPayload)
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
        .header("X-Context-Signature", otherStudentSignature)
        .exchange()
        .expectStatus()
        .isForbidden();

    BddLogger.then("it should return 403");
  }

  @Test
  void shouldReturn403WhenSubscribingToActivityWithoutPermission() {
    BddLogger.given("an authenticated user without the activity:register:own permission");

    BddLogger.when("subscribing to an activity");
    webTestClient
        .post()
        .uri(BASE_PATH + "/subscribe/" + activityId)
        .header("X-Signed-Context", noPermissionPayload)
        .header("X-Context-Signature", noPermissionSignature)
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue("{}")
        .exchange()
        .expectStatus()
        .isForbidden();

    BddLogger.then("it should return 403");
  }

  @Test
  void shouldKeepTheDeclaredActivityAndFlagItAsUnsubscribed() throws Exception {
    BddLogger.given("a student subscribed to an activity");
    String id = subscribeAndGetId(unsubscriptionActivityId);

    BddLogger.when("he unsubscribes from it");
    unsubscribe(unsubscriptionActivityId);

    BddLogger.then("the declared activity still exists with the UNSUBSCRIBED status");
    webTestClient
        .get()
        .uri(BASE_PATH + "/" + id)
        .header("X-Signed-Context", studentPayload)
        .header("X-Context-Signature", studentSignature)
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody()
        .jsonPath("$.status")
        .isEqualTo("UNSUBSCRIBED");

    BddLogger.and("subscribing again reuses the very same declared activity");
    String reSubscribedId = subscribeAndGetId(unsubscriptionActivityId);
    assertEquals(id, reSubscribedId);

    webTestClient
        .get()
        .uri(BASE_PATH + "/" + id)
        .header("X-Signed-Context", studentPayload)
        .header("X-Context-Signature", studentSignature)
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody()
        .jsonPath("$.status")
        .isEqualTo("SUBSCRIBED");
  }

  @Test
  void shouldRejectAnyInteractionWithAnUnsubscribedDeclaredActivity() throws Exception {
    BddLogger.given("a declared activity the student unsubscribed from");
    String id = subscribeAndGetId(lockedInteractionsActivityId);
    unsubscribe(lockedInteractionsActivityId);

    BddLogger.when("he tries to update its reflection");
    BddLogger.then("it should return 409 DECLARED_ACTIVITY_UNSUBSCRIBED");
    webTestClient
        .put()
        .uri(BASE_PATH + "/" + id + "/reflection")
        .header("X-Signed-Context", studentPayload)
        .header("X-Context-Signature", studentSignature)
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue("{\"reflection\": \"Une réflexion\"}")
        .exchange()
        .expectStatus()
        .isEqualTo(HttpStatus.CONFLICT)
        .expectBody()
        .jsonPath("$.code")
        .isEqualTo("DECLARED_ACTIVITY_UNSUBSCRIBED");

    BddLogger.when("he tries to update the declared activity itself");
    BddLogger.then("it should return 409 DECLARED_ACTIVITY_UNSUBSCRIBED");
    webTestClient
        .patch()
        .uri(BASE_PATH + "/" + id)
        .header("X-Signed-Context", studentPayload)
        .header("X-Context-Signature", studentSignature)
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue("{\"valorized\": true}")
        .exchange()
        .expectStatus()
        .isEqualTo(HttpStatus.CONFLICT)
        .expectBody()
        .jsonPath("$.code")
        .isEqualTo("DECLARED_ACTIVITY_UNSUBSCRIBED");

    BddLogger.when("he tries to ask for a feedback");
    BddLogger.then("it should return 409 DECLARED_ACTIVITY_UNSUBSCRIBED");
    webTestClient
        .post()
        .uri(FEEDBACK_BASE_PATH + "/" + id + "/ask-for-feedback")
        .header("X-Signed-Context", studentPayload)
        .header("X-Context-Signature", studentSignature)
        .exchange()
        .expectStatus()
        .isEqualTo(HttpStatus.CONFLICT)
        .expectBody()
        .jsonPath("$.code")
        .isEqualTo("DECLARED_ACTIVITY_UNSUBSCRIBED");
  }

  private void unsubscribe(String subscribedActivityId) {
    webTestClient
        .method(HttpMethod.DELETE)
        .uri(BASE_PATH + "/unsubscribe")
        .header("X-Signed-Context", studentPayload)
        .header("X-Context-Signature", studentSignature)
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue("[\"" + subscribedActivityId + "\"]")
        .exchange()
        .expectStatus()
        .isOk();
  }
}
