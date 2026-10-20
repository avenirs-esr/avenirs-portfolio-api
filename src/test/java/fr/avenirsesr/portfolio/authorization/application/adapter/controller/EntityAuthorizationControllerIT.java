package fr.avenirsesr.portfolio.authorization.application.adapter.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.avenirsesr.portfolio.authorization.application.adapter.request.EntityAuthorizationsRequest;
import fr.avenirsesr.portfolio.authorization.domain.port.input.EntityAuthorizationService;
import fr.avenirsesr.portfolio.common.security.infrastructure.adapter.model.AvenirsSecurityHeaders;
import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import fr.avenirsesr.portfolio.shared.infrastructure.ContainerConfigurationTest;
import fr.avenirsesr.portfolio.shared.infrastructure.adapter.seeder.SeederRunner;
import fr.avenirsesr.portfolio.staff.activity.application.adapter.request.ActivityAuthorizationsRequest;
import fr.avenirsesr.portfolio.staff.activity.application.adapter.request.ActivityDraftCreationRequest;
import fr.avenirsesr.portfolio.staff.activity.application.adapter.request.ActivityDraftUpdateRequest;
import fr.avenirsesr.portfolio.staff.activity.application.adapter.request.StaffAuthorizationRequest;
import fr.avenirsesr.portfolio.staff.activity.domain.model.enums.EActivityRight;
import fr.avenirsesr.portfolio.staff.activity.domain.model.enums.EActivityThematic;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.IntStream;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

class EntityAuthorizationControllerIT extends ContainerConfigurationTest {

  private static final String QUERY_PATH = "/me/authorizations/{entityType}/query";
  private static final String LIST_ACCESS_PATH = "/me/authorizations/{entityType}/list-access";
  private static final String DRAFT_PATH = "/me/activities/draft";
  private static final String DRAFT_UPDATE_PATH = "/me/activities/{draftId}";
  private static final String PUBLISH_PATH = "/me/activities/publish/{draftId}";
  private static final String DRAFT_AUTHORIZATIONS_PATH =
      "/me/activities/DRAFT/{draftId}/authorizations";
  private static final UUID STAFF_ID = UUID.fromString("57ff122f-ff76-4d95-b6c1-61a4efeabd80");
  private static final UUID ANOTHER_STAFF_ID =
      UUID.fromString("5fcd76c5-017c-4d61-99d2-62a6f199c94e");
  private static final List<String> EVERY_AUTHORIZATION =
      List.of("LEADER", "EDIT", "MANAGE_AUTHORIZATIONS", "FEEDBACK", "EVALUATION", "PUBLISH");

  @Autowired private WebTestClient webTestClient;
  @Autowired private ObjectMapper objectMapper;

  @Value("${user.second.student.payload}")
  private String studentPayload;

  @Value("${user.second.student.signature}")
  private String studentSignature;

  @Value("${user.staff.payload}")
  private String staffPayload;

  @Value("${user.staff.signature}")
  private String staffSignature;

  @Value("${user.anotherStaff.payload}")
  private String anotherStaffPayload;

  @Value("${user.anotherStaff.signature}")
  private String anotherStaffSignature;

  @BeforeAll
  void setup(@Autowired SeederRunner seederRunner) {
    seederRunner.run();
  }

  @Nested
  class GivenAuthorizationsEndpoints {

    @BeforeEach
    void setupGiven() {
      BddLogger.given("the contextual authorizations endpoints");
    }

    @Nested
    class WhenQueryingAuthorizations {

      @BeforeEach
      void setupWhen() {
        BddLogger.when("performing a POST on " + QUERY_PATH);
      }

      @Test
      void thenTheAuthorOfADraftShouldHoldEveryAuthorization() throws Exception {
        BddLogger.and("given a draft created by the staff");
        UUID draftId = createDraft("Brouillon de l'auteur");

        BddLogger.then("the author should get every authorization, sorted by name");
        queryAs(EntityAuthorizationControllerIT.this::addStaffHeaders, "ACTIVITY_DRAFT", draftId)
            .expectStatus()
            .isOk()
            .expectHeader()
            .valueEquals("Cache-Control", "no-store")
            .expectBody()
            .jsonPath("$.length()")
            .isEqualTo(1)
            .jsonPath("$[0].entityId")
            .isEqualTo(draftId.toString())
            .jsonPath("$[0].authorizations")
            .isEqualTo(EVERY_AUTHORIZATION);
      }

