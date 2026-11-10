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
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.StreamSupport;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.util.UriBuilder;

class SubActivityControllerIT extends ContainerConfigurationTest {

  private static final String BASE_PATH = "/me/activities";
  private static final String CONTENT_PATH = BASE_PATH + "/{activityStatus}/{activityId}/content";
  private static final String PRESENTATION_PATH =
      BASE_PATH + "/PUBLISHED/{activityId}/presentation";
  private static final String SUB_ACTIVITIES_PATH = BASE_PATH + "/draft/{draftId}/sub-activities";
  private static final String SUB_ACTIVITIES_ORDER_PATH = SUB_ACTIVITIES_PATH + "/order";

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

  private static final UUID STAFF_ID = UUID.fromString("57ff122f-ff76-4d95-b6c1-61a4efeabd80");
  private static final UUID ANOTHER_STAFF_ID =
      UUID.fromString("5fcd76c5-017c-4d61-99d2-62a6f199c94e");

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

  @Value("${user.anotherStaff.payload}")
  private String anotherStaffPayload;

  @Value("${user.anotherStaff.signature}")
  private String anotherStaffSignature;

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

  @Nested
  class GivenAMainDraft {

    private UUID mainDraftId;

    @BeforeEach
    void createMainDraft() throws Exception {
      BddLogger.given("a staff owning a main draft");
      mainDraftId = createDraft("Projet avec sous-activités");
    }

    @AfterEach
    void deleteMainDraft() {
      webTestClient
          .delete()
          .uri(BASE_PATH + "/draft/{draftId}", mainDraftId)
          .headers(staffHeaders())
          .exchange()
          .expectStatus()
          .is2xxSuccessful();
    }

    @Test
    void thenNewSubActivitiesShouldBeAddedLastAndBeEditable() throws Exception {
      BddLogger.when("adding four sub-activities");
      var firstId = createSubActivity(mainDraftId, "Première étape");
      var secondId = createSubActivity(mainDraftId, "Deuxième étape");
      var thirdId = createSubActivity(mainDraftId, "Troisième étape");
      var lastId = createSubActivity(mainDraftId, "Dernière étape");

      BddLogger.then("they should be listed in their order of creation");
      var content =
          readJson(get(uri -> uri.path(CONTENT_PATH).build("DRAFT", mainDraftId), staffHeaders()));
      assertThat(idsOf(content.get("subActivities")))
          .containsExactly(firstId, secondId, thirdId, lastId);

      BddLogger.and("the last one should point to its main draft and carry its rights");
      var subActivityContent =
          readJson(get(uri -> uri.path(CONTENT_PATH).build("DRAFT", lastId), staffHeaders()));
      assertThat(subActivityContent.get("mainActivityId").asText())
          .isEqualTo(mainDraftId.toString());
      assertThat(subActivityContent.get("title").asText()).isEqualTo("Dernière étape");
      assertThat(subActivityContent.get("rights")).isNotEmpty();

      BddLogger.and("it should be editable through the draft update endpoint");
      patchDraft(lastId, Map.of("description", "<p>Consigne</p>")).expectStatus().isOk();
    }

    @Test
    void thenAddingASubActivityToASubActivityDraftShouldBeRejected() throws Exception {
      var subActivityId = createSubActivity(mainDraftId, "Première étape");

      BddLogger.then("adding a sub-activity to a sub-activity draft should be rejected with 409");
      expectSubActivityOperationNotAllowed(
          webTestClient
              .post()
              .uri(SUB_ACTIVITIES_PATH, subActivityId)
              .headers(staffHeaders())
              .contentType(MediaType.APPLICATION_JSON)
              .bodyValue(Map.of("title", "Étape imbriquée")));
    }

    @Test
    void thenAStaffWithoutRightOnTheMainDraftShouldBeRejected() {
      BddLogger.when("another staff adds a sub-activity to the main draft");
      BddLogger.then("it should be rejected with 403");
      postSubActivity(mainDraftId, Map.of("title", "Étape"), anotherStaffHeaders())
          .expectStatus()
          .isForbidden();
    }

    @Test
    void thenABlankTitleShouldBeRejected() {
      BddLogger.when("adding a sub-activity with a blank title");
      BddLogger.then("it should be rejected with 400");
      postSubActivity(mainDraftId, Map.of("title", " "), staffHeaders())
          .expectStatus()
          .isBadRequest();
    }

