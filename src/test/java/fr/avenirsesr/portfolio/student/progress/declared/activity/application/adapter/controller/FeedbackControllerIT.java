package fr.avenirsesr.portfolio.student.progress.declared.activity.application.adapter.controller;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import fr.avenirsesr.portfolio.shared.infrastructure.ContainerConfigurationTest;
import fr.avenirsesr.portfolio.shared.infrastructure.adapter.seeder.SeederRunner;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.transaction.annotation.Transactional;

public class FeedbackControllerIT extends ContainerConfigurationTest {

  private static final String BASE_PATH = "/me/activity-progress/feedbacks";
  private static final String ACTIVITY_BASE_PATH = "/me/activity-progress";
  private static final String ACTIVITY_ID = "9a12e6b4-8c3f-4d22-bf55-6d4c1f2a7e33";

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

  @Value("${user.staff.payload}")
  private String staffPayload;

  @Value("${user.staff.signature}")
  private String staffSignature;

  private final String notFoundId = "00000000-0000-0000-0000-000000000000";
  private String declaredActivityId;

  @BeforeAll
  void setup(@Autowired SeederRunner seederRunner) throws Exception {
    seederRunner.run();
    declaredActivityId = subscribeToDeclaredActivity();
  }

  private String subscribeToDeclaredActivity() throws Exception {
    String response =
        webTestClient
            .post()
            .uri(ACTIVITY_BASE_PATH + "/subscribe/" + ACTIVITY_ID)
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

  private String askForFeedbackAndGetId(String declaredActivityId) throws Exception {
    String body =
        webTestClient
            .post()
            .uri(BASE_PATH + "/" + declaredActivityId + "/ask-for-feedback")
            .header("X-Signed-Context", studentPayload)
            .header("X-Context-Kid", secretKey)
            .header("X-Context-Signature", studentSignature)
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(String.class)
            .returnResult()
            .getResponseBody();
    return objectMapper.readTree(body).get("id").asText();
  }

  private String feedbackPath(String userCategory, String feedbackId) {
    return BASE_PATH + "/" + userCategory + "/" + feedbackId;
  }

  private void updateFeedbackWithText(String feedbackId, String feedbackText) {
    webTestClient
        .put()
        .uri(BASE_PATH + "/" + feedbackId)
        .header("X-Signed-Context", studentPayload)
        .header("X-Context-Kid", secretKey)
        .header("X-Context-Signature", studentSignature)
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue("{\"feedback\": \"" + feedbackText + "\"}")
        .exchange()
        .expectStatus()
        .isNoContent();
  }

  // ── POST /{declaredActivityId}/ask-for-feedback ─────────────────────

  @Test
  @Transactional
  void shouldAskForFeedbackSuccessfully() {
    BddLogger.given("a declared activity owned by the logged-in student");
    String id = declaredActivityId;

    BddLogger.when("the student asks for feedback on this activity");
    BddLogger.then("it should return 201 CREATED with a FeedbackDetailsDTO");

    webTestClient
        .post()
        .uri(BASE_PATH + "/" + id + "/ask-for-feedback")
        .header("X-Signed-Context", studentPayload)
        .header("X-Context-Kid", secretKey)
        .header("X-Context-Signature", studentSignature)
        .exchange()
        .expectStatus()
        .isCreated()
        .expectBody()
        .jsonPath("$.id")
        .isNotEmpty()
        .jsonPath("$.declaredActivityId")
        .isEqualTo(id)
        .jsonPath("$.status")
        .isEqualTo("NEW")
        .jsonPath("$.student")
        .isNotEmpty()
        .jsonPath("$.student.id")
        .isNotEmpty()
        .jsonPath("$.associatedTraces")
        .isArray()
        .jsonPath("$.associatedDeclaredSkills")
        .isArray();
  }

  @Test
  void shouldReturn404WhenAskingForFeedbackOnNonExistentActivity() {
    BddLogger.given("a non-existent declared activity ID");

    BddLogger.when("the student asks for feedback");
    BddLogger.then("it should return 404");

    webTestClient
        .post()
        .uri(BASE_PATH + "/" + notFoundId + "/ask-for-feedback")
        .header("X-Signed-Context", studentPayload)
        .header("X-Context-Kid", secretKey)
        .header("X-Context-Signature", studentSignature)
        .exchange()
        .expectStatus()
        .isNotFound();
  }

  @Test
  @Transactional
  void shouldReturn403WhenAskingForFeedbackOnAnotherStudentActivity() {
    BddLogger.given("a declared activity belonging to the first student");
    String id = declaredActivityId;

    BddLogger.when("another student asks for feedback on this activity");
    BddLogger.then("it should return 403");

    webTestClient
        .post()
        .uri(BASE_PATH + "/" + id + "/ask-for-feedback")
        .header("X-Signed-Context", otherStudentPayload)
        .header("X-Context-Kid", secretKey)
        .header("X-Context-Signature", otherStudentSignature)
        .exchange()
        .expectStatus()
        .isForbidden();
  }

  // ── GET /me/activity-progress/feedbacks/{userCategory}/{feedbackId} ──

  @Test
  @Transactional
  void shouldReturnFeedbackDetailsWhenStudentOwnsTheDeclaredActivity() throws Exception {
    BddLogger.given("a student who asked for feedback — the feedback ID is known");
    String feedbackId = askForFeedbackAndGetId(declaredActivityId);

    BddLogger.when("the student fetches the feedback details");
    BddLogger.then("200 OK is returned with the full feedback details");

    webTestClient
        .get()
        .uri(feedbackPath("STUDENT", feedbackId))
        .header("X-Signed-Context", studentPayload)
        .header("X-Context-Kid", secretKey)
        .header("X-Context-Signature", studentSignature)
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody()
        .jsonPath("$.id")
        .isEqualTo(feedbackId)
        .jsonPath("$.declaredActivityId")
        .isEqualTo(declaredActivityId)
        .jsonPath("$.status")
        .isEqualTo("NEW")
        .jsonPath("$.student")
        .exists()
        .jsonPath("$.associatedTraces")
        .isArray()
        .jsonPath("$.associatedDeclaredSkills")
        .isArray();
  }

  @Test
  @Transactional
  void shouldReturnFeedbackDetailsWhenStaffIsAuthorOfTheActivity() throws Exception {
    BddLogger.given(
        "a feedback on a declared activity whose activity is authored by the student/staff user");
    String feedbackId = askForFeedbackAndGetId(declaredActivityId);

    BddLogger.when("the staff author fetches the feedback details");
    BddLogger.then("200 OK is returned — the user is both the student and the staff author here");

    webTestClient
        .get()
        .uri(feedbackPath("STAFF", feedbackId))
        .header("X-Signed-Context", studentPayload)
        .header("X-Context-Kid", secretKey)
        .header("X-Context-Signature", studentSignature)
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody()
        .jsonPath("$.id")
        .isEqualTo(feedbackId);
  }

  @Test
  @Transactional
  void shouldReturn403WhenUserIsNeitherStudentNorStaffAuthorOfFeedback() throws Exception {
    BddLogger.given("a feedback belonging to another student on activity authored by another user");
    String feedbackId = askForFeedbackAndGetId(declaredActivityId);

    BddLogger.when("a third user (otherStudent) tries to fetch the feedback details as STUDENT");
    BddLogger.then("403 Forbidden is returned");

    webTestClient
        .get()
        .uri(feedbackPath("STUDENT", feedbackId))
        .header("X-Signed-Context", otherStudentPayload)
        .header("X-Context-Kid", secretKey)
        .header("X-Context-Signature", otherStudentSignature)
        .exchange()
        .expectStatus()
        .isForbidden();
  }

  @Test
  void shouldReturn404WhenFeedbackDoesNotExist() {
    BddLogger.given("a non-existent feedback ID");
    BddLogger.when("fetching the feedback details");
    BddLogger.then("404 Not Found is returned with FEEDBACK_NOT_FOUND code");

    webTestClient
        .get()
        .uri(feedbackPath("STUDENT", notFoundId))
        .header("X-Signed-Context", studentPayload)
        .header("X-Context-Kid", secretKey)
        .header("X-Context-Signature", studentSignature)
        .exchange()
        .expectStatus()
        .isNotFound()
        .expectBody()
        .jsonPath("$.code")
        .isEqualTo("FEEDBACK_NOT_FOUND");
  }

  @Test
  void shouldReturn401WhenNotAuthenticatedOnFeedbackDetailsEndpoint() {
    BddLogger.given("the GET /me/activity-progress/feedbacks/{userCategory}/{feedbackId} endpoint");
    BddLogger.when("performing a GET without authentication headers");
    BddLogger.then("401 Unauthorized is returned");

    webTestClient
        .get()
        .uri(feedbackPath("STUDENT", notFoundId))
        .exchange()
        .expectStatus()
        .isUnauthorized();
  }

  // ── GET /me/activity-progress/feedbacks ─────────────────────────────

  @Test
  void shouldReturn401WhenNotAuthenticatedOnStaffFeedbackEndpoint() {
    BddLogger.given("the GET /me/activity-progress/feedbacks endpoint");
    BddLogger.when("performing a GET without authentication headers");
    BddLogger.then("it should return 401");

    webTestClient.get().uri(BASE_PATH).exchange().expectStatus().isUnauthorized();
  }

  @Test
  void shouldReturn403WhenStudentTriesToAccessStaffFeedbackEndpoint() {
    BddLogger.given("a user who is not registered as staff");
    BddLogger.when("performing a GET on the staff feedback endpoint");
    BddLogger.then("it should return 403");

    webTestClient
        .get()
        .uri(BASE_PATH)
        .header("X-Signed-Context", otherStudentPayload)
        .header("X-Context-Kid", secretKey)
        .header("X-Context-Signature", otherStudentSignature)
        .exchange()
        .expectStatus()
        .isForbidden();
  }

  @Test
  void shouldReturnPagedStructureWhenStaffHasNoFeedbacks() {
    BddLogger.given("a staff user who is not the author of any activity with feedbacks");
    BddLogger.when("performing a GET on the staff feedback endpoint");
    BddLogger.then("it should return 200 with empty data and a valid pagination structure");

    webTestClient
        .get()
        .uri(BASE_PATH)
        .header("X-Signed-Context", staffPayload)
        .header("X-Context-Kid", secretKey)
        .header("X-Context-Signature", staffSignature)
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
        .isEqualTo(0);
  }

  @Test
  void shouldReturnSeededFeedbacksForStaffWhoAuthoredActivities() {
    BddLogger.given("a staff user who has authored activities for which feedbacks were seeded");
    BddLogger.when("performing a GET on the staff feedback endpoint");
    BddLogger.then("it should return feedbacks with correct shape");

    webTestClient
        .get()
        .uri(uriBuilder -> uriBuilder.path(BASE_PATH).queryParam("page", 0).build())
        .header("X-Signed-Context", studentPayload)
        .header("X-Context-Kid", secretKey)
        .header("X-Context-Signature", studentSignature)
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody()
        .jsonPath("$.data")
        .isArray()
        .jsonPath("$.page.totalElements")
        .isNumber()
        .jsonPath("$.data[0].id")
        .exists()
        .jsonPath("$.data[0].status")
        .exists()
        .jsonPath("$.data[0].iteration")
        .exists()
        .jsonPath("$.data[0].student")
        .exists()
        .jsonPath("$.data[0].activity")
        .exists();
  }

  @Test
  void shouldReturnDefaultPaginationOnStaffFeedbackEndpoint() {
    BddLogger.given("the staff feedback endpoint called without pagination params");
    BddLogger.when("performing a GET without page/pageSize");
    BddLogger.then("it should return 200 with default pagination (page=0, pageSize=8)");

    webTestClient
        .get()
        .uri(BASE_PATH)
        .header("X-Signed-Context", staffPayload)
        .header("X-Context-Kid", secretKey)
        .header("X-Context-Signature", staffSignature)
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody()
        .jsonPath("$.page.page")
        .isEqualTo(0)
        .jsonPath("$.page.pageSize")
        .isEqualTo(8);
  }

  @Test
  @Transactional
  void shouldReturnOnlyFeedbacksMatchingStatusFilter() throws Exception {
    BddLogger.given(
        "a staff/student who asks for feedback on their declared activity, creating a NEW"
            + " feedback");
    String id = declaredActivityId;
    webTestClient
        .post()
        .uri(BASE_PATH + "/" + id + "/ask-for-feedback")
        .header("X-Signed-Context", studentPayload)
        .header("X-Context-Kid", secretKey)
        .header("X-Context-Signature", studentSignature)
        .exchange()
        .expectStatus()
        .isCreated();

    BddLogger.when("filtering staff feedbacks by status=NEW");
    String body =
        webTestClient
            .get()
            .uri(uriBuilder -> uriBuilder.path(BASE_PATH).queryParam("status", "NEW").build())
            .header("X-Signed-Context", studentPayload)
            .header("X-Context-Kid", secretKey)
            .header("X-Context-Signature", studentSignature)
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody(String.class)
            .returnResult()
            .getResponseBody();

    BddLogger.then("all returned feedbacks have status NEW");
    JsonNode data = objectMapper.readTree(body).get("data");
    assertThat(data.isArray()).isTrue();
    for (JsonNode item : data) {
      assertThat(item.get("status").asText()).isEqualTo("NEW");
    }
  }

  // ── POST /{feedbackId}/submit ────────────────────────────────────────

  @Test
  @Transactional
  void shouldSubmitFeedbackSuccessfully() throws Exception {
    BddLogger.given("a staff/student who asked for feedback and set a feedback text on it");
    String feedbackId = askForFeedbackAndGetId(declaredActivityId);
    updateFeedbackWithText(feedbackId, "Excellent travail !");

    BddLogger.when("the staff author submits the feedback");
    BddLogger.then("204 No Content is returned and the feedback status becomes SUBMITTED");

    webTestClient
        .post()
        .uri(BASE_PATH + "/" + feedbackId + "/submit")
        .header("X-Signed-Context", studentPayload)
        .header("X-Context-Kid", secretKey)
        .header("X-Context-Signature", studentSignature)
        .exchange()
        .expectStatus()
        .isNoContent();
  }

  @Test
  @Transactional
  void shouldReturn400WhenFeedbackTextIsNullOnSubmit() throws Exception {
    BddLogger.given("a feedback that was never updated — its feedback text is null");
    String feedbackId = askForFeedbackAndGetId(declaredActivityId);

    BddLogger.when("the staff author tries to submit the feedback without setting a text");
    BddLogger.then("400 Bad Request is returned");

    webTestClient
        .post()
        .uri(BASE_PATH + "/" + feedbackId + "/submit")
        .header("X-Signed-Context", studentPayload)
        .header("X-Context-Kid", secretKey)
        .header("X-Context-Signature", studentSignature)
        .exchange()
        .expectStatus()
        .isBadRequest();
  }

  @Test
  void shouldReturn404WhenFeedbackNotFoundOnSubmit() {
    BddLogger.given("a non-existent feedback ID");
    BddLogger.when("the staff tries to submit it");
    BddLogger.then("404 Not Found is returned");

    webTestClient
        .post()
        .uri(BASE_PATH + "/" + notFoundId + "/submit")
        .header("X-Signed-Context", studentPayload)
        .header("X-Context-Kid", secretKey)
        .header("X-Context-Signature", studentSignature)
        .exchange()
        .expectStatus()
        .isNotFound();
  }

  @Test
  void shouldReturn401WhenNotAuthenticatedOnSubmit() {
    BddLogger.given("the POST /me/activity-progress/feedbacks/{feedbackId}/submit endpoint");
    BddLogger.when("performing a POST without authentication headers");
    BddLogger.then("401 Unauthorized is returned");

    webTestClient
        .post()
        .uri(BASE_PATH + "/" + notFoundId + "/submit")
        .exchange()
        .expectStatus()
        .isUnauthorized();
  }

  @Test
  @Transactional
  void shouldReturn403WhenNonStaffUserTriesToSubmit() throws Exception {
    BddLogger.given("a feedback and a user who is not registered as staff");
    String feedbackId = askForFeedbackAndGetId(declaredActivityId);

    BddLogger.when("the non-staff user tries to submit the feedback");
    BddLogger.then("403 Forbidden is returned");

    webTestClient
        .post()
        .uri(BASE_PATH + "/" + feedbackId + "/submit")
        .header("X-Signed-Context", otherStudentPayload)
        .header("X-Context-Kid", secretKey)
        .header("X-Context-Signature", otherStudentSignature)
        .exchange()
        .expectStatus()
        .isForbidden();
  }

  @Test
  @Transactional
  void shouldReturn403WhenStaffIsNotAuthorOnSubmit() throws Exception {
    BddLogger.given("a feedback on an activity whose author is not the currently logged-in staff");
    String feedbackId = askForFeedbackAndGetId(declaredActivityId);
    updateFeedbackWithText(feedbackId, "Quelques remarques.");

    BddLogger.when("a different staff (not the activity author) tries to submit");
    BddLogger.then("403 Forbidden is returned");

    webTestClient
        .post()
        .uri(BASE_PATH + "/" + feedbackId + "/submit")
        .header("X-Signed-Context", staffPayload)
        .header("X-Context-Kid", secretKey)
        .header("X-Context-Signature", staffSignature)
        .exchange()
        .expectStatus()
        .isForbidden();
  }

  @Test
  @Transactional
  void shouldReturnOnlyFeedbacksMatchingActivityIdFilter() throws Exception {
    BddLogger.given(
        "a staff/student who asks for feedback, creating a feedback linked to activity "
            + ACTIVITY_ID);

    webTestClient
        .post()
        .uri(BASE_PATH + "/" + declaredActivityId + "/ask-for-feedback")
        .header("X-Signed-Context", studentPayload)
        .header("X-Context-Kid", secretKey)
        .header("X-Context-Signature", studentSignature)
        .exchange()
        .expectStatus()
        .isCreated();

    BddLogger.when("filtering staff feedbacks by activityId=" + ACTIVITY_ID);

    String body =
        webTestClient
            .get()
            .uri(
                uriBuilder ->
                    uriBuilder.path(BASE_PATH).queryParam("activityId", ACTIVITY_ID).build())
            .header("X-Signed-Context", studentPayload)
            .header("X-Context-Kid", secretKey)
            .header("X-Context-Signature", studentSignature)
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody(String.class)
            .returnResult()
            .getResponseBody();

    BddLogger.then(
        "all returned feedbacks belong to declared activities linked to activity " + ACTIVITY_ID);

    JsonNode data = objectMapper.readTree(body).get("data");

    assertThat(data.isArray()).isTrue();
    assertThat(data.size()).isGreaterThan(0);

    for (JsonNode item : data) {
      assertThat(item.get("activity")).isNotNull();
      assertThat(item.get("activity").get("id").asText()).isNotBlank();
    }
  }
}
