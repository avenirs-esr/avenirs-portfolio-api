package fr.avenirsesr.portfolio.student.skill.application.adapter.controller;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import fr.avenirsesr.portfolio.shared.infrastructure.ContainerConfigurationTest;
import fr.avenirsesr.portfolio.shared.infrastructure.adapter.seeder.SeederRunner;
import fr.avenirsesr.portfolio.skill.domain.model.Skill;
import fr.avenirsesr.portfolio.skill.domain.port.output.repository.SkillRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

public class SkillProgressControllerIT extends ContainerConfigurationTest {

  private static final String BASE_PATH = "/me/skill-progress";

  @Autowired private WebTestClient webTestClient;

  @Autowired private ObjectMapper objectMapper;

  @Autowired private SkillRepository skillRepository;

  @Value("${user.student.payload}")
  private String studentPayload;

  @Value("${user.student.signature}")
  private String studentSignature;

  @Value("${user.no-permission.payload}")
  private String noPermissionPayload;

  @Value("${user.no-permission.signature}")
  private String noPermissionSignature;

  @Value("${user.second.student.payload}")
  private String otherStudentPayload;

  @Value("${user.second.student.signature}")
  private String otherStudentSignature;

  @Value("${external-skill.not-found-id}")
  private String notFoundExternalSkillId;

  @BeforeAll
  void setup(@Autowired SeederRunner seederRunner) {
    seederRunner.run();
  }

  @Test
  void shouldReturnPagedSkillProgresses() throws Exception {
    BddLogger.given("the " + BASE_PATH + " enpoint");
    BddLogger.when("performing a GET");
    BddLogger.then("it should return paged skill progresses");
    webTestClient
        .get()
        .uri(BASE_PATH)
        .header("X-Signed-Context", studentPayload)
        .header("X-Context-Signature", studentSignature)
        .attribute("page", "0")
        .attribute("pageSize", "5")
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody()
        .jsonPath("$.data")
        .exists();
  }

  @Test
  void shouldFilterSkillProgressesByIsValorized() throws Exception {
    BddLogger.given("a skill progress marked as valorized");

    UUID createdSkillId = createAvailableSkillProgress().progressId();

    webTestClient
        .put()
        .uri(BASE_PATH + "/" + createdSkillId)
        .header("X-Signed-Context", studentPayload)
        .header("X-Context-Signature", studentSignature)
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(
            objectMapper.writeValueAsString(
                Map.of("level", "BEGINNER", "reflection", "reflection", "valorized", true)))
        .exchange()
        .expectStatus()
        .isOk();

    BddLogger.when("performing a GET with isValorized=true");

    var valorizedOnlyResponse =
        webTestClient
            .get()
            .uri(BASE_PATH + "?isValorized=true")
            .header("X-Signed-Context", studentPayload)
            .header("X-Context-Signature", studentSignature)
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody(String.class)
            .returnResult()
            .getResponseBody();

    BddLogger.then("it should contain the valorized skill progress");
    List<String> valorizedIds = new ArrayList<>();
    objectMapper
        .readTree(valorizedOnlyResponse)
        .get("data")
        .forEach(node -> valorizedIds.add(node.get("id").asText()));
    assertThat(valorizedIds).contains(createdSkillId.toString());

    BddLogger.when("performing a GET with isValorized=false");

    var nonValorizedOnlyResponse =
        webTestClient
            .get()
            .uri(BASE_PATH + "?isValorized=false")
            .header("X-Signed-Context", studentPayload)
            .header("X-Context-Signature", studentSignature)
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody(String.class)
            .returnResult()
            .getResponseBody();

    BddLogger.then("it should not contain the valorized skill progress");
    List<String> nonValorizedIds = new ArrayList<>();
    objectMapper
        .readTree(nonValorizedOnlyResponse)
        .get("data")
        .forEach(node -> nonValorizedIds.add(node.get("id").asText()));
    assertThat(nonValorizedIds).doesNotContain(createdSkillId.toString());
  }

  @Test
  void shouldCreateSkillProgress() throws Exception {
    assertThat(createAvailableSkillProgress().progressId()).isNotNull();
  }

  @Test
  void shouldReturnTheSkillIds() throws Exception {
    BddLogger.given("a skill progress for the logged-in student");
    UUID skillId = createAvailableSkillProgress().skillId();

    BddLogger.when("performing a GET on " + BASE_PATH + "/skill-ids");
    var response =
        webTestClient
            .get()
            .uri(BASE_PATH + "/skill-ids")
            .header("X-Signed-Context", studentPayload)
            .header("X-Context-Signature", studentSignature)
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody(String.class)
            .returnResult()
            .getResponseBody();

    BddLogger.then("it should contain the id of the skill");
    List<UUID> skillIds = new ArrayList<>();
    objectMapper.readTree(response).forEach(node -> skillIds.add(UUID.fromString(node.asText())));
    assertThat(skillIds).contains(skillId);
  }

