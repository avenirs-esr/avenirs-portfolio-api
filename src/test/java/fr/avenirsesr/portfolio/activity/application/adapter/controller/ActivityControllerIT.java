package fr.avenirsesr.portfolio.activity.application.adapter.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.avenirsesr.portfolio.activity.application.adapter.request.ActivityDraftCreationRequest;
import fr.avenirsesr.portfolio.activity.application.adapter.request.ActivityDraftUpdateRequest;
import fr.avenirsesr.portfolio.activity.domain.model.enums.EActivityThematic;
import fr.avenirsesr.portfolio.common.language.domain.model.enums.ELanguage;
import fr.avenirsesr.portfolio.common.security.infrastructure.adapter.model.AvenirsSecurityHeaders;
import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import fr.avenirsesr.portfolio.shared.infrastructure.ContainerConfigurationTest;
import fr.avenirsesr.portfolio.shared.infrastructure.adapter.seeder.SeederRunner;
import java.util.UUID;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

class ActivityControllerIT extends ContainerConfigurationTest {

  private static final String BASE_PATH = "/me/activities";
  private static final String NAVIGATION_BASE_PATH = BASE_PATH + "/navigation";
  private static final String PRESENTATION_BASE_PATH =
      BASE_PATH + "/PUBLISHED/{activityId}" + "/presentation";
  private static final String DRAFT_BASE_PATH = BASE_PATH + "/draft";
  private static final String DRAFT_UPDATE_PATH = BASE_PATH + "/DRAFT/{draftId}";

  @Autowired private WebTestClient webTestClient;
  @Autowired private ObjectMapper objectMapper;

  @Value("${hmac.secret-key}")
  private String secretKey;

  @Value("${user.student.payload}")
  private String studentPayload;

  @Value("${user.student.signature}")
  private String studentSignature;

  @Value("${user.second.student.payload}")
  private String secondStudentPayload;

  @Value("${user.second.student.signature}")
  private String secondStudentSignature;

  @Value("${user.staff.payload}")
  private String staffPayload;

  @Value("${user.staff.signature}")
  private String staffSignature;

  @BeforeAll
  void setup(@Autowired SeederRunner seederRunner) {
    seederRunner.run();
  }

  @Test
  void shouldReturnActivitiesNavigationAsArrayAndValidateItemShapeWhenPresent() throws Exception {

    BddLogger.given("the " + NAVIGATION_BASE_PATH + " endpoint");
    BddLogger.when("performing a GET");
    BddLogger.then("it should return a JSON array");

    String body =
        webTestClient
            .get()
            .uri(NAVIGATION_BASE_PATH)
            .header(AvenirsSecurityHeaders.SIGNED_CONTEXT, studentPayload)
            .header(AvenirsSecurityHeaders.CONTEXT_KID, secretKey)
            .header(AvenirsSecurityHeaders.CONTEXT_SIGNATURE, studentSignature)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody(String.class)
            .returnResult()
            .getResponseBody();

    JsonNode root = objectMapper.readTree(body);

    if (root == null || !root.isArray()) {
      throw new AssertionError("root should be a JSON array");
    }

    if (root.isEmpty()) {
      return;
    }

    JsonNode firstMenuWithItems = null;
    for (JsonNode menu : root) {
      JsonNode items = menu.get("items");
      if (items != null && items.isArray() && !items.isEmpty()) {
        firstMenuWithItems = menu;
        break;
      }
    }

    if (firstMenuWithItems == null) {
      return;
    }

    if (!firstMenuWithItems.hasNonNull("title") || !firstMenuWithItems.get("title").isTextual()) {
      throw new AssertionError("menu should have textual title");
    }

    JsonNode firstItem = firstMenuWithItems.get("items").get(0);

    if (!firstItem.hasNonNull("id")
        || !firstItem.hasNonNull("title")
        || !firstItem.get("id").isTextual()
        || !firstItem.get("title").isTextual()) {
      throw new AssertionError("invalid item shape");
    }
  }