    @Test
    void thenTheTitleShouldBeLimitedToEightyCharacters() throws Exception {
      BddLogger.then("a title of 81 characters should be rejected with 400");
      postSubActivity(mainDraftId, Map.of("title", "a".repeat(81)), staffHeaders())
          .expectStatus()
          .isBadRequest();

      BddLogger.and("a title of 80 characters should be accepted");
      createSubActivity(mainDraftId, "a".repeat(80));
    }

    @Test
    void thenTheRightsOnASubActivityShouldFollowThoseOfTheMainDraft() throws Exception {
      var subActivityId = createSubActivity(mainDraftId, "Première étape");

      BddLogger.then("another staff without right should not edit the sub-activity");
      patchDraft(subActivityId, Map.of("title", "Titre revu"), anotherStaffHeaders())
          .expectStatus()
          .isForbidden();

      BddLogger.when("the edit right on the main draft is granted to that staff");
      webTestClient
          .put()
          .uri(BASE_PATH + "/DRAFT/{draftId}/authorizations", mainDraftId)
          .headers(staffHeaders())
          .contentType(MediaType.APPLICATION_JSON)
          .bodyValue(
              Map.of(
                  "authorizations",
                  List.of(
                      Map.of(
                          "staffId",
                          STAFF_ID,
                          "rights",
                          List.of(
                              "LEADER",
                              "EDIT",
                              "MANAGE_AUTHORIZATIONS",
                              "FEEDBACK",
                              "EVALUATION",
                              "PUBLISH")),
                      Map.of("staffId", ANOTHER_STAFF_ID, "rights", List.of("EDIT")))))
          .exchange()
          .expectStatus()
          .isOk();

      BddLogger.then("that staff should edit the sub-activity and add a new one");
      patchDraft(subActivityId, Map.of("title", "Titre revu"), anotherStaffHeaders())
          .expectStatus()
          .isOk();
      postSubActivity(mainDraftId, Map.of("title", "Deuxième étape"), anotherStaffHeaders())
          .expectStatus()
          .isCreated();
    }

    @Test
    void thenTheSubActivitiesShouldBeReordered() throws Exception {
      var firstId = createSubActivity(mainDraftId, "Première étape");
      var secondId = createSubActivity(mainDraftId, "Deuxième étape");
      var thirdId = createSubActivity(mainDraftId, "Troisième étape");

      BddLogger.when("reordering the sub-activities");
      putSubActivitiesOrder(mainDraftId, List.of(thirdId, firstId, secondId), staffHeaders())
          .expectStatus()
          .isNoContent();

      BddLogger.then("they should be listed in the new order");
      var content =
          readJson(get(uri -> uri.path(CONTENT_PATH).build("DRAFT", mainDraftId), staffHeaders()));
      assertThat(idsOf(content.get("subActivities"))).containsExactly(thirdId, firstId, secondId);

      BddLogger.and("a sub-activity added afterwards should be added last");
      var lastId = createSubActivity(mainDraftId, "Dernière étape");
      var updatedContent =
          readJson(get(uri -> uri.path(CONTENT_PATH).build("DRAFT", mainDraftId), staffHeaders()));
      assertThat(idsOf(updatedContent.get("subActivities")))
          .containsExactly(thirdId, firstId, secondId, lastId);
    }

    @Test
    void thenAnOrderNotMatchingTheSubActivitiesShouldBeRejected() throws Exception {
      var firstId = createSubActivity(mainDraftId, "Première étape");
      var secondId = createSubActivity(mainDraftId, "Deuxième étape");

      BddLogger.then("a missing, unknown or duplicated sub-activity should be rejected with 400");
      for (var order :
          List.of(
              List.of(secondId),
              List.of(secondId, firstId, UUID.randomUUID()),
              List.of(secondId, UUID.randomUUID()),
              List.of(secondId, secondId))) {
        putSubActivitiesOrder(mainDraftId, order, staffHeaders())
            .expectStatus()
            .isBadRequest()
            .expectBody()
            .jsonPath("$.code")
            .isEqualTo("SUB_ACTIVITY_ORDER_MISMATCH");
      }

      BddLogger.and("the order should be left unchanged");
      var content =
          readJson(get(uri -> uri.path(CONTENT_PATH).build("DRAFT", mainDraftId), staffHeaders()));
      assertThat(idsOf(content.get("subActivities"))).containsExactly(firstId, secondId);
    }