      @Test
      void thenAStaffShouldOnlyGetTheAuthorizationsGrantedToIt() throws Exception {
        BddLogger.and("given a draft shared with another staff for feedback and evaluation");
        UUID sharedDraftId = createDraft("Brouillon partagé");
        shareDraft(sharedDraftId, EActivityRight.FEEDBACK, EActivityRight.EVALUATION);
        UUID privateDraftId = createDraft("Brouillon privé");

        BddLogger.then("the other staff should only see its rights, and none on the private draft");
        queryAs(
                EntityAuthorizationControllerIT.this::addOtherStaffHeaders,
                "ACTIVITY_DRAFT",
                sharedDraftId,
                privateDraftId)
            .expectStatus()
            .isOk()
            .expectBody()
            .jsonPath("$[0].entityId")
            .isEqualTo(sharedDraftId.toString())
            .jsonPath("$[0].authorizations")
            .isEqualTo(List.of("FEEDBACK", "EVALUATION"))
            .jsonPath("$[1].entityId")
            .isEqualTo(privateDraftId.toString())
            .jsonPath("$[1].authorizations")
            .isEmpty();
      }

      @Test
      void thenAnUnknownEntityShouldBeAnsweredWithoutAuthorizationNorError() {
        UUID unknownId = UUID.randomUUID();

        BddLogger.then("an entry with no authorization should be returned for each type");
        for (String entityType : List.of("ACTIVITY", "ACTIVITY_DRAFT")) {
          queryAs(EntityAuthorizationControllerIT.this::addStaffHeaders, entityType, unknownId)
              .expectStatus()
              .isOk()
              .expectBody()
              .jsonPath("$.length()")
              .isEqualTo(1)
              .jsonPath("$[0].entityId")
              .isEqualTo(unknownId.toString())
              .jsonPath("$[0].authorizations")
              .isEmpty();
        }
      }

      @Test
      void thenDuplicatedIdsShouldBeAnsweredOnceInRequestOrder() throws Exception {
        BddLogger.and("given two drafts");
        UUID firstId = createDraft("Premier brouillon");
        UUID secondId = createDraft("Second brouillon");

        BddLogger.then("each distinct id should be returned once, in request order");
        queryAs(
                EntityAuthorizationControllerIT.this::addStaffHeaders,
                "ACTIVITY_DRAFT",
                secondId,
                firstId,
                secondId)
            .expectStatus()
            .isOk()
            .expectBody()
            .jsonPath("$.length()")
            .isEqualTo(2)
            .jsonPath("$[0].entityId")
            .isEqualTo(secondId.toString())
            .jsonPath("$[1].entityId")
            .isEqualTo(firstId.toString());
      }

      @Test
      void thenThePublishedActivityShouldKeepTheAuthorOfItsDraftAuthorized() throws Exception {
        BddLogger.and("given a published activity");
        UUID activityId = publishNewActivity("Activité publiée");

        BddLogger.then("the author should hold every authorization on the activity");
        queryAs(EntityAuthorizationControllerIT.this::addStaffHeaders, "ACTIVITY", activityId)
            .expectStatus()
            .isOk()
            .expectBody()
            .jsonPath("$[0].authorizations")
            .isEqualTo(EVERY_AUTHORIZATION);

        BddLogger.and("another staff should hold none");
        queryAs(EntityAuthorizationControllerIT.this::addOtherStaffHeaders, "ACTIVITY", activityId)
            .expectStatus()
            .isOk()
            .expectBody()
            .jsonPath("$[0].authorizations")
            .isEmpty();
      }

      @Test
      void thenAStudentShouldGetNoAuthorization() throws Exception {
        BddLogger.and("given a draft created by the staff");
        UUID draftId = createDraft("Brouillon vu par un étudiant");

        BddLogger.then("a student should get an entry without any authorization");
        queryAs(EntityAuthorizationControllerIT.this::addStudentHeaders, "ACTIVITY_DRAFT", draftId)
            .expectStatus()
            .isOk()
            .expectBody()
            .jsonPath("$[0].authorizations")
            .isEmpty();
      }