  @Test
  void shouldReturnActivitiesView() {
    BddLogger.given("the " + BASE_PATH + " endpoint");
    BddLogger.when("performing a GET");
    BddLogger.then("it should return paged activities");

    webTestClient
        .get()
        .uri(
            uriBuilder ->
                uriBuilder
                    .path(BASE_PATH)
                    .queryParam("page", "0")
                    .queryParam("pageSize", "10")
                    .build())
        .header(AvenirsSecurityHeaders.SIGNED_CONTEXT, studentPayload)
        .header(AvenirsSecurityHeaders.CONTEXT_KID, secretKey)
        .header(AvenirsSecurityHeaders.CONTEXT_SIGNATURE, studentSignature)
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody()
        .jsonPath("$.data")
        .exists();
  }

  @Test
  void shouldReturnActivitiesViewFilteredByThematic() {
    BddLogger.given("the " + BASE_PATH + " endpoint with thematic filter");
    BddLogger.when("performing a GET with thematic filter");
    BddLogger.then("it should return filtered paged activities");

    webTestClient
        .get()
        .uri(
            uriBuilder ->
                uriBuilder
                    .path(BASE_PATH)
                    .queryParam("thematic", EActivityThematic.SELF_KNOWLEDGE.name())
                    .queryParam("page", "0")
                    .queryParam("pageSize", "8")
                    .build())
        .header(AvenirsSecurityHeaders.SIGNED_CONTEXT, studentPayload)
        .header(AvenirsSecurityHeaders.CONTEXT_KID, secretKey)
        .header(AvenirsSecurityHeaders.CONTEXT_SIGNATURE, studentSignature)
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody()
        .jsonPath("$.data")
        .isArray()
        .jsonPath("$.page.page")
        .isEqualTo(0)
        .jsonPath("$.page.pageSize")
        .isEqualTo(8)
        .jsonPath("$.page.totalElements")
        .exists();
  }

  @Test
  void shouldGetActivityPresentation() throws Exception {
    BddLogger.given("an existing activity");
    UUID activityId = getFirstActivityIdFromOverview();

    webTestClient
        .get()
        .uri(PRESENTATION_BASE_PATH, activityId)
        .header("Accept-Language", ELanguage.FRENCH.getCode())
        .header(AvenirsSecurityHeaders.SIGNED_CONTEXT, studentPayload)
        .header(AvenirsSecurityHeaders.CONTEXT_KID, secretKey)
        .header(AvenirsSecurityHeaders.CONTEXT_SIGNATURE, studentSignature)
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody()
        .jsonPath("$.id")
        .isEqualTo(activityId.toString())
        .jsonPath("$.title")
        .exists()
        .jsonPath("$.summary")
        .exists()
        .jsonPath("$.createdAt")
        .exists()
        .jsonPath("$.updatedAt")
        .exists();
  }

  private UUID getFirstActivityIdFromOverview() throws Exception {
    String body =
        webTestClient
            .get()
            .uri(BASE_PATH)
            .header(AvenirsSecurityHeaders.SIGNED_CONTEXT, studentPayload)
            .header(AvenirsSecurityHeaders.CONTEXT_KID, secretKey)
            .header(AvenirsSecurityHeaders.CONTEXT_SIGNATURE, studentSignature)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody(String.class)
            .returnResult()
            .getResponseBody();

    JsonNode json = objectMapper.readTree(body);
    JsonNode activities = json.get("data");

    if (activities == null || !activities.isArray() || activities.isEmpty()) {
      throw new IllegalStateException("Seeder returned no activity");
    }

    return UUID.fromString(activities.get(0).get("id").asText());
  }

  @Test
  void shouldReturn404WhenActivityDetailNotFound() {
    UUID unknownId = UUID.randomUUID();

    webTestClient
        .get()
        .uri(PRESENTATION_BASE_PATH, unknownId)
        .header("Accept-Language", ELanguage.FRENCH.getCode())
        .header(AvenirsSecurityHeaders.SIGNED_CONTEXT, studentPayload)
        .header(AvenirsSecurityHeaders.CONTEXT_KID, secretKey)
        .header(AvenirsSecurityHeaders.CONTEXT_SIGNATURE, studentSignature)
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectStatus()
        .isNotFound()
        .expectBody()
        .jsonPath("$.code")
        .isEqualTo("ACTIVITY_NOT_FOUND");
  }

