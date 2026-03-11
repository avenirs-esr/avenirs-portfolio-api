package fr.avenirsesr.portfolio.student.progress.declared.activity.application.adapter.controller;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.avenirsesr.portfolio.activity.domain.model.Activity;
import fr.avenirsesr.portfolio.activity.domain.model.enums.EActivityThematic;
import fr.avenirsesr.portfolio.activity.domain.port.output.repository.ActivityRepository;
import fr.avenirsesr.portfolio.common.data.domain.FetchGraph;
import fr.avenirsesr.portfolio.common.data.domain.model.User;
import fr.avenirsesr.portfolio.common.language.domain.model.enums.ELanguage;
import fr.avenirsesr.portfolio.common.security.infrastructure.adapter.model.AvenirsSecurityHeaders;
import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import fr.avenirsesr.portfolio.shared.infrastructure.ContainerConfigurationTest;
import fr.avenirsesr.portfolio.shared.infrastructure.adapter.seeder.SeederRunner;
import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.exception.DeclaredActivityNotFoundException;
import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.model.DeclaredActivity;
import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.model.enums.EDeclaredActivityStatus;
import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.port.output.repository.DeclaredActivityRepository;
import fr.avenirsesr.portfolio.user.domain.model.Student;
import fr.avenirsesr.portfolio.user.domain.port.output.repository.StudentRepository;
import fr.avenirsesr.portfolio.user.domain.port.output.repository.UserRepository;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

public class DeclaredActivityControllerIT extends ContainerConfigurationTest {
  private static final String BASE_PATH = "/me/activity-progress";
  private static final String UNSUBSCRIBE_PATH = BASE_PATH + "/unsubscribe";
  private static final String EMPTY_BODY = "{}";

  private Student student;
  @Autowired private DeclaredActivityRepository declaredActivityRepository;
  @Autowired private ActivityRepository activityRepository;
  @Autowired private StudentRepository studentRepository;
  @Autowired private UserRepository userRepository;
  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;

  @Value("${hmac.secret-key}")
  private String secretKey;

  @Value("${user.student.payload}")
  private String studentPayload;

  @Value("${user.student.signature}")
  private String studentSignature;

  @Value("${user.student.id}")
  private String studentId;

  @Value("${user.second.student.payload}")
  private String otherStudentPayload;

  @Value("${user.second.student.signature}")
  private String otherStudentSignature;

  private final String notFoundActivityId = UUID.randomUUID().toString();
  private final String notFoundDeclaredActivityId = UUID.randomUUID().toString();

  @BeforeAll
  void setup(@Autowired SeederRunner seederRunner) {
    seederRunner.run();
  }

  private DeclaredActivity getFirstDeclaredActivityForStudent(
      Student student, FetchGraph fetchGraph) {
    return declaredActivityRepository.findAllByStudent(student, fetchGraph).stream()
        .findFirst()
        .orElseThrow(
            () ->
                new IllegalStateException(
                    "No declared activity found for the student. Did the seeder run properly?"));
  }

  private String subscribeToFirstAvailableActivity() throws Exception {
    Activity activity =
        activityRepository.findAll().stream()
            .findFirst()
            .orElseThrow(
                () -> new IllegalStateException("No activity found. Did the seeder run properly?"));

    String activityId = activity.getId().toString();

    mockMvc
        .perform(
            post(BASE_PATH + "/subscribe/" + activityId)
                .header("X-Signed-Context", studentPayload)
                .header("X-Context-Kid", secretKey)
                .header("X-Context-Signature", studentSignature)
                .contentType(MediaType.APPLICATION_JSON)
                .content(EMPTY_BODY))
        .andExpect(status().isOk());

    return activityId;
  }