  @Test
  void shouldReturnConflictWhenSkillProgressAlreadyExists() throws Exception {
    UUID id = createAvailableSkillProgress().skillId();

    webTestClient
        .post()
        .uri(BASE_PATH)
        .header("X-Signed-Context", studentPayload)
        .header("X-Context-Signature", studentSignature)
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(buildSkillProgressJson(id))
        .exchange()
        .expectStatus()
        .isEqualTo(409);
  }

  @Test
  void shouldReturnNotFoundWhenSkillDoesNotExist() throws Exception {

    webTestClient
        .post()
        .uri(BASE_PATH)
        .header("X-Signed-Context", studentPayload)
        .header("X-Context-Signature", studentSignature)
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(buildSkillProgressJson(UUID.fromString(notFoundExternalSkillId)))
        .exchange()
        .expectStatus()
        .isNotFound();
  }

  @Test
  void shouldReturn403WhenUserNotAuthorized() throws Exception {

    UUID otherStudentSkillProgressId = createSkillProgressForTheOtherStudent();

    webTestClient
        .delete()
        .uri(BASE_PATH + "/" + otherStudentSkillProgressId)
        .header("X-Signed-Context", studentPayload)
        .header("X-Context-Signature", studentSignature)
        .exchange()
        .expectStatus()
        .isForbidden()
        .expectBody()
        .jsonPath("$.code")
        .isEqualTo("USER_NOT_AUTHORIZED");
  }

  @Test
  void shouldReturn404WhenSkillProgressNotFound() throws Exception {

    String unknownId = UUID.randomUUID().toString();

    webTestClient
        .delete()
        .uri(BASE_PATH + "/" + unknownId)
        .header("X-Signed-Context", studentPayload)
        .header("X-Context-Signature", studentSignature)
        .exchange()
        .expectStatus()
        .isNotFound()
        .expectBody()
        .jsonPath("$.code")
        .isEqualTo("SKILL_PROGRESS_NOT_FOUND");
  }

  @Test
  void shouldReturn403WhenUserNotAuthorizedOnSkillProgresses() throws Exception {

    UUID otherStudentSkillProgressId = createSkillProgressForTheOtherStudent();

    webTestClient
        .method(HttpMethod.DELETE)
        .uri(BASE_PATH)
        .header("X-Signed-Context", studentPayload)
        .header("X-Context-Signature", studentSignature)
        .accept(MediaType.APPLICATION_JSON)
        .bodyValue(List.of(otherStudentSkillProgressId))
        .exchange()
        .expectStatus()
        .isForbidden()
        .expectBody()
        .jsonPath("$.code")
        .isEqualTo("USER_NOT_AUTHORIZED");
  }

  @Test
  void shouldReturn404WhenSkillProgressesNotFound() throws Exception {

    String unknownId = UUID.randomUUID().toString();

    webTestClient
        .method(HttpMethod.DELETE)
        .uri(BASE_PATH)
        .header("X-Signed-Context", studentPayload)
        .header("X-Context-Signature", studentSignature)
        .accept(MediaType.APPLICATION_JSON)
        .bodyValue(List.of(UUID.fromString(unknownId)))
        .exchange()
        .expectStatus()
        .isNotFound()
        .expectBody()
        .jsonPath("$.code")
        .isEqualTo("SKILL_PROGRESS_NOT_FOUND");
  }

  private String buildSkillProgressJson(UUID id) {
    return ("{\n" + "  \"id\": \"%s\",\n" + "  \"level\": \"BEGINNER\"\n" + "}\n").formatted(id);
  }

  private record CreatedSkillProgress(UUID skillId, UUID progressId) {}

