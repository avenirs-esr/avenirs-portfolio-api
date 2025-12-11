package fr.avenirsesr.portfolio.student.progress.application.adapter.controller;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import fr.avenirsesr.portfolio.additionalskill.domain.port.output.repository.AdditionalSkillRepository;
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

public class AdditionalSkillProgressControllerIT extends ContainerConfigurationTest {

  private static final String BASE_PATH = "/me/additional-skill-progress";

  @Autowired private MockMvc mockMvc;

  @Autowired private AdditionalSkillRepository additionalSkillRepository;

  @Value("${hmac.secret-key}")
  private String secretKey;

  @Value("${user.student.payload}")
  private String studentPayload;

  @Value("${user.student.signature}")
  private String studentSignature;

  @Value("${external-skill.not-found-id}")
  private String notFoundExternalSkillId;

  @BeforeAll
  void setup(@Autowired SeederRunner seederRunner) {
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

  private String buildAdditionalSkillsJson(UUID id) {
    return ("{\n"
            + "  \"id\": \"%s\",\n"
            + "  \"level\": \"BEGINNER\",\n"
            + "  \"type\": \"ROME4\"\n"
            + "}\n")
        .formatted(id);
  }

  @Transactional
  @Test
  void shouldCreateAdditionalSkillProgress() throws Exception {
    var additionalSkill = additionalSkillRepository.findAll().stream().findFirst().orElseThrow();
    UUID externalSkillId = additionalSkill.getExternalSkillId();

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
                .content(buildAdditionalSkillsJson(externalSkillId)))
        .andExpect(status().isCreated());
  }

  @Transactional
  @Test
  void shouldReturnConflictWhenAdditionalSkillAlreadyExists() throws Exception {
    var additionalSkill =
        additionalSkillRepository.findAll().stream().skip(1).findFirst().orElseThrow();
    UUID externalSkillId = additionalSkill.getExternalSkillId();

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
                .content(buildAdditionalSkillsJson(externalSkillId)))
        .andExpect(status().isCreated());

    mockMvc
        .perform(
            post(BASE_PATH)
                .header("X-Signed-Context", studentPayload)
                .header("X-Context-Kid", secretKey)
                .header("X-Context-Signature", studentSignature)
                .contentType(MediaType.APPLICATION_JSON)
                .content(buildAdditionalSkillsJson(externalSkillId)))
        .andExpect(status().isConflict());
  }

  @Test
  void shouldReturnNotFoundWhenSkillDoesNotExist() throws Exception {

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
                .content(buildAdditionalSkillsJson(UUID.fromString(notFoundExternalSkillId))))
        .andExpect(status().isNotFound());
  }
}
