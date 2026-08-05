package fr.avenirsesr.portfolio.student.progress.declared.skill.application.adapter.controller;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.avenirsesr.portfolio.student.association.domain.data.AssociationData;
import fr.avenirsesr.portfolio.student.association.domain.model.EAssociationType;
import fr.avenirsesr.portfolio.student.association.domain.port.input.AssociationService;
import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import fr.avenirsesr.portfolio.declaredskill.domain.port.output.repository.DeclaredSkillRepository;
import fr.avenirsesr.portfolio.shared.infrastructure.ContainerConfigurationTest;
import fr.avenirsesr.portfolio.shared.infrastructure.adapter.seeder.SeederRunner;
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
import org.springframework.test.context.transaction.TestTransaction;
import org.springframework.test.web.reactive.server.WebTestClient;

public class DeclaredSkillProgressControllerIT extends ContainerConfigurationTest {

  private static final String BASE_PATH = "/me/declared/skill-progress";

  @Autowired private WebTestClient webTestClient;

  @Autowired private ObjectMapper objectMapper;

  @Autowired private DeclaredSkillRepository declaredSkillRepository;

  @Autowired private AssociationService associationService;

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

    var declaredSkill =
        declaredSkillRepository.findAll().stream().skip(2).findFirst().orElseThrow();

    var createResponse =
        webTestClient
            .post()
            .uri(BASE_PATH)
            .header("X-Signed-Context", studentPayload)
            .header("X-Context-Signature", studentSignature)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(buildDeclaredSkillsJson(declaredSkill.getId()))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(String.class)
            .returnResult()
            .getResponseBody();

    UUID createdSkillId =
        objectMapper.readTree(createResponse).get("id").textValue().transform(UUID::fromString);

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
    var declaredSkill = declaredSkillRepository.findAll().stream().findFirst().orElseThrow();
    UUID id = declaredSkill.getId();

