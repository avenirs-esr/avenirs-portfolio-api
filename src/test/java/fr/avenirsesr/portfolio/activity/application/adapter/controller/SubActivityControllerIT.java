package fr.avenirsesr.portfolio.activity.application.adapter.controller;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.avenirsesr.portfolio.common.language.domain.model.enums.ELanguage;
import fr.avenirsesr.portfolio.common.security.infrastructure.adapter.model.AvenirsSecurityHeaders;
import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import fr.avenirsesr.portfolio.shared.infrastructure.ContainerConfigurationTest;
import fr.avenirsesr.portfolio.shared.infrastructure.adapter.seeder.SeederRunner;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.StreamSupport;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.util.UriBuilder;

class SubActivityControllerIT extends ContainerConfigurationTest {

  private static final String BASE_PATH = "/me/activities";
  private static final String CONTENT_PATH = BASE_PATH + "/{activityStatus}/{activityId}/content";
  private static final String PRESENTATION_PATH =
      BASE_PATH + "/PUBLISHED/{activityId}/presentation";

  private static final UUID MAIN_ACTIVITY_ID =
      UUID.fromString("b3e7a1c9-4f2d-4a8e-9c61-7d2e5f8a1b01");
  private static final UUID FIRST_SUB_ACTIVITY_ID =
      UUID.fromString("b3e7a1c9-4f2d-4a8e-9c61-7d2e5f8a1b11");
  private static final UUID SECOND_SUB_ACTIVITY_ID =
      UUID.fromString("b3e7a1c9-4f2d-4a8e-9c61-7d2e5f8a1b12");
  private static final UUID THIRD_SUB_ACTIVITY_ID =
      UUID.fromString("b3e7a1c9-4f2d-4a8e-9c61-7d2e5f8a1b13");
  private static final List<UUID> SUB_ACTIVITY_IDS =
      List.of(FIRST_SUB_ACTIVITY_ID, SECOND_SUB_ACTIVITY_ID, THIRD_SUB_ACTIVITY_ID);

  private static final UUID DELETABLE_MAIN_ACTIVITY_ID =
      UUID.fromString("b3e7a1c9-4f2d-4a8e-9c61-7d2e5f8a1b02");
  private static final List<UUID> DELETABLE_SUB_ACTIVITY_IDS =
      List.of(
          UUID.fromString("b3e7a1c9-4f2d-4a8e-9c61-7d2e5f8a1b21"),
          UUID.fromString("b3e7a1c9-4f2d-4a8e-9c61-7d2e5f8a1b22"));

  private static final String SUB_ACTIVITY_OPERATION_NOT_ALLOWED =
      "SUB_ACTIVITY_OPERATION_NOT_ALLOWED";

  @Autowired private WebTestClient webTestClient;
  @Autowired private ObjectMapper objectMapper;

  @Value("${user.student.payload}")
  private String studentPayload;

  @Value("${user.student.signature}")
  private String studentSignature;

  @Value("${user.staff.payload}")
  private String staffPayload;

  @Value("${user.staff.signature}")
  private String staffSignature;

  @Value("${user.super-admin.payload}")
  private String superAdminPayload;

  @Value("${user.super-admin.signature}")
  private String superAdminSignature;

  @BeforeAll
  void setup(@Autowired SeederRunner seederRunner) {
    seederRunner.run();
  }

  @Nested
  class GivenAMainActivityWithSubActivities {

    @Test
    void thenTheStudentCatalogShouldOnlyListTheMainActivity() throws Exception {
      BddLogger.given("a published main activity with three sub-activities");
      BddLogger.when("a student browses the activity catalog");
      var body = getAllPages(BASE_PATH, studentHeaders());

      BddLogger.then("the main activity should be listed, not its sub-activities");
      assertThat(body).contains(MAIN_ACTIVITY_ID.toString());
      SUB_ACTIVITY_IDS.forEach(id -> assertThat(body).doesNotContain(id.toString()));
    }