  @Test
  void shouldCreateActivityDraftAndReturnItsId() throws Exception {
    BddLogger.given("the " + BASE_PATH + "/draft endpoint");
    BddLogger.when("performing a POST with a valid title");
    BddLogger.then("it should return 200 with the created draft id");

    String requestBody =
        objectMapper.writeValueAsString(new ActivityDraftCreationRequest("Mon brouillon de test"));

    webTestClient
        .post()
        .uri(BASE_PATH + "/draft")
        .header(AvenirsSecurityHeaders.SIGNED_CONTEXT, staffPayload)
        .header(AvenirsSecurityHeaders.CONTEXT_KID, secretKey)
        .header(AvenirsSecurityHeaders.CONTEXT_SIGNATURE, staffSignature)
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(requestBody)
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody()
        .jsonPath("$.draftId")
        .exists()
        .jsonPath("$.draftId")
        .isNotEmpty();
  }

  @Test
  void shouldReturn4xxWhenBodyIsMissing() {
    BddLogger.given("the " + BASE_PATH + "/draft endpoint");
    BddLogger.when("performing a POST without a body");
    BddLogger.then("it should return a 4xx error");

    webTestClient
        .post()
        .uri(BASE_PATH + "/draft")
        .header(AvenirsSecurityHeaders.SIGNED_CONTEXT, staffPayload)
        .header(AvenirsSecurityHeaders.CONTEXT_KID, secretKey)
        .header(AvenirsSecurityHeaders.CONTEXT_SIGNATURE, staffSignature)
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectStatus()
        .is4xxClientError();
  }

  @Test
  void shouldReturn401WhenNotAuthenticated() throws Exception {
    BddLogger.given("the " + BASE_PATH + "/draft endpoint");
    BddLogger.when("performing a POST without authentication headers");
    BddLogger.then("it should return 401");

    String requestBody =
        objectMapper.writeValueAsString(
            new ActivityDraftCreationRequest("Brouillon non authentifié"));

    webTestClient
        .post()
        .uri(BASE_PATH + "/draft")
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(requestBody)
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectStatus()
        .isUnauthorized();
  }

  @Test
  void shouldReturn403WhenUserIsNotStaff() throws Exception {
    BddLogger.given("the " + BASE_PATH + "/draft endpoint");
    BddLogger.when("performing a POST with a student (non-staff) account");
    BddLogger.then("it should return 403 with USER_IS_NOT_STAFF error code");

    String requestBody =
        objectMapper.writeValueAsString(new ActivityDraftCreationRequest("Brouillon étudiant"));

    webTestClient
        .post()
        .uri(BASE_PATH + "/draft")
        .header(AvenirsSecurityHeaders.SIGNED_CONTEXT, secondStudentPayload)
        .header(AvenirsSecurityHeaders.CONTEXT_KID, secretKey)
        .header(AvenirsSecurityHeaders.CONTEXT_SIGNATURE, secondStudentSignature)
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(requestBody)
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectStatus()
        .isForbidden()
        .expectBody()
        .jsonPath("$.code")
        .isEqualTo("USER_IS_NOT_STAFF_EXCEPTION");
  }

  @Test
  void shouldUpdateActivityAndReturnItsId() throws Exception {
    BddLogger.given("an existing activity draft");
    UUID draftId = createDraftAndGetId("Brouillon à mettre à jour");

    BddLogger.when("performing a PATCH with valid fields");
    BddLogger.then("it should return 200 with the updated draft id");

    String requestBody =
        objectMapper.writeValueAsString(
            new ActivityDraftUpdateRequest(
                "Titre mis à jour",
                EActivityThematic.EXPERIENCES,
                "Nouveau summary",
                "<p>Nouvelle description</p>",
                "Avant entretien",
                "Label court",
                5,
                3,
                false));

    webTestClient
        .patch()
        .uri(DRAFT_UPDATE_PATH, draftId)
        .header(AvenirsSecurityHeaders.SIGNED_CONTEXT, staffPayload)
        .header(AvenirsSecurityHeaders.CONTEXT_KID, secretKey)
        .header(AvenirsSecurityHeaders.CONTEXT_SIGNATURE, staffSignature)
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(requestBody)
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody()
        .jsonPath("$.draftId")
        .isEqualTo(draftId.toString());
  }