  /**
   * Creates a skill progress on the first assessable skill of the catalog that is not already
   * assigned to the logged-in student. Skills assigned by the seeder, or by tests that ran earlier
   * against the same database, are skipped so that this helper never returns 409.
   */
  private CreatedSkillProgress createAvailableSkillProgress() throws Exception {
    for (var skill : assessableSkills()) {
      var result =
          webTestClient
              .post()
              .uri(BASE_PATH)
              .header("X-Signed-Context", studentPayload)
              .header("X-Context-Signature", studentSignature)
              .contentType(MediaType.APPLICATION_JSON)
              .bodyValue(buildSkillProgressJson(skill.getId()))
              .exchange()
              .expectBody(String.class)
              .returnResult();

      if (result.getStatus().is2xxSuccessful()) {
        return new CreatedSkillProgress(
            skill.getId(),
            UUID.fromString(objectMapper.readTree(result.getResponseBody()).get("id").asText()));
      }
    }

    throw new IllegalStateException(
        "No assessable skill available for the logged-in student in the seeded catalog");
  }

  @Test
  void shouldReturn403WhenGettingSkillProgressesWithoutPermission() {
    BddLogger.given("an authenticated user without the declared-skill:list:own permission");
    BddLogger.when("performing a GET on " + BASE_PATH);
    BddLogger.then("it should return 403");

    webTestClient
        .get()
        .uri(BASE_PATH)
        .header("X-Signed-Context", noPermissionPayload)
        .header("X-Context-Signature", noPermissionSignature)
        .exchange()
        .expectStatus()
        .isForbidden();
  }

  @Test
  void shouldReturn403WhenCreatingSkillProgressWithoutPermission() throws Exception {
    BddLogger.given("an authenticated user without the declared-skill:create:own permission");
    BddLogger.when("performing a POST on " + BASE_PATH);
    BddLogger.then("it should return 403");

    webTestClient
        .post()
        .uri(BASE_PATH)
        .header("X-Signed-Context", noPermissionPayload)
        .header("X-Context-Signature", noPermissionSignature)
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(
            objectMapper.writeValueAsString(
                Map.of("id", UUID.randomUUID().toString(), "type", "ROME4")))
        .exchange()
        .expectStatus()
        .isForbidden();
  }

  @Test
  void shouldExposeTheAncestorTitlesOfASkill() throws Exception {
    BddLogger.given("a skill progress on a skill nested in its referential");
    var created = createAvailableSkillProgress();

    BddLogger.when("performing a GET on " + BASE_PATH + "/{id}");
    BddLogger.then("the path segments carry the ancestor libelles and their kind");
    webTestClient
        .get()
        .uri(BASE_PATH + "/" + created.progressId())
        .header("X-Signed-Context", studentPayload)
        .header("X-Context-Signature", studentSignature)
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody()
        .jsonPath("$.skillId")
        .isEqualTo(created.skillId().toString())
        .jsonPath("$.pathSegments")
        .isNotEmpty()
        .jsonPath("$.pathSegments[0].libelle")
        .isNotEmpty()
        .jsonPath("$.pathSegments[0].type")
        .isEqualTo("DOMAIN");
  }

  @Test
  void shouldRejectASkillThatOnlyCategorizesItsChildren() throws Exception {
    BddLogger.given("a seeded skill that is not assessable");
    Skill categorySkill =
        skillRepository.findAll().stream()
            .filter(skill -> !skill.isAssessable())
            .findFirst()
            .orElseThrow(
                () -> new IllegalStateException("No category skill in the seeded catalog"));

    BddLogger.when("performing a POST on " + BASE_PATH + " with that skill");
    BddLogger.then("it should return 400 with the SKILL_NOT_ASSESSABLE code");
    webTestClient
        .post()
        .uri(BASE_PATH)
        .header("X-Signed-Context", studentPayload)
        .header("X-Context-Signature", studentSignature)
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(buildSkillProgressJson(categorySkill.getId()))
        .exchange()
        .expectStatus()
        .isBadRequest()
        .expectBody()
        .jsonPath("$.code")
        .isEqualTo("SKILL_NOT_ASSESSABLE");
  }

  private UUID createSkillProgressForTheOtherStudent() throws Exception {
    for (var skill : assessableSkills()) {
      var result =
          webTestClient
              .post()
              .uri(BASE_PATH)
              .header("X-Signed-Context", otherStudentPayload)
              .header("X-Context-Signature", otherStudentSignature)
              .contentType(MediaType.APPLICATION_JSON)
              .bodyValue(buildSkillProgressJson(skill.getId()))
              .exchange()
              .expectBody(String.class)
              .returnResult();

      if (result.getStatus().is2xxSuccessful()) {
        return UUID.fromString(objectMapper.readTree(result.getResponseBody()).get("id").asText());
      }
    }

    throw new IllegalStateException(
        "No assessable skill available for the other student in the seeded catalog");
  }

  private List<Skill> assessableSkills() {
    return skillRepository.findAll().stream().filter(Skill::isAssessable).toList();
  }
}