    @Test
    void thenReorderingWithoutRightOnTheMainDraftShouldBeRejected() throws Exception {
      var firstId = createSubActivity(mainDraftId, "Première étape");
      var secondId = createSubActivity(mainDraftId, "Deuxième étape");

      BddLogger.when("another staff reorders the sub-activities of the main draft");
      BddLogger.then("it should be rejected with 403");
      putSubActivitiesOrder(mainDraftId, List.of(secondId, firstId), anotherStaffHeaders())
          .expectStatus()
          .isForbidden();
    }

    @Test
    void thenReorderingTheSubActivitiesOfASubActivityDraftShouldBeRejected() throws Exception {
      var subActivityId = createSubActivity(mainDraftId, "Première étape");

      BddLogger.then("reordering from a sub-activity draft should be rejected with 409");
      putSubActivitiesOrder(subActivityId, List.of(), staffHeaders())
          .expectStatus()
          .isEqualTo(409)
          .expectBody()
          .jsonPath("$.code")
          .isEqualTo(SUB_ACTIVITY_OPERATION_NOT_ALLOWED);
    }
  }

  @Nested
  class GivenAnUnknownMainDraft {

    @Test
    void thenAddingASubActivityShouldReturnNotFound() {
      BddLogger.when("adding a sub-activity to a draft that does not exist");
      BddLogger.then("it should return 404");
      postSubActivity(UUID.randomUUID(), Map.of("title", "Étape"), staffHeaders())
          .expectStatus()
          .isNotFound();
    }

    @Test
    void thenAddingASubActivityToAPublishedActivityWithoutDraftShouldReturnNotFound() {
      BddLogger.when("adding a sub-activity to a published activity that has no draft");
      BddLogger.then("it should return 404");
      postSubActivity(MAIN_ACTIVITY_ID, Map.of("title", "Étape"), staffHeaders())
          .expectStatus()
          .isNotFound();
    }

    @Test
    void thenReorderingItsSubActivitiesShouldReturnNotFound() {
      BddLogger.when("reordering the sub-activities of a draft that does not exist");
      BddLogger.then("it should return 404");
      putSubActivitiesOrder(UUID.randomUUID(), List.of(), staffHeaders())
          .expectStatus()
          .isNotFound();
    }
  }

  @Nested
  class GivenAMainDraftReadyToBePublished {

    private UUID mainDraftId;

    @BeforeEach
    void createMainDraft() throws Exception {
      BddLogger.given("a staff owning a main draft ready to be published");
      mainDraftId = createDraft("Projet à publier");
      patchDraft(
              mainDraftId,
              Map.of("thematic", "OTHER", "summary", "Résumé", "description", "<p>Description</p>"))
          .expectStatus()
          .isOk();
    }

    @Test
    void thenPublishingItShouldPublishItsSubActivitiesInOrder() throws Exception {
      var firstId = createPublishableSubActivity("Première étape");
      var secondId = createPublishableSubActivity("Deuxième étape");
      var lastId = createPublishableSubActivity("Dernière étape");

      BddLogger.when("publishing the main draft");
      publish(mainDraftId).expectStatus().isOk();

      BddLogger.then("the published activity should list its sub-activities in order");
      assertThat(publishedSubActivityIds(mainDraftId)).containsExactly(firstId, secondId, lastId);

      BddLogger.and("a published sub-activity should point to its main activity");
      var subActivityContent =
          readJson(get(uri -> uri.path(CONTENT_PATH).build("PUBLISHED", lastId), staffHeaders()));
      assertThat(subActivityContent.get("mainActivityId").asText())
          .isEqualTo(mainDraftId.toString());
      assertThat(subActivityContent.get("title").asText()).isEqualTo("Dernière étape");
      assertThat(subActivityContent.get("description").asText()).contains("Consigne");

      BddLogger.and("the main draft and its sub-activity drafts should be gone");
      List.of(mainDraftId, firstId, secondId, lastId).forEach(this::expectNoDraft);
    }