  @Test
  void shouldUpdateActivityWithPartialFields() throws Exception {
    BddLogger.given("an existing activity draft");
    UUID draftId = createDraftAndGetId("Brouillon mise à jour partielle");

    BddLogger.when("performing a PATCH with only the title");
    BddLogger.then("it should return 200 with the draft id");

    String requestBody =
        objectMapper.writeValueAsString(
            new ActivityDraftUpdateRequest(
                "Titre seul mis à jour", null, null, null, null, null, null, null, null));

    webTestClient
        .patch()
        .uri(DRAFT_UPDATE_PATH, draftId)
        .header(AvenirsSecurityHeaders.SIGNED_CONTEXT, staffPayload)
        .header(AvenirsSecurityHeaders.CONTEXT_KID, secretKey)
        .header(AvenirsSecurityHeaders.CONTEXT_SIGNATURE, staffSignature)
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(requestBody)
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody()
        .jsonPath("$.draftId")
        .isEqualTo(draftId.toString());
  }

  @Test
  void shouldReturn404WhenDraftNotFound() throws Exception {
    BddLogger.given("a non-existent draft id");
    UUID unknownId = UUID.randomUUID();

    BddLogger.when("performing a PATCH on an unknown draft");
    BddLogger.then("it should return 404 with ACTIVITY_DRAFT_NOT_FOUND error code");

    String requestBody =
        objectMapper.writeValueAsString(
            new ActivityDraftUpdateRequest(
                "Titre", null, null, null, null, null, null, null, null));

    webTestClient
        .patch()
        .uri(DRAFT_UPDATE_PATH, unknownId)
        .header(AvenirsSecurityHeaders.SIGNED_CONTEXT, staffPayload)
        .header(AvenirsSecurityHeaders.CONTEXT_KID, secretKey)
        .header(AvenirsSecurityHeaders.CONTEXT_SIGNATURE, staffSignature)
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(requestBody)
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectStatus()
        .isNotFound()
        .expectBody()
        .jsonPath("$.code")
        .isEqualTo("ACTIVITY_DRAFT_NOT_FOUND");
  }

  @Test
  void shouldReturn403WhenStudentTriesToUpdateDraft() throws Exception {
    BddLogger.given("an existing activity draft and a student (non-staff) account");
    UUID draftId = createDraftAndGetId("Brouillon accès refusé");

    BddLogger.when("performing a PATCH with a student account");
    BddLogger.then("it should return 403 with USER_IS_NOT_STAFF error code");

    String requestBody =
        objectMapper.writeValueAsString(
            new ActivityDraftUpdateRequest(
                "Titre étudiant", null, null, null, null, null, null, null, null));

    webTestClient
        .patch()
        .uri(DRAFT_UPDATE_PATH, draftId)
        .header(AvenirsSecurityHeaders.SIGNED_CONTEXT, secondStudentPayload)
        .header(AvenirsSecurityHeaders.CONTEXT_KID, secretKey)
        .header(AvenirsSecurityHeaders.CONTEXT_SIGNATURE, secondStudentSignature)
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(requestBody)
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectStatus()
        .isForbidden()
        .expectBody()
        .jsonPath("$.code")
        .isEqualTo("USER_IS_NOT_STAFF_EXCEPTION");
  }

  @Test
  void shouldReturn401WhenNotAuthenticatedOnUpdate() throws Exception {
    BddLogger.given("the " + DRAFT_UPDATE_PATH + " endpoint");
    BddLogger.when("performing a PATCH without authentication headers");
    BddLogger.then("it should return 401");

    String requestBody =
        objectMapper.writeValueAsString(
            new ActivityDraftUpdateRequest(
                "Titre", null, null, null, null, null, null, null, null));

    webTestClient
        .patch()
        .uri(DRAFT_UPDATE_PATH, UUID.randomUUID())
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(requestBody)
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectStatus()
        .isUnauthorized();
  }

  private UUID createDraftAndGetId(String title) throws Exception {
    String requestBody = objectMapper.writeValueAsString(new ActivityDraftCreationRequest(title));

    String body =
        webTestClient
            .post()
            .uri(DRAFT_BASE_PATH)
            .header(AvenirsSecurityHeaders.SIGNED_CONTEXT, staffPayload)
            .header(AvenirsSecurityHeaders.CONTEXT_KID, secretKey)
            .header(AvenirsSecurityHeaders.CONTEXT_SIGNATURE, staffSignature)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(requestBody)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody(String.class)
            .returnResult()
            .getResponseBody();

    return UUID.fromString(objectMapper.readTree(body).get("draftId").asText());
  }
}