      @Test
      void thenItShouldReturn400WhenNoIdIsProvided() {
        BddLogger.then("an empty id list should return 400");
        queryAs(EntityAuthorizationControllerIT.this::addStaffHeaders, "ACTIVITY_DRAFT")
            .expectStatus()
            .isBadRequest()
            .expectBody()
            .jsonPath("$.code")
            .isEqualTo("NOT_BLANK");
      }

      @Test
      void thenItShouldReturn400WhenTooManyIdsAreProvided() {
        BddLogger.then("more than the maximum number of ids should return 400");
        UUID[] ids =
            IntStream.rangeClosed(0, EntityAuthorizationService.MAX_ENTITY_IDS)
                .mapToObj(i -> UUID.randomUUID())
                .toArray(UUID[]::new);

        queryAs(EntityAuthorizationControllerIT.this::addStaffHeaders, "ACTIVITY_DRAFT", ids)
            .expectStatus()
            .isBadRequest()
            .expectBody()
            .jsonPath("$.code")
            .isEqualTo("TOO_LONG");
      }

      @Test
      void thenItShouldAcceptExactlyTheMaximumNumberOfIds() {
        BddLogger.then("the maximum number of ids should be accepted");
        UUID[] ids =
            IntStream.range(0, EntityAuthorizationService.MAX_ENTITY_IDS)
                .mapToObj(i -> UUID.randomUUID())
                .toArray(UUID[]::new);

        queryAs(EntityAuthorizationControllerIT.this::addStaffHeaders, "ACTIVITY_DRAFT", ids)
            .expectStatus()
            .isOk()
            .expectBody()
            .jsonPath("$.length()")
            .isEqualTo(EntityAuthorizationService.MAX_ENTITY_IDS);
      }

      @Test
      void thenItShouldReturn400WhenTheEntityTypeIsUnknown() {
        BddLogger.then("an unknown entity type should return 400");
        queryAs(
                EntityAuthorizationControllerIT.this::addStaffHeaders,
                "UNKNOWN_TYPE",
                UUID.randomUUID())
            .expectStatus()
            .isBadRequest();
      }

      @Test
      void thenItShouldReturn400WhenAnIdIsNotAnUuid() {
        BddLogger.then("a malformed id should return 400");
        webTestClient
            .post()
            .uri(QUERY_PATH, "ACTIVITY_DRAFT")
            .headers(EntityAuthorizationControllerIT.this::addStaffHeaders)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue("{\"entityIds\": [\"not-an-uuid\"]}")
            .exchange()
            .expectStatus()
            .isBadRequest();
      }

      @Test
      void thenItShouldReturn401WhenNotAuthenticated() {
        BddLogger.then("a request without signed context should return 401");
        webTestClient
            .post()
            .uri(QUERY_PATH, "ACTIVITY_DRAFT")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue("{\"entityIds\": [\"" + UUID.randomUUID() + "\"]}")
            .exchange()
            .expectStatus()
            .isUnauthorized();
      }
    }

    @Nested
    class WhenCheckingListAccess {

      @BeforeEach
      void setupWhen() {
        BddLogger.when("performing a GET on " + LIST_ACCESS_PATH);
      }

      @Test
      void thenAStaffHoldingARightOnADraftShouldAccessTheDraftList() throws Exception {
        BddLogger.and("given a draft created by the staff");
        createDraft("Brouillon donnant accès à la liste");

        BddLogger.then("the staff can access the list of drafts");
        listAccessAs(EntityAuthorizationControllerIT.this::addStaffHeaders, "ACTIVITY_DRAFT")
            .expectStatus()
            .isOk()
            .expectHeader()
            .valueEquals("Cache-Control", "no-store")
            .expectBody()
            .jsonPath("$.canAccessList")
            .isEqualTo(true);
      }

      @Test
      void thenAStudentShouldNotAccessAnyList() {
        BddLogger.then("a student cannot access the lists of activities nor drafts");
        for (String entityType : List.of("ACTIVITY", "ACTIVITY_DRAFT")) {
          listAccessAs(EntityAuthorizationControllerIT.this::addStudentHeaders, entityType)
              .expectStatus()
              .isOk()
              .expectBody()
              .jsonPath("$.canAccessList")
              .isEqualTo(false);
        }
      }

