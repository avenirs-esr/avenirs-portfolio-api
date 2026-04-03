package fr.avenirsesr.portfolio.student.progress.declared.skill.application.adapter.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

public class DeclaredSkillProgressControllerIT extends ContainerConfigurationTest {

  private static final String BASE_PATH = "/me/declared/skill-progress";

  @Autowired private WebTestClient webTestClient;

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
    webTestClient
        .get()
        .uri(BASE_PATH)
        .header("X-Signed-Context", studentPayload)
        .header("X-Context-Kid", secretKey)
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
  void shouldCreateDeclaredSkillProgress() throws Exception {
    var declaredSkill = declaredSkillRepository.findAll().stream().findFirst().orElseThrow();
    UUID externalSkillId = declaredSkill.getExternalSkillId();

    webTestClient
        .post()
        .uri(BASE_PATH)
        .header("X-Signed-Context", studentPayload)
        .header("X-Context-Kid", secretKey)
        .header("X-Context-Signature", studentSignature)
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(buildDeclaredSkillsJson(externalSkillId))
        .exchange()
        .expectStatus()
        .isCreated();
  }

  @Test
  void shouldReturnConflictWhenDeclaredSkillAlreadyExists() throws Exception {
    var declaredSkill =
        declaredSkillRepository.findAll().stream().skip(1).findFirst().orElseThrow();
    UUID externalSkillId = declaredSkill.getExternalSkillId();

    webTestClient
        .post()
        .uri(BASE_PATH)
        .header("X-Signed-Context", studentPayload)
        .header("X-Context-Kid", secretKey)
        .header("X-Context-Signature", studentSignature)
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(buildDeclaredSkillsJson(externalSkillId))
        .exchange()
        .expectStatus()
        .isCreated();

    webTestClient
        .post()
        .uri(BASE_PATH)
        .header("X-Signed-Context", studentPayload)
        .header("X-Context-Kid", secretKey)
        .header("X-Context-Signature", studentSignature)
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(buildDeclaredSkillsJson(externalSkillId))
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
        .header("X-Context-Kid", secretKey)
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
        .header("X-Context-Kid", secretKey)
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
        .header("X-Context-Kid", secretKey)
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
        .header("X-Context-Kid", secretKey)
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
        .header("X-Context-Kid", secretKey)
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
}