    @Test
    void thenTheLatestActivitiesShouldNotListTheSubActivities() throws Exception {
      BddLogger.when("a student browses the latest activities");
      var body = getAllPages(BASE_PATH + "/latest", studentHeaders());

      BddLogger.then("no sub-activity should be listed");
      SUB_ACTIVITY_IDS.forEach(id -> assertThat(body).doesNotContain(id.toString()));
    }

    @Test
    void thenTheNavigationShouldOnlyListTheMainActivity() throws Exception {
      BddLogger.when("a student gets the activity navigation");
      var body = get(uri -> uri.path(BASE_PATH + "/navigation").build(), studentHeaders());

      BddLogger.then("the main activity should be listed, not its sub-activities");
      assertThat(body).contains(MAIN_ACTIVITY_ID.toString());
      SUB_ACTIVITY_IDS.forEach(id -> assertThat(body).doesNotContain(id.toString()));
    }

    @Test
    void thenTheStaffLibraryShouldOnlyListTheMainActivity() throws Exception {
      BddLogger.when("a staff browses the activity library");
      var body = getAllPages(BASE_PATH + "/staff/library", staffHeaders());

      BddLogger.then("the main activity should be listed, not its sub-activities");
      assertThat(body).contains(MAIN_ACTIVITY_ID.toString());
      SUB_ACTIVITY_IDS.forEach(id -> assertThat(body).doesNotContain(id.toString()));
    }

    @Test
    void thenTheWorkingSpaceShouldNotListTheSubActivities() throws Exception {
      BddLogger.when("the author of the main activity browses its working space");
      var body = getAllPages(BASE_PATH + "/staff/working-space", studentHeaders());

      BddLogger.then("no sub-activity should be listed");
      SUB_ACTIVITY_IDS.forEach(id -> assertThat(body).doesNotContain(id.toString()));
    }

    @Test
    void thenTheContentOfTheMainActivityShouldListItsSubActivitiesInOrder() throws Exception {
      BddLogger.when("a staff reads the content of the main activity");
      var content =
          readJson(
              get(
                  uri -> uri.path(CONTENT_PATH).build("PUBLISHED", MAIN_ACTIVITY_ID),
                  staffHeaders()));

      BddLogger.then("the sub-activities should be listed with their id and title, in order");
      assertThat(idsOf(content.get("subActivities"))).isEqualTo(SUB_ACTIVITY_IDS);
      assertThat(content.get("subActivities").get(0).get("title").asText())
          .isEqualTo("Faire le point sur ses envies");
      assertThat(content.get("mainActivityId").isNull()).isTrue();
    }

    @Test
    void thenTheContentOfASubActivityShouldPointToItsMainActivityAndCarryItsRights()
        throws Exception {
      BddLogger.when("the author of the main activity reads the content of a sub-activity");
      var content =
          readJson(
              get(
                  uri -> uri.path(CONTENT_PATH).build("PUBLISHED", SECOND_SUB_ACTIVITY_ID),
                  studentHeaders()));

      BddLogger.then("the content should point to the main activity and carry its rights");
      assertThat(content.get("mainActivityId").asText()).isEqualTo(MAIN_ACTIVITY_ID.toString());
      assertThat(content.get("title").asText()).isEqualTo("Explorer les métiers ciblés");
      assertThat(content.get("activityStatus").asText()).isEqualTo("PUBLISHED");
      assertThat(content.get("subActivities")).isEmpty();
      assertThat(content.get("rights")).isNotEmpty();
    }

    @Test
    void thenThePresentationOfTheMainActivityShouldListItsSubActivities() throws Exception {
      BddLogger.when("a student reads the presentation of the main activity");
      var presentation =
          readJson(
              get(uri -> uri.path(PRESENTATION_PATH).build(MAIN_ACTIVITY_ID), studentHeaders()));

      BddLogger.then("the sub-activities should be listed in order");
      assertThat(idsOf(presentation.get("subActivities"))).isEqualTo(SUB_ACTIVITY_IDS);
    }

