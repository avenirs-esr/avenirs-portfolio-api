package fr.avenirsesr.portfolio.student.skill.application.adapter.controller;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import fr.avenirsesr.portfolio.shared.infrastructure.ContainerConfigurationTest;
import fr.avenirsesr.portfolio.shared.infrastructure.adapter.seeder.SeederRunner;
import fr.avenirsesr.portfolio.student.skill.domain.port.output.repository.DeclaredSkillRepository;
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

public class DeclaredSkillProgressControllerIT extends ContainerConfigurationTest {

  private static final String BASE_PATH = "/me/declared/skill-progress";

  @Autowired private WebTestClient webTestClient;

  @Autowired private ObjectMapper objectMapper;

  @Autowired private DeclaredSkillRepository declaredSkillRepository;

  @Value("${user.student.payload}")
  private String studentPayload;

  @Value("${user.student.signature}")
  private String studentSignature;

  @Value("${user.no-permission.payload}")
  private String noPermissionPayload;

  @Value("${user.no-permission.signature}")
  private String noPermissionSignature;

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
  void shouldFilterDeclaredSkillProgressesByIsValorized() throws Exception {
    BddLogger.given("a declared skill progress marked as valorized");

    UUID createdSkillId = createAvailableDeclaredSkillProgress().progressId();

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

    BddLogger.then("it should contain the valorized declared skill progress");
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

    BddLogger.then("it should not contain the valorized declared skill progress");
    List<String> nonValorizedIds = new ArrayList<>();
    objectMapper
        .readTree(nonValorizedOnlyResponse)
        .get("data")
        .forEach(node -> nonValorizedIds.add(node.get("id").asText()));
    assertThat(nonValorizedIds).doesNotContain(createdSkillId.toString());
  }

  @Test
  void shouldCreateDeclaredSkillProgress() throws Exception {
    assertThat(createAvailableDeclaredSkillProgress().progressId()).isNotNull();
  }

  @Test
  void shouldReturnAssociatedExternalSkillIds() throws Exception {
    BddLogger.given("a declared skill progress for the logged-in student");
    UUID declaredSkillId = createAvailableDeclaredSkillProgress().declaredSkillId();

    BddLogger.when("performing a GET on " + BASE_PATH + "/external-ids");
    var response =
        webTestClient
            .get()
            .uri(BASE_PATH + "/external-ids")
            .header("X-Signed-Context", studentPayload)
            .header("X-Context-Signature", studentSignature)
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody(String.class)
            .returnResult()
            .getResponseBody();

    BddLogger.then("it should contain the external id of the declared skill");
    List<UUID> externalIds = new ArrayList<>();
    objectMapper
        .readTree(response)
        .forEach(node -> externalIds.add(UUID.fromString(node.asText())));
    assertThat(externalIds).contains(declaredSkillId);
  }

  @Test
  void shouldReturnConflictWhenDeclaredSkillAlreadyExists() throws Exception {
    UUID id = createAvailableDeclaredSkillProgress().declaredSkillId();

    webTestClient
        .post()
        .uri(BASE_PATH)
        .header("X-Signed-Context", studentPayload)
        .header("X-Context-Signature", studentSignature)
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(buildDeclaredSkillsJson(id))
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
        .bodyValue(buildDeclaredSkillsJson(UUID.fromString(notFoundExternalSkillId)))
        .exchange()
        .expectStatus()
        .isNotFound();
  }

  @Test
  void shouldReturn403WhenUserNotAuthorized() throws Exception {

    UUID existingId = UUID.fromString("72de2a8e-be49-437e-b759-15f8e3a06de3");

    webTestClient
        .delete()
        .uri(BASE_PATH + "/" + existingId)
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
  void shouldReturn404WhenDeclaredSkillProgressNotFound() throws Exception {

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
        .isEqualTo("DECLARED_SKILL_PROGRESS_NOT_FOUND");
  }

  @Test
  void shouldReturn403WhenUserNotAuthorizedOnDeclaredSkillProgresses() throws Exception {

    UUID existingId = UUID.fromString("72de2a8e-be49-437e-b759-15f8e3a06de3");

    webTestClient
        .method(HttpMethod.DELETE)
        .uri(BASE_PATH)
        .header("X-Signed-Context", studentPayload)
        .header("X-Context-Signature", studentSignature)
        .accept(MediaType.APPLICATION_JSON)
        .bodyValue(List.of(existingId))
        .exchange()
        .expectStatus()
        .isForbidden()
        .expectBody()
        .jsonPath("$.code")
        .isEqualTo("USER_NOT_AUTHORIZED");
  }

  @Test
  void shouldReturn404WhenDeclaredSkillProgressesNotFound() throws Exception {

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
        .isEqualTo("DECLARED_SKILL_PROGRESS_NOT_FOUND");
  }

  private String buildDeclaredSkillsJson(UUID id) {
    return ("{\n"
            + "  \"id\": \"%s\",\n"
            + "  \"level\": \"BEGINNER\",\n"
            + "  \"type\": \"ROME4\"\n"
            + "}\n")
        .formatted(id);
  }

  private record CreatedDeclaredSkillProgress(UUID declaredSkillId, UUID progressId) {}

  /**
   * Creates a declared skill progress on the first declared skill of the catalog that is not
   * already assigned to the logged-in student. Skills assigned by the seeder, or by tests that ran
   * earlier against the same database, are skipped so that this helper never returns 409.
   */
  private CreatedDeclaredSkillProgress createAvailableDeclaredSkillProgress() throws Exception {
    for (var declaredSkill : declaredSkillRepository.findAll()) {
      var result =
          webTestClient
              .post()
              .uri(BASE_PATH)
              .header("X-Signed-Context", studentPayload)
              .header("X-Context-Signature", studentSignature)
              .contentType(MediaType.APPLICATION_JSON)
              .bodyValue(buildDeclaredSkillsJson(declaredSkill.getId()))
              .exchange()
              .expectBody(String.class)
              .returnResult();

      if (result.getStatus().is2xxSuccessful()) {
        return new CreatedDeclaredSkillProgress(
            declaredSkill.getId(),
            UUID.fromString(objectMapper.readTree(result.getResponseBody()).get("id").asText()));
      }
    }

    throw new IllegalStateException(
        "No declared skill available for the logged-in student in the seeded catalog");
  }

  @Test
  void shouldReturn403WhenGettingDeclaredSkillProgressesWithoutPermission() {
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
  void shouldReturn403WhenCreatingDeclaredSkillProgressWithoutPermission() throws Exception {
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
}