    @Test
    void thenASubActivityWithoutDescriptionShouldBlockThePublication() throws Exception {
      createSubActivity(mainDraftId, "Étape sans consigne");

      BddLogger.when("publishing the main draft");
      BddLogger.then("it should be rejected with 400");
      publish(mainDraftId)
          .expectStatus()
          .isBadRequest()
          .expectBody()
          .jsonPath("$.code")
          .isEqualTo("NOT_BLANK");

      BddLogger.and("the main draft should remain and nothing should be published");
      get(uri -> uri.path(CONTENT_PATH).build("DRAFT", mainDraftId), staffHeaders());
      webTestClient
          .get()
          .uri(CONTENT_PATH, "PUBLISHED", mainDraftId)
          .headers(staffHeaders())
          .exchange()
          .expectStatus()
          .isNotFound();

      deleteMainDraft();
    }

    @Test
    void thenEditingAPublishedActivityWithoutStudentShouldKeepItsSubActivities() throws Exception {
      var firstId = createPublishableSubActivity("Première étape");
      publish(mainDraftId).expectStatus().isOk();

      BddLogger.when("editing the published activity and adding a sub-activity");
      createDraftFromPublishedActivity();
      var secondId = createPublishableSubActivity("Deuxième étape");
      publish(mainDraftId).expectStatus().isOk();

      BddLogger.then("both sub-activities should be published in order");
      assertThat(publishedSubActivityIds(mainDraftId)).containsExactly(firstId, secondId);
    }

    @Test
    void thenReorderingThePublishedSubActivitiesShouldPublishTheNewOrder() throws Exception {
      var firstId = createPublishableSubActivity("Première étape");
      var secondId = createPublishableSubActivity("Deuxième étape");
      var lastId = createPublishableSubActivity("Dernière étape");
      publish(mainDraftId).expectStatus().isOk();

      BddLogger.when("editing the published activity and reordering its sub-activities");
      createDraftFromPublishedActivity();
      putSubActivitiesOrder(mainDraftId, List.of(lastId, firstId, secondId), staffHeaders())
          .expectStatus()
          .isNoContent();
      publish(mainDraftId).expectStatus().isOk();

      BddLogger.then("the published activity should list its sub-activities in the new order");
      assertThat(publishedSubActivityIds(mainDraftId)).containsExactly(lastId, firstId, secondId);
    }

    @Test
    void thenEditingAPublishedActivityWithEnrolledStudentsShouldUpdateItsSubActivities()
        throws Exception {
      var firstId = createPublishableSubActivity("Première étape");
      publish(mainDraftId).expectStatus().isOk();

      BddLogger.and("a student enrolled in the published activity");
      webTestClient
          .post()
          .uri("/me/activity-progress/subscribe/{activityId}", mainDraftId)
          .headers(studentHeaders())
          .contentType(MediaType.APPLICATION_JSON)
          .bodyValue(Map.of())
          .exchange()
          .expectStatus()
          .isCreated();

      BddLogger.when("editing a sub-activity of the published activity");
      createDraftFromPublishedActivity();
      patchDraft(firstId, Map.of("title", "Première étape revue")).expectStatus().isOk();

      BddLogger.then("adding a new sub-activity should be rejected with 409");
      postSubActivity(mainDraftId, Map.of("title", "Étape en trop"), staffHeaders())
          .expectStatus()
          .isEqualTo(409)
          .expectBody()
          .jsonPath("$.code")
          .isEqualTo("SUB_ACTIVITY_ADDITION_NOT_ALLOWED");

      BddLogger.when("publishing the main draft");
      publish(mainDraftId).expectStatus().isOk();

      BddLogger.then("the published sub-activity should reflect its draft");
      assertThat(publishedSubActivityIds(mainDraftId)).containsExactly(firstId);
      var subActivityContent =
          readJson(get(uri -> uri.path(CONTENT_PATH).build("PUBLISHED", firstId), staffHeaders()));
      assertThat(subActivityContent.get("title").asText()).isEqualTo("Première étape revue");
    }