    @Test
    void thenThePresentationOfASubActivityShouldPointToItsMainActivity() throws Exception {
      BddLogger.when("a student reads the presentation of a sub-activity");
      var presentation =
          readJson(
              get(
                  uri -> uri.path(PRESENTATION_PATH).build(FIRST_SUB_ACTIVITY_ID),
                  studentHeaders()));

      BddLogger.then("the presentation should point to the main activity");
      assertThat(presentation.get("mainActivityId").asText())
          .isEqualTo(MAIN_ACTIVITY_ID.toString());
      assertThat(presentation.get("activityStatus").asText()).isEqualTo("PUBLISHED");
    }
  }

  @Nested
  class GivenAnOperationReservedToMainActivities {

    @Test
    void thenSubscribingToASubActivityShouldBeRejected() {
      BddLogger.when("a student subscribes to a sub-activity");
      BddLogger.then("it should be rejected with 409");
      expectSubActivityOperationNotAllowed(
          webTestClient
              .post()
              .uri("/me/activity-progress/subscribe/{activityId}", FIRST_SUB_ACTIVITY_ID)
              .headers(studentHeaders())
              .contentType(MediaType.APPLICATION_JSON)
              .bodyValue(Map.of()));
    }

    @Test
    void thenUnpublishingASubActivityShouldBeRejected() {
      BddLogger.then("unpublishing a sub-activity should be rejected with 409");
      expectSubActivityOperationNotAllowed(
          webTestClient
              .post()
              .uri(BASE_PATH + "/unpublish/{activityId}", FIRST_SUB_ACTIVITY_ID)
              .headers(staffHeaders()));
    }

    @Test
    void thenCreatingADraftFromASubActivityShouldBeRejected() {
      BddLogger.then("creating a draft from a sub-activity should be rejected with 409");
      expectSubActivityOperationNotAllowed(
          webTestClient
              .post()
              .uri(BASE_PATH + "/create-draft/{activityId}", FIRST_SUB_ACTIVITY_ID)
              .headers(staffHeaders()));
    }

    @Test
    void thenDuplicatingASubActivityShouldBeRejected() {
      BddLogger.then("duplicating a sub-activity should be rejected with 409");
      expectSubActivityOperationNotAllowed(
          webTestClient
              .post()
              .uri(BASE_PATH + "/duplicate/{activityId}", FIRST_SUB_ACTIVITY_ID)
              .headers(staffHeaders())
              .contentType(MediaType.APPLICATION_JSON)
              .bodyValue(Map.of("title", "Copie")));
    }

    @Test
    void thenDeletingASubActivityDefinitivelyShouldBeRejected() {
      BddLogger.then("deleting a sub-activity definitively should be rejected with 409");
      expectSubActivityOperationNotAllowed(
          webTestClient
              .delete()
              .uri(BASE_PATH + "/{activityId}", FIRST_SUB_ACTIVITY_ID)
              .headers(superAdminHeaders()));
    }

    @Test
    void thenTheDashboardOfASubActivityShouldBeRejected() {
      BddLogger.then("the dashboard of a sub-activity should be rejected with 409");
      expectSubActivityOperationNotAllowed(
          webTestClient
              .get()
              .uri(BASE_PATH + "/{activityId}/dashboard", FIRST_SUB_ACTIVITY_ID)
              .headers(staffHeaders()));
      expectSubActivityOperationNotAllowed(
          webTestClient
              .get()
              .uri(BASE_PATH + "/{activityId}/dashboard/inactive-students", FIRST_SUB_ACTIVITY_ID)
              .headers(staffHeaders()));
    }

