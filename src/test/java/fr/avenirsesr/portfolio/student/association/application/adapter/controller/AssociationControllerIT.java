package fr.avenirsesr.portfolio.student.association.application.adapter.controller;

import static fr.avenirsesr.portfolio.common.testutils.BddLogger.when;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.avenirsesr.portfolio.common.configuration.domain.model.TraceConfiguration;
import fr.avenirsesr.portfolio.common.security.infrastructure.adapter.model.AvenirsSecurityHeaders;
import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import fr.avenirsesr.portfolio.shared.infrastructure.ContainerConfigurationTest;
import fr.avenirsesr.portfolio.shared.infrastructure.adapter.seeder.SeederRunner;
import fr.avenirsesr.portfolio.student.association.application.adapter.request.AssociationsCreationRequest;
import fr.avenirsesr.portfolio.student.association.application.adapter.request.AssociationsDeleteRequest;
import fr.avenirsesr.portfolio.student.association.domain.data.AssociationData;
import fr.avenirsesr.portfolio.student.association.domain.model.EAssociationType;
import fr.avenirsesr.portfolio.student.association.domain.port.input.AssociationService;
import fr.avenirsesr.portfolio.student.skill.domain.port.output.repository.DeclaredSkillRepository;
import fr.avenirsesr.portfolio.student.trace.infrastructure.adapter.client.TraceConfigurationClient;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.StreamSupport;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.transaction.TestTransaction;
import org.springframework.test.web.reactive.server.WebTestClient;

class AssociationControllerIT extends ContainerConfigurationTest {

  private static final String BASE_PATH = "/me/associations";
  private static final String ASSOCIATIONS_PATH = BASE_PATH + "/{contextType}/{elementId}";
  private static final String ASSOCIATE_PATH =
      BASE_PATH + "/{contextType}/{elementId}/{associatedContextType}";
  private static final String SEARCH_PATH =
      BASE_PATH + "/{contextType}/{elementId}/{associatedContextType}/search";

  private static final String TRACE_ASSOCIATIONS_PATH = BASE_PATH + "/TRACE";
  private static final String DECLARED_ACTIVITY_ASSOCIATIONS_PATH =
      BASE_PATH + "/DECLARED_ACTIVITY";
  private static final String DECLARED_SKILL_ASSOCIATIONS_PATH = BASE_PATH + "/DECLARED_SKILL";
  private static final String DECLARED_EXPERIENCE_ASSOCIATIONS_PATH =
      BASE_PATH + "/DECLARED_EXPERIENCE";
  private static final String DECLARED_PROGRAM_BASE_PATH = "/me/declared/programs";

  private static final String DECLARED_SKILL_BASE_PATH = "/me/declared/skill-progress";
  private static final String DECLARED_EXPERIENCE_BASE_PATH = "/me/declared/experiences";
  private static final String DECLARED_ACTIVITY_BASE_PATH = "/me/activity-progress";
  private static final String TRACE_OVERVIEW_URL = "/me/traces/overview";
  private static final String DECLARED_EXPERIENCE_VIEW_URL =
      DECLARED_EXPERIENCE_BASE_PATH + "/view";

  @TestConfiguration
  static class TestConfig {

    @Bean
    @Primary
    public TraceConfigurationClient traceConfigurationClient() {
      TraceConfigurationClient mock = org.mockito.Mockito.mock(TraceConfigurationClient.class);
      TraceConfiguration mockConfig = new TraceConfiguration(30, 7, 3);
      org.mockito.Mockito.when(mock.getTraceConfiguration()).thenReturn(mockConfig);
      return mock;
    }
  }

  @Autowired private WebTestClient webTestClient;
  @Autowired private ObjectMapper objectMapper;
  @Autowired private DeclaredSkillRepository declaredSkillRepository;
  @Autowired private AssociationService associationService;

  @Value("${user.student.payload}")
  private String studentPayload;

  @Value("${user.student.signature}")
  private String studentSignature;

  @Value("${user.second.student.payload}")
  private String otherStudentPayload;

  @Value("${user.second.student.signature}")
  private String otherStudentSignature;

  @Value("${user.no-permission.payload}")
  private String noPermissionPayload;

  @Value("${user.no-permission.signature}")
  private String noPermissionSignature;

  private final String notFoundDeclaredExperienceId = "00000000-0000-0000-0000-000000000000";

  @BeforeAll
  void setup(@Autowired SeederRunner seederRunner) {
    seederRunner.run();
  }

