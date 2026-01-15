package fr.avenirsesr.portfolio.student.progress.declared.skill.application.adapter.controller;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import fr.avenirsesr.portfolio.declaredskill.domain.port.output.repository.DeclaredSkillRepository;
import fr.avenirsesr.portfolio.shared.infrastructure.ContainerConfigurationTest;
import fr.avenirsesr.portfolio.shared.infrastructure.adapter.seeder.SeederRunner;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

public class DeclaredSkillProgressControllerIT extends ContainerConfigurationTest {

  private static final String BASE_PATH = "/me/declared/skill-progress";

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  @Autowired private DeclaredSkillRepository declaredSkillRepository;

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
  void shouldReturnPagedDeclaredSkillProgresses() throws Exception {
    BddLogger.given("the " + BASE_PATH + " enpoint");
    BddLogger.when("performing a GET");
    BddLogger.then("it should return paged declared skill progresses");
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

  @Transactional
  @Test
  void shouldCreateDeclaredSkillProgress() throws Exception {
    var declaredSkill = declaredSkillRepository.findAll().stream().findFirst().orElseThrow();
    UUID externalSkillId = declaredSkill.getExternalSkillId();

    BddLogger.given("the " + BASE_PATH + " enpoint");
    BddLogger.when("performing a POST with a non already existing declared skill progress");
    BddLogger.then("it should create the declared skill progress and return the created status");
    mockMvc
        .perform(
            post(BASE_PATH)
                .header("X-Signed-Context", studentPayload)
                .header("X-Context-Kid", secretKey)
                .header("X-Context-Signature", studentSignature)
                .contentType(MediaType.APPLICATION_JSON)
                .content(buildDeclaredSkillsJson(externalSkillId)))
        .andExpect(status().isCreated());
  }

  @Transactional
  @Test
  void shouldReturnConflictWhenDeclaredSkillAlreadyExists() throws Exception {
    var declaredSkill =
        declaredSkillRepository.findAll().stream().skip(1).findFirst().orElseThrow();
    UUID externalSkillId = declaredSkill.getExternalSkillId();

    BddLogger.given("the " + BASE_PATH + " enpoint");
    BddLogger.when("performing a POST with and already existing declared skill progress");
    BddLogger.then("it should return a conflict status and not create the declared skill progress");

    mockMvc
        .perform(
            post(BASE_PATH)
                .header("X-Signed-Context", studentPayload)
                .header("X-Context-Kid", secretKey)
                .header("X-Context-Signature", studentSignature)
                .contentType(MediaType.APPLICATION_JSON)
                .content(buildDeclaredSkillsJson(externalSkillId)))
        .andExpect(status().isCreated());

    mockMvc
        .perform(
            post(BASE_PATH)
                .header("X-Signed-Context", studentPayload)
                .header("X-Context-Kid", secretKey)
                .header("X-Context-Signature", studentSignature)
                .contentType(MediaType.APPLICATION_JSON)
                .content(buildDeclaredSkillsJson(externalSkillId)))
        .andExpect(status().isConflict());
  }

  @Test
  void shouldReturnNotFoundWhenSkillDoesNotExist() throws Exception {

    BddLogger.given("the " + BASE_PATH + " enpoint");
    BddLogger.when("performing a POST with an unknown additionnal skill progress");
    BddLogger.then("it should return not found status and not create the declared skill progress");
    mockMvc
        .perform(
            post(BASE_PATH)
                .header("X-Signed-Context", studentPayload)
                .header("X-Context-Kid", secretKey)
                .header("X-Context-Signature", studentSignature)
                .contentType(MediaType.APPLICATION_JSON)
                .content(buildDeclaredSkillsJson(UUID.fromString(notFoundExternalSkillId))))
        .andExpect(status().isNotFound());
  }

  @Test
  void shouldDeleteDeclaredSkillProgress() throws Exception {
    BddLogger.given("the " + BASE_PATH + "/{declaredSkillProgressId} DELETE enpoint");
    BddLogger.when("performing a DELETE with student's declared skill progress");
    BddLogger.then("it should delete the declared skill progress");

    String declaredSkillProgressId = extractId(createElement());

    mockMvc
        .perform(
            delete(BASE_PATH + "/{declaredSkillProgressId}", declaredSkillProgressId)
                .header("X-Signed-Context", studentPayload)
                .header("X-Context-Kid", secretKey)
                .header("X-Context-Signature", studentSignature)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().string("Declared skill progress successfully deleted"));
  }

  @Test
  void shouldReturn404WhenUserNotAuthorized() throws Exception {
    BddLogger.given("the " + BASE_PATH + "/{declaredSkillProgressId} DELETE enpoint");
    BddLogger.when("performing a DELETE with an unauthorized user");
    BddLogger.then("it should return a 403 USER_NOT_AUTHORIZED");

    UUID existingDeclaredSkillProgressId = UUID.fromString("72de2a8e-be49-437e-b759-15f8e3a06de3");

    mockMvc
        .perform(
            delete(BASE_PATH + "/{declaredSkillProgressId}", existingDeclaredSkillProgressId)
                .header("X-Signed-Context", studentPayload)
                .header("X-Context-Kid", secretKey)
                .header("X-Context-Signature", studentSignature)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isForbidden())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.code").value("USER_NOT_AUTHORIZED"));
  }

  @Test
  void shouldReturn404WhenDeclaredSkillProgressNotFound() throws Exception {
    BddLogger.given("the " + BASE_PATH + "/{declaredSkillProgressId} DELETE enpoint");
    BddLogger.when("performing a DELETE with an unknown declared skill progress id");
    BddLogger.then("it should return a 404 DECLARED_SKILL_PROGRESS_NOT_FOUND");

    String unknownDeclaredSkillProgressId = UUID.randomUUID().toString();

    mockMvc
        .perform(
            delete(BASE_PATH + "/{declaredSkillProgressId}", unknownDeclaredSkillProgressId)
                .header("X-Signed-Context", studentPayload)
                .header("X-Context-Kid", secretKey)
                .header("X-Context-Signature", studentSignature))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.code").value("DECLARED_SKILL_PROGRESS_NOT_FOUND"));
  }

  @Test
  void shouldDeleteDeclaredSkillProgresses() throws Exception {
    BddLogger.given("the " + BASE_PATH + " DELETE enpoint");
    BddLogger.when("performing a DELETE with student's declared skill progress");
    BddLogger.then("it should delete the declared skill progress");

    String declaredSkillProgressId = extractId(createElement());

    mockMvc
        .perform(
            delete(BASE_PATH)
                .header("X-Signed-Context", studentPayload)
                .header("X-Context-Kid", secretKey)
                .header("X-Context-Signature", studentSignature)
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    objectMapper.writeValueAsString(
                        List.of(UUID.fromString(declaredSkillProgressId)))))
        .andExpect(status().isOk())
        .andExpect(content().string("Declared skill progresses successfully deleted"));
  }

