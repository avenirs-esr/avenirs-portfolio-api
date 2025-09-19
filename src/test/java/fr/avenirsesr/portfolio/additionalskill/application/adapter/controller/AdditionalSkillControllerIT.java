package fr.avenirsesr.portfolio.additionalskill.application.adapter.controller;

import static fr.avenirsesr.portfolio.common.testutils.infrastructure.adapter.util.TestResourceUtils.loadJson;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import fr.avenirsesr.portfolio.common.seeder.infrastructure.adapter.SeederRunner;
import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class AdditionalSkillControllerIT {

  private static final String BASE_PATH = "/me/additional-skills";

  @Autowired private MockMvc mockMvc;

  @Value("${hmac.secret-key}")
  private String secretKey;

  @Value("${user.student.payload}")
  private String studentPayload;

  @Value("${user.student.signature}")
  private String studentSignature;

  @BeforeAll
  static void setup(@Autowired SeederRunner seederRunner) {
    seederRunner.run();
  }

  @Test
  void shouldReturnPagedAdditionalSkillProgresses() throws Exception {
    BddLogger.given("the " + BASE_PATH + " enpoint");
    BddLogger.when("performing a GET");
    BddLogger.then("it should return paged additional skill progresses");
    mockMvc
        .perform(
            get(BASE_PATH)
                .header("X-Signed-Context", studentPayload)
                .header("X-Context-Kid", secretKey)
                .header("X-Context-Signature", studentSignature)
                .param("page", "0")
                .param("pageSize", "5"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data", notNullValue()))
        .andExpect(jsonPath("$.page.totalElements", greaterThanOrEqualTo(0)))
        .andExpect(jsonPath("$.page.page").value(0))
        .andExpect(jsonPath("$.page.pageSize").value(5));
  }

  @Test
  void shouldSearchAdditionalSkillsByKeyword() throws Exception {
    BddLogger.given("the " + BASE_PATH + " enpoint");
    BddLogger.when("performing a GET with keywords");
    BddLogger.then("it should return paged additional skill progresses filtered by keywords");
    mockMvc
        .perform(
            get(BASE_PATH)
                .header("X-Signed-Context", studentPayload)
                .header("X-Context-Kid", secretKey)
                .header("X-Context-Signature", studentSignature)
                .param("keyword", "acc")
                .param("page", "0")
                .param("pageSize", "5"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data", notNullValue()))
        .andExpect(jsonPath("$.page.page").value(0))
        .andExpect(jsonPath("$.page.pageSize").value(5));
  }

  @Test
  void shouldCreateAdditionalSkillProgress() throws Exception {
    String payloadJson = loadJson("additionalskill/mock-new-additional-skill-progress.json");

    BddLogger.given("the " + BASE_PATH + " enpoint");
    BddLogger.when("performing a POST with a non already existing additional skill progress");
    BddLogger.then("it should create the additional skill progress and return the created status");
    mockMvc
        .perform(
            post(BASE_PATH)
                .header("X-Signed-Context", studentPayload)
                .header("X-Context-Kid", secretKey)
                .header("X-Context-Signature", studentSignature)
                .contentType(MediaType.APPLICATION_JSON)
                .content(payloadJson))
        .andExpect(status().isCreated());
  }

  @Test
  void shouldReturnConflictWhenAdditionalSkillAlreadyExists() throws Exception {
    String payloadJson =
        loadJson("additionalskill/mock-new-additional-skill-progress-to-duplicate.json");

    BddLogger.given("the " + BASE_PATH + " enpoint");
    BddLogger.when("performing a POST with and already existing additional skill progress");
    BddLogger.then(
        "it should return a conflict status and not create the additional skill progress");

    mockMvc
        .perform(
            post(BASE_PATH)
                .header("X-Signed-Context", studentPayload)
                .header("X-Context-Kid", secretKey)
                .header("X-Context-Signature", studentSignature)
                .contentType(MediaType.APPLICATION_JSON)
                .content(payloadJson))
        .andExpect(status().isCreated());

    mockMvc
        .perform(
            post(BASE_PATH)
                .header("X-Signed-Context", studentPayload)
                .header("X-Context-Kid", secretKey)
                .header("X-Context-Signature", studentSignature)
                .contentType(MediaType.APPLICATION_JSON)
                .content(payloadJson))
        .andExpect(status().isConflict());
  }

  @Test
  void shouldReturnNotFoundWhenSkillDoesNotExist() throws Exception {
    String payloadJson = loadJson("additionalskill/mock-unknown-additional-skill.json");

    BddLogger.given("the " + BASE_PATH + " enpoint");
    BddLogger.when("performing a POST with an unknown additionnal skill progress");
    BddLogger.then(
        "it should return not found status and not create the additional skill progress");
    mockMvc
        .perform(
            post(BASE_PATH)
                .header("X-Signed-Context", studentPayload)
                .header("X-Context-Kid", secretKey)
                .header("X-Context-Signature", studentSignature)
                .contentType(MediaType.APPLICATION_JSON)
                .content(payloadJson))
        .andExpect(status().isNotFound());
  }
}