    @Test
    void thenModifiedSubActivitiesShouldBeNotifiedOnceAsPartOfTheirMainActivity() throws Exception {
      var firstId = createPublishableSubActivity("Première étape");
      var secondId = createPublishableSubActivity("Deuxième étape");
      createPublishableSubActivity("Dernière étape");
      publish(mainDraftId).expectStatus().isOk();

      BddLogger.and("a student enrolled in the published activity with notifications enabled");
      var declaredActivityId = subscribe(mainDraftId);
      enableStudentNotifications();

      BddLogger.when("modifying the main activity and two of its sub-activities in a draft");
      createDraftFromPublishedActivity();
      patchDraft(mainDraftId, Map.of("title", "Projet à publier revu")).expectStatus().isOk();
      patchDraft(firstId, Map.of("title", "Première étape revue")).expectStatus().isOk();
      patchDraft(secondId, Map.of("description", "<p>Nouvelle consigne</p>")).expectStatus().isOk();

      BddLogger.then("saving the draft should not notify the student");
      assertThat(studentNotificationsOf(declaredActivityId)).isEmpty();

      BddLogger.when("publishing the main draft");
      publish(mainDraftId).expectStatus().isOk();

      BddLogger.then("the student should get a single notification about the main activity");
      var notifications = studentNotificationsOf(declaredActivityId);
      assertThat(notifications).hasSize(1);
      var notification = notifications.getFirst();
      assertThat(notification.get("type").asText()).isEqualTo("ACTIVITY_MODIFIED");

      BddLogger.and("it should mention the current title and the modified sub-activities");
      var parameters = notification.get("parameters");
      assertThat(parameters.get("activityTitle").asText()).isEqualTo("Projet à publier revu");
      assertThat(textsOf(parameters.get("updatedFields")))
          .containsExactly("ACTIVITY_TITLE", "SUB_ACTIVITIES");
      assertThat(textsOf(parameters.get("updatedSubActivityTitles")))
          .containsExactly("Première étape revue", "Deuxième étape");
    }

    @Test
    void thenASubActivityShouldBeConfigurableThroughTheDraftEndpointsAndPublished()
        throws Exception {
      BddLogger.when("adding a sub-activity with only a title");
      var subActivityId = createSubActivity(mainDraftId, "Étape à compléter");

      BddLogger.then("its detail should be readable before being completed");
      var initialContent =
          readJson(
              get(uri -> uri.path(CONTENT_PATH).build("DRAFT", subActivityId), staffHeaders()));
      assertThat(initialContent.get("title").asText()).isEqualTo("Étape à compléter");
      assertThat(initialContent.get("description").isNull()).isTrue();
      get(
          uri -> uri.path(BASE_PATH + "/DRAFT/{activityId}/presentation").build(subActivityId),
          staffHeaders());

      BddLogger.when("completing it through the draft update and file endpoints");
      patchDraft(
              subActivityId,
              Map.of(
                  "title", "Étape complétée",
                  "description", "<p>Consigne</p>",
                  "recommendedCompletionContexts", "En groupe",
                  "links", List.of("https://example.com/ressource"),
                  "enableCompletionPeriod", true,
                  "startDate", "2027-03-01",
                  "endDate", "2027-03-31"))
          .expectStatus()
          .isOk();
      uploadFile(subActivityId);

      BddLogger.then("the draft should hold its whole configuration");
      assertConfigured(
          readJson(
              get(uri -> uri.path(CONTENT_PATH).build("DRAFT", subActivityId), staffHeaders())));

      BddLogger.when("publishing the main draft");
      publish(mainDraftId).expectStatus().isOk();

      BddLogger.then("the published sub-activity should hold its whole configuration");
      assertConfigured(
          readJson(
              get(
                  uri -> uri.path(CONTENT_PATH).build("PUBLISHED", subActivityId),
                  staffHeaders())));
    }

    @Test
    void thenTheFieldsOfAMainActivityShouldBeRejectedOnASubActivity() throws Exception {
      var subActivityId = createSubActivity(mainDraftId, "Première étape");

      BddLogger.then("the out-of-scope settings should be rejected with 400");
      Map.<String, Object>of(
              "thematic",
              "OTHER",
              "summary",
              "Résumé",
              "traceAllowedAssociations",
              1,
              "feedbackAllowedIterations",
              1,
              "enableReflection",
              true,
              "national",
              true)
          .forEach(
              (field, value) ->
                  patchDraft(subActivityId, Map.of(field, value))
                      .expectStatus()
                      .isBadRequest()
                      .expectBody()
                      .jsonPath("$.code")
                      .isEqualTo("SUB_ACTIVITY_FIELD_NOT_ALLOWED"));

      deleteMainDraft();
    }