  private String subscribeAndGetDeclaredActivityId(String payload, String signature)
      throws Exception {
    Activity activity =
        activityRepository.findAll().stream()
            .findFirst()
            .orElseThrow(
                () -> new IllegalStateException("No activity found. Did the seeder run properly?"));

    MvcResult result =
        mockMvc
            .perform(
                post(BASE_PATH + "/subscribe/" + activity.getId())
                    .header("X-Signed-Context", payload)
                    .header("X-Context-Kid", secretKey)
                    .header("X-Context-Signature", signature)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(EMPTY_BODY))
            .andExpect(status().isOk())
            .andReturn();

    String responseBody = result.getResponse().getContentAsString();
    int idIndex = responseBody.indexOf("\"id\":\"") + 6;
    int endIndex = responseBody.indexOf("\"", idIndex);
    return responseBody.substring(idIndex, endIndex);
  }

  /**
   * Helper permettant de simuler l'action "Démarrer une activité" directement en base pour pouvoir
   * tester le endpoint finish() qui l'exige.
   */
  private void markDeclaredActivityAsStartedInDatabase(String declaredActivityId) {
    DeclaredActivity declaredActivity =
        declaredActivityRepository
            .findById(UUID.fromString(declaredActivityId))
            .orElseThrow(DeclaredActivityNotFoundException::new);
    declaredActivity.setStartedAt(Instant.now());
    declaredActivityRepository.save(declaredActivity);
  }

  @Transactional
  @Test
  void shouldGetDeclaredActivitiesViewWithDefaultPagination() throws Exception {
    BddLogger.given("at least one declared activity exists for the student");
    subscribeToFirstAvailableActivity();

    BddLogger.when("performing a GET on the base path without pagination params");
    BddLogger.then("it should return a paged list of declared activities");

    mockMvc
        .perform(
            get(BASE_PATH)
                .header("X-Signed-Context", studentPayload)
                .header("X-Context-Kid", secretKey)
                .header("X-Context-Signature", studentSignature))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data").isArray())
        .andExpect(jsonPath("$.data.length()").value(lessThanOrEqualTo(8)))
        .andExpect(jsonPath("$.page.page").value(0))
        .andExpect(jsonPath("$.page.pageSize").value(8))
        .andExpect(jsonPath("$.page.totalElements").exists())
        .andExpect(jsonPath("$.page.totalPages").exists());
  }

  @Transactional
  @Test
  void shouldGetDeclaredActivitiesViewWithPaginationParams() throws Exception {
    BddLogger.given("at least one declared activity exists for the student");
    subscribeToFirstAvailableActivity();

    BddLogger.when("performing a GET on the base path with pagination params");
    BddLogger.then("it should return a paged list respecting pagination");

    mockMvc
        .perform(
            get(BASE_PATH)
                .param("page", "0")
                .param("pageSize", "5")
                .header("X-Signed-Context", studentPayload)
                .header("X-Context-Kid", secretKey)
                .header("X-Context-Signature", studentSignature))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data").isArray())
        .andExpect(jsonPath("$.data.length()").value(lessThanOrEqualTo(5)))
        .andExpect(jsonPath("$.page.page").value(0))
        .andExpect(jsonPath("$.page.pageSize").value(5))
        .andExpect(jsonPath("$.page.totalElements").exists())
        .andExpect(jsonPath("$.page.totalPages").exists());
  }

  @Transactional
  @Test
  void shouldSubscribeToActivityWithoutDates() throws Exception {
    BddLogger.given("the " + BASE_PATH + "/subscribe endpoint and a valid activity id");
    Activity activity =
        activityRepository.findAll().stream()
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("No activity found"));

    BddLogger.when("performing a POST to subscribe to an activity without body");
    BddLogger.then("it should return OK status and the declared activity");