      @Test
      void thenAStaffGrantedARightOnADraftShouldAccessTheDraftList() throws Exception {
        BddLogger.and("given a draft shared with another staff");
        UUID draftId = createDraft("Brouillon partagé pour la liste");
        shareDraft(draftId, EActivityRight.FEEDBACK);

        BddLogger.then("the other staff can access the list of drafts");
        listAccessAs(EntityAuthorizationControllerIT.this::addOtherStaffHeaders, "ACTIVITY_DRAFT")
            .expectStatus()
            .isOk()
            .expectBody()
            .jsonPath("$.canAccessList")
            .isEqualTo(true);
      }

      @Test
      void thenItShouldReturn400WhenTheEntityTypeIsUnknown() {
        BddLogger.then("an unknown entity type should return 400");
        listAccessAs(EntityAuthorizationControllerIT.this::addStaffHeaders, "UNKNOWN_TYPE")
            .expectStatus()
            .isBadRequest();
      }

      @Test
      void thenItShouldReturn401WhenNotAuthenticated() {
        BddLogger.then("a request without signed context should return 401");
        webTestClient
            .get()
            .uri(LIST_ACCESS_PATH, "ACTIVITY")
            .exchange()
            .expectStatus()
            .isUnauthorized();
      }
    }
  }

  private WebTestClient.ResponseSpec queryAs(
      Consumer<HttpHeaders> headers, String entityType, UUID... entityIds) {
    String body;
    try {
      body = objectMapper.writeValueAsString(new EntityAuthorizationsRequest(List.of(entityIds)));
    } catch (Exception e) {
      throw new IllegalStateException(e);
    }
    return webTestClient
        .post()
        .uri(QUERY_PATH, entityType)
        .headers(headers)
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(body)
        .accept(MediaType.APPLICATION_JSON)
        .exchange();
  }

  private WebTestClient.ResponseSpec listAccessAs(
      Consumer<HttpHeaders> headers, String entityType) {
    return webTestClient
        .get()
        .uri(LIST_ACCESS_PATH, entityType)
        .headers(headers)
        .accept(MediaType.APPLICATION_JSON)
        .exchange();
  }

  private UUID createDraft(String title) throws Exception {
    String body =
        webTestClient
            .post()
            .uri(DRAFT_PATH)
            .headers(this::addStaffHeaders)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(objectMapper.writeValueAsString(new ActivityDraftCreationRequest(title)))
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody(String.class)
            .returnResult()
            .getResponseBody();
    return UUID.fromString(objectMapper.readTree(body).get("draftId").asText());
  }

  private void shareDraft(UUID draftId, EActivityRight... rights) throws Exception {
    webTestClient
        .put()
        .uri(DRAFT_AUTHORIZATIONS_PATH, draftId)
        .headers(this::addStaffHeaders)
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(
            objectMapper.writeValueAsString(
                new ActivityAuthorizationsRequest(
                    List.of(
                        new StaffAuthorizationRequest(STAFF_ID, List.of(EActivityRight.values())),
                        new StaffAuthorizationRequest(ANOTHER_STAFF_ID, List.of(rights))))))
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectStatus()
        .isOk();
  }

  private UUID publishNewActivity(String title) throws Exception {
    UUID draftId = createDraft(title);
    webTestClient
        .patch()
        .uri(DRAFT_UPDATE_PATH, draftId)
        .headers(this::addStaffHeaders)
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(
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
                    null,
                    null,
                    false,
                    null,
                    null,
                    null)))
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectStatus()
        .isOk();

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

  private void addStudentHeaders(HttpHeaders headers) {
    headers.add(AvenirsSecurityHeaders.SIGNED_CONTEXT, studentPayload);
    headers.add(AvenirsSecurityHeaders.CONTEXT_SIGNATURE, studentSignature);
  }

  private void addStaffHeaders(HttpHeaders headers) {
    headers.add(AvenirsSecurityHeaders.SIGNED_CONTEXT, staffPayload);
    headers.add(AvenirsSecurityHeaders.CONTEXT_SIGNATURE, staffSignature);
  }

  private void addOtherStaffHeaders(HttpHeaders headers) {
    headers.add(AvenirsSecurityHeaders.SIGNED_CONTEXT, anotherStaffPayload);
    headers.add(AvenirsSecurityHeaders.CONTEXT_SIGNATURE, anotherStaffSignature);
  }
}
