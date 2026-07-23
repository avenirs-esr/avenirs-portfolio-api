package fr.avenirsesr.portfolio.activity.application.adapter.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

class ActivityControllerIT extends ContainerConfigurationTest {

  private static final String BASE_PATH = "/me/activities";
  private static final String NAVIGATION_PATH = BASE_PATH + "/navigation";
  private static final String PRESENTATION_PATH =
      BASE_PATH + "/PUBLISHED/{activityId}/presentation";
  private static final String DRAFT_PATH = BASE_PATH + "/draft";
  private static final String DRAFT_UPDATE_PATH = BASE_PATH + "/{draftId}";
  private static final String WORKING_SPACE_PATH = BASE_PATH + "/staff/working-space";
  private static final String LIBRARY_PATH = BASE_PATH + "/staff/library";
  private static final String PUBLISH_PATH = BASE_PATH + "/publish/{draftId}";
  private static final String UNPUBLISH_PATH = BASE_PATH + "/unpublish/{activityId}";
  private static final String CONTENT_PATH = BASE_PATH + "/{activityStatus}/{activityId}/content";
  private static final String CREATE_DRAFT_PATH = BASE_PATH + "/create-draft/{activityId}";
  private static final String SUBSCRIBE_PATH = "/me/activity-progress/subscribe/{activityId}";

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

  @Nested
  class GivenActivityEndpoint {

    @BeforeEach
    void setupGiven() {
      BddLogger.given("activity endpoints");
    }

    @Nested
    class WhenGettingNavigation {

      @BeforeEach
      void setupWhen() {
        BddLogger.when("performing a GET on " + NAVIGATION_PATH);
      }