  private UUID getFirstTraceId() throws Exception {
    String body =
        webTestClient
            .get()
            .uri(TRACE_OVERVIEW_URL)
            .header(AvenirsSecurityHeaders.SIGNED_CONTEXT, studentPayload)
            .header(AvenirsSecurityHeaders.CONTEXT_SIGNATURE, studentSignature)
            .accept(APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody(String.class)
            .returnResult()
            .getResponseBody();

    JsonNode json = objectMapper.readTree(body);

    if (!json.isArray() || json.isEmpty()) {
      throw new IllegalStateException("Seeder returned no trace in /overview");
    }

    return UUID.fromString(json.get(0).get("id").asText());
  }

  private UUID getFirstDeclaredExperienceId() throws Exception {
    String body =
        webTestClient
            .get()
            .uri(DECLARED_EXPERIENCE_VIEW_URL)
            .header(AvenirsSecurityHeaders.SIGNED_CONTEXT, studentPayload)
            .header(AvenirsSecurityHeaders.CONTEXT_SIGNATURE, studentSignature)
            .accept(APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody(String.class)
            .returnResult()
            .getResponseBody();

    JsonNode data = objectMapper.readTree(body).get("data");

    if (data == null || !data.isArray() || data.isEmpty()) {
      throw new IllegalStateException("Seeder returned no experience in /view");
    }

    return UUID.fromString(data.get(0).get("id").asText());
  }

  private UUID searchDeclaredSkillIdForTrace(UUID traceId, int index) throws Exception {
    String body =
        webTestClient
            .get()
            .uri(
                uriBuilder ->
                    uriBuilder
                        .path(SEARCH_PATH)
                        .queryParam("page", "0")
                        .queryParam("pageSize", "8")
                        .build("TRACE", traceId, "DECLARED_SKILL"))
            .header(AvenirsSecurityHeaders.SIGNED_CONTEXT, studentPayload)
            .header(AvenirsSecurityHeaders.CONTEXT_SIGNATURE, studentSignature)
            .accept(APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody(String.class)
            .returnResult()
            .getResponseBody();

    JsonNode data = objectMapper.readTree(body).get("data");

    if (data == null || !data.isArray() || data.size() <= index) {
      throw new IllegalStateException("No declared skill available for association");
    }

    return UUID.fromString(data.get(index).get("id").asText());
  }

  private JsonNode associate(
      UUID elementId, String contextType, String associatedContextType, List<UUID> idsToAssociate)
      throws Exception {
    String body =
        webTestClient
            .post()
            .uri(ASSOCIATE_PATH, contextType, elementId, associatedContextType)
            .contentType(APPLICATION_JSON)
            .bodyValue(
                objectMapper.writeValueAsString(new AssociationsCreationRequest(idsToAssociate)))
            .header(AvenirsSecurityHeaders.SIGNED_CONTEXT, studentPayload)
            .header(AvenirsSecurityHeaders.CONTEXT_SIGNATURE, studentSignature)
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody(String.class)
            .returnResult()
            .getResponseBody();

    return objectMapper.readTree(body);
  }

  @Test
  void shouldGetTheAssociationsOfATrace() throws Exception {
    BddLogger.given("an existing trace");
    UUID traceId = getFirstTraceId();

    when("getting its associations");

    webTestClient
        .get()
        .uri(ASSOCIATIONS_PATH, "TRACE", traceId)
        .header(AvenirsSecurityHeaders.SIGNED_CONTEXT, studentPayload)
        .header(AvenirsSecurityHeaders.CONTEXT_SIGNATURE, studentSignature)
        .accept(APPLICATION_JSON)
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody()
        .jsonPath("$.traceAssociations")
        .isArray()
        .jsonPath("$.declaredActivityAssociations")
        .isArray()
        .jsonPath("$.declaredSkillAssociations")
        .isArray()
        .jsonPath("$.declaredExperienceAssociations")
        .isArray()
        .jsonPath("$.declaredProgramAssociations")
        .isArray();

    BddLogger.then("it should return every associated element grouped by context");
  }

  @Test
  void shouldReturn404WhenGettingTheAssociationsOfAnUnknownTrace() {
    BddLogger.given("an unknown trace");

    when("getting its associations");

    webTestClient
        .get()
        .uri(ASSOCIATIONS_PATH, "TRACE", UUID.randomUUID())
        .header(AvenirsSecurityHeaders.SIGNED_CONTEXT, studentPayload)
        .header(AvenirsSecurityHeaders.CONTEXT_SIGNATURE, studentSignature)
        .accept(APPLICATION_JSON)
        .exchange()
        .expectStatus()
        .isNotFound();

    BddLogger.then("it should return 404");
  }

  @Test
  void shouldReturn403WhenGettingTheAssociationsWithoutPermission() {
    BddLogger.given("an authenticated user without the trace list permission");

    when("getting the associations of a trace");

    webTestClient
        .get()
        .uri(ASSOCIATIONS_PATH, "TRACE", UUID.randomUUID())
        .header(AvenirsSecurityHeaders.SIGNED_CONTEXT, noPermissionPayload)
        .header(AvenirsSecurityHeaders.CONTEXT_SIGNATURE, noPermissionSignature)
        .accept(APPLICATION_JSON)
        .exchange()
        .expectStatus()
        .isForbidden();

    BddLogger.then("it should return 403");
  }

  @Test
  void shouldAssociateATraceWithADeclaredSkill() throws Exception {
    BddLogger.given("an existing trace and an available declared skill");
    UUID traceId = getFirstTraceId();
    UUID declaredSkillId = searchDeclaredSkillIdForTrace(traceId, 0);

    when("associating the trace with the declared skill");

    JsonNode associations = associate(traceId, "TRACE", "DECLARED_SKILL", List.of(declaredSkillId));

    BddLogger.then("it should return the associations of the trace");
    assertThat(associations.get("declaredSkillAssociations").isArray()).isTrue();
  }

  @Test
  void shouldAssociateATraceWithADeclaredExperience() throws Exception {
    BddLogger.given("an existing trace and an existing declared experience");
    UUID traceId = getFirstTraceId();
    UUID declaredExperienceId = getFirstDeclaredExperienceId();

    when("associating the trace with the declared experience");

    JsonNode associations =
        associate(traceId, "TRACE", "DECLARED_EXPERIENCE", List.of(declaredExperienceId));

    BddLogger.then("it should return the associations of the trace");
    assertThat(associations.get("declaredExperienceAssociations").isArray()).isTrue();
  }

  @Test
  void shouldReturn404WhenAssociatingAnUnknownTrace() throws Exception {
    BddLogger.given("an unknown trace and an existing declared experience");
    UUID declaredExperienceId = getFirstDeclaredExperienceId();

    when("associating the unknown trace with the declared experience");

    webTestClient
        .post()
        .uri(ASSOCIATE_PATH, "TRACE", UUID.randomUUID(), "DECLARED_EXPERIENCE")
        .contentType(APPLICATION_JSON)
        .bodyValue(
            objectMapper.writeValueAsString(
                new AssociationsCreationRequest(List.of(declaredExperienceId))))
        .header(AvenirsSecurityHeaders.SIGNED_CONTEXT, studentPayload)
        .header(AvenirsSecurityHeaders.CONTEXT_SIGNATURE, studentSignature)
        .exchange()
        .expectStatus()
        .isNotFound();

    BddLogger.then("it should return 404");
  }

  @Test
  void shouldReturn404WhenAssociatingAnUnknownDeclaredExperience() throws Exception {
    BddLogger.given("an existing trace and an unknown declared experience");
    UUID traceId = getFirstTraceId();

    when("associating the trace with the unknown declared experience");

    webTestClient
        .post()
        .uri(ASSOCIATE_PATH, "TRACE", traceId, "DECLARED_EXPERIENCE")
        .contentType(APPLICATION_JSON)
        .bodyValue(
            objectMapper.writeValueAsString(
                new AssociationsCreationRequest(List.of(UUID.randomUUID()))))
        .header(AvenirsSecurityHeaders.SIGNED_CONTEXT, studentPayload)
        .header(AvenirsSecurityHeaders.CONTEXT_SIGNATURE, studentSignature)
        .exchange()
        .expectStatus()
        .isNotFound();

    BddLogger.then("it should return 404");
  }

  @Test
  void shouldUnassociateTheAssociationsOfATrace() throws Exception {
    BddLogger.given("a trace associated with a declared skill");
    UUID traceId = getFirstTraceId();
    UUID declaredSkillId = searchDeclaredSkillIdForTrace(traceId, 1);

    JsonNode associations = associate(traceId, "TRACE", "DECLARED_SKILL", List.of(declaredSkillId));
    UUID associationId =
        UUID.fromString(
            associations.get("declaredSkillAssociations").get(0).get("associationId").asText());

    when("unassociating the association");

    webTestClient
        .method(HttpMethod.DELETE)
        .uri(ASSOCIATIONS_PATH, "TRACE", traceId)
        .contentType(APPLICATION_JSON)
        .bodyValue(
            objectMapper.writeValueAsString(new AssociationsDeleteRequest(List.of(associationId))))
        .header(AvenirsSecurityHeaders.SIGNED_CONTEXT, studentPayload)
        .header(AvenirsSecurityHeaders.CONTEXT_SIGNATURE, studentSignature)
        .exchange()
        .expectStatus()
        .isNoContent();

    BddLogger.then("it should delete the association");
  }

  @Test
  void shouldSearchDeclaredActivitiesForAssociationWithATrace() throws Exception {
    BddLogger.given("an existing trace");
    UUID traceId = getFirstTraceId();

    when("searching the declared activities to associate");

    webTestClient
        .get()
        .uri(
            uriBuilder ->
                uriBuilder
                    .path(SEARCH_PATH)
                    .queryParam("page", "0")
                    .queryParam("pageSize", "8")
                    .build("TRACE", traceId, "DECLARED_ACTIVITY"))
        .header(AvenirsSecurityHeaders.SIGNED_CONTEXT, studentPayload)
        .header(AvenirsSecurityHeaders.CONTEXT_SIGNATURE, studentSignature)
        .accept(APPLICATION_JSON)
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody()
        .jsonPath("$.data")
        .isArray()
        .jsonPath("$.data[0].id")
        .exists()
        .jsonPath("$.data[0].title")
        .exists()
        .jsonPath("$.data[0].category")
        .exists()
        .jsonPath("$.data[0].disabled")
        .exists()
        .jsonPath("$.page.page")
        .isEqualTo(0)
        .jsonPath("$.page.pageSize")
        .isEqualTo(8);

    BddLogger.then("it should return paged results with correct structure");
  }

  @Test
  void shouldSearchDeclaredSkillsForAssociationWithATrace() throws Exception {
    BddLogger.given("an existing trace");
    UUID traceId = getFirstTraceId();

    when("searching the declared skills to associate");

    webTestClient
        .get()
        .uri(
            uriBuilder ->
                uriBuilder
                    .path(SEARCH_PATH)
                    .queryParam("page", "0")
                    .queryParam("pageSize", "8")
                    .build("TRACE", traceId, "DECLARED_SKILL"))
        .header(AvenirsSecurityHeaders.SIGNED_CONTEXT, studentPayload)
        .header(AvenirsSecurityHeaders.CONTEXT_SIGNATURE, studentSignature)
        .accept(APPLICATION_JSON)
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody()
        .jsonPath("$.data")
        .isArray()
        .jsonPath("$.data[0].id")
        .exists()
        .jsonPath("$.data[0].title")
        .exists()
        .jsonPath("$.data[0].category")
        .exists()
        .jsonPath("$.data[0].disabled")
        .exists();

    BddLogger.then("it should return paged results with correct structure");
  }

  @Test
  void shouldSearchDeclaredExperiencesForAssociationWithATrace() throws Exception {
    BddLogger.given("an existing trace");
    UUID traceId = getFirstTraceId();

    when("searching the declared experiences to associate");

    webTestClient
        .get()
        .uri(
            uriBuilder ->
                uriBuilder
                    .path(SEARCH_PATH)
                    .queryParam("page", "0")
                    .queryParam("pageSize", "8")
                    .build("TRACE", traceId, "DECLARED_EXPERIENCE"))
        .header(AvenirsSecurityHeaders.SIGNED_CONTEXT, studentPayload)
        .header(AvenirsSecurityHeaders.CONTEXT_SIGNATURE, studentSignature)
        .accept(APPLICATION_JSON)
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody()
        .jsonPath("$.data")
        .isArray()
        .jsonPath("$.data[0].id")
        .exists()
        .jsonPath("$.data[0].title")
        .exists()
        .jsonPath("$.data[0].category")
        .exists()
        .jsonPath("$.data[0].disabled")
        .exists();

    BddLogger.then("it should return paged results with correct structure");
  }

  @Test
  void shouldSearchTracesForAssociationWithADeclaredExperience() throws Exception {
    BddLogger.given("an existing declared experience");
    UUID declaredExperienceId = getFirstDeclaredExperienceId();

    when("searching the traces to associate");

    webTestClient
        .get()
        .uri(
            uriBuilder ->
                uriBuilder
                    .path(SEARCH_PATH)
                    .queryParam("page", "0")
                    .queryParam("pageSize", "8")
                    .build("DECLARED_EXPERIENCE", declaredExperienceId, "TRACE"))
        .header(AvenirsSecurityHeaders.SIGNED_CONTEXT, studentPayload)
        .header(AvenirsSecurityHeaders.CONTEXT_SIGNATURE, studentSignature)
        .accept(APPLICATION_JSON)
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody()
        .jsonPath("$.data")
        .isArray()
        .jsonPath("$.data[0].id")
        .exists()
        .jsonPath("$.data[0].title")
        .exists()
        .jsonPath("$.data[0].disabled")
        .exists();

    BddLogger.then("it should return the traces of the student");
  }

  @Test
  void shouldReturnEmptyResultsWhenSearchingWithAKeywordThatMatchesNothing() throws Exception {
    BddLogger.given("an existing trace and a keyword matching nothing");
    UUID traceId = getFirstTraceId();

    when("searching the declared activities to associate");

    webTestClient
        .get()
        .uri(
            uriBuilder ->
                uriBuilder
                    .path(SEARCH_PATH)
                    .queryParam("keyword", "zzzzzznonexistent")
                    .queryParam("page", "0")
                    .queryParam("pageSize", "8")
                    .build("TRACE", traceId, "DECLARED_ACTIVITY"))
        .header(AvenirsSecurityHeaders.SIGNED_CONTEXT, studentPayload)
        .header(AvenirsSecurityHeaders.CONTEXT_SIGNATURE, studentSignature)
        .accept(APPLICATION_JSON)
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody()
        .jsonPath("$.data")
        .isArray()
        .jsonPath("$.data.length()")
        .isEqualTo(0);

    BddLogger.then("it should return empty results");
  }

  @Test
  void shouldReturn404WhenSearchingForAnUnknownTrace() {
    BddLogger.given("an unknown trace");

    when("searching the declared activities to associate");

    webTestClient
        .get()
        .uri(
            uriBuilder ->
                uriBuilder
                    .path(SEARCH_PATH)
                    .queryParam("page", "0")
                    .queryParam("pageSize", "8")
                    .build("TRACE", UUID.randomUUID(), "DECLARED_ACTIVITY"))
        .header(AvenirsSecurityHeaders.SIGNED_CONTEXT, studentPayload)
        .header(AvenirsSecurityHeaders.CONTEXT_SIGNATURE, studentSignature)
        .accept(APPLICATION_JSON)
        .exchange()
        .expectStatus()
        .isNotFound();

    BddLogger.then("it should return 404");
  }

  @Test
  void shouldReturn400WhenTheContextTypeIsUnknown() throws Exception {
    BddLogger.given("an existing trace and an unknown context type");
    UUID traceId = getFirstTraceId();

    when("searching the elements to associate");

    webTestClient
        .get()
        .uri(SEARCH_PATH, "TRACE", traceId, "UNKNOWN")
        .header(AvenirsSecurityHeaders.SIGNED_CONTEXT, studentPayload)
        .header(AvenirsSecurityHeaders.CONTEXT_SIGNATURE, studentSignature)
        .accept(APPLICATION_JSON)
        .exchange()
        .expectStatus()
        .isBadRequest();

    BddLogger.then("it should return 400");
  }

  @Test
  void shouldReturn403WhenSearchingForAssociationWithoutPermission() {
    BddLogger.given("an authenticated user without the trace association permission");

    when("searching the declared skills to associate");

    webTestClient
        .get()
        .uri(SEARCH_PATH, "TRACE", UUID.randomUUID(), "DECLARED_SKILL")
        .header(AvenirsSecurityHeaders.SIGNED_CONTEXT, noPermissionPayload)
        .header(AvenirsSecurityHeaders.CONTEXT_SIGNATURE, noPermissionSignature)
        .accept(APPLICATION_JSON)
        .exchange()
        .expectStatus()
        .isForbidden();

    BddLogger.then("it should return 403");
  }

  private String buildCreateExperienceJson() {
    return "{\n"
        + "  \"title\": \"My Experience\",\n"
        + "  \"experienceType\": \"PROFESSIONAL\",\n"
        + "  \"organization\": \"ACME Inc\",\n"
        + "  \"activitySector\": \"IT\",\n"
        + "  \"location\": \"Paris\",\n"
        + "  \"description\": \"Some description\",\n"
        + "  \"sourceOfInformation\": \"SELF_DECLARED\",\n"
        + "  \"summary\": \"Summary text\",\n"
        + "  \"externalLink\": \"https://example.com\",\n"
        + "  \"startDate\": \"2024-01-01\",\n"
        + "  \"endDate\": \"2024-03-01\",\n"
        + "  \"result\": \"Certified\"\n"
        + "}\n";
  }

  private String extractIdFromResponse(String responseBody) throws Exception {
    JsonNode jsonNode = objectMapper.readTree(responseBody);
    return jsonNode.get("id").asText();
  }

  private String createDeclaredExperienceAs(String payload, String signature) throws Exception {
    String responseBody =
        webTestClient
            .post()
            .uri(DECLARED_EXPERIENCE_BASE_PATH + "/")
            .header("X-Signed-Context", payload)
            .header("X-Context-Signature", signature)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(buildCreateExperienceJson())
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(String.class)
            .returnResult()
            .getResponseBody();

    return extractIdFromResponse(responseBody);
  }

  private UUID createAnyAvailableDeclaredSkillProgressAs(String payload, String signature)
      throws Exception {
    return createAnyAvailableDeclaredSkillProgressAs(List.of(), payload, signature);
  }

  private UUID createAnyAvailableDeclaredSkillProgressAs(
      List<UUID> excludedSkillIds, String payload, String signature) throws Exception {
    for (var declaredSkill : declaredSkillRepository.findAll()) {
      if (excludedSkillIds.contains(declaredSkill.getId())) {
        continue;
      }

      var result =
          webTestClient
              .post()
              .uri("/me/declared/skill-progress")
              .header("X-Signed-Context", payload)
              .header("X-Context-Signature", signature)
              .contentType(MediaType.APPLICATION_JSON)
              .bodyValue(
                  "{\n"
                      + "  \"id\": \"%s\",\n".formatted(declaredSkill.getId())
                      + "  \"level\": \"BEGINNER\",\n"
                      + "  \"type\": \"ROME4\"\n"
                      + "}\n")
              .exchange()
              .expectBody(String.class)
              .returnResult();

      if (result.getStatus().is2xxSuccessful()) {
        return UUID.fromString(extractIdFromResponse(result.getResponseBody()));
      }
    }

    throw new IllegalStateException(
        "No declared skill available for association in the seeded catalog");
  }

  private UUID createTraceAs(String title, String payload, String signature) throws Exception {
    String responseBody =
        webTestClient
            .post()
            .uri("/me/traces")
            .header("X-Signed-Context", payload)
            .header("X-Context-Signature", signature)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(
                objectMapper.writeValueAsString(
                    Map.of(
                        "title", title,
                        "language", "FRENCH",
                        "authorType", "PERSONAL")))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(String.class)
            .returnResult()
            .getResponseBody();

    return UUID.fromString(objectMapper.readTree(responseBody).get("traceId").asText());
  }

  @Test
  void shouldGetDeclaredExperienceAssociations() throws Exception {
    BddLogger.given("an existing declared experience");

    String responseBody =
        webTestClient
            .post()
            .uri(DECLARED_EXPERIENCE_BASE_PATH + "/")
            .header("X-Signed-Context", studentPayload)
            .header("X-Context-Signature", studentSignature)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(buildCreateExperienceJson())
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(String.class)
            .returnResult()
            .getResponseBody();

    String experienceId = extractIdFromResponse(responseBody);

    BddLogger.when("getting associations of declared experience");

    webTestClient
        .get()
        .uri(DECLARED_EXPERIENCE_ASSOCIATIONS_PATH + "/" + experienceId)
        .header("X-Signed-Context", studentPayload)
        .header("X-Context-Signature", studentSignature)
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody()
        .jsonPath("$.traceAssociations")
        .isArray();

    BddLogger.then("it should return associations");
  }

  @Test
  void shouldReturn404WhenGettingAssociationsOfUnknownDeclaredExperience() {
    BddLogger.given("a non existing declared experience id");

    BddLogger.when("getting associations");

    webTestClient
        .get()
        .uri(DECLARED_EXPERIENCE_ASSOCIATIONS_PATH + "/" + notFoundDeclaredExperienceId)
        .header("X-Signed-Context", studentPayload)
        .header("X-Context-Signature", studentSignature)
        .exchange()
        .expectStatus()
        .isNotFound();

    BddLogger.then("it should return 404");
  }

  @Test
  void shouldReturnForbiddenWhenGettingAssociationsOfAnotherStudentExperience() throws Exception {
    BddLogger.given("a declared experience belonging to another student");

    String responseBody =
        webTestClient
            .post()
            .uri(DECLARED_EXPERIENCE_BASE_PATH + "/")
            .header("X-Signed-Context", otherStudentPayload)
            .header("X-Context-Signature", otherStudentSignature)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(buildCreateExperienceJson())
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(String.class)
            .returnResult()
            .getResponseBody();

    String otherExperienceId = extractIdFromResponse(responseBody);

    BddLogger.when("another student tries to access associations");

    webTestClient
        .get()
        .uri(DECLARED_EXPERIENCE_ASSOCIATIONS_PATH + "/" + otherExperienceId)
        .header("X-Signed-Context", studentPayload)
        .header("X-Context-Signature", studentSignature)
        .exchange()
        .expectStatus()
        .isForbidden();

    BddLogger.then("it should return forbidden");
  }

  @Test
  void shouldAssociateDeclaredExperienceWithDeclaredSkillsSuccessfully() throws Exception {
    BddLogger.given("a declared experience and two declared skill progresses of the student");

    String experienceId = createDeclaredExperienceAs(studentPayload, studentSignature);
    UUID skill1 = createAnyAvailableDeclaredSkillProgressAs(studentPayload, studentSignature);
    UUID skill2 =
        createAnyAvailableDeclaredSkillProgressAs(
            List.of(skill1), studentPayload, studentSignature);

    BddLogger.when(
        "performing a POST to associate the experience with both declared skill progresses");

    String responseBody =
        webTestClient
            .post()
            .uri(DECLARED_EXPERIENCE_ASSOCIATIONS_PATH + "/" + experienceId + "/DECLARED_SKILL")
            .header("X-Signed-Context", studentPayload)
            .header("X-Context-Signature", studentSignature)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(
                objectMapper.writeValueAsString(Map.of("idsToAssociate", List.of(skill1, skill2))))
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody(String.class)
            .returnResult()
            .getResponseBody();

    BddLogger.then("it should create both declared skill associations");

    var declaredSkillAssociations =
        objectMapper.readTree(responseBody).get("declaredSkillAssociations");
    assertThat(declaredSkillAssociations.isArray()).isTrue();
    assertThat(declaredSkillAssociations.size()).isEqualTo(2);
  }

  @Test
  void shouldReturn404WhenAssociatingDeclaredSkillsWithNonExistentExperience() throws Exception {
    BddLogger.given("a non-existent declared experience");

    UUID skillId = createAnyAvailableDeclaredSkillProgressAs(studentPayload, studentSignature);

    BddLogger.when("performing a POST to associate declared skills");

    webTestClient
        .post()
        .uri(
            DECLARED_EXPERIENCE_ASSOCIATIONS_PATH
                + "/"
                + notFoundDeclaredExperienceId
                + "/DECLARED_SKILL")
        .header("X-Signed-Context", studentPayload)
        .header("X-Context-Signature", studentSignature)
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(objectMapper.writeValueAsString(Map.of("idsToAssociate", List.of(skillId))))
        .exchange()
        .expectStatus()
        .isNotFound();

    BddLogger.then("it should return 404");
  }

  @Test
  void shouldReturn403WhenAssociatingDeclaredSkillsWithOtherStudentExperience() throws Exception {
    BddLogger.given("a declared experience belonging to another student");

    String otherExperienceId =
        createDeclaredExperienceAs(otherStudentPayload, otherStudentSignature);
    UUID skillId = createAnyAvailableDeclaredSkillProgressAs(studentPayload, studentSignature);

    BddLogger.when("performing a POST to associate declared skills");

    webTestClient
        .post()
        .uri(DECLARED_EXPERIENCE_ASSOCIATIONS_PATH + "/" + otherExperienceId + "/DECLARED_SKILL")
        .header("X-Signed-Context", studentPayload)
        .header("X-Context-Signature", studentSignature)
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(objectMapper.writeValueAsString(Map.of("idsToAssociate", List.of(skillId))))
        .exchange()
        .expectStatus()
        .isForbidden();

    BddLogger.then("it should return forbidden");
  }

  @Test
  void shouldReturn404WhenAssociatingWithNonExistentDeclaredSkill() throws Exception {
    BddLogger.given("a declared experience and a non-existent declared skill progress id");

    String experienceId = createDeclaredExperienceAs(studentPayload, studentSignature);
    UUID nonExistentSkillId = UUID.randomUUID();

    BddLogger.when("performing a POST to associate with a non-existent declared skill");

    webTestClient
        .post()
        .uri(DECLARED_EXPERIENCE_ASSOCIATIONS_PATH + "/" + experienceId + "/DECLARED_SKILL")
        .header("X-Signed-Context", studentPayload)
        .header("X-Context-Signature", studentSignature)
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(
            objectMapper.writeValueAsString(Map.of("idsToAssociate", List.of(nonExistentSkillId))))
        .exchange()
        .expectStatus()
        .isNotFound()
        .expectBody()
        .jsonPath("$.code")
        .isEqualTo("DECLARED_SKILL_PROGRESS_NOT_FOUND");

    BddLogger.then("it should return 404 for declared skill progress not found");
  }

  @Test
  void shouldReturn403WhenAssociatingWithOtherStudentDeclaredSkill() throws Exception {
    BddLogger.given("a declared skill progress belonging to another student");

    String experienceId = createDeclaredExperienceAs(studentPayload, studentSignature);
    UUID otherStudentSkillId = UUID.fromString("72de2a8e-be49-437e-b759-15f8e3a06de3");

    BddLogger.when("performing a POST to associate with the other student's declared skill");

    webTestClient
        .post()
        .uri(DECLARED_EXPERIENCE_ASSOCIATIONS_PATH + "/" + experienceId + "/DECLARED_SKILL")
        .header("X-Signed-Context", studentPayload)
        .header("X-Context-Signature", studentSignature)
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(
            objectMapper.writeValueAsString(Map.of("idsToAssociate", List.of(otherStudentSkillId))))
        .exchange()
        .expectStatus()
        .isForbidden()
        .expectBody()
        .jsonPath("$.code")
        .isEqualTo("USER_NOT_AUTHORIZED");

    BddLogger.then("it should return forbidden");
  }

  @Test
  void shouldHandleEmptyDeclaredSkillListWhenAssociating() throws Exception {
    BddLogger.given("a declared experience and an empty declared skill list");

    String experienceId = createDeclaredExperienceAs(studentPayload, studentSignature);

    BddLogger.when("performing a POST with an empty declared skill list");

    webTestClient
        .post()
        .uri(DECLARED_EXPERIENCE_ASSOCIATIONS_PATH + "/" + experienceId + "/DECLARED_SKILL")
        .header("X-Signed-Context", studentPayload)
        .header("X-Context-Signature", studentSignature)
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(objectMapper.writeValueAsString(Map.of("idsToAssociate", List.of())))
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody()
        .jsonPath("$.declaredSkillAssociations")
        .isArray()
        .jsonPath("$.declaredSkillAssociations")
        .isEmpty();

    BddLogger.then("it should succeed with no association created");
  }

  @Test
  void shouldCreateOnlyOneAssociationWhenDeclaredSkillIdIsDuplicatedInRequest() throws Exception {
    BddLogger.given("a request containing the same declared skill progress id twice");

    String experienceId = createDeclaredExperienceAs(studentPayload, studentSignature);
    UUID skillId = createAnyAvailableDeclaredSkillProgressAs(studentPayload, studentSignature);

    BddLogger.when("performing a POST with the duplicated declared skill id");

    String responseBody =
        webTestClient
            .post()
            .uri(DECLARED_EXPERIENCE_ASSOCIATIONS_PATH + "/" + experienceId + "/DECLARED_SKILL")
            .header("X-Signed-Context", studentPayload)
            .header("X-Context-Signature", studentSignature)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(
                objectMapper.writeValueAsString(
                    Map.of("idsToAssociate", List.of(skillId, skillId))))
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody(String.class)
            .returnResult()
            .getResponseBody();

    BddLogger.then("it should create a single association, not two");

    var declaredSkillAssociations =
        objectMapper.readTree(responseBody).get("declaredSkillAssociations");
    assertThat(declaredSkillAssociations.size()).isEqualTo(1);
  }

  @Test
  void shouldReturn409WhenDeclaredSkillAlreadyAssociated() throws Exception {
    BddLogger.given("a declared skill progress already associated with the experience");

    String experienceId = createDeclaredExperienceAs(studentPayload, studentSignature);
    UUID skillId = createAnyAvailableDeclaredSkillProgressAs(studentPayload, studentSignature);

    webTestClient
        .post()
        .uri(DECLARED_EXPERIENCE_ASSOCIATIONS_PATH + "/" + experienceId + "/DECLARED_SKILL")
        .header("X-Signed-Context", studentPayload)
        .header("X-Context-Signature", studentSignature)
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(objectMapper.writeValueAsString(Map.of("idsToAssociate", List.of(skillId))))
        .exchange()
        .expectStatus()
        .isOk();

    BddLogger.when("performing the same association request again");

    webTestClient
        .post()
        .uri(DECLARED_EXPERIENCE_ASSOCIATIONS_PATH + "/" + experienceId + "/DECLARED_SKILL")
        .header("X-Signed-Context", studentPayload)
        .header("X-Context-Signature", studentSignature)
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(objectMapper.writeValueAsString(Map.of("idsToAssociate", List.of(skillId))))
        .exchange()
        .expectStatus()
        .isEqualTo(409)
        .expectBody()
        .jsonPath("$.code")
        .isEqualTo("ASSOCIATION_ALREADY_EXIST");

    BddLogger.then("it should reject the already existing association with a 409 conflict");
  }

  @Test
  void shouldAssociateDeclaredExperienceWithTracesSuccessfully() throws Exception {
    BddLogger.given("a declared experience and two traces of the student");

    String experienceId = createDeclaredExperienceAs(studentPayload, studentSignature);
    UUID trace1 = createTraceAs("Trace 1", studentPayload, studentSignature);
    UUID trace2 = createTraceAs("Trace 2", studentPayload, studentSignature);

    BddLogger.when("performing a POST to associate the experience with both traces");

    String responseBody =
        webTestClient
            .post()
            .uri(DECLARED_EXPERIENCE_ASSOCIATIONS_PATH + "/" + experienceId + "/TRACE")
            .header("X-Signed-Context", studentPayload)
            .header("X-Context-Signature", studentSignature)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(
                objectMapper.writeValueAsString(Map.of("idsToAssociate", List.of(trace1, trace2))))
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody(String.class)
            .returnResult()
            .getResponseBody();

    BddLogger.then("it should create both trace associations");

    var traceAssociations = objectMapper.readTree(responseBody).get("traceAssociations");
    assertThat(traceAssociations.isArray()).isTrue();
    assertThat(traceAssociations.size()).isEqualTo(2);
  }

  @Test
  void shouldReturn404WhenAssociatingTracesWithNonExistentExperience() throws Exception {
    BddLogger.given("a non-existent declared experience");

    UUID traceId = createTraceAs("Trace", studentPayload, studentSignature);

    BddLogger.when("performing a POST to associate traces");

    webTestClient
        .post()
        .uri(DECLARED_EXPERIENCE_ASSOCIATIONS_PATH + "/" + notFoundDeclaredExperienceId + "/TRACE")
        .header("X-Signed-Context", studentPayload)
        .header("X-Context-Signature", studentSignature)
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(objectMapper.writeValueAsString(Map.of("idsToAssociate", List.of(traceId))))
        .exchange()
        .expectStatus()
        .isNotFound();

    BddLogger.then("it should return 404");
  }

  @Test
  void shouldReturn403WhenAssociatingTracesWithOtherStudentExperience() throws Exception {
    BddLogger.given("a declared experience belonging to another student");

    String otherExperienceId =
        createDeclaredExperienceAs(otherStudentPayload, otherStudentSignature);
    UUID traceId = createTraceAs("Trace", studentPayload, studentSignature);

    BddLogger.when("performing a POST to associate traces");

    webTestClient
        .post()
        .uri(DECLARED_EXPERIENCE_ASSOCIATIONS_PATH + "/" + otherExperienceId + "/TRACE")
        .header("X-Signed-Context", studentPayload)
        .header("X-Context-Signature", studentSignature)
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(objectMapper.writeValueAsString(Map.of("idsToAssociate", List.of(traceId))))
        .exchange()
        .expectStatus()
        .isForbidden();

    BddLogger.then("it should return forbidden");
  }

  @Test
  void shouldReturn404WhenAssociatingWithNonExistentTrace() throws Exception {
    BddLogger.given("a declared experience and a non-existent trace id");

    String experienceId = createDeclaredExperienceAs(studentPayload, studentSignature);
    UUID nonExistentTraceId = UUID.randomUUID();

    BddLogger.when("performing a POST to associate with a non-existent trace");

    webTestClient
        .post()
        .uri(DECLARED_EXPERIENCE_ASSOCIATIONS_PATH + "/" + experienceId + "/TRACE")
        .header("X-Signed-Context", studentPayload)
        .header("X-Context-Signature", studentSignature)
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(
            objectMapper.writeValueAsString(Map.of("idsToAssociate", List.of(nonExistentTraceId))))
        .exchange()
        .expectStatus()
        .isNotFound()
        .expectBody()
        .jsonPath("$.code")
        .isEqualTo("TRACE_NOT_FOUND");

    BddLogger.then("it should return 404 for trace not found");
  }

  @Test
  void shouldReturn403WhenAssociatingWithOtherStudentTrace() throws Exception {
    BddLogger.given("a trace belonging to another student");

    String experienceId = createDeclaredExperienceAs(studentPayload, studentSignature);
    UUID otherStudentTraceId =
        createTraceAs("Other student trace", otherStudentPayload, otherStudentSignature);

    BddLogger.when("performing a POST to associate with the other student's trace");

    webTestClient
        .post()
        .uri(DECLARED_EXPERIENCE_ASSOCIATIONS_PATH + "/" + experienceId + "/TRACE")
        .header("X-Signed-Context", studentPayload)
        .header("X-Context-Signature", studentSignature)
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(
            objectMapper.writeValueAsString(Map.of("idsToAssociate", List.of(otherStudentTraceId))))
        .exchange()
        .expectStatus()
        .isForbidden()
        .expectBody()
        .jsonPath("$.code")
        .isEqualTo("USER_NOT_AUTHORIZED");

    BddLogger.then("it should return forbidden");
  }

  @Test
  void shouldHandleEmptyTraceListWhenAssociating() throws Exception {
    BddLogger.given("a declared experience and an empty trace list");

    String experienceId = createDeclaredExperienceAs(studentPayload, studentSignature);

    BddLogger.when("performing a POST with an empty trace list");

    webTestClient
        .post()
        .uri(DECLARED_EXPERIENCE_ASSOCIATIONS_PATH + "/" + experienceId + "/TRACE")
        .header("X-Signed-Context", studentPayload)
        .header("X-Context-Signature", studentSignature)
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(objectMapper.writeValueAsString(Map.of("idsToAssociate", List.of())))
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody()
        .jsonPath("$.traceAssociations")
        .isArray()
        .jsonPath("$.traceAssociations")
        .isEmpty();

    BddLogger.then("it should succeed with no association created");
  }

  @Test
  void shouldCreateOnlyOneAssociationWhenTraceIdIsDuplicatedInRequest() throws Exception {
    BddLogger.given("a request containing the same trace id twice");

    String experienceId = createDeclaredExperienceAs(studentPayload, studentSignature);
    UUID traceId = createTraceAs("Duplicated trace", studentPayload, studentSignature);

    BddLogger.when("performing a POST with the duplicated trace id");

    String responseBody =
        webTestClient
            .post()
            .uri(DECLARED_EXPERIENCE_ASSOCIATIONS_PATH + "/" + experienceId + "/TRACE")
            .header("X-Signed-Context", studentPayload)
            .header("X-Context-Signature", studentSignature)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(
                objectMapper.writeValueAsString(
                    Map.of("idsToAssociate", List.of(traceId, traceId))))
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody(String.class)
            .returnResult()
            .getResponseBody();

    BddLogger.then("it should create a single association, not two");

    var traceAssociations = objectMapper.readTree(responseBody).get("traceAssociations");
    assertThat(traceAssociations.size()).isEqualTo(1);
  }

  @Test
  void shouldReturn409WhenTraceAlreadyAssociated() throws Exception {
    BddLogger.given("a trace already associated with the experience");

    String experienceId = createDeclaredExperienceAs(studentPayload, studentSignature);
    UUID traceId = createTraceAs("Already associated trace", studentPayload, studentSignature);

    webTestClient
        .post()
        .uri(DECLARED_EXPERIENCE_ASSOCIATIONS_PATH + "/" + experienceId + "/TRACE")
        .header("X-Signed-Context", studentPayload)
        .header("X-Context-Signature", studentSignature)
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(objectMapper.writeValueAsString(Map.of("idsToAssociate", List.of(traceId))))
        .exchange()
        .expectStatus()
        .isOk();

    BddLogger.when("performing the same association request again");

    webTestClient
        .post()
        .uri(DECLARED_EXPERIENCE_ASSOCIATIONS_PATH + "/" + experienceId + "/TRACE")
        .header("X-Signed-Context", studentPayload)
        .header("X-Context-Signature", studentSignature)
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(objectMapper.writeValueAsString(Map.of("idsToAssociate", List.of(traceId))))
        .exchange()
        .expectStatus()
        .isEqualTo(409)
        .expectBody()
        .jsonPath("$.code")
        .isEqualTo("ASSOCIATION_ALREADY_EXIST");

    BddLogger.then("it should reject the already existing association with a 409 conflict");
  }

  @Test
  void shouldDeleteSingleTraceAssociationSuccessfully() throws Exception {
    BddLogger.given("a declared experience associated with a trace");

    String experienceId = createDeclaredExperienceAs(studentPayload, studentSignature);
    UUID traceId = createTraceAs("Trace to unassociate", studentPayload, studentSignature);
    UUID associationId = associateExperienceWithTraceAndGetAssociationId(experienceId, traceId);

    BddLogger.when("performing a DELETE to unassociate the trace");

    webTestClient
        .method(HttpMethod.DELETE)
        .uri(DECLARED_EXPERIENCE_ASSOCIATIONS_PATH + "/" + experienceId)
        .header("X-Signed-Context", studentPayload)
        .header("X-Context-Signature", studentSignature)
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(objectMapper.writeValueAsString(Map.of("idsToDelete", List.of(associationId))))
        .exchange()
        .expectStatus()
        .isNoContent();

    BddLogger.then("the trace association should no longer appear on the experience");

    webTestClient
        .get()
        .uri(DECLARED_EXPERIENCE_ASSOCIATIONS_PATH + "/" + experienceId)
        .header("X-Signed-Context", studentPayload)
        .header("X-Context-Signature", studentSignature)
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody()
        .jsonPath("$.traceAssociations")
        .isEmpty();
  }

  @Test
  void shouldDeleteOnlySelectedTraceAssociationsKeepingOthers() throws Exception {
    BddLogger.given("a declared experience associated with three traces");

    String experienceId = createDeclaredExperienceAs(studentPayload, studentSignature);
    UUID trace1 = createTraceAs("Trace to keep 1", studentPayload, studentSignature);
    UUID trace2 = createTraceAs("Trace to remove", studentPayload, studentSignature);
    UUID trace3 = createTraceAs("Trace to keep 2", studentPayload, studentSignature);

    UUID association1 = associateExperienceWithTraceAndGetAssociationId(experienceId, trace1);
    UUID association2 = associateExperienceWithTraceAndGetAssociationId(experienceId, trace2);
    UUID association3 = associateExperienceWithTraceAndGetAssociationId(experienceId, trace3);

    BddLogger.when("performing a DELETE to unassociate only the middle trace");

    webTestClient
        .method(HttpMethod.DELETE)
        .uri(DECLARED_EXPERIENCE_ASSOCIATIONS_PATH + "/" + experienceId)
        .header("X-Signed-Context", studentPayload)
        .header("X-Context-Signature", studentSignature)
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(objectMapper.writeValueAsString(Map.of("idsToDelete", List.of(association2))))
        .exchange()
        .expectStatus()
        .isNoContent();

    BddLogger.then("only the remaining two trace associations should be present");

    String responseBody =
        webTestClient
            .get()
            .uri(DECLARED_EXPERIENCE_ASSOCIATIONS_PATH + "/" + experienceId)
            .header("X-Signed-Context", studentPayload)
            .header("X-Context-Signature", studentSignature)
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody(String.class)
            .returnResult()
            .getResponseBody();

    var remainingAssociationIds = new ArrayList<String>();
    objectMapper
        .readTree(responseBody)
        .get("traceAssociations")
        .forEach(node -> remainingAssociationIds.add(node.get("associationId").asText()));

    assertThat(remainingAssociationIds)
        .containsExactlyInAnyOrder(association1.toString(), association3.toString());
  }

  @Test
  void shouldKeepDeclaredSkillAssociationsWhenDeletingTraceAssociations() throws Exception {
    BddLogger.given("a declared experience associated with a trace and a declared skill");

    String experienceId = createDeclaredExperienceAs(studentPayload, studentSignature);
    UUID traceId = createTraceAs("Trace", studentPayload, studentSignature);
    UUID skillId = createAnyAvailableDeclaredSkillProgressAs(studentPayload, studentSignature);

    UUID traceAssociationId =
        associateExperienceWithTraceAndGetAssociationId(experienceId, traceId);

    webTestClient
        .post()
        .uri(DECLARED_EXPERIENCE_ASSOCIATIONS_PATH + "/" + experienceId + "/DECLARED_SKILL")
        .header("X-Signed-Context", studentPayload)
        .header("X-Context-Signature", studentSignature)
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(objectMapper.writeValueAsString(Map.of("idsToAssociate", List.of(skillId))))
        .exchange()
        .expectStatus()
        .isOk();

    BddLogger.when("performing a DELETE to unassociate only the trace");

    webTestClient
        .method(HttpMethod.DELETE)
        .uri(DECLARED_EXPERIENCE_ASSOCIATIONS_PATH + "/" + experienceId)
        .header("X-Signed-Context", studentPayload)
        .header("X-Context-Signature", studentSignature)
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(
            objectMapper.writeValueAsString(Map.of("idsToDelete", List.of(traceAssociationId))))
        .exchange()
        .expectStatus()
        .isNoContent();

    BddLogger.then("the declared skill association should remain untouched");

    webTestClient
        .get()
        .uri(DECLARED_EXPERIENCE_ASSOCIATIONS_PATH + "/" + experienceId)
        .header("X-Signed-Context", studentPayload)
        .header("X-Context-Signature", studentSignature)
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody()
        .jsonPath("$.traceAssociations")
        .isEmpty()
        .jsonPath("$.declaredSkillAssociations")
        .value(list -> assertThat((List<?>) list).hasSize(1));
  }

  @Test
  void shouldKeepOtherExperienceAssociationOfTraceWhenUnassociatingOneExperience()
      throws Exception {
    BddLogger.given("a trace associated with two different declared experiences");

    String experience1Id = createDeclaredExperienceAs(studentPayload, studentSignature);
    String experience2Id = createDeclaredExperienceAs(studentPayload, studentSignature);
    UUID traceId = createTraceAs("Shared trace", studentPayload, studentSignature);

    UUID association1 = associateExperienceWithTraceAndGetAssociationId(experience1Id, traceId);
    associateExperienceWithTraceAndGetAssociationId(experience2Id, traceId);

    BddLogger.when("unassociating the trace from the first experience only");

    webTestClient
        .method(HttpMethod.DELETE)
        .uri(DECLARED_EXPERIENCE_ASSOCIATIONS_PATH + "/" + experience1Id)
        .header("X-Signed-Context", studentPayload)
        .header("X-Context-Signature", studentSignature)
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(objectMapper.writeValueAsString(Map.of("idsToDelete", List.of(association1))))
        .exchange()
        .expectStatus()
        .isNoContent();

    BddLogger.then("the trace should still be associated with the second experience");

    webTestClient
        .get()
        .uri(DECLARED_EXPERIENCE_ASSOCIATIONS_PATH + "/" + experience2Id)
        .header("X-Signed-Context", studentPayload)
        .header("X-Context-Signature", studentSignature)
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody()
        .jsonPath("$.traceAssociations[0].trace.id")
        .isEqualTo(traceId.toString());
  }

  @Test
  void shouldReturn404WhenDeletingAssociationsForNonExistentExperience() throws Exception {
    BddLogger.given("a non-existent declared experience");

    BddLogger.when("performing a DELETE to delete an association");

    webTestClient
        .method(HttpMethod.DELETE)
        .uri(DECLARED_EXPERIENCE_ASSOCIATIONS_PATH + "/" + notFoundDeclaredExperienceId)
        .header("X-Signed-Context", studentPayload)
        .header("X-Context-Signature", studentSignature)
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(
            objectMapper.writeValueAsString(Map.of("idsToDelete", List.of(UUID.randomUUID()))))
        .exchange()
        .expectStatus()
        .isNotFound()
        .expectBody()
        .jsonPath("$.code")
        .isEqualTo("DECLARED_EXPERIENCE_NOT_FOUND");

    BddLogger.then("it should return 404");
  }

  @Test
  void shouldReturn403WhenDeletingAssociationsForOtherStudentExperience() throws Exception {
    BddLogger.given("a declared experience belonging to another student");

    String otherExperienceId =
        createDeclaredExperienceAs(otherStudentPayload, otherStudentSignature);

    BddLogger.when("performing a DELETE to delete an association");

    webTestClient
        .method(HttpMethod.DELETE)
        .uri(DECLARED_EXPERIENCE_ASSOCIATIONS_PATH + "/" + otherExperienceId)
        .header("X-Signed-Context", studentPayload)
        .header("X-Context-Signature", studentSignature)
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(
            objectMapper.writeValueAsString(Map.of("idsToDelete", List.of(UUID.randomUUID()))))
        .exchange()
        .expectStatus()
        .isForbidden()
        .expectBody()
        .jsonPath("$.code")
        .isEqualTo("USER_NOT_AUTHORIZED");

    BddLogger.then("it should return forbidden");
  }

  @Test
  void shouldReturn404WhenAssociationIdDoesNotBelongToExperience() throws Exception {
    BddLogger.given("a declared experience with no matching association for the given id");

    String experienceId = createDeclaredExperienceAs(studentPayload, studentSignature);

    BddLogger.when("performing a DELETE with an unknown association id");

    webTestClient
        .method(HttpMethod.DELETE)
        .uri(DECLARED_EXPERIENCE_ASSOCIATIONS_PATH + "/" + experienceId)
        .header("X-Signed-Context", studentPayload)
        .header("X-Context-Signature", studentSignature)
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(
            objectMapper.writeValueAsString(Map.of("idsToDelete", List.of(UUID.randomUUID()))))
        .exchange()
        .expectStatus()
        .isNotFound()
        .expectBody()
        .jsonPath("$.code")
        .isEqualTo("ASSOCIATION_NOT_FOUND");

    BddLogger.then("it should return not found, since the association does not belong to it");
  }

  @Test
  void shouldNotDeleteAnyAssociationWhenRequestMixesValidAndInvalidIds() throws Exception {
    BddLogger.given("a valid association id and an unknown association id");

    String experienceId = createDeclaredExperienceAs(studentPayload, studentSignature);
    UUID traceId = createTraceAs("Trace kept on error", studentPayload, studentSignature);
    UUID associationId = associateExperienceWithTraceAndGetAssociationId(experienceId, traceId);

    BddLogger.when("performing a DELETE mixing the valid id with an unknown one");

    webTestClient
        .method(HttpMethod.DELETE)
        .uri(DECLARED_EXPERIENCE_ASSOCIATIONS_PATH + "/" + experienceId)
        .header("X-Signed-Context", studentPayload)
        .header("X-Context-Signature", studentSignature)
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(
            objectMapper.writeValueAsString(
                Map.of("idsToDelete", List.of(associationId, UUID.randomUUID()))))
        .exchange()
        .expectStatus()
        .isNotFound();

    BddLogger.then("the valid association should not have been deleted");

    webTestClient
        .get()
        .uri(DECLARED_EXPERIENCE_ASSOCIATIONS_PATH + "/" + experienceId)
        .header("X-Signed-Context", studentPayload)
        .header("X-Context-Signature", studentSignature)
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody()
        .jsonPath("$.traceAssociations[0].associationId")
        .isEqualTo(associationId.toString());
  }

  @Test
  void shouldIgnoreDuplicateAssociationIdsInRequest() throws Exception {
    BddLogger.given("a request containing the same association id twice");

    String experienceId = createDeclaredExperienceAs(studentPayload, studentSignature);
    UUID traceId = createTraceAs("Trace duplicated in delete", studentPayload, studentSignature);
    UUID associationId = associateExperienceWithTraceAndGetAssociationId(experienceId, traceId);

    BddLogger.when("performing a DELETE with the duplicated association id");

    webTestClient
        .method(HttpMethod.DELETE)
        .uri(DECLARED_EXPERIENCE_ASSOCIATIONS_PATH + "/" + experienceId)
        .header("X-Signed-Context", studentPayload)
        .header("X-Context-Signature", studentSignature)
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(
            objectMapper.writeValueAsString(
                Map.of("idsToDelete", List.of(associationId, associationId))))
        .exchange()
        .expectStatus()
        .isNoContent();

    BddLogger.then("the association should be deleted once, without error");

    webTestClient
        .get()
        .uri(DECLARED_EXPERIENCE_ASSOCIATIONS_PATH + "/" + experienceId)
        .header("X-Signed-Context", studentPayload)
        .header("X-Context-Signature", studentSignature)
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody()
        .jsonPath("$.traceAssociations")
        .isEmpty();
  }

  @Test
  void shouldHandleEmptyAssociationListWhenDeleting() throws Exception {
    BddLogger.given("a declared experience and an empty association list");

    String experienceId = createDeclaredExperienceAs(studentPayload, studentSignature);

    BddLogger.when("performing a DELETE with an empty association list");

    webTestClient
        .method(HttpMethod.DELETE)
        .uri(DECLARED_EXPERIENCE_ASSOCIATIONS_PATH + "/" + experienceId)
        .header("X-Signed-Context", studentPayload)
        .header("X-Context-Signature", studentSignature)
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(objectMapper.writeValueAsString(Map.of("idsToDelete", List.of())))
        .exchange()
        .expectStatus()
        .isNoContent();

    BddLogger.then("it should succeed with no effect");
  }

  @Test
  void shouldRemoveAssociationBidirectionallyBetweenTraceAndExperience() throws Exception {
    BddLogger.given("a declared experience associated with a trace");

    String experienceId = createDeclaredExperienceAs(studentPayload, studentSignature);
    UUID traceId = createTraceAs("Bidirectional trace", studentPayload, studentSignature);
    UUID associationId = associateExperienceWithTraceAndGetAssociationId(experienceId, traceId);

    BddLogger.when("unassociating the trace from the experience");

    webTestClient
        .method(HttpMethod.DELETE)
        .uri(DECLARED_EXPERIENCE_ASSOCIATIONS_PATH + "/" + experienceId)
        .header("X-Signed-Context", studentPayload)
        .header("X-Context-Signature", studentSignature)
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(objectMapper.writeValueAsString(Map.of("idsToDelete", List.of(associationId))))
        .exchange()
        .expectStatus()
        .isNoContent();

    BddLogger.then("the trace should no longer show the experience in its own associations");

    webTestClient
        .get()
        .uri(TRACE_ASSOCIATIONS_PATH + "/" + traceId)
        .header("X-Signed-Context", studentPayload)
        .header("X-Context-Signature", studentSignature)
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody()
        .jsonPath("$.declaredExperienceAssociations")
        .isEmpty();
  }

  @Test
  void shouldSearchTracesForAssociation() throws Exception {
    BddLogger.given("a declared experience and a trace of the student");

    String experienceId = createDeclaredExperienceAs(studentPayload, studentSignature);
    createTraceAs("Trace for association search", studentPayload, studentSignature);

    BddLogger.when("searching traces for association with the experience");

    webTestClient
        .get()
        .uri(
            uriBuilder ->
                uriBuilder
                    .path(
                        DECLARED_EXPERIENCE_ASSOCIATIONS_PATH
                            + "/"
                            + experienceId
                            + "/TRACE/search")
                    .queryParam("page", "0")
                    .queryParam("pageSize", "8")
                    .build())
        .header("X-Signed-Context", studentPayload)
        .header("X-Context-Signature", studentSignature)
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody()
        .jsonPath("$.data")
        .isArray()
        .jsonPath("$.data[0].id")
        .exists()
        .jsonPath("$.data[0].title")
        .exists()
        .jsonPath("$.data[0].disabled")
        .exists()
        .jsonPath("$.page.page")
        .isEqualTo(0)
        .jsonPath("$.page.pageSize")
        .isEqualTo(8);

    BddLogger.then("it should return paged results with correct structure");
  }

  @Test
  void shouldFilterTracesForAssociationByKeyword() throws Exception {
    BddLogger.given("a declared experience and a keyword matching no trace");

    String experienceId = createDeclaredExperienceAs(studentPayload, studentSignature);
    createTraceAs("Some trace title", studentPayload, studentSignature);

    BddLogger.when("searching with a keyword that matches nothing");

    webTestClient
        .get()
        .uri(
            uriBuilder ->
                uriBuilder
                    .path(
                        DECLARED_EXPERIENCE_ASSOCIATIONS_PATH
                            + "/"
                            + experienceId
                            + "/TRACE/search")
                    .queryParam("keyword", "zzzzzznonexistent")
                    .queryParam("page", "0")
                    .queryParam("pageSize", "8")
                    .build())
        .header("X-Signed-Context", studentPayload)
        .header("X-Context-Signature", studentSignature)
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody()
        .jsonPath("$.data")
        .isArray()
        .jsonPath("$.data.length()")
        .isEqualTo(0);

    BddLogger.then("it should return empty results");
  }

  @Test
  void shouldMarkAlreadyAssociatedTraceAsDisabledWhenSearchingForAssociation() throws Exception {
    BddLogger.given("a declared experience already associated with a trace");

    String experienceId = createDeclaredExperienceAs(studentPayload, studentSignature);
    UUID traceId =
        createTraceAs("Trace already associated for search", studentPayload, studentSignature);
    associateExperienceWithTraceAndGetAssociationId(experienceId, traceId);

    BddLogger.when("searching traces for association filtered on that trace's title");

    webTestClient
        .get()
        .uri(
            uriBuilder ->
                uriBuilder
                    .path(
                        DECLARED_EXPERIENCE_ASSOCIATIONS_PATH
                            + "/"
                            + experienceId
                            + "/TRACE/search")
                    .queryParam("keyword", "Trace already associated for search")
                    .queryParam("page", "0")
                    .queryParam("pageSize", "8")
                    .build())
        .header("X-Signed-Context", studentPayload)
        .header("X-Context-Signature", studentSignature)
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody()
        .jsonPath("$.data[0].id")
        .isEqualTo(traceId.toString())
        .jsonPath("$.data[0].disabled")
        .isEqualTo(true);

    BddLogger.then("it should mark the already associated trace as disabled");
  }

  @Test
  void shouldReturn404WhenSearchingTracesForNonExistentDeclaredExperience() {
    BddLogger.given("a non-existent declared experience");

    BddLogger.when("searching traces for association");

    webTestClient
        .get()
        .uri(
            uriBuilder ->
                uriBuilder
                    .path(
                        DECLARED_EXPERIENCE_ASSOCIATIONS_PATH
                            + "/"
                            + notFoundDeclaredExperienceId
                            + "/TRACE/search")
                    .queryParam("page", "0")
                    .queryParam("pageSize", "8")
                    .build())
        .header("X-Signed-Context", studentPayload)
        .header("X-Context-Signature", studentSignature)
        .exchange()
        .expectStatus()
        .isNotFound();

    BddLogger.then("it should return 404");
  }

  @Test
  void shouldReturn403WhenSearchingTracesForOtherStudentDeclaredExperience() throws Exception {
    BddLogger.given("a declared experience belonging to another student");

    String experienceId = createDeclaredExperienceAs(studentPayload, studentSignature);

    BddLogger.when("another student searches traces for association with that experience");

    webTestClient
        .get()
        .uri(
            uriBuilder ->
                uriBuilder
                    .path(
                        DECLARED_EXPERIENCE_ASSOCIATIONS_PATH
                            + "/"
                            + experienceId
                            + "/TRACE/search")
                    .queryParam("page", "0")
                    .queryParam("pageSize", "8")
                    .build())
        .header("X-Signed-Context", otherStudentPayload)
        .header("X-Context-Signature", otherStudentSignature)
        .exchange()
        .expectStatus()
        .isForbidden();

    BddLogger.then("it should return 403");
  }

  private UUID associateExperienceWithTraceAndGetAssociationId(String experienceId, UUID traceId)
      throws Exception {
    String responseBody =
        webTestClient
            .post()
            .uri(DECLARED_EXPERIENCE_ASSOCIATIONS_PATH + "/" + experienceId + "/TRACE")
            .header("X-Signed-Context", studentPayload)
            .header("X-Context-Signature", studentSignature)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(objectMapper.writeValueAsString(Map.of("idsToAssociate", List.of(traceId))))
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody(String.class)
            .returnResult()
            .getResponseBody();

    for (JsonNode node : objectMapper.readTree(responseBody).get("traceAssociations")) {
      if (node.get("trace").get("id").asText().equals(traceId.toString())) {
        return UUID.fromString(node.get("associationId").asText());
      }
    }
    throw new IllegalStateException("Association not found for trace " + traceId);
  }

  private record CreatedDeclaredSkillProgress(UUID declaredSkillId, UUID progressId) {}

  private String buildDeclaredSkillsJson(UUID id) {
    return ("{\n"
            + "  \"id\": \"%s\",\n"
            + "  \"level\": \"BEGINNER\",\n"
            + "  \"type\": \"ROME4\"\n"
            + "}\n")
        .formatted(id);
  }

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
              .uri(DECLARED_SKILL_BASE_PATH)
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

  private UUID createTrace(String title) throws Exception {
    var body =
        Map.of(
            "title", title,
            "language", "FRENCH",
            "authorType", "PERSONAL");

    var response =
        webTestClient
            .post()
            .uri("/me/traces")
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

    return UUID.fromString(objectMapper.readTree(response).get("traceId").asText());
  }

  @Test
  void shouldReturn404WhenGettingAssociationsForNonExistentSkill() throws Exception {
    BddLogger.given("a non-existent declared skill progress ID");

    UUID nonExistentId = UUID.randomUUID();

    BddLogger.when("performing a GET to get associations");

    webTestClient
        .get()
        .uri(DECLARED_SKILL_ASSOCIATIONS_PATH + "/" + nonExistentId)
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
        .uri(DECLARED_SKILL_ASSOCIATIONS_PATH + "/" + otherStudentSkillId)
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
        .uri(DECLARED_SKILL_ASSOCIATIONS_PATH + "/" + nonExistentId + "/DECLARED_ACTIVITY")
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
        .uri(DECLARED_SKILL_ASSOCIATIONS_PATH + "/" + otherStudentSkillId + "/DECLARED_ACTIVITY")
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

    UUID createdSkillId = createAvailableDeclaredSkillProgress().progressId();

    List<UUID> nonExistentActivityIds = List.of(UUID.randomUUID(), UUID.randomUUID());

    BddLogger.when("performing a POST to associate with non-existent activities");

    String requestBody =
        objectMapper.writeValueAsString(Map.of("idsToAssociate", nonExistentActivityIds));

    webTestClient
        .post()
        .uri(DECLARED_SKILL_ASSOCIATIONS_PATH + "/" + createdSkillId + "/DECLARED_ACTIVITY")
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

    UUID createdSkillId = createAvailableDeclaredSkillProgress().progressId();

    BddLogger.when("performing a POST with empty activity list");

    String requestBody = objectMapper.writeValueAsString(Map.of("idsToAssociate", List.of()));

    webTestClient
        .post()
        .uri(DECLARED_SKILL_ASSOCIATIONS_PATH + "/" + createdSkillId + "/DECLARED_ACTIVITY")
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
  void shouldDeleteTheAssociationsOfADeclaredSkill() throws Exception {
    BddLogger.given(
        "a declared skill progress associated with one of the logged-in student's activities");

    UUID createdSkillId = createAvailableDeclaredSkillProgress().progressId();

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
            .uri(DECLARED_SKILL_ASSOCIATIONS_PATH + "/" + createdSkillId + "/DECLARED_ACTIVITY")
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
        .uri(DECLARED_SKILL_ASSOCIATIONS_PATH + "/" + createdSkillId)
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
        .uri(DECLARED_SKILL_ASSOCIATIONS_PATH + "/" + createdSkillId)
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
        .uri(DECLARED_SKILL_ASSOCIATIONS_PATH + "/" + nonExistentId)
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
        .uri(DECLARED_SKILL_ASSOCIATIONS_PATH + "/" + otherStudentSkillId)
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
        .uri(DECLARED_SKILL_ASSOCIATIONS_PATH + "/" + nonExistentId + "/DECLARED_EXPERIENCE")
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
        .uri(DECLARED_SKILL_ASSOCIATIONS_PATH + "/" + otherStudentSkillId + "/DECLARED_EXPERIENCE")
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

    UUID createdSkillId = createAvailableDeclaredSkillProgress().progressId();

    List<UUID> nonExistentExperienceIds = List.of(UUID.randomUUID(), UUID.randomUUID());

    BddLogger.when("performing a POST to associate with non-existent declared experiences");

    String requestBody =
        objectMapper.writeValueAsString(Map.of("idsToAssociate", nonExistentExperienceIds));

    webTestClient
        .post()
        .uri(DECLARED_SKILL_ASSOCIATIONS_PATH + "/" + createdSkillId + "/DECLARED_EXPERIENCE")
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

    UUID createdSkillId = createAvailableDeclaredSkillProgress().progressId();

    BddLogger.when("performing a POST with empty declared experience list");

    String requestBody = objectMapper.writeValueAsString(Map.of("idsToAssociate", List.of()));

    webTestClient
        .post()
        .uri(DECLARED_SKILL_ASSOCIATIONS_PATH + "/" + createdSkillId + "/DECLARED_EXPERIENCE")
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

    UUID createdSkillId = createAvailableDeclaredSkillProgress().progressId();

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
            .uri(DECLARED_SKILL_ASSOCIATIONS_PATH + "/" + createdSkillId + "/DECLARED_EXPERIENCE")
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
        .uri(DECLARED_SKILL_ASSOCIATIONS_PATH + "/" + createdSkillId + "/DECLARED_EXPERIENCE")
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

  @Test
  void shouldReturnDeclaredExperienceAssociationsOrderedByMostRecentlyAssociatedFirst()
      throws Exception {
    BddLogger.given(
        "a declared skill progress with a professional and a personal declared experience"
            + " associated, the personal one being associated most recently");

    UUID declaredSkillProgressId = createAvailableDeclaredSkillProgress().progressId();

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
            .uri(DECLARED_SKILL_ASSOCIATIONS_PATH + "/" + declaredSkillProgressId)
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

  @Test
  void shouldReturn404WhenAssociatingWithNonExistentSkill_traces() throws Exception {
    BddLogger.given("a non-existent declared skill progress");

    UUID nonExistentId = UUID.randomUUID();
    List<UUID> traceIds = List.of(UUID.randomUUID());

    BddLogger.when("performing a POST to associate traces");

    String requestBody = objectMapper.writeValueAsString(Map.of("idsToAssociate", traceIds));

    webTestClient
        .post()
        .uri(DECLARED_SKILL_ASSOCIATIONS_PATH + "/" + nonExistentId + "/TRACE")
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
  void shouldReturn403WhenAssociatingWithOtherStudentSkill_traces() throws Exception {
    BddLogger.given("a declared skill progress belonging to another student");

    UUID otherStudentSkillId = UUID.fromString("72de2a8e-be49-437e-b759-15f8e3a06de3");
    List<UUID> traceIds = List.of(UUID.randomUUID());

    BddLogger.when("performing a POST to associate traces");

    String requestBody = objectMapper.writeValueAsString(Map.of("idsToAssociate", traceIds));

    webTestClient
        .post()
        .uri(DECLARED_SKILL_ASSOCIATIONS_PATH + "/" + otherStudentSkillId + "/TRACE")
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
  void shouldReturn404WhenAssociatingWithNonExistentTraces() throws Exception {
    BddLogger.given("a declared skill progress and non-existent trace IDs");

    UUID createdSkillId = createAvailableDeclaredSkillProgress().progressId();

    List<UUID> nonExistentTraceIds = List.of(UUID.randomUUID(), UUID.randomUUID());

    BddLogger.when("performing a POST to associate with non-existent traces");

    String requestBody =
        objectMapper.writeValueAsString(Map.of("idsToAssociate", nonExistentTraceIds));

    webTestClient
        .post()
        .uri(DECLARED_SKILL_ASSOCIATIONS_PATH + "/" + createdSkillId + "/TRACE")
        .header("X-Signed-Context", studentPayload)
        .header("X-Context-Signature", studentSignature)
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(requestBody)
        .exchange()
        .expectStatus()
        .isNotFound()
        .expectBody()
        .jsonPath("$.code")
        .isEqualTo("TRACE_NOT_FOUND");

    BddLogger.then("it should return 404 for trace not found");
  }

  @Test
  void shouldHandleEmptyTraceListWhenAssociatingWithADeclaredSkill() throws Exception {
    BddLogger.given("a declared skill progress and empty trace list");

    UUID createdSkillId = createAvailableDeclaredSkillProgress().progressId();

    BddLogger.when("performing a POST with empty trace list");

    String requestBody = objectMapper.writeValueAsString(Map.of("idsToAssociate", List.of()));

    webTestClient
        .post()
        .uri(DECLARED_SKILL_ASSOCIATIONS_PATH + "/" + createdSkillId + "/TRACE")
        .header("X-Signed-Context", studentPayload)
        .header("X-Context-Signature", studentSignature)
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(requestBody)
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody()
        .jsonPath("$.traceAssociations")
        .isArray()
        .jsonPath("$.traceAssociations")
        .isEmpty();

    BddLogger.then("it should succeed with empty associations");
  }

  @Test
  void shouldAssociateDeclaredSkillWithTracesSuccessfully() throws Exception {
    BddLogger.given("a declared skill progress and two traces owned by the student");

    UUID createdSkillId = createAvailableDeclaredSkillProgress().progressId();

    UUID traceId1 = createTrace("Compte-rendu de stage");
    UUID traceId2 = createTrace("Rapport de projet");

    BddLogger.when("performing a POST to associate both traces, one of them twice");

    String requestBody =
        objectMapper.writeValueAsString(
            Map.of("idsToAssociate", List.of(traceId1, traceId2, traceId1)));

    var responseBody =
        webTestClient
            .post()
            .uri(DECLARED_SKILL_ASSOCIATIONS_PATH + "/" + createdSkillId + "/TRACE")
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
        "it should create exactly one association per distinct trace, ignoring the duplicated id"
            + " in the request");

    var traceAssociations = objectMapper.readTree(responseBody).get("traceAssociations");
    assertThat(traceAssociations.size()).isEqualTo(2);

    var associatedTraceIds = new ArrayList<String>();
    traceAssociations.forEach(node -> associatedTraceIds.add(node.get("trace").get("id").asText()));
    assertThat(associatedTraceIds)
        .containsExactlyInAnyOrder(traceId1.toString(), traceId2.toString());

    BddLogger.when("performing the same association request again");

    webTestClient
        .post()
        .uri(DECLARED_SKILL_ASSOCIATIONS_PATH + "/" + createdSkillId + "/TRACE")
        .header("X-Signed-Context", studentPayload)
        .header("X-Context-Signature", studentSignature)
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(objectMapper.writeValueAsString(Map.of("idsToAssociate", List.of(traceId1))))
        .exchange()
        .expectStatus()
        .isEqualTo(409)
        .expectBody()
        .jsonPath("$.code")
        .isEqualTo("ASSOCIATION_ALREADY_EXIST");

    BddLogger.then("it should reject the already existing association with a 409 conflict");
  }

  private UUID getFirstActiveDeclaredActivityId() throws Exception {
    String body =
        webTestClient
            .get()
            .uri(
                uriBuilder ->
                    uriBuilder
                        .path(DECLARED_ACTIVITY_BASE_PATH)
                        .queryParam("pageSize", 100)
                        .build())
            .header(AvenirsSecurityHeaders.SIGNED_CONTEXT, studentPayload)
            .header(AvenirsSecurityHeaders.CONTEXT_SIGNATURE, studentSignature)
            .accept(APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody(String.class)
            .returnResult()
            .getResponseBody();

    for (JsonNode declaredActivity : objectMapper.readTree(body).get("data")) {
      var status = declaredActivity.get("status").asText();

      if (!status.equals("UNSUBSCRIBED") && !status.equals("COMPLETED")) {
        return UUID.fromString(declaredActivity.get("id").asText());
      }
    }

    throw new IllegalStateException("No active declared activity for the logged-in student");
  }

  @Test
  void shouldAssociateADeclaredActivityWithADeclaredSkill() throws Exception {
    BddLogger.given("an active declared activity and an available declared skill");
    UUID declaredActivityId = getFirstActiveDeclaredActivityId();
    UUID declaredSkillProgressId = createAvailableDeclaredSkillProgress().progressId();

    BddLogger.when("associating the declared activity with the declared skill");

    JsonNode associations =
        associate(
            declaredActivityId,
            "DECLARED_ACTIVITY",
            "DECLARED_SKILL",
            List.of(declaredSkillProgressId));

    BddLogger.then("it should return the associations of the declared activity");
    assertThat(
            associations
                .get("declaredSkillAssociations")
                .get(0)
                .get("declaredSkill")
                .get("id")
                .asText())
        .isEqualTo(declaredSkillProgressId.toString());
  }

  @Test
  void shouldDeleteTheAssociationsOfADeclaredActivity() throws Exception {
    BddLogger.given("a declared activity associated with a declared skill");
    UUID declaredActivityId = getFirstActiveDeclaredActivityId();
    UUID declaredSkillProgressId = createAvailableDeclaredSkillProgress().progressId();

    JsonNode associations =
        associate(
            declaredActivityId,
            "DECLARED_ACTIVITY",
            "DECLARED_SKILL",
            List.of(declaredSkillProgressId));
    UUID associationId =
        UUID.fromString(
            associations.get("declaredSkillAssociations").get(0).get("associationId").asText());

    BddLogger.when("performing a DELETE with that association id");

    webTestClient
        .method(HttpMethod.DELETE)
        .uri(DECLARED_ACTIVITY_ASSOCIATIONS_PATH + "/" + declaredActivityId)
        .header(AvenirsSecurityHeaders.SIGNED_CONTEXT, studentPayload)
        .header(AvenirsSecurityHeaders.CONTEXT_SIGNATURE, studentSignature)
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(objectMapper.writeValueAsString(Map.of("idsToDelete", List.of(associationId))))
        .exchange()
        .expectStatus()
        .isNoContent();

    BddLogger.then("it should delete the association");
  }

  @Test
  void shouldReturn404WhenAssociatingAnUnknownDeclaredActivity() throws Exception {
    BddLogger.given("an unknown declared activity");

    BddLogger.when("associating it with a declared skill");

    webTestClient
        .post()
        .uri(DECLARED_ACTIVITY_ASSOCIATIONS_PATH + "/" + UUID.randomUUID() + "/DECLARED_SKILL")
        .header(AvenirsSecurityHeaders.SIGNED_CONTEXT, studentPayload)
        .header(AvenirsSecurityHeaders.CONTEXT_SIGNATURE, studentSignature)
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(
            objectMapper.writeValueAsString(Map.of("idsToAssociate", List.of(UUID.randomUUID()))))
        .exchange()
        .expectStatus()
        .isNotFound();

    BddLogger.then("it should return 404");
  }

  @Test
  void shouldReturn404WhenSearchingTracesForAnUnknownDeclaredActivity() {
    BddLogger.given("an unknown declared activity");

    BddLogger.when("searching the traces to associate");

    webTestClient
        .get()
        .uri(SEARCH_PATH, "DECLARED_ACTIVITY", UUID.randomUUID(), "TRACE")
        .header(AvenirsSecurityHeaders.SIGNED_CONTEXT, studentPayload)
        .header(AvenirsSecurityHeaders.CONTEXT_SIGNATURE, studentSignature)
        .accept(APPLICATION_JSON)
        .exchange()
        .expectStatus()
        .isNotFound();

    BddLogger.then("it should return 404");
  }

  @Test
  void shouldReturn403WhenSearchingTracesForAnotherStudentDeclaredActivity() throws Exception {
    BddLogger.given("a declared activity of the logged-in student");
    UUID declaredActivityId = getFirstActiveDeclaredActivityId();

    BddLogger.when("another student searches the traces to associate");

    webTestClient
        .get()
        .uri(SEARCH_PATH, "DECLARED_ACTIVITY", declaredActivityId, "TRACE")
        .header(AvenirsSecurityHeaders.SIGNED_CONTEXT, otherStudentPayload)
        .header(AvenirsSecurityHeaders.CONTEXT_SIGNATURE, otherStudentSignature)
        .accept(APPLICATION_JSON)
        .exchange()
        .expectStatus()
        .isForbidden();

    BddLogger.then("it should return 403");
  }

  private UUID createDeclaredProgramAs(
      String title, String organization, String payload, String signature) throws Exception {
    var body =
        Map.of(
            "title", title,
            "organization", organization,
            "startDate", "2024-01-01");

    var response =
        webTestClient
            .post()
            .uri(DECLARED_PROGRAM_BASE_PATH)
            .header(AvenirsSecurityHeaders.SIGNED_CONTEXT, payload)
            .header(AvenirsSecurityHeaders.CONTEXT_SIGNATURE, signature)
            .contentType(APPLICATION_JSON)
            .bodyValue(objectMapper.writeValueAsString(body))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(String.class)
            .returnResult()
            .getResponseBody();

    return UUID.fromString(objectMapper.readTree(response).get("id").asText());
  }

  private UUID createDeclaredProgram(String title, String organization) throws Exception {
    return createDeclaredProgramAs(title, organization, studentPayload, studentSignature);
  }

  private JsonNode searchForAssociation(
      String contextType, UUID elementId, String associatedContextType, String keyword)
      throws Exception {
    String body =
        webTestClient
            .get()
            .uri(
                uriBuilder ->
                    uriBuilder
                        .path(SEARCH_PATH)
                        .queryParam("page", "0")
                        .queryParam("pageSize", "100")
                        .queryParam("keyword", keyword)
                        .build(contextType, elementId, associatedContextType))
            .header(AvenirsSecurityHeaders.SIGNED_CONTEXT, studentPayload)
            .header(AvenirsSecurityHeaders.CONTEXT_SIGNATURE, studentSignature)
            .accept(APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody(String.class)
            .returnResult()
            .getResponseBody();

    return objectMapper.readTree(body).get("data");
  }

  private JsonNode getAssociations(String contextType, UUID elementId) throws Exception {
    String body =
        webTestClient
            .get()
            .uri(ASSOCIATIONS_PATH, contextType, elementId)
            .header(AvenirsSecurityHeaders.SIGNED_CONTEXT, studentPayload)
            .header(AvenirsSecurityHeaders.CONTEXT_SIGNATURE, studentSignature)
            .accept(APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody(String.class)
            .returnResult()
            .getResponseBody();

    return objectMapper.readTree(body);
  }

  @Test
  void shouldGetTheAssociationsOfADeclaredProgram() throws Exception {
    BddLogger.given("an existing declared program");
    UUID declaredProgramId = createDeclaredProgram("Master informatique", "Université");

    when("getting its associations");

    var associations = getAssociations("DECLARED_PROGRAM", declaredProgramId);

    BddLogger.then("it should return every associated element grouped by context");
    assertThat(associations.get("traceAssociations").isArray()).isTrue();
    assertThat(associations.get("declaredSkillAssociations").isArray()).isTrue();
    assertThat(associations.get("declaredExperienceAssociations").isArray()).isTrue();
    assertThat(associations.get("declaredProgramAssociations").isArray()).isTrue();
  }

  @Test
  void shouldReturn404WhenGettingTheAssociationsOfAnUnknownDeclaredProgram() {
    BddLogger.given("an unknown declared program");

    when("getting its associations");

    webTestClient
        .get()
        .uri(ASSOCIATIONS_PATH, "DECLARED_PROGRAM", UUID.randomUUID())
        .header(AvenirsSecurityHeaders.SIGNED_CONTEXT, studentPayload)
        .header(AvenirsSecurityHeaders.CONTEXT_SIGNATURE, studentSignature)
        .accept(APPLICATION_JSON)
        .exchange()
        .expectStatus()
        .isNotFound();

    BddLogger.then("it should return 404");
  }

  @Test
  void shouldReturn403WhenGettingTheAssociationsOfTheDeclaredProgramOfAnotherStudent()
      throws Exception {
    BddLogger.given("a declared program owned by another student");
    UUID declaredProgramId =
        createDeclaredProgramAs(
            "Licence de droit", "Université", otherStudentPayload, otherStudentSignature);

    when("getting its associations");

    webTestClient
        .get()
        .uri(ASSOCIATIONS_PATH, "DECLARED_PROGRAM", declaredProgramId)
        .header(AvenirsSecurityHeaders.SIGNED_CONTEXT, studentPayload)
        .header(AvenirsSecurityHeaders.CONTEXT_SIGNATURE, studentSignature)
        .accept(APPLICATION_JSON)
        .exchange()
        .expectStatus()
        .isForbidden();

    BddLogger.then("it should return 403");
  }

  @Test
  void shouldAssociateSeveralDeclaredSkillsWithADeclaredProgram() throws Exception {
    BddLogger.given("an existing declared program and two available declared skills");
    UUID declaredProgramId = createDeclaredProgram("Mastère data", "Université");
    UUID firstDeclaredSkillId = createAvailableDeclaredSkillProgress().progressId();
    UUID secondDeclaredSkillId = createAvailableDeclaredSkillProgress().progressId();

    when("associating the declared program with both declared skills");

    JsonNode associations =
        associate(
            declaredProgramId,
            "DECLARED_PROGRAM",
            "DECLARED_SKILL",
            List.of(firstDeclaredSkillId, secondDeclaredSkillId));

    BddLogger.then("it should return the two declared skills associated with the declared program");
    assertThat(associations.get("declaredSkillAssociations")).hasSize(2);
    assertThat(associations.get("declaredSkillAssociations"))
        .allSatisfy(association -> assertThat(association.get("associationId").isNull()).isFalse());
  }

  @Test
  void shouldReturnTheDeclaredProgramInTheAssociationsOfItsDeclaredSkill() throws Exception {
    BddLogger.given("a declared skill associated with a declared program");
    UUID declaredProgramId = createDeclaredProgram("Diplôme ingénieur", "École centrale");
    UUID declaredSkillId = createAvailableDeclaredSkillProgress().progressId();

    associate(declaredProgramId, "DECLARED_PROGRAM", "DECLARED_SKILL", List.of(declaredSkillId));

    when("getting the associations of the declared skill");

    var associations = getAssociations("DECLARED_SKILL", declaredSkillId);

    BddLogger.then("it should return the declared program on the declared skill side");
    assertThat(associations.get("declaredProgramAssociations")).hasSize(1);
    assertThat(
            associations
                .get("declaredProgramAssociations")
                .get(0)
                .get("declaredProgram")
                .get("id")
                .asText())
        .isEqualTo(declaredProgramId.toString());
    assertThat(
            associations
                .get("declaredProgramAssociations")
                .get(0)
                .get("declaredProgram")
                .get("title")
                .asText())
        .isEqualTo("Diplôme ingénieur");
  }

  @Test
  void shouldReturn409WhenAssociatingTwiceTheSameDeclaredSkillWithADeclaredProgram()
      throws Exception {
    BddLogger.given("a declared program already associated with a declared skill");
    UUID declaredProgramId = createDeclaredProgram("DU communication", "Université");
    UUID declaredSkillId = createAvailableDeclaredSkillProgress().progressId();

    associate(declaredProgramId, "DECLARED_PROGRAM", "DECLARED_SKILL", List.of(declaredSkillId));

    when("associating the same declared skill again");

    webTestClient
        .post()
        .uri(ASSOCIATE_PATH, "DECLARED_PROGRAM", declaredProgramId, "DECLARED_SKILL")
        .contentType(APPLICATION_JSON)
        .bodyValue(
            objectMapper.writeValueAsString(
                new AssociationsCreationRequest(List.of(declaredSkillId))))
        .header(AvenirsSecurityHeaders.SIGNED_CONTEXT, studentPayload)
        .header(AvenirsSecurityHeaders.CONTEXT_SIGNATURE, studentSignature)
        .exchange()
        .expectStatus()
        .isEqualTo(409);

    BddLogger.then("it should return 409");
  }

  @Test
  void shouldReturn404WhenAssociatingADeclaredProgramWithAnUnknownDeclaredSkill() throws Exception {
    BddLogger.given("an existing declared program and an unknown declared skill");
    UUID declaredProgramId = createDeclaredProgram("Formation continue", "CNAM");

    when("associating the declared program with the unknown declared skill");

    webTestClient
        .post()
        .uri(ASSOCIATE_PATH, "DECLARED_PROGRAM", declaredProgramId, "DECLARED_SKILL")
        .contentType(APPLICATION_JSON)
        .bodyValue(
            objectMapper.writeValueAsString(
                new AssociationsCreationRequest(List.of(UUID.randomUUID()))))
        .header(AvenirsSecurityHeaders.SIGNED_CONTEXT, studentPayload)
        .header(AvenirsSecurityHeaders.CONTEXT_SIGNATURE, studentSignature)
        .exchange()
        .expectStatus()
        .isNotFound();

    BddLogger.then("it should return 404");
  }

  @Test
  void shouldUnassociateTheDeclaredSkillsOfADeclaredProgram() throws Exception {
    BddLogger.given("a declared program associated with two declared skills");
    UUID declaredProgramId = createDeclaredProgram("Certificat qualité", "AFNOR");
    UUID firstDeclaredSkillId = createAvailableDeclaredSkillProgress().progressId();
    UUID secondDeclaredSkillId = createAvailableDeclaredSkillProgress().progressId();

    JsonNode associations =
        associate(
            declaredProgramId,
            "DECLARED_PROGRAM",
            "DECLARED_SKILL",
            List.of(firstDeclaredSkillId, secondDeclaredSkillId));

    List<UUID> associationIds = new ArrayList<>();
    associations
        .get("declaredSkillAssociations")
        .forEach(
            association ->
                associationIds.add(UUID.fromString(association.get("associationId").asText())));

    when("unassociating both associations");

    webTestClient
        .method(HttpMethod.DELETE)
        .uri(ASSOCIATIONS_PATH, "DECLARED_PROGRAM", declaredProgramId)
        .contentType(APPLICATION_JSON)
        .bodyValue(objectMapper.writeValueAsString(new AssociationsDeleteRequest(associationIds)))
        .header(AvenirsSecurityHeaders.SIGNED_CONTEXT, studentPayload)
        .header(AvenirsSecurityHeaders.CONTEXT_SIGNATURE, studentSignature)
        .exchange()
        .expectStatus()
        .isNoContent();

    BddLogger.then("the declared program should not have any declared skill left");
    assertThat(
            getAssociations("DECLARED_PROGRAM", declaredProgramId).get("declaredSkillAssociations"))
        .isEmpty();
  }

  @Test
  void shouldSearchTheDeclaredSkillsToAssociateWithADeclaredProgram() throws Exception {
    BddLogger.given("a declared program already associated with a declared skill");
    UUID declaredProgramId = createDeclaredProgram("BUT informatique", "IUT");
    UUID declaredSkillId = createAvailableDeclaredSkillProgress().progressId();

    associate(declaredProgramId, "DECLARED_PROGRAM", "DECLARED_SKILL", List.of(declaredSkillId));

    when("searching the declared skills to associate");

    var results = searchForAssociation("DECLARED_PROGRAM", declaredProgramId, "DECLARED_SKILL", "");

    BddLogger.then("the already associated declared skill should be disabled");
    var alreadyAssociated =
        StreamSupport.stream(results.spliterator(), false)
            .filter(result -> result.get("id").asText().equals(declaredSkillId.toString()))
            .findFirst()
            .orElseThrow();

    assertThat(alreadyAssociated.get("disabled").asBoolean()).isTrue();
  }

  @Test
  void shouldSearchTheDeclaredProgramsToAssociateWithADeclaredSkill() throws Exception {
    BddLogger.given("a declared skill and a declared program matching the keyword");
    UUID declaredProgramId = createDeclaredProgram("Alternance cybersécurité", "Orange");
    UUID declaredSkillId = createAvailableDeclaredSkillProgress().progressId();

    when("searching the declared programs to associate");

    var results =
        searchForAssociation(
            "DECLARED_SKILL", declaredSkillId, "DECLARED_PROGRAM", "cybersécurité");

    BddLogger.then("it should return the matching declared program with its organization");
    var found =
        StreamSupport.stream(results.spliterator(), false)
            .filter(result -> result.get("id").asText().equals(declaredProgramId.toString()))
            .findFirst()
            .orElseThrow();

    assertThat(found.get("title").asText()).isEqualTo("Alternance cybersécurité");
    assertThat(found.get("category").asText()).isEqualTo("Orange");
    assertThat(found.get("disabled").asBoolean()).isFalse();
  }

  @Test
  void shouldDisableTheDeclaredProgramsAlreadyAssociatedWithADeclaredSkill() throws Exception {
    BddLogger.given("a declared skill already associated with a declared program");
    UUID declaredProgramId = createDeclaredProgram("Doctorat physique", "Sorbonne");
    UUID declaredSkillId = createAvailableDeclaredSkillProgress().progressId();

    associate(declaredProgramId, "DECLARED_PROGRAM", "DECLARED_SKILL", List.of(declaredSkillId));

    when("searching the declared programs to associate");

    var results =
        searchForAssociation("DECLARED_SKILL", declaredSkillId, "DECLARED_PROGRAM", "Doctorat");

    BddLogger.then("the already associated declared program should be disabled");
    var found =
        StreamSupport.stream(results.spliterator(), false)
            .filter(result -> result.get("id").asText().equals(declaredProgramId.toString()))
            .findFirst()
            .orElseThrow();

    assertThat(found.get("disabled").asBoolean()).isTrue();
  }

  @Test
  void shouldDeleteTheAssociationsOfADeletedDeclaredProgram() throws Exception {
    BddLogger.given("a declared program associated with a declared skill");
    UUID declaredProgramId = createDeclaredProgram("Prépa scientifique", "Lycée Kléber");
    UUID declaredSkillId = createAvailableDeclaredSkillProgress().progressId();

    associate(declaredProgramId, "DECLARED_PROGRAM", "DECLARED_SKILL", List.of(declaredSkillId));

    when("deleting the declared program");

    webTestClient
        .method(HttpMethod.DELETE)
        .uri(DECLARED_PROGRAM_BASE_PATH)
        .contentType(APPLICATION_JSON)
        .bodyValue(objectMapper.writeValueAsString(List.of(declaredProgramId)))
        .header(AvenirsSecurityHeaders.SIGNED_CONTEXT, studentPayload)
        .header(AvenirsSecurityHeaders.CONTEXT_SIGNATURE, studentSignature)
        .exchange()
        .expectStatus()
        .isOk();

    BddLogger.then("the declared skill should not reference the declared program anymore");
    assertThat(
            getAssociations("DECLARED_SKILL", declaredSkillId).get("declaredProgramAssociations"))
        .isEmpty();
  }
}