    @Test
    void thenManagingTheAuthorizationsOfASubActivityShouldBeRejected() {
      BddLogger.then("reading or defining the authorizations of a sub-activity should be rejected");
      expectSubActivityOperationNotAllowed(
          webTestClient
              .get()
              .uri(BASE_PATH + "/PUBLISHED/{activityId}/authorizations", FIRST_SUB_ACTIVITY_ID)
              .headers(staffHeaders()));
      expectSubActivityOperationNotAllowed(
          webTestClient
              .put()
              .uri(BASE_PATH + "/PUBLISHED/{activityId}/authorizations", FIRST_SUB_ACTIVITY_ID)
              .headers(staffHeaders())
              .contentType(MediaType.APPLICATION_JSON)
              .bodyValue(Map.of("authorizations", List.of())));
    }
  }

  @Nested
  class GivenADuplicatedMainActivity {

    @Test
    void thenTheSubActivitiesShouldBeDuplicatedEditedAndDeletedWithTheirMainDraft()
        throws Exception {
      BddLogger.given("a staff duplicating the main activity");
      var duplicateId = duplicate(MAIN_ACTIVITY_ID, "Copie du projet professionnel");

      BddLogger.when("reading the content of the duplicate");
      var duplicateContent =
          readJson(get(uri -> uri.path(CONTENT_PATH).build("DRAFT", duplicateId), staffHeaders()));

      BddLogger.then("its sub-activities should be new drafts keeping the titles and the order");
      var duplicatedSubActivities = duplicateContent.get("subActivities");
      assertThat(duplicatedSubActivities).hasSize(3);
      assertThat(
              StreamSupport.stream(duplicatedSubActivities.spliterator(), false)
                  .map(subActivity -> subActivity.get("title").asText())
                  .toList())
          .containsExactly(
              "Faire le point sur ses envies",
              "Explorer les métiers ciblés",
              "Formaliser son projet");
      var duplicatedSubActivityIds = idsOf(duplicatedSubActivities);
      assertThat(duplicatedSubActivityIds).doesNotContainAnyElementsOf(SUB_ACTIVITY_IDS);
      var duplicatedSubActivityId = duplicatedSubActivityIds.getFirst();

      BddLogger.and("a sub-activity draft should carry the rights held on the duplicate");
      var subActivityContent =
          readJson(
              get(
                  uri -> uri.path(CONTENT_PATH).build("DRAFT", duplicatedSubActivityId),
                  staffHeaders()));
      assertThat(subActivityContent.get("mainActivityId").asText())
          .isEqualTo(duplicateId.toString());
      assertThat(subActivityContent.get("rights")).isNotEmpty();

      BddLogger.and("its sub-activity fields should be editable");
      patchDraft(duplicatedSubActivityId, Map.of("title", "Faire le point sur ses priorités"))
          .expectStatus()
          .isOk();

      BddLogger.and("the fields of a main activity should be rejected on it");
      patchDraft(duplicatedSubActivityId, Map.of("thematic", "OTHER"))
          .expectStatus()
          .isBadRequest()
          .expectBody()
          .jsonPath("$.code")
          .isEqualTo("SUB_ACTIVITY_FIELD_NOT_ALLOWED");

      BddLogger.and("the sub-activity draft should neither be published nor deleted on its own");
      expectSubActivityOperationNotAllowed(
          webTestClient
              .post()
              .uri(BASE_PATH + "/publish/{draftId}", duplicatedSubActivityId)
              .headers(staffHeaders()));
      expectSubActivityOperationNotAllowed(
          webTestClient
              .delete()
              .uri(BASE_PATH + "/draft/{draftId}", duplicatedSubActivityId)
              .headers(staffHeaders()));

      BddLogger.when("deleting the duplicate");
      webTestClient
          .delete()
          .uri(BASE_PATH + "/draft/{draftId}", duplicateId)
          .headers(staffHeaders())
          .exchange()
          .expectStatus()
          .is2xxSuccessful();

      BddLogger.then("its sub-activity drafts should be deleted too");
      duplicatedSubActivityIds.forEach(
          id ->
              webTestClient
                  .get()
                  .uri(CONTENT_PATH, "DRAFT", id)
                  .headers(staffHeaders())
                  .exchange()
                  .expectStatus()
                  .isNotFound());
    }
  }