    @Test
    void thenThePeriodOfASubActivityShouldStayWithinTheOneOfItsMainDraft() throws Exception {
      BddLogger.given("a main draft with a period and a sub-activity");
      patchPeriod(mainDraftId, "2027-03-01", "2027-03-31").expectStatus().isOk();
      var subActivityId = createSubActivity(mainDraftId, "Première étape");

      BddLogger.then("a period outside the one of the main draft should be rejected with 400");
      patchPeriod(subActivityId, "2027-02-28", "2027-03-15")
          .expectStatus()
          .isBadRequest()
          .expectBody()
          .jsonPath("$.code")
          .isEqualTo("SUB_ACTIVITY_PERIOD_OUTSIDE_MAIN_ACTIVITY");

      BddLogger.and("a period matching the bounds of the main draft should be accepted");
      patchPeriod(subActivityId, "2027-03-01", "2027-03-31").expectStatus().isOk();

      deleteMainDraft();
    }

    @Test
    void thenThePeriodOfAMainDraftShouldCoverTheOnesOfItsSubActivities() throws Exception {
      BddLogger.given("a sub-activity with a period");
      var subActivityId = createSubActivity(mainDraftId, "Première étape");
      patchPeriod(subActivityId, "2027-03-10", "2027-03-20").expectStatus().isOk();

      BddLogger.then("a main period not covering it should be rejected with 400");
      patchPeriod(mainDraftId, "2027-03-15", "2027-03-31")
          .expectStatus()
          .isBadRequest()
          .expectBody()
          .jsonPath("$.code")
          .isEqualTo("ACTIVITY_PERIOD_EXCLUDES_SUB_ACTIVITIES");

      BddLogger.and("a main period covering it should be accepted");
      patchPeriod(mainDraftId, "2027-03-10", "2027-03-20").expectStatus().isOk();

      deleteMainDraft();
    }

    private void assertConfigured(JsonNode content) {
      assertThat(content.get("title").asText()).isEqualTo("Étape complétée");
      assertThat(content.get("description").asText()).contains("Consigne");
      assertThat(content.get("recommendedCompletionContexts").asText()).isEqualTo("En groupe");
      assertThat(content.get("startDate").asText()).isEqualTo("2027-03-01");
      assertThat(content.get("endDate").asText()).isEqualTo("2027-03-31");
      assertThat(content.get("links").get(0).asText()).isEqualTo("https://example.com/ressource");
      assertThat(content.get("files")).hasSize(1);
      assertThat(content.get("rights")).isNotEmpty();
    }

    private WebTestClient.ResponseSpec patchPeriod(UUID draftId, String startDate, String endDate) {
      return patchDraft(
          draftId,
          Map.of("enableCompletionPeriod", true, "startDate", startDate, "endDate", endDate));
    }

    private void uploadFile(UUID draftId) {
      var multipart = new MultipartBodyBuilder();
      multipart
          .part("file", new ByteArrayResource("contenu".getBytes(StandardCharsets.UTF_8)))
          .filename("consigne.pdf")
          .contentType(MediaType.APPLICATION_PDF);
      webTestClient
          .post()
          .uri(BASE_PATH + "/draft/{draftId}/files", draftId)
          .headers(staffHeaders())
          .contentType(MediaType.MULTIPART_FORM_DATA)
          .body(BodyInserters.fromMultipartData(multipart.build()))
          .exchange()
          .expectStatus()
          .isCreated();
    }

    private void deleteMainDraft() {
      webTestClient
          .delete()
          .uri(BASE_PATH + "/draft/{draftId}", mainDraftId)
          .headers(staffHeaders())
          .exchange()
          .expectStatus()
          .is2xxSuccessful();
    }

    private UUID createPublishableSubActivity(String title) throws Exception {
      var subActivityId = createSubActivity(mainDraftId, title);
      patchDraft(subActivityId, Map.of("description", "<p>Consigne</p>")).expectStatus().isOk();
      return subActivityId;
    }

