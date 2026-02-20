package fr.avenirsesr.portfolio.student.progress.declared.activity.application.adapter.controller;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import fr.avenirsesr.portfolio.activity.domain.model.Activity;
import fr.avenirsesr.portfolio.activity.domain.port.output.repository.ActivityRepository;
import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import fr.avenirsesr.portfolio.shared.infrastructure.ContainerConfigurationTest;
import fr.avenirsesr.portfolio.shared.infrastructure.adapter.seeder.SeederRunner;
import java.util.UUID;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

public class DeclaredActivityControllerIT extends ContainerConfigurationTest {

  private static final String BASE_PATH = "/me/activity-progress";

  @Autowired private MockMvc mockMvc;

  @Autowired private ActivityRepository activityRepository;

  @Value("${hmac.secret-key}")
  private String secretKey;

  @Value("${user.student.payload}")
  private String studentPayload;

  @Value("${user.student.signature}")
  private String studentSignature;

  private final String notFoundActivityId = UUID.randomUUID().toString();

  @BeforeAll
  void setup(@Autowired SeederRunner seederRunner) {
    seederRunner.run();
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
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());

    return activityId;
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
  void shouldSubscribeToActivity() throws Exception {
    BddLogger.given("the " + BASE_PATH + "/subscribe endpoint and a valid activity id");
    Activity activity =
        activityRepository.findAll().stream()
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("No activity found"));

    BddLogger.when("performing a POST to subscribe to an activity");
    BddLogger.then("it should return OK status and the declared activity");

    mockMvc
        .perform(
            post(BASE_PATH + "/subscribe/" + activity.getId())
                .header("X-Signed-Context", studentPayload)
                .header("X-Context-Kid", secretKey)
                .header("X-Context-Signature", studentSignature)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id", notNullValue()))
        .andExpect(jsonPath("$.activity").exists())
        .andExpect(jsonPath("$.hasStarted").value(false));
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
                .contentType(MediaType.APPLICATION_JSON))
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
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isConflict());
  }
}