  @Nested
  class GivenAMainActivityDeletedDefinitively {

    @Test
    void thenItsSubActivitiesShouldBeDeletedToo() {
      BddLogger.given("a published main activity with two sub-activities");
      BddLogger.when("a super admin deletes the main activity definitively");
      webTestClient
          .delete()
          .uri(BASE_PATH + "/{activityId}", DELETABLE_MAIN_ACTIVITY_ID)
          .headers(superAdminHeaders())
          .exchange()
          .expectStatus()
          .is2xxSuccessful();

      BddLogger.then("its sub-activities should not be found anymore");
      DELETABLE_SUB_ACTIVITY_IDS.forEach(
          id ->
              webTestClient
                  .get()
                  .uri(CONTENT_PATH, "PUBLISHED", id)
                  .headers(staffHeaders())
                  .exchange()
                  .expectStatus()
                  .isNotFound());
    }
  }

  private UUID duplicate(UUID activityId, String title) throws Exception {
    var body =
        webTestClient
            .post()
            .uri(BASE_PATH + "/duplicate/{activityId}", activityId)
            .headers(staffHeaders())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(Map.of("title", title))
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody(String.class)
            .returnResult()
            .getResponseBody();
    return UUID.fromString(readJson(body).get("draftId").asText());
  }

  private WebTestClient.ResponseSpec patchDraft(UUID draftId, Map<String, Object> body) {
    return webTestClient
        .patch()
        .uri(BASE_PATH + "/{draftId}", draftId)
        .headers(staffHeaders())
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(body)
        .exchange();
  }

  private String get(Function<UriBuilder, URI> uri, Consumer<HttpHeaders> headers) {
    return webTestClient
        .get()
        .uri(uri)
        .headers(headers)
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody(String.class)
        .returnResult()
        .getResponseBody();
  }

  private String getAllPages(String path, Consumer<HttpHeaders> headers) throws Exception {
    var pages = new StringBuilder();
    var totalPages = 1;
    for (var page = 0; page < totalPages; page++) {
      var pageNumber = page;
      var body =
          get(
              uri ->
                  uri.path(path).queryParam("page", pageNumber).queryParam("pageSize", 100).build(),
              headers);
      totalPages = readJson(body).get("page").get("totalPages").asInt();
      pages.append(body);
    }
    return pages.toString();
  }

  private void expectSubActivityOperationNotAllowed(WebTestClient.RequestHeadersSpec<?> request) {
    request
        .exchange()
        .expectStatus()
        .isEqualTo(409)
        .expectBody()
        .jsonPath("$.code")
        .isEqualTo(SUB_ACTIVITY_OPERATION_NOT_ALLOWED);
  }

  private JsonNode readJson(String body) throws Exception {
    return objectMapper.readTree(body);
  }

  private List<UUID> idsOf(JsonNode subActivities) {
    return StreamSupport.stream(subActivities.spliterator(), false)
        .map(subActivity -> UUID.fromString(subActivity.get("id").asText()))
        .toList();
  }

  private Consumer<HttpHeaders> studentHeaders() {
    return headers -> withContext(headers, studentPayload, studentSignature);
  }

  private Consumer<HttpHeaders> staffHeaders() {
    return headers -> withContext(headers, staffPayload, staffSignature);
  }

  private Consumer<HttpHeaders> superAdminHeaders() {
    return headers -> withContext(headers, superAdminPayload, superAdminSignature);
  }

  private void withContext(HttpHeaders headers, String payload, String signature) {
    headers.add(HttpHeaders.ACCEPT_LANGUAGE, ELanguage.FRENCH.getCode());
    headers.add(AvenirsSecurityHeaders.SIGNED_CONTEXT, payload);
    headers.add(AvenirsSecurityHeaders.CONTEXT_SIGNATURE, signature);
  }
}