      @Test
      void thenItShouldReturnActivitiesNavigationAsArrayAndValidateItemShapeWhenPresent()
          throws Exception {
        BddLogger.then("it should return a JSON array with valid item shape when present");

        String body =
            webTestClient
                .get()
                .uri(NAVIGATION_PATH)
                .headers(ActivityControllerIT.this::addStudentHeaders)
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

        if (!firstMenuWithItems.hasNonNull("title")
            || !firstMenuWithItems.get("title").isTextual()) {
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
    }

    @Nested
    class WhenGettingActivitiesView {

      @BeforeEach
      void setupWhen() {
        BddLogger.when("performing a GET on " + BASE_PATH);
      }

      @Test
      void thenItShouldReturnActivitiesView() {
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
            .headers(ActivityControllerIT.this::addStudentHeaders)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody()
            .jsonPath("$.data")
            .exists();
      }

      @Test
      void thenItShouldReturnActivitiesViewFilteredByThematic() {
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
            .headers(ActivityControllerIT.this::addStudentHeaders)
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
    }

    @Nested
    class WhenGettingActivityPresentation {

      @BeforeEach
      void setupWhen() {
        BddLogger.when("performing a GET on " + PRESENTATION_PATH);
      }

      @Test
      void thenItShouldGetActivityPresentation() throws Exception {
        BddLogger.and("given an existing activity");
        UUID activityId = getFirstActivityIdFromOverview();

        BddLogger.then("it should return 200 with the activity presentation");

        webTestClient
            .get()
            .uri(PRESENTATION_PATH, activityId)
            .header("Accept-Language", ELanguage.FRENCH.getCode())
            .headers(ActivityControllerIT.this::addStudentHeaders)
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

      @Test
      void thenItShouldReturn404WhenActivityNotFound() {
        BddLogger.and("given a non-existent activity id");
        UUID unknownId = UUID.randomUUID();

        BddLogger.then("it should return 404 with ACTIVITY_NOT_FOUND error code");

        webTestClient
            .get()
            .uri(PRESENTATION_PATH, unknownId)
            .header("Accept-Language", ELanguage.FRENCH.getCode())
            .headers(ActivityControllerIT.this::addStudentHeaders)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound()
            .expectBody()
            .jsonPath("$.code")
            .isEqualTo("ACTIVITY_NOT_FOUND");
      }
    }

    @Nested
    class WhenCreatingActivityDraft {

      @BeforeEach
      void setupWhen() {
        BddLogger.when("performing a POST on " + DRAFT_PATH);
      }

      @Test
      void thenItShouldCreateActivityDraftAndReturnItsId() throws Exception {
        BddLogger.then("it should return 200 with the created draft id");

        String requestBody =
            objectMapper.writeValueAsString(
                new ActivityDraftCreationRequest("Mon brouillon de test"));

        webTestClient
            .post()
            .uri(DRAFT_PATH)
            .headers(ActivityControllerIT.this::addStaffHeaders)
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
      void thenItShouldReturn4xxWhenBodyIsMissing() {
        BddLogger.then("it should return a 4xx error");

        webTestClient
            .post()
            .uri(DRAFT_PATH)
            .headers(ActivityControllerIT.this::addStaffHeaders)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .is4xxClientError();
      }

      @Test
      void thenItShouldReturn401WhenNotAuthenticated() throws Exception {
        BddLogger.then("it should return 401");

        String requestBody =
            objectMapper.writeValueAsString(
                new ActivityDraftCreationRequest("Brouillon non authentifié"));

        webTestClient
            .post()
            .uri(DRAFT_PATH)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(requestBody)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isUnauthorized();
      }

      @Test
      void thenItShouldReturn403WhenUserIsNotStaff() throws Exception {
        BddLogger.then("it should return 403 with USER_IS_NOT_STAFF error code");

        String requestBody =
            objectMapper.writeValueAsString(new ActivityDraftCreationRequest("Brouillon étudiant"));

        webTestClient
            .post()
            .uri(DRAFT_PATH)
            .headers(ActivityControllerIT.this::addSecondStudentHeaders)
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
    }

    @Nested
    class WhenUpdatingActivityDraft {

      @BeforeEach
      void setupWhen() {
        BddLogger.when("performing a PATCH on " + DRAFT_UPDATE_PATH);
      }

      @Test
      void thenItShouldUpdateActivityAndReturnItsId() throws Exception {
        BddLogger.and("given an existing activity draft");
        UUID draftId = createDraftAndGetId("Brouillon à mettre à jour");

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
                    false,
                    List.of("https://example.com", "https://avenirs-esr.fr")));

        webTestClient
            .patch()
            .uri(DRAFT_UPDATE_PATH, draftId)
            .headers(ActivityControllerIT.this::addStaffHeaders)
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
      void thenItShouldUpdateActivityWithPartialFields() throws Exception {
        BddLogger.and("given an existing activity draft");
        UUID draftId = createDraftAndGetId("Brouillon mise à jour partielle");

        BddLogger.then("it should return 200 with the draft id");

        String requestBody =
            objectMapper.writeValueAsString(
                new ActivityDraftUpdateRequest(
                    "Titre seul mis à jour", null, null, null, null, null, null, null, null, null));

        webTestClient
            .patch()
            .uri(DRAFT_UPDATE_PATH, draftId)
            .headers(ActivityControllerIT.this::addStaffHeaders)
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
      void thenItShouldReturn404WhenDraftNotFound() throws Exception {
        BddLogger.and("given a non-existent draft id");
        UUID unknownId = UUID.randomUUID();

        BddLogger.then("it should return 404 with ACTIVITY_DRAFT_NOT_FOUND error code");

        String requestBody =
            objectMapper.writeValueAsString(
                new ActivityDraftUpdateRequest(
                    "Titre", null, null, null, null, null, null, null, null, null));

        webTestClient
            .patch()
            .uri(DRAFT_UPDATE_PATH, unknownId)
            .headers(ActivityControllerIT.this::addStaffHeaders)
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
      void thenItShouldReturn403WhenStudentTriesToUpdateDraft() throws Exception {
        BddLogger.and("given an existing activity draft and a student account");
        UUID draftId = createDraftAndGetId("Brouillon accès refusé");

        BddLogger.then("it should return 403 with USER_IS_NOT_STAFF error code");

        String requestBody =
            objectMapper.writeValueAsString(
                new ActivityDraftUpdateRequest(
                    "Titre étudiant", null, null, null, null, null, null, null, null, null));

        webTestClient
            .patch()
            .uri(DRAFT_UPDATE_PATH, draftId)
            .headers(ActivityControllerIT.this::addSecondStudentHeaders)
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
      void thenItShouldReturn401WhenNotAuthenticated() throws Exception {
        BddLogger.then("it should return 401");

        String requestBody =
            objectMapper.writeValueAsString(
                new ActivityDraftUpdateRequest(
                    "Titre", null, null, null, null, null, null, null, null, null));

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
    }

    @Nested
    class WhenPublishingActivityDraft {

      @BeforeEach
      void setupWhen() {
        BddLogger.when("performing a POST on " + PUBLISH_PATH);
      }

      @Test
      void thenItShouldPublishActivityDraftAndReturnActivityId() throws Exception {
        BddLogger.and("given an existing activity draft with a summary");
        UUID draftId = createDraftAndGetId("Brouillon à publier");
        fillDraftWithSummaryAndDescription(draftId);

        BddLogger.then("it should return 200 with the published activity id");

        webTestClient
            .post()
            .uri(PUBLISH_PATH, draftId)
            .headers(ActivityControllerIT.this::addStaffHeaders)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody()
            .jsonPath("$.createdItemId")
            .exists()
            .jsonPath("$.createdItemId")
            .isNotEmpty();
      }

      @Test
      void thenItShouldPublishActivityDraftAndReturnSameIdAsDraft() throws Exception {
        BddLogger.and("given an existing activity draft with a summary");
        UUID draftId = createDraftAndGetId("Brouillon id conservé");
        fillDraftWithSummaryAndDescription(draftId);

        BddLogger.then("it should return the same id as the draft");

        webTestClient
            .post()
            .uri(PUBLISH_PATH, draftId)
            .headers(ActivityControllerIT.this::addStaffHeaders)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody()
            .jsonPath("$.createdItemId")
            .isEqualTo(draftId.toString());
      }

      @Test
      void thenItShouldDeleteDraftAfterPublishing() throws Exception {
        BddLogger.and("given an existing activity draft with a summary");
        UUID draftId = createDraftAndGetId("Brouillon supprimé après publication");
        fillDraftWithSummaryAndDescription(draftId);

        webTestClient
            .post()
            .uri(PUBLISH_PATH, draftId)
            .headers(ActivityControllerIT.this::addStaffHeaders)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk();

        BddLogger.then("the draft should no longer exist");
        webTestClient
            .get()
            .uri(BASE_PATH + "/DRAFT/{draftId}/presentation", draftId)
            .headers(ActivityControllerIT.this::addStaffHeaders)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
      }

      @Test
      void thenItShouldMakeActivityAccessibleAfterPublishing() throws Exception {
        BddLogger.and("given an existing draft with a summary");
        UUID draftId = createDraftAndGetId("Brouillon accessible après publication");
        fillDraftWithSummaryAndDescription(draftId);

        webTestClient
            .post()
            .uri(PUBLISH_PATH, draftId)
            .headers(ActivityControllerIT.this::addStaffHeaders)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk();

        BddLogger.then("the published activity should be accessible via the presentation endpoint");

        webTestClient
            .get()
            .uri(PRESENTATION_PATH, draftId)
            .headers(ActivityControllerIT.this::addStudentHeaders)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody()
            .jsonPath("$.id")
            .isEqualTo(draftId.toString());
      }

      @Test
      void thenItShouldReturn404WhenDraftNotFound() {
        BddLogger.and("given a non-existent draft id");
        UUID unknownId = UUID.randomUUID();

        BddLogger.then("it should return 404 with ACTIVITY_DRAFT_NOT_FOUND error code");

        webTestClient
            .post()
            .uri(PUBLISH_PATH, unknownId)
            .headers(ActivityControllerIT.this::addStaffHeaders)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound()
            .expectBody()
            .jsonPath("$.code")
            .isEqualTo("ACTIVITY_DRAFT_NOT_FOUND");
      }

      @Test
      void thenItShouldReturn400WhenDraftHasNoSummary() throws Exception {
        BddLogger.and("given an existing draft without a summary");
        UUID draftId = createDraftAndGetId("Brouillon sans résumé");

        BddLogger.then("it should return 400 because summary is required to publish");

        webTestClient
            .post()
            .uri(PUBLISH_PATH, draftId)
            .headers(ActivityControllerIT.this::addStaffHeaders)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isEqualTo(400)
            .expectBody()
            .jsonPath("$.code")
            .exists();
      }

      @Test
      void thenItShouldReturn400WhenDraftHasNoDescription() throws Exception {
        BddLogger.and("given an existing draft with a summary but no description");
        UUID draftId = createDraftAndGetId("Brouillon sans consigne");
        fillDraftWithSummary(draftId);

        BddLogger.then("it should return 400 because description is required to publish");

        webTestClient
            .post()
            .uri(PUBLISH_PATH, draftId)
            .headers(ActivityControllerIT.this::addStaffHeaders)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isEqualTo(400)
            .expectBody()
            .jsonPath("$.code")
            .exists();
      }

      @Test
      void thenItShouldReturn403WhenStudentTriesToPublish() throws Exception {
        BddLogger.and("given an existing draft and a student account");
        UUID draftId = createDraftAndGetId("Brouillon publication refusée");
        fillDraftWithSummaryAndDescription(draftId);

        BddLogger.then("it should return 403 with USER_IS_NOT_STAFF error code");

        webTestClient
            .post()
            .uri(PUBLISH_PATH, draftId)
            .headers(ActivityControllerIT.this::addSecondStudentHeaders)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isForbidden()
            .expectBody()
            .jsonPath("$.code")
            .isEqualTo("USER_IS_NOT_STAFF_EXCEPTION");
      }

      @Test
      void thenItShouldReturn401WhenNotAuthenticated() {
        BddLogger.and("given a non-authenticated request");
        BddLogger.then("it should return 401");

        webTestClient
            .post()
            .uri(PUBLISH_PATH, UUID.randomUUID())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isUnauthorized();
      }
    }

    @Nested
    class WhenUnpublishingActivity {

      @BeforeEach
      void setupWhen() {
        BddLogger.when("performing a POST on " + UNPUBLISH_PATH);
      }

      @Test
      void thenItShouldUnpublishActivityAndReturn204() throws Exception {
        BddLogger.and("given an existing published activity created by the staff");
        UUID activityId = publishNewActivity("Activité à dépublier");

        BddLogger.then("it should return 204 No Content");

        webTestClient
            .post()
            .uri(UNPUBLISH_PATH, activityId)
            .headers(ActivityControllerIT.this::addStaffHeaders)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();
      }

      @Test
      void thenItShouldHideUnpublishedActivityFromActivitiesView() throws Exception {
        BddLogger.and("given a newly published activity");
        UUID activityId = publishNewActivity("Activité masquée après dépublication");

        webTestClient
            .post()
            .uri(UNPUBLISH_PATH, activityId)
            .headers(ActivityControllerIT.this::addStaffHeaders)
            .exchange()
            .expectStatus()
            .isNoContent();

        BddLogger.then("the activity should no longer appear in the student activities view");

        String body =
            webTestClient
                .get()
                .uri(
                    uriBuilder ->
                        uriBuilder
                            .path(BASE_PATH)
                            .queryParam("page", "0")
                            .queryParam("pageSize", "100")
                            .build())
                .headers(ActivityControllerIT.this::addStudentHeaders)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(String.class)
                .returnResult()
                .getResponseBody();

        JsonNode data = objectMapper.readTree(body).get("data");
        for (JsonNode item : data) {
          if (activityId.toString().equals(item.get("id").asText())) {
            throw new AssertionError("Unpublished activity should not appear in activitiesView");
          }
        }
      }

      @Test
      void thenItShouldHideUnpublishedActivityFromNavigation() throws Exception {
        BddLogger.and("given a newly published activity");
        UUID activityId = publishNewActivity("Activité navigation dépubliée");

        webTestClient
            .post()
            .uri(UNPUBLISH_PATH, activityId)
            .headers(ActivityControllerIT.this::addStaffHeaders)
            .exchange()
            .expectStatus()
            .isNoContent();

        BddLogger.then("the activity should not appear in the navigation");

        String body =
            webTestClient
                .get()
                .uri(NAVIGATION_PATH)
                .headers(ActivityControllerIT.this::addStudentHeaders)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(String.class)
                .returnResult()
                .getResponseBody();

        String activityIdAsString = activityId.toString();
        JsonNode root = objectMapper.readTree(body);
        for (JsonNode menu : root) {
          JsonNode items = menu.get("items");
          if (items == null) {
            continue;
          }
          for (JsonNode item : items) {
            if (activityIdAsString.equals(item.get("id").asText())) {
              throw new AssertionError("Unpublished activity should not appear in navigation");
            }
          }
        }
      }

      @Test
      void thenItShouldShowUnpublishedActivityInStaffWorkingSpace() throws Exception {
        BddLogger.and("given a published activity that gets unpublished");
        UUID activityId = publishNewActivity("Activité visible dans working space");

        webTestClient
            .post()
            .uri(UNPUBLISH_PATH, activityId)
            .headers(ActivityControllerIT.this::addStaffHeaders)
            .exchange()
            .expectStatus()
            .isNoContent();

        BddLogger.then("the unpublished activity should appear with UNPUBLISHED status");

        String body =
            webTestClient
                .get()
                .uri(
                    uriBuilder ->
                        uriBuilder
                            .path(WORKING_SPACE_PATH)
                            .queryParam("page", "0")
                            .queryParam("pageSize", "100")
                            .build())
                .headers(ActivityControllerIT.this::addStaffHeaders)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(String.class)
                .returnResult()
                .getResponseBody();

        JsonNode data = objectMapper.readTree(body).get("data");
        boolean found = false;

        for (JsonNode item : data) {
          if (activityId.toString().equals(item.get("activityId").asText())) {
            assertTrue(
                "UNPUBLISHED".equals(item.get("activityStatus").asText()),
                "Expected UNPUBLISHED status but got: " + item.get("activityStatus").asText());
            found = true;
            break;
          }
        }

        assertTrue(found, "Unpublished activity should still appear in staff working space");
      }

      @Test
      void thenItShouldReturn404WhenActivityNotFound() {
        BddLogger.and("given a non-existent activity id");
        UUID unknownId = UUID.randomUUID();

        BddLogger.then("it should return 404 with ACTIVITY_NOT_FOUND error code");

        webTestClient
            .post()
            .uri(UNPUBLISH_PATH, unknownId)
            .headers(ActivityControllerIT.this::addStaffHeaders)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound()
            .expectBody()
            .jsonPath("$.code")
            .isEqualTo("ACTIVITY_NOT_FOUND");
      }

      @Test
      void thenItShouldReturn403WhenStudentTriesToUnpublish() throws Exception {
        BddLogger.and("given an existing published activity and a student (non-staff) account");
        UUID activityId = publishNewActivity("Activité dépublication refusée");

        BddLogger.then("it should return 403 with USER_IS_NOT_STAFF error code");

        webTestClient
            .post()
            .uri(UNPUBLISH_PATH, activityId)
            .headers(ActivityControllerIT.this::addSecondStudentHeaders)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isForbidden()
            .expectBody()
            .jsonPath("$.code")
            .isEqualTo("USER_IS_NOT_STAFF_EXCEPTION");
      }

      @Test
      void thenItShouldReturn401WhenNotAuthenticated() {
        BddLogger.then("it should return 401");

        webTestClient
            .post()
            .uri(UNPUBLISH_PATH, UUID.randomUUID())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isUnauthorized();
      }
    }

    @Nested
    class WhenGettingStaffWorkingSpace {

      @BeforeEach
      void setupWhen() {
        BddLogger.when("performing a GET on " + WORKING_SPACE_PATH);
      }

      @Nested
      class AndStatusIsNotProvided {

        @BeforeEach
        void setupAnd() {
          BddLogger.and("status is not provided");
        }

        @Test
        void thenItShouldReturnStaffActivityWorkingSpace() {
          BddLogger.then("it should return paged activities with data and page info");

          webTestClient
              .get()
              .uri(
                  uriBuilder ->
                      uriBuilder
                          .path(WORKING_SPACE_PATH)
                          .queryParam("page", "0")
                          .queryParam("pageSize", "8")
                          .build())
              .headers(ActivityControllerIT.this::addStaffHeaders)
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
        void thenItShouldReturnDefaultPaginationWhenNoParamsProvided() {
          BddLogger.then("it should return 200 with default pagination applied");

          webTestClient
              .get()
              .uri(WORKING_SPACE_PATH)
              .headers(ActivityControllerIT.this::addStaffHeaders)
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
        void thenItShouldReturnOnlyStaffOwnActivities() throws Exception {
          BddLogger.and("given a staff user with created drafts");
          createDraftAndGetId("Mon brouillon working space");

          BddLogger.then("it should return at least one activity belonging to the staff");

          String body =
              webTestClient
                  .get()
                  .uri(
                      uriBuilder ->
                          uriBuilder
                              .path(WORKING_SPACE_PATH)
                              .queryParam("page", "0")
                              .queryParam("pageSize", "12")
                              .build())
                  .headers(ActivityControllerIT.this::addStaffHeaders)
                  .accept(MediaType.APPLICATION_JSON)
                  .exchange()
                  .expectStatus()
                  .isOk()
                  .expectBody(String.class)
                  .returnResult()
                  .getResponseBody();

          JsonNode data = objectMapper.readTree(body).get("data");

          assertTrue(data.isArray());
          assertTrue(data.size() > 0);

          JsonNode first = data.get(0);
          assertTrue(first.hasNonNull("activityId"));
          assertTrue(first.hasNonNull("title"));
          assertTrue(first.hasNonNull("activityStatus"));
        }

        @Test
        void thenItShouldReturnItemsWithExpectedShape() throws Exception {
          BddLogger.and("given a staff user with at least one activity or draft");
          createDraftAndGetId("Brouillon pour vérifier la shape");

          BddLogger.then(
              "each item should have activityId, title, thematic and activityStatus fields");

          String body =
              webTestClient
                  .get()
                  .uri(
                      uriBuilder ->
                          uriBuilder
                              .path(WORKING_SPACE_PATH)
                              .queryParam("page", "0")
                              .queryParam("pageSize", "12")
                              .build())
                  .headers(ActivityControllerIT.this::addStaffHeaders)
                  .accept(MediaType.APPLICATION_JSON)
                  .exchange()
                  .expectStatus()
                  .isOk()
                  .expectBody(String.class)
                  .returnResult()
                  .getResponseBody();

          JsonNode data = objectMapper.readTree(body).get("data");

          if (data.isEmpty()) {
            return;
          }

          for (JsonNode item : data) {
            assertTrue(item.hasNonNull("activityId"), "missing activityId");
            assertTrue(item.hasNonNull("title"), "missing title");
            assertTrue(item.hasNonNull("activityStatus"), "missing activityStatus");

            String status = item.get("activityStatus").asText();
            assertTrue(
                status.equals("DRAFT")
                    || status.equals("PUBLISHED")
                    || status.equals("UNPUBLISHED"),
                "unexpected status: " + status);
          }
        }

        @Test
        void thenItShouldRespectPageSizeLimit() {
          BddLogger.then("it should cap the pageSize to the maximum allowed value");

          webTestClient
              .get()
              .uri(
                  uriBuilder ->
                      uriBuilder
                          .path(WORKING_SPACE_PATH)
                          .queryParam("page", "0")
                          .queryParam("pageSize", "999")
                          .build())
              .headers(ActivityControllerIT.this::addStaffHeaders)
              .accept(MediaType.APPLICATION_JSON)
              .exchange()
              .expectStatus()
              .isOk()
              .expectBody()
              .jsonPath("$.page.pageSize")
              .isEqualTo(100);
        }

        @Test
        void thenItShouldReturn401WhenNotAuthenticated() {
          BddLogger.then("it should return 401");

          webTestClient
              .get()
              .uri(WORKING_SPACE_PATH)
              .accept(MediaType.APPLICATION_JSON)
              .exchange()
              .expectStatus()
              .isUnauthorized();
        }

        @Test
        void shouldReturn403WhenUserIsNotStaff() {
          BddLogger.given("the " + WORKING_SPACE_PATH + " endpoint");
          BddLogger.when("performing a GET with a student account");
          BddLogger.then("it should return 403 with USER_IS_NOT_STAFF error code");

          webTestClient
              .get()
              .uri(WORKING_SPACE_PATH)
              .headers(ActivityControllerIT.this::addSecondStudentHeaders)
              .accept(MediaType.APPLICATION_JSON)
              .exchange()
              .expectStatus()
              .isForbidden()
              .expectBody()
              .jsonPath("$.code")
              .isEqualTo("USER_IS_NOT_STAFF_EXCEPTION");
        }
      }

      @Nested
      class AndStatusIsSetToDraft {

        @BeforeEach
        void setupAnd() {
          BddLogger.and("status is set to draft");
        }

        @Test
        void thenItShouldReturnOnlyDraftActivities() throws Exception {
          BddLogger.and("given a staff user with created drafts");
          createDraftAndGetId("Mon brouillon working space");
          publishNewActivity("Activité publiée working space");

          BddLogger.then("it should return at least one draft activity");

          String body =
              webTestClient
                  .get()
                  .uri(
                      uriBuilder ->
                          uriBuilder
                              .path(WORKING_SPACE_PATH)
                              .queryParam("page", "0")
                              .queryParam("pageSize", "12")
                              .queryParam("status", "DRAFT")
                              .build())
                  .headers(ActivityControllerIT.this::addStaffHeaders)
                  .accept(MediaType.APPLICATION_JSON)
                  .exchange()
                  .expectStatus()
                  .isOk()
                  .expectBody(String.class)
                  .returnResult()
                  .getResponseBody();

          JsonNode data = objectMapper.readTree(body).get("data");

          assertTrue(data.isArray());
          assertTrue(data.size() > 0);

          for (JsonNode item : data) {
            assertEquals("DRAFT", item.get("activityStatus").asText());
          }
        }
      }

      @Nested
      class AndStatusIsSetToPublished {

        @BeforeEach
        void setupAnd() {
          BddLogger.and("status is set to published");
        }

        @Test
        void thenItShouldReturnOnlyPublishedActivities() throws Exception {
          BddLogger.and("given a staff user with created drafts");
          createDraftAndGetId("Mon brouillon working space");
          publishNewActivity("Activité publiée working space");

          BddLogger.then("it should return at least one published activity");

          String body =
              webTestClient
                  .get()
                  .uri(
                      uriBuilder ->
                          uriBuilder
                              .path(WORKING_SPACE_PATH)
                              .queryParam("page", "0")
                              .queryParam("pageSize", "12")
                              .queryParam("status", "PUBLISHED")
                              .build())
                  .headers(ActivityControllerIT.this::addStaffHeaders)
                  .accept(MediaType.APPLICATION_JSON)
                  .exchange()
                  .expectStatus()
                  .isOk()
                  .expectBody(String.class)
                  .returnResult()
                  .getResponseBody();

          JsonNode data = objectMapper.readTree(body).get("data");

          assertTrue(data.isArray());
          assertTrue(data.size() > 0);

          for (JsonNode item : data) {
            assertEquals("PUBLISHED", item.get("activityStatus").asText());
          }
        }
      }
    }

    @Nested
    class WhenGettingStaffLibrary {

      void setupWhen() {
        BddLogger.when("performing a GET on " + LIBRARY_PATH);
      }

      @Test
      void thenItShouldReturnStaffActivityLibrary() {
        BddLogger.then("it should return paged activities with data and page info");

        webTestClient
            .get()
            .uri(
                uriBuilder ->
                    uriBuilder
                        .path(LIBRARY_PATH)
                        .queryParam("page", "0")
                        .queryParam("pageSize", "8")
                        .build())
            .headers(ActivityControllerIT.this::addStaffHeaders)
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
      void thenItShouldReturnStaffActivityLibraryFilteredByThematic() {
        BddLogger.then("it should return filtered paged activities");

        webTestClient
            .get()
            .uri(
                uriBuilder ->
                    uriBuilder
                        .path(LIBRARY_PATH)
                        .queryParam("thematic", EActivityThematic.SELF_KNOWLEDGE.name())
                        .queryParam("page", "0")
                        .queryParam("pageSize", "8")
                        .build())
            .headers(ActivityControllerIT.this::addStaffHeaders)
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
      void thenItShouldReturnDefaultPaginationWhenNoParamsProvided() {
        BddLogger.then("it should return 200 with default pagination applied");

        webTestClient
            .get()
            .uri(LIBRARY_PATH)
            .headers(ActivityControllerIT.this::addStaffHeaders)
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
      void thenItShouldReturn401WhenNotAuthenticated() {
        BddLogger.and("given a non-authenticated request");
        BddLogger.then("it should return 401");

        webTestClient
            .get()
            .uri(LIBRARY_PATH)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isUnauthorized();
      }

      @Test
      void thenItShouldReturn403WhenUserIsNotStaff() {
        BddLogger.and("given a student account");
        BddLogger.then("it should return 403 with USER_IS_NOT_STAFF error code");

        webTestClient
            .get()
            .uri(LIBRARY_PATH)
            .header(AvenirsSecurityHeaders.SIGNED_CONTEXT, secondStudentPayload)
            .header(AvenirsSecurityHeaders.CONTEXT_KID, secretKey)
            .header(AvenirsSecurityHeaders.CONTEXT_SIGNATURE, secondStudentSignature)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isForbidden()
            .expectBody()
            .jsonPath("$.code")
            .isEqualTo("USER_IS_NOT_STAFF_EXCEPTION");
      }
    }

    @Nested
    class WhenGettingActivityContent {

      @BeforeEach
      void setupWhen() {
        BddLogger.when("performing a GET on " + CONTENT_PATH);
      }

      @Test
      void thenItShouldReturnNullHasEnrolledStudentForPublishedActivity() throws Exception {
        BddLogger.and("given a published activity");
        UUID activityId = publishNewActivity("Activité contenu publiée");

        BddLogger.then("it should return 200 with hasEnrolledStudent equal to null");

        webTestClient
            .get()
            .uri(CONTENT_PATH, "PUBLISHED", activityId)
            .headers(ActivityControllerIT.this::addStudentHeaders)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody()
            .jsonPath("$.id")
            .isEqualTo(activityId.toString())
            .jsonPath("$.hasEnrolledStudent")
            .doesNotExist()
            .jsonPath("$.files")
            .isArray()
            .jsonPath("$.links")
            .isArray();
      }

      @Test
      void thenItShouldReturn404WhenActivityNotFound() {
        BddLogger.and("given a non-existent activity id");
        UUID unknownId = UUID.randomUUID();

        BddLogger.then("it should return 404 with ACTIVITY_NOT_FOUND error code");

        webTestClient
            .get()
            .uri(CONTENT_PATH, "PUBLISHED", unknownId)
            .headers(ActivityControllerIT.this::addStudentHeaders)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound()
            .expectBody()
            .jsonPath("$.code")
            .isEqualTo("ACTIVITY_NOT_FOUND");
      }

      @Test
      void thenItShouldReturnTrueHasEnrolledStudentForDraftWhenStudentIsEnrolled()
          throws Exception {
        BddLogger.and("given a published activity with an enrolled student");
        UUID activityId = publishNewActivity("Activité brouillon avec étudiant inscrit");
        subscribeStudentToActivity(activityId);

        webTestClient
            .post()
            .uri(CREATE_DRAFT_PATH, activityId)
            .headers(ActivityControllerIT.this::addStaffHeaders)
            .exchange()
            .expectStatus()
            .isOk();

        BddLogger.then("the draft content should have hasEnrolledStudent equal to true");

        webTestClient
            .get()
            .uri(CONTENT_PATH, "DRAFT", activityId)
            .headers(ActivityControllerIT.this::addStaffHeaders)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody()
            .jsonPath("$.hasEnrolledStudent")
            .isEqualTo(true);
      }

      @Test
      void thenItShouldReturnFalseHasEnrolledStudentForDraftWhenNoStudentIsEnrolled()
          throws Exception {
        BddLogger.and("given a published activity with no enrolled student");
        UUID activityId = publishNewActivity("Activité brouillon sans étudiant inscrit");

        webTestClient
            .post()
            .uri(CREATE_DRAFT_PATH, activityId)
            .headers(ActivityControllerIT.this::addStaffHeaders)
            .exchange()
            .expectStatus()
            .isOk();

        BddLogger.then("the draft content should have hasEnrolledStudent equal to false");

        webTestClient
            .get()
            .uri(CONTENT_PATH, "DRAFT", activityId)
            .headers(ActivityControllerIT.this::addStaffHeaders)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody()
            .jsonPath("$.hasEnrolledStudent")
            .isEqualTo(false);
      }

      @Test
      void thenItShouldReturn404WhenDraftNotFound() {
        BddLogger.and("given a non-existent draft id");
        UUID unknownId = UUID.randomUUID();

        BddLogger.then("it should return 404 with ACTIVITY_DRAFT_NOT_FOUND error code");

        webTestClient
            .get()
            .uri(CONTENT_PATH, "DRAFT", unknownId)
            .headers(ActivityControllerIT.this::addStaffHeaders)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound()
            .expectBody()
            .jsonPath("$.code")
            .isEqualTo("ACTIVITY_DRAFT_NOT_FOUND");
      }
    }

    @Nested
    class WhenCreatingDraftFromActivity {

      @BeforeEach
      void setupWhen() {
        BddLogger.when("performing a POST on " + CREATE_DRAFT_PATH);
      }

      @Test
      void thenItShouldCreateDraftWithSameIdAsActivity() throws Exception {
        BddLogger.and("given an existing published activity created by the staff");
        UUID activityId = publishNewActivity("Activité pour édition de brouillon");

        BddLogger.then("it should return 200 with the same id as the activity");

        webTestClient
            .post()
            .uri(CREATE_DRAFT_PATH, activityId)
            .headers(ActivityControllerIT.this::addStaffHeaders)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody()
            .jsonPath("$.draftId")
            .isEqualTo(activityId.toString());
      }

      @Test
      void thenItShouldUnpublishActivityWhenNoStudentIsEnrolled() throws Exception {
        BddLogger.and("given a published activity with no enrolled student");
        UUID activityId = publishNewActivity("Activité dépubliée après édition");

        webTestClient
            .post()
            .uri(CREATE_DRAFT_PATH, activityId)
            .headers(ActivityControllerIT.this::addStaffHeaders)
            .exchange()
            .expectStatus()
            .isOk();

        BddLogger.then("the underlying activity should now be UNPUBLISHED");

        String body =
            webTestClient
                .get()
                .uri(
                    uriBuilder ->
                        uriBuilder
                            .path(WORKING_SPACE_PATH)
                            .queryParam("page", "0")
                            .queryParam("pageSize", "100")
                            .build())
                .headers(ActivityControllerIT.this::addStaffHeaders)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(String.class)
                .returnResult()
                .getResponseBody();

        JsonNode data = objectMapper.readTree(body).get("data");
        boolean found = false;

        for (JsonNode item : data) {
          if (activityId.toString().equals(item.get("activityId").asText())) {
            assertTrue(
                "UNPUBLISHED".equals(item.get("activityStatus").asText()),
                "Expected UNPUBLISHED status but got: " + item.get("activityStatus").asText());
            found = true;
            break;
          }
        }

        assertTrue(found, "Activity should still appear in staff working space as UNPUBLISHED");
      }

      @Test
      void thenItShouldReturn404WhenActivityNotFound() {
        BddLogger.and("given a non-existent activity id");
        UUID unknownId = UUID.randomUUID();

        BddLogger.then("it should return 404 with ACTIVITY_NOT_FOUND error code");

        webTestClient
            .post()
            .uri(CREATE_DRAFT_PATH, unknownId)
            .headers(ActivityControllerIT.this::addStaffHeaders)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound()
            .expectBody()
            .jsonPath("$.code")
            .isEqualTo("ACTIVITY_NOT_FOUND");
      }

      @Test
      void thenItShouldReturn403WhenStudentTriesToCreateDraft() throws Exception {
        BddLogger.and("given an existing published activity and a student (non-staff) account");
        UUID activityId = publishNewActivity("Activité édition refusée");

        BddLogger.then("it should return 403 with USER_IS_NOT_STAFF error code");

        webTestClient
            .post()
            .uri(CREATE_DRAFT_PATH, activityId)
            .headers(ActivityControllerIT.this::addSecondStudentHeaders)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isForbidden()
            .expectBody()
            .jsonPath("$.code")
            .isEqualTo("USER_IS_NOT_STAFF_EXCEPTION");
      }

      @Test
      void thenItShouldReturn401WhenNotAuthenticated() {
        BddLogger.and("given a non-authenticated request");
        BddLogger.then("it should return 401");

        webTestClient
            .post()
            .uri(CREATE_DRAFT_PATH, UUID.randomUUID())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isUnauthorized();
      }
    }
  }

  private UUID getFirstActivityIdFromOverview() throws Exception {
    String body =
        webTestClient
            .get()
            .uri(BASE_PATH)
            .headers(this::addStudentHeaders)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody(String.class)
            .returnResult()
            .getResponseBody();

    JsonNode activities = objectMapper.readTree(body).get("data");

    if (activities == null || !activities.isArray() || activities.isEmpty()) {
      throw new IllegalStateException("Seeder returned no activity");
    }

    return UUID.fromString(activities.get(0).get("id").asText());
  }

  private UUID createDraftAndGetId(String title) throws Exception {
    String requestBody = objectMapper.writeValueAsString(new ActivityDraftCreationRequest(title));

    String body =
        webTestClient
            .post()
            .uri(DRAFT_PATH)
            .headers(this::addStaffHeaders)
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

  private void fillDraftWithSummary(UUID draftId) throws Exception {
    String requestBody =
        objectMapper.writeValueAsString(
            new ActivityDraftUpdateRequest(
                null,
                EActivityThematic.EXPERIENCES,
                "Un résumé valide pour la publication",
                null,
                null,
                null,
                null,
                null,
                null,
                null));

    webTestClient
        .patch()
        .uri(DRAFT_UPDATE_PATH, draftId)
        .headers(this::addStaffHeaders)
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(requestBody)
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectStatus()
        .isOk();
  }

  private void fillDraftWithSummaryAndDescription(UUID draftId) throws Exception {
    String requestBody =
        objectMapper.writeValueAsString(
            new ActivityDraftUpdateRequest(
                null,
                EActivityThematic.EXPERIENCES,
                "Un résumé valide pour la publication",
                "Une consigne valide pour la publication",
                null,
                null,
                null,
                null,
                null,
                null));

    webTestClient
        .patch()
        .uri(DRAFT_UPDATE_PATH, draftId)
        .headers(this::addStaffHeaders)
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(requestBody)
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectStatus()
        .isOk();
  }

  private UUID publishNewActivity(String title) throws Exception {
    UUID draftId = createDraftAndGetId(title);
    fillDraftWithSummaryAndDescription(draftId);

    String body =
        webTestClient
            .post()
            .uri(PUBLISH_PATH, draftId)
            .headers(this::addStaffHeaders)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody(String.class)
            .returnResult()
            .getResponseBody();

    return UUID.fromString(objectMapper.readTree(body).get("createdItemId").asText());
  }

  private void subscribeStudentToActivity(UUID activityId) {
    webTestClient
        .post()
        .uri(SUBSCRIBE_PATH, activityId)
        .headers(this::addStudentHeaders)
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue("{}")
        .exchange()
        .expectStatus()
        .isCreated();
  }

  private void addStudentHeaders(HttpHeaders headers) {
    headers.add(AvenirsSecurityHeaders.SIGNED_CONTEXT, studentPayload);
    headers.add(AvenirsSecurityHeaders.CONTEXT_KID, secretKey);
    headers.add(AvenirsSecurityHeaders.CONTEXT_SIGNATURE, studentSignature);
  }

  private void addSecondStudentHeaders(HttpHeaders headers) {
    headers.add(AvenirsSecurityHeaders.SIGNED_CONTEXT, secondStudentPayload);
    headers.add(AvenirsSecurityHeaders.CONTEXT_KID, secretKey);
    headers.add(AvenirsSecurityHeaders.CONTEXT_SIGNATURE, secondStudentSignature);
  }

  private void addStaffHeaders(HttpHeaders headers) {
    headers.add(AvenirsSecurityHeaders.SIGNED_CONTEXT, staffPayload);
    headers.add(AvenirsSecurityHeaders.CONTEXT_KID, secretKey);
    headers.add(AvenirsSecurityHeaders.CONTEXT_SIGNATURE, staffSignature);
  }
}