    mockMvc
        .perform(
            post(BASE_PATH + "/subscribe/" + activity.getId())
                .header("X-Signed-Context", studentPayload)
                .header("X-Context-Kid", secretKey)
                .header("X-Context-Signature", studentSignature)
                .contentType(MediaType.APPLICATION_JSON)
                .content(EMPTY_BODY))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id", notNullValue()))
        .andExpect(jsonPath("$.activity").exists())
        .andExpect(jsonPath("$.status").value(EDeclaredActivityStatus.SUBSCRIBED.name()));
  }

  @Transactional
  @Test
  void shouldSubscribeToActivityWithValidDates() throws Exception {
    BddLogger.given("the subscribe endpoint and a valid activity id with dates");
    Activity activity =
        activityRepository.findAll().stream()
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("No activity found"));

    String tomorrow = LocalDate.now().plusDays(1).toString();
    String nextWeek = LocalDate.now().plusDays(7).toString();
    String requestBody =
        String.format(
            "{\"period\": {\"startDate\": \"%s\", \"endDate\": \"%s\"} }", tomorrow, nextWeek);

    BddLogger.when("performing a POST to subscribe with valid dates");
    BddLogger.then("it should return OK status and the dates are saved");

    mockMvc
        .perform(
            post(BASE_PATH + "/subscribe/" + activity.getId())
                .header("X-Signed-Context", studentPayload)
                .header("X-Context-Kid", secretKey)
                .header("X-Context-Signature", studentSignature)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id", notNullValue()))
        .andExpect(jsonPath("$.startDate").value(tomorrow))
        .andExpect(jsonPath("$.endDate").value(nextWeek));
  }

  @Transactional
  @Test
  void shouldReturnBadRequestWhenSubscribingWithMissingDate() throws Exception {
    BddLogger.given("the subscribe endpoint and a request body missing endDate");
    Activity activity =
        activityRepository.findAll().stream()
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("No activity found"));

    String requestBody = "{\"period\": {\"startDate\": \"2026-01-01\"} }";

    BddLogger.when("performing a POST to subscribe with incomplete body");
    BddLogger.then("it should return Bad Request status");

    mockMvc
        .perform(
            post(BASE_PATH + "/subscribe/" + activity.getId())
                .header("X-Signed-Context", studentPayload)
                .header("X-Context-Kid", secretKey)
                .header("X-Context-Signature", studentSignature)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
        .andExpect(status().isBadRequest());
  }

  @Test
  void shouldReturnNotFoundWhenSubscribingToNonExistingActivity() throws Exception {
    BddLogger.given("an activity id that does not exist");
    BddLogger.when("performing a POST to subscribe with unknown id");
    BddLogger.then("it should return not found");

    mockMvc
        .perform(
            post(BASE_PATH + "/subscribe/" + notFoundActivityId)
                .header("X-Signed-Context", studentPayload)
                .header("X-Context-Kid", secretKey)
                .header("X-Context-Signature", studentSignature)
                .contentType(MediaType.APPLICATION_JSON)
                .content(EMPTY_BODY))
        .andExpect(status().isNotFound());
  }

  @Transactional
  @Test
  void shouldReturnConflictWhenSubscribingToAlreadySubscribedActivity() throws Exception {
    BddLogger.given("an activity the student is already subscribed to");
    String activityId = subscribeToFirstAvailableActivity();

    BddLogger.when("performing a POST to subscribe to the same activity again");
    BddLogger.then("it should return conflict/bad request depending on global exception handler");

    mockMvc
        .perform(
            post(BASE_PATH + "/subscribe/" + activityId)
                .header("X-Signed-Context", studentPayload)
                .header("X-Context-Kid", secretKey)
                .header("X-Context-Signature", studentSignature)
                .contentType(MediaType.APPLICATION_JSON)
                .content(EMPTY_BODY))
        .andExpect(status().isConflict());
  }

  private Student getStudent() {
    BddLogger.then("Create and persist user");

    User user =
        User.create(UUID.fromString(studentId), "other.student@example.com", "Other", "Student");
    user = userRepository.save(user);
    BddLogger.then("Create and persist student");

    student = Student.create(user, "Some bio");
    student = studentRepository.save(student);
    return student;
  }

  private @NonNull Activity getActivity(int i) {
    BddLogger.then("Create and persist Activity");

    var activity =
        Activity.create(
            UUID.randomUUID(),
            "Activity " + i,
            EActivityThematic.EXPERIENCES,
            "Test activity " + i,
            "2026",
            "Short label");
    activityRepository.save(activity);
    return activity;
  }

  @Nested
  class WhenUnsubscribeADeclaredActivity {

    @Transactional
    @Test
    void shouldUnsubscribeMultipleDeclaredActivities_whenOwnedByStudent() throws Exception {
      BddLogger.given("declared activities belonging to the student");
      var activityIds = createDeclaredActivitiesForStudentAndReturnActivityIds();

      BddLogger.when("performing DELETE on declared activities (by activityIds)");

      var result =
          mockMvc.perform(
              delete(UNSUBSCRIBE_PATH)
                  .header(AvenirsSecurityHeaders.SIGNED_CONTEXT, studentPayload)
                  .header(AvenirsSecurityHeaders.CONTEXT_KID, secretKey)
                  .header(AvenirsSecurityHeaders.CONTEXT_SIGNATURE, studentSignature)
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(activityIds))
                  .accept(MediaType.APPLICATION_JSON));

      BddLogger.then("the activities should be removed from the database");

      result
          .andExpect(status().isOk())
          .andExpect(content().string("Declared activities successfully unsubscribed"));

      var remaining =
          declaredActivityRepository.findAllByActivityIdAndStudent(
              activityIds, student, FetchGraph.init().fetch("activity"));

      assertTrue(remaining.isEmpty(), "All declared activities of the student must be deleted");
    }

    private List<UUID> createDeclaredActivitiesForStudentAndReturnActivityIds() {
      // Important: create ONE student for ALL declared activities (ownership must be consistent)
      student = getStudent();

      return IntStream.range(0, 2)
          .mapToObj(
              i -> {
                var activity = getActivity(i);
                BddLogger.then("Create and persist DeclaredActivity for student");

                var declaredActivity =
                    DeclaredActivity.create(
                        UUID.randomUUID(), student, activity, null, null, null, null, null);
                declaredActivityRepository.save(declaredActivity);

                return activity.getId(); // controller expects activityIds
              })
          .toList();
    }

    @Transactional
    @Test
    void shouldReturn400WhenBodyIsMissing() throws Exception {
      BddLogger.given("the DELETE declared activities endpoint");
      BddLogger.when("performing a DELETE request without body");
      BddLogger.then("it should return 400 Bad Request");

      mockMvc
          .perform(
              delete(UNSUBSCRIBE_PATH)
                  .contentType(MediaType.APPLICATION_JSON)
                  .header(HttpHeaders.ACCEPT_LANGUAGE, ELanguage.FRENCH.getCode())
                  .header(AvenirsSecurityHeaders.SIGNED_CONTEXT, studentPayload)
                  .header(AvenirsSecurityHeaders.CONTEXT_KID, secretKey)
                  .header(AvenirsSecurityHeaders.CONTEXT_SIGNATURE, studentSignature))
          .andExpect(status().isBadRequest());
    }

    @Transactional
    @Test
    void shouldReturn404WhenUnsubscribingUnknownActivityIds() throws Exception {
      BddLogger.given("a list of activityIds that the student is not subscribed to");
      var unknownActivityIds = List.of(UUID.randomUUID(), UUID.randomUUID());

      BddLogger.when("performing DELETE on unsubscribe endpoint with unknown ids");
      BddLogger.then("it should return 404 Not Found");

      mockMvc
          .perform(
              delete(UNSUBSCRIBE_PATH)
                  .header(AvenirsSecurityHeaders.SIGNED_CONTEXT, studentPayload)
                  .header(AvenirsSecurityHeaders.CONTEXT_KID, secretKey)
                  .header(AvenirsSecurityHeaders.CONTEXT_SIGNATURE, studentSignature)
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(unknownActivityIds))
                  .accept(MediaType.APPLICATION_JSON))
          .andExpect(status().isNotFound());
    }
  }

  @Nested
  class WhenFinishADeclaredActivity {

    @Transactional
    @Test
    void shouldFinishDeclaredActivity() throws Exception {
      BddLogger.given("an existing STARTED declared activity for the logged-in student");
      String declaredActivityId =
          subscribeAndGetDeclaredActivityId(studentPayload, studentSignature);
      markDeclaredActivityAsStartedInDatabase(declaredActivityId);

      BddLogger.when("performing a PUT to finish the declared activity");
      BddLogger.then("it should return OK status and update the finishedAt field");

      mockMvc
          .perform(
              put(BASE_PATH + "/finish/" + declaredActivityId)
                  .header("X-Signed-Context", studentPayload)
                  .header("X-Context-Kid", secretKey)
                  .header("X-Context-Signature", studentSignature)
                  .contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.id").value(declaredActivityId))
          .andExpect(jsonPath("$.finishedAt", notNullValue()));
    }

    @Test
    void shouldReturnNotFoundWhenFinishingNonExistingDeclaredActivity() throws Exception {
      BddLogger.given("a declared activity id that does not exist");
      BddLogger.when("performing a PUT to finish with unknown id");
      BddLogger.then("it should return not found");

      mockMvc
          .perform(
              put(BASE_PATH + "/finish/" + notFoundDeclaredActivityId)
                  .header("X-Signed-Context", studentPayload)
                  .header("X-Context-Kid", secretKey)
                  .header("X-Context-Signature", studentSignature)
                  .contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isNotFound());
    }

    @Transactional
    @Test
    void shouldReturnForbiddenWhenFinishingAnotherStudentsActivity() throws Exception {
      BddLogger.given("an existing declared activity belonging to another student");
      String otherDeclaredActivityId =
          subscribeAndGetDeclaredActivityId(otherStudentPayload, otherStudentSignature);

      BddLogger.when("performing a PUT to finish the activity with the main student's payload");
      BddLogger.then("it should return forbidden (403)");

      mockMvc
          .perform(
              put(BASE_PATH + "/finish/" + otherDeclaredActivityId)
                  .header("X-Signed-Context", studentPayload)
                  .header("X-Context-Kid", secretKey)
                  .header("X-Context-Signature", studentSignature)
                  .contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isForbidden());
    }

    @Transactional
    @Test
    void shouldReturnConflictWhenFinishingNotStartedActivity() throws Exception {
      BddLogger.given("an existing declared activity that has not started yet");
      String declaredActivityId =
          subscribeAndGetDeclaredActivityId(studentPayload, studentSignature);

      BddLogger.when("performing a PUT to finish the declared activity");
      BddLogger.then("it should return conflict or bad request because it hasn't started");

      mockMvc
          .perform(
              put(BASE_PATH + "/finish/" + declaredActivityId)
                  .header("X-Signed-Context", studentPayload)
                  .header("X-Context-Kid", secretKey)
                  .header("X-Context-Signature", studentSignature)
                  .contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isConflict());
    }

    @Transactional
    @Test
    void shouldReturnConflictWhenFinishingAlreadyFinishedActivity() throws Exception {
      BddLogger.given("an already finished declared activity for the student");
      String declaredActivityId =
          subscribeAndGetDeclaredActivityId(studentPayload, studentSignature);
      markDeclaredActivityAsStartedInDatabase(declaredActivityId);

      mockMvc
          .perform(
              put(BASE_PATH + "/finish/" + declaredActivityId)
                  .header("X-Signed-Context", studentPayload)
                  .header("X-Context-Kid", secretKey)
                  .header("X-Context-Signature", studentSignature)
                  .contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isOk());

      BddLogger.when("performing a new PUT to finish it again");
      BddLogger.then("it should return conflict/bad request");

      mockMvc
          .perform(
              put(BASE_PATH + "/finish/" + declaredActivityId)
                  .header("X-Signed-Context", studentPayload)
                  .header("X-Context-Kid", secretKey)
                  .header("X-Context-Signature", studentSignature)
                  .contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isConflict());
    }
  }

  @Test
  void shouldUpdateReflectionAndSetStatusToInProgress() throws Exception {

    // Given
    BddLogger.given("Un étudiant et une activité");
    student = getStudent();
    var activity = getActivity(1);

    BddLogger.and("Une DeclaredActivity persistée pour cet étudiant");
    var declaredActivity =
        DeclaredActivity.create(UUID.randomUUID(), student, activity, null, null, null, null, null);
    declaredActivityRepository.save(declaredActivity);

    String reflection = "Nouvelle prise de recul sur mon activité";
    String body =
        """
        {
              "reflection": "%s"
        }
        """
            .formatted(reflection);
    BddLogger.and("Un body JSON contenant la reflection");
    // When
    BddLogger.when("On appelle le endpoint PUT /{activityId}/reflection");

    mockMvc
        .perform(
            put(BASE_PATH + "/" + declaredActivity.getId() + "/reflection")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .header(AvenirsSecurityHeaders.SIGNED_CONTEXT, studentPayload)
                .header(AvenirsSecurityHeaders.CONTEXT_KID, secretKey)
                .header(AvenirsSecurityHeaders.CONTEXT_SIGNATURE, studentSignature))
        .andExpect(status().isOk());

    // Then
    BddLogger.then("La reflection est mise à jour en base");
    DeclaredActivity updated =
        declaredActivityRepository.findById(declaredActivity.getId()).orElseThrow();

    assertEquals(reflection, updated.getReflection());

    BddLogger.and("Le statut passe automatiquement à IN_PROGRESS");
    assertEquals(EDeclaredActivityStatus.IN_PROGRESS, updated.getStatus());
  }

  @Test
  void shouldReturnValidationMessageWhenReflectionExceeds4000Characters() throws Exception {

    // Given
    BddLogger.given("Un étudiant et une activité");
    student = getStudent();
    var activity = getActivity(1);

    var declaredActivity =
        DeclaredActivity.create(UUID.randomUUID(), student, activity, null, null, null, null, null);
    declaredActivityRepository.save(declaredActivity);

    BddLogger.and("Une reflection de plus de 4000 caractères");
    String reflection = "a".repeat(4001);
    String body =
        """
        {
              "reflection": "%s"
        }
        """
            .formatted(reflection);

    // When
    BddLogger.when("On appelle le endpoint avec une reflection trop longue");

    mockMvc
        .perform(
            put(BASE_PATH + "/" + declaredActivity.getId() + "/reflection")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .header(AvenirsSecurityHeaders.SIGNED_CONTEXT, studentPayload)
                .header(AvenirsSecurityHeaders.CONTEXT_KID, secretKey)
                .header(AvenirsSecurityHeaders.CONTEXT_SIGNATURE, studentSignature))
        .andExpect(status().isBadRequest())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.code").value("TOO_LONG"))
        .andExpect(
            jsonPath("$.message").value("The field reflection exceeds the maximum allowed length"));
  }

  @Nested
  class WhenGettingDeclaredActivityDetails {

    @Transactional
    @Test
    void shouldGetDeclaredActivityDetails() throws Exception {
      BddLogger.given("an existing declared activity id for the logged-in student");

      BddLogger.when("performing a GET on /me/activity-progress/{declaredActivityId}");
      BddLogger.then("it should return the declared activity details DTO");

      FetchGraph graph = FetchGraph.init().fetch("activity");
      Student student = studentRepository.findById(UUID.fromString(studentId)).orElseThrow();
      DeclaredActivity declaredActivity = getFirstDeclaredActivityForStudent(student, graph);
      mockMvc
          .perform(
              get(BASE_PATH + "/" + declaredActivity.getId())
                  .header(AvenirsSecurityHeaders.SIGNED_CONTEXT, studentPayload)
                  .header(AvenirsSecurityHeaders.CONTEXT_KID, secretKey)
                  .header(AvenirsSecurityHeaders.CONTEXT_SIGNATURE, studentSignature)
                  .accept(MediaType.APPLICATION_JSON))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.status").value(declaredActivity.getStatus().name()))
          .andExpect(jsonPath("$.activity.title").value(declaredActivity.getActivity().getTitle()));
    }

    @Test
    void shouldReturnNotFoundWhenDeclaredActivityDetailsDoesNotExist() throws Exception {
      BddLogger.given("a declared activity id that does not exist");
      BddLogger.when("performing a GET on details endpoint with unknown id");
      BddLogger.then("it should return 404 not found");

      mockMvc
          .perform(
              get(BASE_PATH + "/" + notFoundDeclaredActivityId)
                  .header(AvenirsSecurityHeaders.SIGNED_CONTEXT, studentPayload)
                  .header(AvenirsSecurityHeaders.CONTEXT_KID, secretKey)
                  .header(AvenirsSecurityHeaders.CONTEXT_SIGNATURE, studentSignature)
                  .accept(MediaType.APPLICATION_JSON))
          .andExpect(status().isNotFound());
    }

    @Transactional
    @Test
    void shouldReturnForbiddenWhenGettingAnotherStudentsDeclaredActivityDetails() throws Exception {
      BddLogger.given("an existing declared activity belonging to another student");
      String otherDeclaredActivityId =
          subscribeAndGetDeclaredActivityId(otherStudentPayload, otherStudentSignature);

      BddLogger.when("performing a GET on details endpoint with the main student's payload");
      BddLogger.then("it should return forbidden (403)");

      mockMvc
          .perform(
              get(BASE_PATH + "/" + otherDeclaredActivityId)
                  .header(AvenirsSecurityHeaders.SIGNED_CONTEXT, studentPayload)
                  .header(AvenirsSecurityHeaders.CONTEXT_KID, secretKey)
                  .header(AvenirsSecurityHeaders.CONTEXT_SIGNATURE, studentSignature)
                  .accept(MediaType.APPLICATION_JSON))
          .andExpect(status().isForbidden());
    }
  }

  @Nested
  class whenUpdateDatesDeclaredActivity {
    @Test
    void shouldUpdatePeriodSuccessfully() throws Exception {

      student = getStudent();
      var activity = getActivity(1);

      BddLogger.and("A persisted DeclaredActivity.");
      var declaredActivity =
          DeclaredActivity.create(
              UUID.randomUUID(), student, activity, null, null, null, null, null);

      declaredActivityRepository.save(declaredActivity);

      LocalDate startDate = LocalDate.now().plusDays(1);
      LocalDate endDate = LocalDate.now().plusDays(10);
      String body =
          """
          {
                "period": {
                  "startDate": "%s",
                  "endDate": "%s"
                }
          }
          """
              .formatted(startDate, endDate);
      BddLogger.when("On appelle PUT /{declaredActivityId}/period");

      mockMvc
          .perform(
              put(BASE_PATH + "/" + declaredActivity.getId() + "/period")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(body)
                  .header(AvenirsSecurityHeaders.SIGNED_CONTEXT, studentPayload)
                  .header(AvenirsSecurityHeaders.CONTEXT_KID, secretKey)
                  .header(AvenirsSecurityHeaders.CONTEXT_SIGNATURE, studentSignature))
          .andExpect(status().isOk());

      BddLogger.then("The dates are updated.");

      var updated = declaredActivityRepository.findById(declaredActivity.getId()).orElseThrow();

      assertEquals(startDate, updated.getStartDate());
      assertEquals(endDate, updated.getEndDate());
    }

    @Test
    void shouldReturn400WhenStartDateBeforeInscriptionDate() throws Exception {

      student = getStudent();
      var activity = getActivity(1);

      var declaredActivity =
          DeclaredActivity.create(
              UUID.randomUUID(), student, activity, null, null, null, null, null);

      declaredActivityRepository.save(declaredActivity);

      LocalDate startDate =
          declaredActivity.getCreatedAt().atZone(ZoneId.systemDefault()).toLocalDate().minusDays(1);

      LocalDate endDate = startDate.plusDays(5);
      String body =
          """
          {
                "period": {
                  "startDate": "%s",
                  "endDate": "%s"
                }
          }
          """
              .formatted(startDate, endDate);

      BddLogger.when("send a start date that is earlier than the registration date.");

      mockMvc
          .perform(
              put(BASE_PATH + "/" + declaredActivity.getId() + "/period")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(body)
                  .header(AvenirsSecurityHeaders.SIGNED_CONTEXT, studentPayload)
                  .header(AvenirsSecurityHeaders.CONTEXT_KID, secretKey)
                  .header(AvenirsSecurityHeaders.CONTEXT_SIGNATURE, studentSignature))
          .andExpect(status().isBadRequest());
    }
  }
}
