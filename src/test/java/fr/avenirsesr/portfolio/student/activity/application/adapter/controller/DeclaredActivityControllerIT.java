package fr.avenirsesr.portfolio.student.activity.application.adapter.controller;

import static org.assertj.core.api.Assertions.assertThat;
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
import org.springframework.test.web.reactive.server.WebTestClient.ResponseSpec;
import org.springframework.transaction.annotation.Transactional;

public class DeclaredActivityControllerIT extends ContainerConfigurationTest {

  private static final String BASE_PATH = "/me/activity-progress";
  private static final String FEEDBACK_BASE_PATH = BASE_PATH + "/feedbacks";
  private static final String ACTIVITY_BASE_PATH = "/me/activities";

  @Autowired private WebTestClient webTestClient;
  @Autowired private ObjectMapper objectMapper;

  @Value("${user.student.payload}")
  private String studentPayload;

  @Value("${user.student.signature}")
  private String studentSignature;

  @Value("${user.no-permission.payload}")
  private String noPermissionPayload;

  @Value("${user.no-permission.signature}")
  private String noPermissionSignature;

  @Value("${user.second.student.payload}")
  private String secondStudentPayload;

  @Value("${user.second.student.signature}")
  private String secondStudentSignature;

  @Value("${user.staff.payload}")
  private String staffPayload;

  @Value("${user.staff.signature}")
  private String staffSignature;

  @Value("${user.anotherStaff.payload}")
  private String anotherStaffPayload;

  @Value("${user.anotherStaff.signature}")
  private String anotherStaffSignature;

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

  private String getDeclaredActivityTitle() throws Exception {
    byte[] response =
        webTestClient
            .get()
            .uri(uriBuilder -> uriBuilder.path(BASE_PATH).queryParam("pageSize", 100).build())
            .header("X-Signed-Context", studentPayload)
            .header("X-Context-Signature", studentSignature)
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody()
            .returnResult()
            .getResponseBody();

    JsonNode root = objectMapper.readTree(response);

    for (JsonNode activity : root.path("data")) {
      if (declaredActivityId.equals(activity.path("id").asText())) {
        return activity.path("title").asText();
      }
    }

    throw new AssertionError("Declared activity not found: " + declaredActivityId);
  }

  @Test
  @Transactional
  void shouldGetDeclaredActivitiesFilteredBySearch() throws Exception {
    BddLogger.given("a declared activity exists");

    String title = getDeclaredActivityTitle();

    assertThat(title).isNotBlank();

    BddLogger.when("searching declared activities by title");

    BddLogger.then("it should return the matching declared activity");

    webTestClient
        .get()
        .uri(
            uriBuilder ->
                uriBuilder
                    .path(BASE_PATH)
                    .queryParam("keyword", title)
                    .queryParam("pageSize", 100)
                    .build())
        .header("X-Signed-Context", studentPayload)
        .header("X-Context-Signature", studentSignature)
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody()
        .jsonPath("$.data[?(@.id == '" + declaredActivityId + "')].id")
        .exists();
  }