  @Test
  void shouldReturn404WhenUserNotAuthorizedOnDeclaredSkillProgresses() throws Exception {
    BddLogger.given("the " + BASE_PATH + " DELETE enpoint");
    BddLogger.when("performing a DELETE with an unauthorized user");
    BddLogger.then("it should return a 403 USER_NOT_AUTHORIZED");

    UUID existingDeclaredSkillProgressId = UUID.fromString("72de2a8e-be49-437e-b759-15f8e3a06de3");

    mockMvc
        .perform(
            delete(BASE_PATH)
                .header("X-Signed-Context", studentPayload)
                .header("X-Context-Kid", secretKey)
                .header("X-Context-Signature", studentSignature)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(List.of(existingDeclaredSkillProgressId))))
        .andExpect(status().isForbidden())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.code").value("USER_NOT_AUTHORIZED"));
  }

  @Test
  void shouldReturn404WhenDeclaredSkillProgressesNotFound() throws Exception {
    BddLogger.given("the " + BASE_PATH + " DELETE enpoint");
    BddLogger.when("performing a DELETE with an unknown declared skill progress id");
    BddLogger.then("it should return a 404 DECLARED_SKILL_PROGRESS_NOT_FOUND");

    String unknownDeclaredSkillProgressId = UUID.randomUUID().toString();

    mockMvc
        .perform(
            delete(BASE_PATH)
                .header("X-Signed-Context", studentPayload)
                .header("X-Context-Kid", secretKey)
                .header("X-Context-Signature", studentSignature)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(List.of(unknownDeclaredSkillProgressId))))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.code").value("DECLARED_SKILL_PROGRESS_NOT_FOUND"));
  }

  private String buildDeclaredSkillsJson(UUID id) {
    return ("{\n"
            + "  \"id\": \"%s\",\n"
            + "  \"level\": \"BEGINNER\",\n"
            + "  \"type\": \"ROME4\"\n"
            + "}\n")
        .formatted(id);
  }

  private MvcResult createElement() throws Exception {
    var declaredSkill = declaredSkillRepository.findAll().getFirst();
    UUID externalSkillId = declaredSkill.getExternalSkillId();

    return mockMvc
        .perform(
            post(BASE_PATH)
                .header("X-Signed-Context", studentPayload)
                .header("X-Context-Kid", secretKey)
                .header("X-Context-Signature", studentSignature)
                .contentType(MediaType.APPLICATION_JSON)
                .content(buildDeclaredSkillsJson(externalSkillId)))
        .andExpect(status().isCreated())
        .andReturn();
  }

  private String extractId(MvcResult result) throws Exception {
    JsonNode json = objectMapper.readTree(result.getResponse().getContentAsString());
    return json.get("id").asText();
  }
}