    private void createDraftFromPublishedActivity() {
      webTestClient
          .post()
          .uri(BASE_PATH + "/create-draft/{activityId}", mainDraftId)
          .headers(staffHeaders())
          .exchange()
          .expectStatus()
          .isOk();
    }

    private WebTestClient.ResponseSpec publish(UUID draftId) {
      return webTestClient
          .post()
          .uri(BASE_PATH + "/publish/{draftId}", draftId)
          .headers(staffHeaders())
          .exchange();
    }

    private UUID subscribe(UUID activityId) throws Exception {
      var body =
          webTestClient
              .post()
              .uri("/me/activity-progress/subscribe/{activityId}", activityId)
              .headers(studentHeaders())
              .contentType(MediaType.APPLICATION_JSON)
              .bodyValue(Map.of())
              .exchange()
              .expectStatus()
              .isCreated()
              .expectBody(String.class)
              .returnResult()
              .getResponseBody();
      return UUID.fromString(readJson(body).get("createdItemId").asText());
    }

    private void enableStudentNotifications() {
      webTestClient
          .patch()
          .uri("/me/preferences/notification")
          .headers(studentHeaders())
          .contentType(MediaType.APPLICATION_JSON)
          .bodyValue(Map.of("notificationEnabled", true))
          .exchange()
          .expectStatus()
          .isNoContent();
    }

    private List<JsonNode> studentNotificationsOf(UUID elementId) throws Exception {
      var notifications =
          readJson(
                  get(
                      uri ->
                          uri.path("/me/notifications/STUDENT").queryParam("pageSize", 100).build(),
                      studentHeaders()))
              .get("data");
      return StreamSupport.stream(notifications.spliterator(), false)
          .filter(
              notification -> elementId.toString().equals(notification.get("elementId").asText()))
          .toList();
    }

    private List<String> textsOf(JsonNode values) {
      return StreamSupport.stream(values.spliterator(), false).map(JsonNode::asText).toList();
    }

    private List<UUID> publishedSubActivityIds(UUID activityId) throws Exception {
      return idsOf(
          readJson(
                  get(uri -> uri.path(CONTENT_PATH).build("PUBLISHED", activityId), staffHeaders()))
              .get("subActivities"));
    }

    private void expectNoDraft(UUID draftId) {
      webTestClient
          .get()
          .uri(CONTENT_PATH, "DRAFT", draftId)
          .headers(staffHeaders())
          .exchange()
          .expectStatus()
          .isNotFound();
    }
  }

  private UUID createDraft(String title) throws Exception {
    var body =
        webTestClient
            .post()
            .uri(BASE_PATH + "/draft")
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

  private UUID createSubActivity(UUID draftId, String title) throws Exception {
    var body =
        postSubActivity(draftId, Map.of("title", title), staffHeaders())
            .expectStatus()
            .isCreated()
            .expectBody(String.class)
            .returnResult()
            .getResponseBody();
    return UUID.fromString(readJson(body).get("draftId").asText());
  }

  private WebTestClient.ResponseSpec postSubActivity(
      UUID draftId, Map<String, Object> body, Consumer<HttpHeaders> headers) {
    return webTestClient
        .post()
        .uri(SUB_ACTIVITIES_PATH, draftId)
        .headers(headers)
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(body)
        .exchange();
  }

  private WebTestClient.ResponseSpec putSubActivitiesOrder(
      UUID draftId, List<UUID> subActivityIds, Consumer<HttpHeaders> headers) {
    return webTestClient
        .put()
        .uri(SUB_ACTIVITIES_ORDER_PATH, draftId)
        .headers(headers)
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(subActivityIds)
        .exchange();
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
    return patchDraft(draftId, body, staffHeaders());
  }

  private WebTestClient.ResponseSpec patchDraft(
      UUID draftId, Map<String, Object> body, Consumer<HttpHeaders> headers) {
    return webTestClient
        .patch()
        .uri(BASE_PATH + "/{draftId}", draftId)
        .headers(headers)
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

  private Consumer<HttpHeaders> anotherStaffHeaders() {
    return headers -> withContext(headers, anotherStaffPayload, anotherStaffSignature);
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