    webTestClient
        .post()
        .uri(BASE_PATH)
        .header("X-Signed-Context", studentPayload)
        .header("X-Context-Signature", studentSignature)
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(buildDeclaredSkillsJson(id))
        .exchange()
        .expectStatus()
        .isCreated();
  }

  @Test
  void shouldReturnConflictWhenDeclaredSkillAlreadyExists() throws Exception {
    var declaredSkill =
        declaredSkillRepository.findAll().stream().skip(1).findFirst().orElseThrow();
    UUID id = declaredSkill.getId();

    webTestClient
        .post()
        .uri(BASE_PATH)
        .header("X-Signed-Context", studentPayload)
        .header("X-Context-Signature", studentSignature)
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(buildDeclaredSkillsJson(id))
        .exchange()
        .expectStatus()
        .isCreated();

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

  @Test
  void shouldReturn404WhenGettingAssociationsForNonExistentSkill() throws Exception {
    BddLogger.given("a non-existent declared skill progress ID");

    UUID nonExistentId = UUID.randomUUID();

    BddLogger.when("performing a GET to get associations");

    webTestClient
        .get()
        .uri(BASE_PATH + "/" + nonExistentId + "/associations")
        .header("X-Signed-Context", studentPayload)
        .header("X-Context-Signature", studentSignature)
        .exchange()
        .expectStatus()
        .isNotFound()
        .expectBody()
        .jsonPath("$.code")
        .isEqualTo("DECLARED_SKILL_PROGRESS_NOT_FOUND");

    BddLogger.then("it should return 404 with appropriate error code");
  }

  @Test
  void shouldReturn403WhenGettingAssociationsForOtherStudentSkill() throws Exception {
    BddLogger.given("a declared skill progress belonging to another student");

    // Using a declared skill progress ID that belongs to another student
    UUID otherStudentSkillId = UUID.fromString("72de2a8e-be49-437e-b759-15f8e3a06de3");

    BddLogger.when("performing a GET to get associations");

    webTestClient
        .get()
        .uri(BASE_PATH + "/" + otherStudentSkillId + "/associations")
        .header("X-Signed-Context", studentPayload)
        .header("X-Context-Signature", studentSignature)
        .exchange()
        .expectStatus()
        .isForbidden()
        .expectBody()
        .jsonPath("$.code")
        .isEqualTo("USER_NOT_AUTHORIZED");

    BddLogger.then("it should return 403 forbidden");
  }

  @Test
  void shouldReturn404WhenAssociatingWithNonExistentSkill() throws Exception {
    BddLogger.given("a non-existent declared skill progress");

    UUID nonExistentId = UUID.randomUUID();
    List<UUID> activityIds = List.of(UUID.randomUUID());

    BddLogger.when("performing a POST to associate activities");

    String requestBody = objectMapper.writeValueAsString(Map.of("idsToAssociate", activityIds));

    webTestClient
        .post()
        .uri(BASE_PATH + "/" + nonExistentId + "/associate/declared-activities")
        .header("X-Signed-Context", studentPayload)
        .header("X-Context-Signature", studentSignature)
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(requestBody)
        .exchange()
        .expectStatus()
        .isNotFound()
        .expectBody()
        .jsonPath("$.code")
        .isEqualTo("DECLARED_SKILL_PROGRESS_NOT_FOUND");

    BddLogger.then("it should return 404");
  }

  @Test
  void shouldReturn403WhenAssociatingWithOtherStudentSkill() throws Exception {
    BddLogger.given("a declared skill progress belonging to another student");

    UUID otherStudentSkillId = UUID.fromString("72de2a8e-be49-437e-b759-15f8e3a06de3");
    List<UUID> activityIds = List.of(UUID.randomUUID());

    BddLogger.when("performing a POST to associate activities");

    String requestBody = objectMapper.writeValueAsString(Map.of("idsToAssociate", activityIds));

    webTestClient
        .post()
        .uri(BASE_PATH + "/" + otherStudentSkillId + "/associate/declared-activities")
        .header("X-Signed-Context", studentPayload)
        .header("X-Context-Signature", studentSignature)
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(requestBody)
        .exchange()
        .expectStatus()
        .isForbidden()
        .expectBody()
        .jsonPath("$.code")
        .isEqualTo("USER_NOT_AUTHORIZED");

    BddLogger.then("it should return 403 forbidden");
  }

  @Test
  void shouldReturn404WhenAssociatingWithNonExistentActivities() throws Exception {
    BddLogger.given("a declared skill progress and non-existent activity IDs");

    // First create a declared skill progress
    var declaredSkill =
        declaredSkillRepository.findAll().stream().skip(3).findFirst().orElseThrow();
    UUID id = declaredSkill.getId();

    // Create the declared skill progress
    var createResponse =
        webTestClient
            .post()
            .uri(BASE_PATH)
            .header("X-Signed-Context", studentPayload)
            .header("X-Context-Signature", studentSignature)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(buildDeclaredSkillsJson(id))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(String.class)
            .returnResult()
            .getResponseBody();

    UUID createdSkillId =
        objectMapper.readTree(createResponse).get("id").textValue().transform(UUID::fromString);

    List<UUID> nonExistentActivityIds = List.of(UUID.randomUUID(), UUID.randomUUID());

    BddLogger.when("performing a POST to associate with non-existent activities");

    String requestBody =
        objectMapper.writeValueAsString(Map.of("idsToAssociate", nonExistentActivityIds));

    webTestClient
        .post()
        .uri(BASE_PATH + "/" + createdSkillId + "/associate/declared-activities")
        .header("X-Signed-Context", studentPayload)
        .header("X-Context-Signature", studentSignature)
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(requestBody)
        .exchange()
        .expectStatus()
        .isNotFound()
        .expectBody()
        .jsonPath("$.code")
        .isEqualTo("DECLARED_ACTIVITY_NOT_FOUND");

    BddLogger.then("it should return 404 for activity not found");
  }

  @Test
  void shouldHandleEmptyActivityListWhenAssociating() throws Exception {
    BddLogger.given("a declared skill progress and empty activity list");

    // First create a declared skill progress
    var declaredSkill =
        declaredSkillRepository.findAll().stream().skip(4).findFirst().orElseThrow();
    UUID id = declaredSkill.getId();

    // Create the declared skill progress
    var createResponse =
        webTestClient
            .post()
            .uri(BASE_PATH)
            .header("X-Signed-Context", studentPayload)
            .header("X-Context-Signature", studentSignature)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(buildDeclaredSkillsJson(id))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(String.class)
            .returnResult()
            .getResponseBody();

    UUID createdSkillId =
        objectMapper.readTree(createResponse).get("id").textValue().transform(UUID::fromString);

    BddLogger.when("performing a POST with empty activity list");

    String requestBody = objectMapper.writeValueAsString(Map.of("idsToAssociate", List.of()));

    webTestClient
        .post()
        .uri(BASE_PATH + "/" + createdSkillId + "/associate/declared-activities")
        .header("X-Signed-Context", studentPayload)
        .header("X-Context-Signature", studentSignature)
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(requestBody)
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody()
        .jsonPath("$.declaredActivityAssociations")
        .isArray()
        .jsonPath("$.declaredActivityAssociations")
        .isEmpty();

    BddLogger.then("it should succeed with empty associations");
  }

  @Test
  void shouldDeleteAssociations() throws Exception {
    BddLogger.given(
        "a declared skill progress associated with one of the logged-in student's activities");

    var declaredSkill =
        declaredSkillRepository.findAll().stream().skip(5).findFirst().orElseThrow();
    var createResponse =
        webTestClient
            .post()
            .uri(BASE_PATH)
            .header("X-Signed-Context", studentPayload)
            .header("X-Context-Signature", studentSignature)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(buildDeclaredSkillsJson(declaredSkill.getId()))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(String.class)
            .returnResult()
            .getResponseBody();
    UUID createdSkillId =
        objectMapper.readTree(createResponse).get("id").textValue().transform(UUID::fromString);

    var activityListResponse =
        webTestClient
            .get()
            .uri("/me/activity-progress")
            .header("X-Signed-Context", studentPayload)
            .header("X-Context-Signature", studentSignature)
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody(String.class)
            .returnResult()
            .getResponseBody();
    UUID declaredActivityId =
        UUID.fromString(
            objectMapper.readTree(activityListResponse).get("data").get(0).get("id").asText());

    var associateResponse =
        webTestClient
            .post()
            .uri(BASE_PATH + "/" + createdSkillId + "/associate/declared-activities")
            .header("X-Signed-Context", studentPayload)
            .header("X-Context-Signature", studentSignature)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(
                objectMapper.writeValueAsString(
                    Map.of("idsToAssociate", List.of(declaredActivityId))))
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody(String.class)
            .returnResult()
            .getResponseBody();
    UUID associationId =
        UUID.fromString(
            objectMapper
                .readTree(associateResponse)
                .get("declaredActivityAssociations")
                .get(0)
                .get("associationId")
                .asText());

    BddLogger.when("performing a DELETE with that association id");

    webTestClient
        .method(HttpMethod.DELETE)
        .uri(BASE_PATH + "/" + createdSkillId + "/associations")
        .header("X-Signed-Context", studentPayload)
        .header("X-Context-Signature", studentSignature)
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(objectMapper.writeValueAsString(Map.of("idsToDelete", List.of(associationId))))
        .exchange()
        .expectStatus()
        .isNoContent();

    BddLogger.then("it returns 204 and the association is gone");

    webTestClient
        .get()
        .uri(BASE_PATH + "/" + createdSkillId + "/associations")
        .header("X-Signed-Context", studentPayload)
        .header("X-Context-Signature", studentSignature)
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody()
        .jsonPath("$.declaredActivityAssociations")
        .isArray()
        .jsonPath("$.declaredActivityAssociations")
        .isEmpty();
  }

  @Test
  void shouldReturn404WhenDeletingAssociationsForNonExistentSkill() throws Exception {
    BddLogger.given("a non-existent declared skill progress ID");

    UUID nonExistentId = UUID.randomUUID();

    BddLogger.when("performing a DELETE to delete associations");

    webTestClient
        .method(HttpMethod.DELETE)
        .uri(BASE_PATH + "/" + nonExistentId + "/associations")
        .header("X-Signed-Context", studentPayload)
        .header("X-Context-Signature", studentSignature)
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue("{\"idsToDelete\":[]}")
        .exchange()
        .expectStatus()
        .isNotFound()
        .expectBody()
        .jsonPath("$.code")
        .isEqualTo("DECLARED_SKILL_PROGRESS_NOT_FOUND");

    BddLogger.then("it should return 404 with appropriate error code");
  }

  @Test
  void shouldReturn403WhenDeletingAssociationsForOtherStudentSkill() throws Exception {
    BddLogger.given("a declared skill progress belonging to another student");

    UUID otherStudentSkillId = UUID.fromString("72de2a8e-be49-437e-b759-15f8e3a06de3");

    BddLogger.when("performing a DELETE to delete associations");

    webTestClient
        .method(HttpMethod.DELETE)
        .uri(BASE_PATH + "/" + otherStudentSkillId + "/associations")
        .header("X-Signed-Context", studentPayload)
        .header("X-Context-Signature", studentSignature)
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue("{\"idsToDelete\":[]}")
        .exchange()
        .expectStatus()
        .isForbidden()
        .expectBody()
        .jsonPath("$.code")
        .isEqualTo("USER_NOT_AUTHORIZED");

    BddLogger.then("it should return 403 forbidden");
  }

  @Test
  void shouldReturn404WhenAssociatingWithNonExistentSkill_declaredExperiences() throws Exception {
    BddLogger.given("a non-existent declared skill progress");

    UUID nonExistentId = UUID.randomUUID();
    List<UUID> experienceIds = List.of(UUID.randomUUID());

    BddLogger.when("performing a POST to associate declared experiences");

    String requestBody = objectMapper.writeValueAsString(Map.of("idsToAssociate", experienceIds));

    webTestClient
        .post()
        .uri(BASE_PATH + "/" + nonExistentId + "/associate/declared-experiences")
        .header("X-Signed-Context", studentPayload)
        .header("X-Context-Signature", studentSignature)
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(requestBody)
        .exchange()
        .expectStatus()
        .isNotFound()
        .expectBody()
        .jsonPath("$.code")
        .isEqualTo("DECLARED_SKILL_PROGRESS_NOT_FOUND");

    BddLogger.then("it should return 404");
  }

  @Test
  void shouldReturn403WhenAssociatingWithOtherStudentSkill_declaredExperiences() throws Exception {
    BddLogger.given("a declared skill progress belonging to another student");

    UUID otherStudentSkillId = UUID.fromString("72de2a8e-be49-437e-b759-15f8e3a06de3");
    List<UUID> experienceIds = List.of(UUID.randomUUID());

    BddLogger.when("performing a POST to associate declared experiences");

    String requestBody = objectMapper.writeValueAsString(Map.of("idsToAssociate", experienceIds));

    webTestClient
        .post()
        .uri(BASE_PATH + "/" + otherStudentSkillId + "/associate/declared-experiences")
        .header("X-Signed-Context", studentPayload)
        .header("X-Context-Signature", studentSignature)
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(requestBody)
        .exchange()
        .expectStatus()
        .isForbidden()
        .expectBody()
        .jsonPath("$.code")
        .isEqualTo("USER_NOT_AUTHORIZED");

    BddLogger.then("it should return 403 forbidden");
  }

  @Test
  void shouldReturn404WhenAssociatingWithNonExistentExperiences() throws Exception {
    BddLogger.given("a declared skill progress and non-existent declared experience IDs");

    var declaredSkill =
        declaredSkillRepository.findAll().stream().skip(7).findFirst().orElseThrow();

    var createResponse =
        webTestClient
            .post()
            .uri(BASE_PATH)
            .header("X-Signed-Context", studentPayload)
            .header("X-Context-Signature", studentSignature)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(buildDeclaredSkillsJson(declaredSkill.getId()))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(String.class)
            .returnResult()
            .getResponseBody();

    UUID createdSkillId =
        objectMapper.readTree(createResponse).get("id").textValue().transform(UUID::fromString);

    List<UUID> nonExistentExperienceIds = List.of(UUID.randomUUID(), UUID.randomUUID());

    BddLogger.when("performing a POST to associate with non-existent declared experiences");

    String requestBody =
        objectMapper.writeValueAsString(Map.of("idsToAssociate", nonExistentExperienceIds));

    webTestClient
        .post()
        .uri(BASE_PATH + "/" + createdSkillId + "/associate/declared-experiences")
        .header("X-Signed-Context", studentPayload)
        .header("X-Context-Signature", studentSignature)
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(requestBody)
        .exchange()
        .expectStatus()
        .isNotFound()
        .expectBody()
        .jsonPath("$.code")
        .isEqualTo("DECLARED_EXPERIENCE_NOT_FOUND");

    BddLogger.then("it should return 404 for declared experience not found");
  }

  @Test
  void shouldHandleEmptyExperienceListWhenAssociating() throws Exception {
    BddLogger.given("a declared skill progress and empty declared experience list");

    var declaredSkill =
        declaredSkillRepository.findAll().stream().skip(8).findFirst().orElseThrow();

    var createResponse =
        webTestClient
            .post()
            .uri(BASE_PATH)
            .header("X-Signed-Context", studentPayload)
            .header("X-Context-Signature", studentSignature)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(buildDeclaredSkillsJson(declaredSkill.getId()))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(String.class)
            .returnResult()
            .getResponseBody();

    UUID createdSkillId =
        objectMapper.readTree(createResponse).get("id").textValue().transform(UUID::fromString);

    BddLogger.when("performing a POST with empty declared experience list");

    String requestBody = objectMapper.writeValueAsString(Map.of("idsToAssociate", List.of()));

    webTestClient
        .post()
        .uri(BASE_PATH + "/" + createdSkillId + "/associate/declared-experiences")
        .header("X-Signed-Context", studentPayload)
        .header("X-Context-Signature", studentSignature)
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(requestBody)
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody()
        .jsonPath("$.declaredExperienceAssociations")
        .isArray()
        .jsonPath("$.declaredExperienceAssociations")
        .isEmpty();

    BddLogger.then("it should succeed with empty associations");
  }

  @Test
  void shouldAssociateDeclaredSkillWithDeclaredExperiencesSuccessfully() throws Exception {
    BddLogger.given("a declared skill progress and two declared experiences owned by the student");

    var declaredSkill =
        declaredSkillRepository.findAll().stream().skip(9).findFirst().orElseThrow();

    var createResponse =
        webTestClient
            .post()
            .uri(BASE_PATH)
            .header("X-Signed-Context", studentPayload)
            .header("X-Context-Signature", studentSignature)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(buildDeclaredSkillsJson(declaredSkill.getId()))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(String.class)
            .returnResult()
            .getResponseBody();

    UUID createdSkillId =
        objectMapper.readTree(createResponse).get("id").textValue().transform(UUID::fromString);

    UUID experienceId1 =
        createDeclaredExperience("Backend Developer", "PROFESSIONAL", "2022-01-10", null);
    UUID experienceId2 =
        createDeclaredExperience("Bénévolat associatif", "PERSONAL", "2023-03-01", "2023-09-01");

    BddLogger.when("performing a POST to associate both declared experiences, one of them twice");

    String requestBody =
        objectMapper.writeValueAsString(
            Map.of("idsToAssociate", List.of(experienceId1, experienceId2, experienceId1)));

    var responseBody =
        webTestClient
            .post()
            .uri(BASE_PATH + "/" + createdSkillId + "/associate/declared-experiences")
            .header("X-Signed-Context", studentPayload)
            .header("X-Context-Signature", studentSignature)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(requestBody)
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody(String.class)
            .returnResult()
            .getResponseBody();

    BddLogger.then(
        "it should create exactly one association per distinct declared experience, ignoring the"
            + " duplicated id in the request");

    var declaredExperienceAssociations =
        objectMapper.readTree(responseBody).get("declaredExperienceAssociations");
    assertThat(declaredExperienceAssociations.size()).isEqualTo(2);

    var associatedExperienceIds = new ArrayList<String>();
    declaredExperienceAssociations.forEach(
        node -> associatedExperienceIds.add(node.get("declaredExperience").get("id").asText()));
    assertThat(associatedExperienceIds)
        .containsExactlyInAnyOrder(experienceId1.toString(), experienceId2.toString());

    BddLogger.when("performing the same association request again");

    webTestClient
        .post()
        .uri(BASE_PATH + "/" + createdSkillId + "/associate/declared-experiences")
        .header("X-Signed-Context", studentPayload)
        .header("X-Context-Signature", studentSignature)
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(
            objectMapper.writeValueAsString(Map.of("idsToAssociate", List.of(experienceId1))))
        .exchange()
        .expectStatus()
        .isEqualTo(409)
        .expectBody()
        .jsonPath("$.code")
        .isEqualTo("ASSOCIATION_ALREADY_EXIST");

    BddLogger.then("it should reject the already existing association with a 409 conflict");
  }

  private UUID createDeclaredExperience(
      String title, String experienceType, String startDate, String endDate) throws Exception {
    var body = new java.util.HashMap<String, Object>();
    body.put("title", title);
    body.put("experienceType", experienceType);
    body.put("organization", "Organization");
    body.put("startDate", startDate);
    body.put("endDate", endDate);

    var response =
        webTestClient
            .post()
            .uri("/me/declared/experiences/")
            .header("X-Signed-Context", studentPayload)
            .header("X-Context-Signature", studentSignature)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(objectMapper.writeValueAsString(body))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(String.class)
            .returnResult()
            .getResponseBody();

    return UUID.fromString(objectMapper.readTree(response).get("id").asText());
  }

  @Test
  void shouldReturnDeclaredExperienceAssociationsOrderedByMostRecentlyAssociatedFirst()
      throws Exception {
    BddLogger.given(
        "a declared skill progress with a professional and a personal declared experience"
            + " associated, the personal one being associated most recently");

    var declaredSkill =
        declaredSkillRepository.findAll().stream().skip(6).findFirst().orElseThrow();

    var createSkillResponse =
        webTestClient
            .post()
            .uri(BASE_PATH)
            .header("X-Signed-Context", studentPayload)
            .header("X-Context-Signature", studentSignature)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(buildDeclaredSkillsJson(declaredSkill.getId()))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(String.class)
            .returnResult()
            .getResponseBody();

    UUID declaredSkillProgressId =
        UUID.fromString(objectMapper.readTree(createSkillResponse).get("id").asText());

    UUID professionalExperienceId =
        createDeclaredExperience("Backend Developer", "PROFESSIONAL", "2022-01-10", null);
    UUID personalExperienceId =
        createDeclaredExperience("Bénévolat associatif", "PERSONAL", "2023-03-01", "2023-09-01");

    associationService.createAll(
        List.of(
            new AssociationData(
                professionalExperienceId,
                declaredSkillProgressId,
                EAssociationType.DECLARED_EXPERIENCE_DECLARED_SKILL)));
    associationService.createAll(
        List.of(
            new AssociationData(
                personalExperienceId,
                declaredSkillProgressId,
                EAssociationType.DECLARED_EXPERIENCE_DECLARED_SKILL)));

    // The declared skill progress, the experiences and the associations above are created on the
    // test thread's transaction; committing it here makes them visible to the embedded server's
    // request-handling threads used by webTestClient below.
    TestTransaction.flagForCommit();
    TestTransaction.end();

    BddLogger.when("performing a GET to get associations");

    var responseBody =
        webTestClient
            .get()
            .uri(BASE_PATH + "/" + declaredSkillProgressId + "/associations")
            .header("X-Signed-Context", studentPayload)
            .header("X-Context-Signature", studentSignature)
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody(String.class)
            .returnResult()
            .getResponseBody();

    BddLogger.then(
        "it should return both declared experience associations, ordered antichronologically by"
            + " association date, with the correct declared experience data");

    var declaredExperienceAssociations =
        objectMapper.readTree(responseBody).get("declaredExperienceAssociations");
    assertThat(declaredExperienceAssociations).isNotNull();
    assertThat(declaredExperienceAssociations.isArray()).isTrue();
    assertThat(declaredExperienceAssociations.size()).isEqualTo(2);

    var mostRecent = declaredExperienceAssociations.get(0).get("declaredExperience");
    var oldest = declaredExperienceAssociations.get(1).get("declaredExperience");

    assertThat(mostRecent.get("id").asText()).isEqualTo(personalExperienceId.toString());
    assertThat(mostRecent.get("title").asText()).isEqualTo("Bénévolat associatif");
    assertThat(mostRecent.get("experienceType").asText()).isEqualTo("PERSONAL");
    assertThat(mostRecent.get("startDate").asText()).isEqualTo("2023-03-01");
    assertThat(mostRecent.get("endDate").asText()).isEqualTo("2023-09-01");

    assertThat(oldest.get("id").asText()).isEqualTo(professionalExperienceId.toString());
    assertThat(oldest.get("title").asText()).isEqualTo("Backend Developer");
    assertThat(oldest.get("experienceType").asText()).isEqualTo("PROFESSIONAL");
    assertThat(oldest.get("startDate").asText()).isEqualTo("2022-01-10");
    assertThat(oldest.get("endDate").isNull()).isTrue();
  }
}