  @Test
  @Transactional
  void shouldGetValorizedDeclaredActivities() {
    BddLogger.given("valorized declared activities exist");

    BddLogger.when("getting valorized declared activities");

    BddLogger.then("it should return a list of valorized declared activities");

    webTestClient
        .get()
        .uri(BASE_PATH + "?isValorized=true")
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

  @Test
  void shouldSubmitADeclaredActivityOfAnAssessableActivityForAssessment() throws Exception {
    BddLogger.given("a student subscribed to an activity expecting an assessment");
    String assessableActivityId =
        publishActivityAsStaff("Activité évaluable à soumettre", "{\"comment\": true}");
    String id = subscribeAndGetId(assessableActivityId);

    BddLogger.when("he submits it for assessment");
    submitForAssessment(id).expectStatus().isNoContent();

    BddLogger.then("the declared activity is SUBMITTED");
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
        .isEqualTo("SUBMITTED");

    BddLogger.and("submitting it again is rejected");
    submitForAssessment(id)
        .expectStatus()
        .isEqualTo(HttpStatus.CONFLICT)
        .expectBody()
        .jsonPath("$.code")
        .isEqualTo("DECLARED_ACTIVITY_LOCKED");
  }

  @Test
  void shouldRejectTheSubmissionOfADeclaredActivityWithoutAssessment() throws Exception {
    BddLogger.given("a student subscribed to an activity expecting no assessment");
    String activityWithoutAssessmentId =
        publishActivityAsStaff("Activité sans appréciation à soumettre", "null");
    String id = subscribeAndGetId(activityWithoutAssessmentId);

    BddLogger.when("he submits it for assessment");
    BddLogger.then("it should return 400 ACTIVITY_ASSESSMENT");
    submitForAssessment(id)
        .expectStatus()
        .isBadRequest()
        .expectBody()
        .jsonPath("$.code")
        .isEqualTo("ACTIVITY_ASSESSMENT");
  }

  @Test
  void shouldRejectTheSubmissionOfADeclaredActivityOfAnotherStudent() throws Exception {
    BddLogger.given("a declared activity of another student");
    String assessableActivityId =
        publishActivityAsStaff("Activité évaluable d'un autre étudiant", "{\"comment\": true}");
    String id = subscribeAndGetId(assessableActivityId);

    BddLogger.when("another student submits it for assessment");
    BddLogger.then("it should return 403");
    webTestClient
        .put()
        .uri(BASE_PATH + "/" + id + "/submit-for-assessment")
        .header("X-Signed-Context", secondStudentPayload)
        .header("X-Context-Signature", secondStudentSignature)
        .exchange()
        .expectStatus()
        .isForbidden();
  }

  @Test
  void shouldReturn404WhenSubmittingAnUnknownDeclaredActivity() {
    BddLogger.given("an unknown declared activity id");
    BddLogger.when("the student submits it for assessment");
    BddLogger.then("it should return 404");
    submitForAssessment(notFoundId).expectStatus().isNotFound();
  }

  @Test
  void shouldGetTheAssessmentDetailsOfASubmittedDeclaredActivityAsStaff() throws Exception {
    BddLogger.given("a student who submitted an assessable activity for assessment");
    String assessableActivityId =
        publishActivityAsStaff("Activité évaluable à consulter", "{\"comment\": true}");
    String id = subscribeAndGetId(assessableActivityId);
    updateReflection(id, "Ma prise de recul");
    submitForAssessment(id).expectStatus().isNoContent();

    BddLogger.when("the staff gets the assessment details of the submission");
    BddLogger.then("it should return the learner, the activity, the realization and the setup");
    getAssessmentDetails(id, staffPayload, staffSignature)
        .expectStatus()
        .isOk()
        .expectBody()
        .jsonPath("$.declaredActivityId")
        .isEqualTo(id)
        .jsonPath("$.activity.id")
        .isEqualTo(assessableActivityId)
        .jsonPath("$.activity.title")
        .isEqualTo("Activité évaluable à consulter")
        .jsonPath("$.activity.assessment.comment")
        .isEqualTo(true)
        .jsonPath("$.student.firstName")
        .exists()
        .jsonPath("$.student.lastName")
        .exists()
        .jsonPath("$.reflexion")
        .isEqualTo("Ma prise de recul")
        .jsonPath("$.associations.traces")
        .isArray()
        .jsonPath("$.associations.skillProgresses")
        .isArray()
        .jsonPath("$.associations.declaredExperiences")
        .isArray()
        .jsonPath("$.assessment.requestedAt")
        .exists()
        .jsonPath("$.submittedAt")
        .exists()
        .jsonPath("$.status")
        .isEqualTo("SUBMITTED");
  }

  @Test
  void shouldReturn404WhenGettingTheAssessmentDetailsOfANotSubmittedDeclaredActivity()
      throws Exception {
    BddLogger.given("a declared activity that was not submitted for assessment");
    String assessableActivityId =
        publishActivityAsStaff("Activité évaluable non consultable", "{\"comment\": true}");
    String id = subscribeAndGetId(assessableActivityId);

    BddLogger.when("the staff gets its assessment details");
    BddLogger.then("it should return 404");
    getAssessmentDetails(id, staffPayload, staffSignature).expectStatus().isNotFound();
  }

  @Test
  void shouldReturn403WhenGettingTheAssessmentDetailsWithoutTheEvaluationRight() throws Exception {
    BddLogger.given("a submission on an activity another staff holds no right on");
    String assessableActivityId =
        publishActivityAsStaff("Activité évaluable d'un autre staff", "{\"comment\": true}");
    String id = subscribeAndGetId(assessableActivityId);
    submitForAssessment(id).expectStatus().isNoContent();

    BddLogger.when("that staff gets its assessment details");
    BddLogger.then("it should return 403");
    getAssessmentDetails(id, anotherStaffPayload, anotherStaffSignature)
        .expectStatus()
        .isForbidden();

    BddLogger.and("the student who submitted it cannot read the staff view either");
    getAssessmentDetails(id, studentPayload, studentSignature).expectStatus().isForbidden();
  }

  @Test
  void shouldReturn404WhenGettingTheAssessmentDetailsOfAnUnknownDeclaredActivity() {
    BddLogger.given("an unknown declared activity id");
    BddLogger.when("the staff gets its assessment details");
    BddLogger.then("it should return 404");
    getAssessmentDetails(notFoundId, staffPayload, staffSignature).expectStatus().isNotFound();
  }

  @Test
  void shouldReturn401WhenGettingTheAssessmentDetailsUnauthenticated() {
    BddLogger.given("a declared activity id");
    BddLogger.when("getting its assessment details without being authenticated");
    BddLogger.then("it should return 401");
    webTestClient
        .get()
        .uri(BASE_PATH + "/assessments/" + declaredActivityId)
        .exchange()
        .expectStatus()
        .isUnauthorized();
  }

  private ResponseSpec getAssessmentDetails(String id, String payload, String signature) {
    return webTestClient
        .get()
        .uri(BASE_PATH + "/assessments/" + id)
        .header("X-Signed-Context", payload)
        .header("X-Context-Signature", signature)
        .exchange();
  }

  private void updateReflection(String id, String reflection) {
    webTestClient
        .put()
        .uri(BASE_PATH + "/" + id + "/reflection")
        .header("X-Signed-Context", studentPayload)
        .header("X-Context-Signature", studentSignature)
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue("{\"reflection\": \"" + reflection + "\"}")
        .exchange()
        .expectStatus()
        .isOk();
  }

  private ResponseSpec submitForAssessment(String id) {
    return webTestClient
        .put()
        .uri(BASE_PATH + "/" + id + "/submit-for-assessment")
        .header("X-Signed-Context", studentPayload)
        .header("X-Context-Signature", studentSignature)
        .exchange();
  }

  private String publishActivityAsStaff(String title, String assessment) throws Exception {
    String draftResponse =
        webTestClient
            .post()
            .uri("/me/activities/draft")
            .header("X-Signed-Context", staffPayload)
            .header("X-Context-Signature", staffSignature)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue("{\"title\": \"" + title + "\"}")
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody(String.class)
            .returnResult()
            .getResponseBody();

    String draftId = objectMapper.readTree(draftResponse).get("draftId").asText();

    webTestClient
        .patch()
        .uri("/me/activities/" + draftId)
        .header("X-Signed-Context", staffPayload)
        .header("X-Context-Signature", staffSignature)
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(
            "{\"thematic\": \"EXPERIENCES\", \"summary\": \"Un résumé valide\","
                + " \"description\": \"Une consigne valide\", \"enableReflection\": true,"
                + " \"assessment\": "
                + assessment
                + "}")
        .exchange()
        .expectStatus()
        .isOk();

    webTestClient
        .post()
        .uri("/me/activities/publish/" + draftId)
        .header("X-Signed-Context", staffPayload)
        .header("X-Context-Signature", staffSignature)
        .exchange()
        .expectStatus()
        .isOk();

    return draftId;
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

  @Test
  void rejectContentDeletionIfStudentStillSubscribed() throws Exception {
    BddLogger.given("a declared activity the student is still subscribed to");
    String id = subscribeAndGetId(lockedInteractionsActivityId);
    webTestClient
        .method(HttpMethod.DELETE)
        .uri(BASE_PATH + "/" + id)
        .header("X-Signed-Context", studentPayload)
        .header("X-Context-Signature", studentSignature)
        .exchange()
        .expectStatus()
        .isEqualTo(HttpStatus.CONFLICT)
        .expectBody()
        .jsonPath("$.code")
        .isEqualTo("DECLARED_ACTIVITY_NOT_UNSUBSCRIBED");

    BddLogger.then("Verifying that the activity content has not been deleted");
    webTestClient
        .get()
        .uri(BASE_PATH + "/" + id)
        .header("X-Signed-Context", studentPayload)
        .header("X-Context-Signature", studentSignature)
        .exchange()
        .expectStatus()
        .is2xxSuccessful();
  }

  @Test
  void deleteContentAndActivityAfterUnsubscribe() throws Exception {
    BddLogger.given("a declared activity the student unsubscribed from");
    String id = subscribeAndGetId(lockedInteractionsActivityId);
    unsubscribe(lockedInteractionsActivityId);

    webTestClient
        .method(HttpMethod.DELETE)
        .uri(BASE_PATH + "/" + id)
        .header("X-Signed-Context", studentPayload)
        .header("X-Context-Signature", studentSignature)
        .exchange()
        .expectStatus()
        .isNoContent();

    BddLogger.then("Verifying that the activity content has  been deleted");
    webTestClient
        .get()
        .uri(BASE_PATH + "/" + id)
        .header("X-Signed-Context", studentPayload)
        .header("X-Context-Signature", studentSignature)
        .exchange()
        .expectStatus()
        .isNotFound();
  }

  @Test
  void deleteContentAndActivityWithSubmittedFeedbackAfterUnsubscribe() throws Exception {
    BddLogger.given("a declared activity with a submitted feedback the student unsubscribed from");
    String id = subscribeAndGetId(lockedInteractionsActivityId);
    String feedbackId = askForFeedbackAndGetId(id);
    webTestClient
        .put()
        .uri(FEEDBACK_BASE_PATH + "/" + feedbackId)
        .header("X-Signed-Context", studentPayload)
        .header("X-Context-Signature", studentSignature)
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue("{\"feedback\": \"Great work\"}")
        .exchange()
        .expectStatus()
        .isNoContent();
    webTestClient
        .post()
        .uri(FEEDBACK_BASE_PATH + "/" + feedbackId + "/submit")
        .header("X-Signed-Context", studentPayload)
        .header("X-Context-Signature", studentSignature)
        .exchange()
        .expectStatus()
        .isNoContent();
    unsubscribe(lockedInteractionsActivityId);

    BddLogger.when("he deletes the declared activity");
    webTestClient
        .method(HttpMethod.DELETE)
        .uri(BASE_PATH + "/" + id)
        .header("X-Signed-Context", studentPayload)
        .header("X-Context-Signature", studentSignature)
        .exchange()
        .expectStatus()
        .isNoContent();

    BddLogger.then("the declared activity and its feedbacks are deleted");
    webTestClient
        .get()
        .uri(BASE_PATH + "/" + id)
        .header("X-Signed-Context", studentPayload)
        .header("X-Context-Signature", studentSignature)
        .exchange()
        .expectStatus()
        .isNotFound();
  }

  @Test
  void shouldSendTheAssessmentAndNotifyTheStudent() throws Exception {
    BddLogger.given("a declared activity on an activity expecting a comment and a grade");
    String title = "Activité évaluée envoi";
    String assessableActivityId =
        publishAssessableActivity(title, "{\"comment\":true,\"grade\":20}");
    String id = subscribeAndGetId(assessableActivityId);
    enableStudentNotifications();

    BddLogger.when("the evaluator sends an assessment");
    BddLogger.then("it should return 204");

    sendAssessment(id, "{\"comment\":\"Bon travail\",\"grade\":15}", staffPayload, staffSignature)
        .expectStatus()
        .isNoContent();

    BddLogger.and("the student should have been notified");
    JsonNode notification = findStudentNotificationOf(id);
    assertEquals("ASSESSMENT_SENT", notification.get("type").asText());
    assertEquals(title, notification.get("parameters").get("activityTitle").asText());

    BddLogger.and("the declared activity should be completed");
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
        .isEqualTo("COMPLETED")
        .jsonPath("$.finishedAt")
        .exists();

    BddLogger.and("sending it again should return 400");
    sendAssessment(id, "{\"comment\":\"Autre version\",\"grade\":12}", staffPayload, staffSignature)
        .expectStatus()
        .isBadRequest()
        .expectBody()
        .jsonPath("$.code")
        .isEqualTo("ACTIVITY_ASSESSMENT");
  }

  @Test
  void shouldRejectAnIncompleteAssessment() throws Exception {
    BddLogger.given("a declared activity on an activity expecting a comment and a grade");
    String assessableActivityId =
        publishAssessableActivity("Activité évaluée incomplète", "{\"comment\":true,\"grade\":20}");
    String id = subscribeAndGetId(assessableActivityId);

    BddLogger.when("the evaluator sends an assessment without grade");
    BddLogger.then("it should return 400");

    sendAssessment(id, "{\"comment\":\"Bon travail\"}", staffPayload, staffSignature)
        .expectStatus()
        .isBadRequest()
        .expectBody()
        .jsonPath("$.code")
        .isEqualTo("NOT_NULL");
  }

  @Test
  void shouldRejectAnAssessmentFieldThatIsNotEnabled() throws Exception {
    BddLogger.given("a declared activity on an activity expecting a comment only");
    String assessableActivityId =
        publishAssessableActivity("Activité évaluée commentaire", "{\"comment\":true}");
    String id = subscribeAndGetId(assessableActivityId);

    BddLogger.when("the evaluator sends an assessment holding a grade");
    BddLogger.then("it should return 400");

    sendAssessment(id, "{\"comment\":\"Bon travail\",\"grade\":15}", staffPayload, staffSignature)
        .expectStatus()
        .isBadRequest()
        .expectBody()
        .jsonPath("$.code")
        .isEqualTo("ACTIVITY_ASSESSMENT");
  }

  @Test
  void shouldRejectAnAssessmentOnAnActivityExpectingNone() throws Exception {
    BddLogger.given("a declared activity on an activity expecting no assessment");
    String assessableActivityId = publishAssessableActivity("Activité sans évaluation", "{}");
    String id = subscribeAndGetId(assessableActivityId);

    BddLogger.when("the evaluator sends an assessment");
    BddLogger.then("it should return 400");

    sendAssessment(id, "{\"comment\":\"Bon travail\"}", staffPayload, staffSignature)
        .expectStatus()
        .isBadRequest()
        .expectBody()
        .jsonPath("$.code")
        .isEqualTo("ACTIVITY_ASSESSMENT");
  }

  @Test
  void shouldRejectTheAssessmentOfAStaffWithoutTheEvaluationRight() throws Exception {
    BddLogger.given("a declared activity on an activity another staff does not evaluate");
    String assessableActivityId =
        publishAssessableActivity("Activité évaluée droits", "{\"comment\":true}");
    String id = subscribeAndGetId(assessableActivityId);

    BddLogger.when("that other staff sends an assessment");
    BddLogger.then("it should return 403");

    sendAssessment(id, "{\"comment\":\"Bon travail\"}", anotherStaffPayload, anotherStaffSignature)
        .expectStatus()
        .isForbidden();
  }

  @Test
  void shouldReturnNotFoundWhenAssessingAnUnknownDeclaredActivity() {
    BddLogger.given("an unknown declared activity");

    BddLogger.when("the evaluator sends an assessment");
    BddLogger.then("it should return 404");

    sendAssessment(notFoundId, "{\"comment\":\"Bon travail\"}", staffPayload, staffSignature)
        .expectStatus()
        .isNotFound();
  }

  private ResponseSpec sendAssessment(
      String assessedDeclaredActivityId, String body, String payload, String signature) {
    return webTestClient
        .put()
        .uri(BASE_PATH + "/" + assessedDeclaredActivityId + "/assessment")
        .header("X-Signed-Context", payload)
        .header("X-Context-Signature", signature)
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(body)
        .exchange();
  }

  private void enableStudentNotifications() {
    webTestClient
        .patch()
        .uri("/me/preferences/notification")
        .header("X-Signed-Context", studentPayload)
        .header("X-Context-Signature", studentSignature)
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue("{\"notificationEnabled\":true}")
        .exchange()
        .expectStatus()
        .isNoContent();
  }

  private JsonNode findStudentNotificationOf(String elementId) throws Exception {
    String body =
        webTestClient
            .get()
            .uri(
                uriBuilder ->
                    uriBuilder
                        .path("/me/notifications/STUDENT")
                        .queryParam("pageSize", 100)
                        .build())
            .header("X-Signed-Context", studentPayload)
            .header("X-Context-Signature", studentSignature)
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody(String.class)
            .returnResult()
            .getResponseBody();

    for (JsonNode notification : objectMapper.readTree(body).get("data")) {
      if (elementId.equals(notification.get("elementId").asText())) {
        return notification;
      }
    }

    throw new AssertionError("No notification found for element " + elementId);
  }

  private String publishAssessableActivity(String title, String assessmentSetup) throws Exception {
    String creation =
        webTestClient
            .post()
            .uri(ACTIVITY_BASE_PATH + "/draft")
            .header("X-Signed-Context", staffPayload)
            .header("X-Context-Signature", staffSignature)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue("{\"title\":\"" + title + "\"}")
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody(String.class)
            .returnResult()
            .getResponseBody();
    String draftId = objectMapper.readTree(creation).get("draftId").asText();

    webTestClient
        .patch()
        .uri(ACTIVITY_BASE_PATH + "/" + draftId)
        .header("X-Signed-Context", staffPayload)
        .header("X-Context-Signature", staffSignature)
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(
            "{\"thematic\":\"EXPERIENCES\",\"summary\":\"Un résumé valide pour la publication\","
                + "\"description\":\"Une consigne valide pour la publication\",\"assessment\":"
                + assessmentSetup
                + "}")
        .exchange()
        .expectStatus()
        .isOk();

    webTestClient
        .post()
        .uri(ACTIVITY_BASE_PATH + "/publish/" + draftId)
        .header("X-Signed-Context", staffPayload)
        .header("X-Context-Signature", staffSignature)
        .exchange()
        .expectStatus()
        .isOk();

    return draftId;
  }

  private String askForFeedbackAndGetId(String declaredActivityId) throws Exception {
    String body =
        webTestClient
            .post()
            .uri(FEEDBACK_BASE_PATH + "/" + declaredActivityId + "/ask-for-feedback")
            .header("X-Signed-Context", studentPayload)
            .header("X-Context-Signature", studentSignature)
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(String.class)
            .returnResult()
            .getResponseBody();
    return objectMapper.readTree(body).get("id").asText();
  }
}
