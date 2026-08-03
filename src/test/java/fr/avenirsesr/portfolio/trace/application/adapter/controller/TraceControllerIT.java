package fr.avenirsesr.portfolio.trace.application.adapter.controller;

import static fr.avenirsesr.portfolio.common.testutils.BddLogger.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.avenirsesr.portfolio.common.configuration.domain.model.TraceConfiguration;
import fr.avenirsesr.portfolio.common.language.domain.model.enums.ELanguage;
import fr.avenirsesr.portfolio.common.security.infrastructure.adapter.model.AvenirsSecurityHeaders;
import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import fr.avenirsesr.portfolio.shared.application.adapter.dto.AssociationsCreationRequest;
import fr.avenirsesr.portfolio.shared.infrastructure.ContainerConfigurationTest;
import fr.avenirsesr.portfolio.shared.infrastructure.adapter.seeder.SeederRunner;
import fr.avenirsesr.portfolio.trace.application.adapter.dto.CreateTraceDTO;
import fr.avenirsesr.portfolio.trace.application.adapter.dto.UpdateTraceDTO;
import fr.avenirsesr.portfolio.trace.domain.filter.TraceFilter;
import fr.avenirsesr.portfolio.trace.domain.model.enums.ETraceAuthorType;
import fr.avenirsesr.portfolio.trace.infrastructure.adapter.client.TraceConfigurationClient;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpMethod;
import org.springframework.test.web.reactive.server.WebTestClient;

class TraceControllerIT extends ContainerConfigurationTest {

  private static final String BASE_PATH = "/me/traces";
  private static final String OVERVIEW_BASE_PATH = BASE_PATH + "/overview";
  private static final String VIEW_BASE_PATH = BASE_PATH + "/view";
  private static final String DETAIL_BASE_PATH = BASE_PATH + "/{traceId}/detail";
  private static final String LOCKED_DECLARED_ACTIVITIES_BASE_PATH =
      BASE_PATH + "/locked-declared-activities";

  private static final String SEARCH_ASSOCIATION_DECLARED_SKILL_BASE_PATH =
      BASE_PATH + "/{traceId}/search-for-association/declared-skills";
  private static final String SEARCH_ASSOCIATION_DECLARED_ACTIVITY_BASE_PATH =
      BASE_PATH + "/{traceId}/search-for-association/declared-activities";
  private static final String SEARCH_ASSOCIATION_DECLARED_EXPERIENCE_BASE_PATH =
      BASE_PATH + "/{traceId}/search-for-association/declared-experiences";

  private static final String DECLARED_EXPERIENCE_VIEW_URL = "/me/declared/experiences/view";

  @Autowired private WebTestClient webTestClient;
  @Autowired private ObjectMapper objectMapper;

  @Value("${user.student.payload}")
  private String studentPayload;

  @Value("${user.student.signature}")
  private String studentSignature;

  @BeforeAll
  void setup(@Autowired SeederRunner seederRunner) {
    seederRunner.run();
  }

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

  private UUID getFirstTraceIdFromOverview() throws Exception {
    String body =
        webTestClient
            .get()
            .uri(OVERVIEW_BASE_PATH)
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
      throw new IllegalStateException("Seeder returned no traces in /overview");
    }

    return UUID.fromString(json.get(0).get("id").asText());
  }

  private UUID searchFirstAssociationDeclaredSkillId(int index) throws Exception {
    UUID traceId = getFirstTraceIdFromOverview();

    String body =
        webTestClient
            .get()
            .uri(
                uriBuilder ->
                    uriBuilder
                        .path(SEARCH_ASSOCIATION_DECLARED_SKILL_BASE_PATH)
                        .queryParam("page", "0")
                        .queryParam("pageSize", "8")
                        .build(traceId))
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
      throw new IllegalStateException("No declared skill association data");
    }

    return UUID.fromString(data.get(index).get("id").asText());
  }

  private UUID searchFirstAssociationDeclaredActivityId() throws Exception {
    UUID traceId = getFirstTraceIdFromOverview();

    String body =
        webTestClient
            .get()
            .uri(
                uriBuilder ->
                    uriBuilder
                        .path(SEARCH_ASSOCIATION_DECLARED_ACTIVITY_BASE_PATH)
                        .queryParam("page", "0")
                        .queryParam("pageSize", "8")
                        .build(traceId))
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
      throw new IllegalStateException("No declared activity association data");
    }

    return UUID.fromString(data.get(0).get("id").asText());
  }

  private UUID getFirstDeclaredExperienceIdFromView() throws Exception {
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

  private UUID createTrace(String title) throws Exception {
    CreateTraceDTO dto =
        new CreateTraceDTO(title, ELanguage.FRENCH, ETraceAuthorType.PERSONAL, null, null, null);

    String body =
        webTestClient
            .post()
            .uri(BASE_PATH)
            .contentType(APPLICATION_JSON)
            .bodyValue(objectMapper.writeValueAsString(dto))
            .header(AvenirsSecurityHeaders.SIGNED_CONTEXT, studentPayload)
            .header(AvenirsSecurityHeaders.CONTEXT_SIGNATURE, studentSignature)
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(String.class)
            .returnResult()
            .getResponseBody();

    return UUID.fromString(objectMapper.readTree(body).get("traceId").asText());
  }

  private void updateTraceValorized(UUID traceId, String title, boolean valorized) {
    UpdateTraceDTO dto =
        new UpdateTraceDTO(
            title, ELanguage.FRENCH, ETraceAuthorType.PERSONAL, null, null, null, valorized);

    webTestClient
        .put()
        .uri(BASE_PATH + "/{traceId}", traceId)
        .contentType(APPLICATION_JSON)
        .bodyValue(dto)
        .header(AvenirsSecurityHeaders.SIGNED_CONTEXT, studentPayload)
        .header(AvenirsSecurityHeaders.CONTEXT_SIGNATURE, studentSignature)
        .exchange()
        .expectStatus()
        .isOk();
  }

  @Test
  void shouldReturnTraceOverview() throws Exception {
    BddLogger.given("a newly created trace with an AI use justification");

    CreateTraceDTO dto =
        new CreateTraceDTO(
            "Trace overview",
            ELanguage.FRENCH,
            ETraceAuthorType.PERSONAL,
            "Note",
            "Justification IA",
            null);

    webTestClient
        .post()
        .uri(BASE_PATH)
        .contentType(APPLICATION_JSON)
        .bodyValue(objectMapper.writeValueAsString(dto))
        .header(AvenirsSecurityHeaders.SIGNED_CONTEXT, studentPayload)
        .header(AvenirsSecurityHeaders.CONTEXT_SIGNATURE, studentSignature)
        .exchange()
        .expectStatus()
        .isCreated();

    when("getting trace overview");

    webTestClient
        .get()
        .uri(OVERVIEW_BASE_PATH)
        .header(AvenirsSecurityHeaders.SIGNED_CONTEXT, studentPayload)
        .header(AvenirsSecurityHeaders.CONTEXT_SIGNATURE, studentSignature)
        .accept(APPLICATION_JSON)
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody()
        .jsonPath("$[0].id")
        .exists()
        .jsonPath("$[0].title")
        .isEqualTo("Trace overview")
        .jsonPath("$[0].programName")
        .exists()
        .jsonPath("$[0].aiUseJustification")
        .isEqualTo("Justification IA");

    BddLogger.then("it should return the newly created trace first, with its AI use justification");
  }

  @Test
  void shouldReturnTraceDetailWithAuthorType() throws Exception {
    BddLogger.given("an existing trace");
    UUID traceId = getFirstTraceIdFromOverview();

    when("getting trace detail");

    webTestClient
        .get()
        .uri(DETAIL_BASE_PATH, traceId)
        .header(AvenirsSecurityHeaders.SIGNED_CONTEXT, studentPayload)
        .header(AvenirsSecurityHeaders.CONTEXT_SIGNATURE, studentSignature)
        .accept(APPLICATION_JSON)
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody()
        .jsonPath("$.id")
        .isEqualTo(traceId.toString())
        .jsonPath("$.title")
        .exists()
        .jsonPath("$.isAssociated")
        .exists()
        .jsonPath("$.programName")
        .exists()
        .jsonPath("$.authorType")
        .exists()
        .jsonPath("$.createdAt")
        .exists()
        .jsonPath("$.updatedAt")
        .exists();

    BddLogger.then("it should return trace detail with authorType");
  }

  @Test
  void shouldCreateNewTrace() throws Exception {
    BddLogger.given("a valid create trace request");

    CreateTraceDTO dto =
        new CreateTraceDTO(
            "Nouvelle trace",
            ELanguage.FRENCH,
            ETraceAuthorType.PERSONAL,
            "Note",
            "Justification",
            null);

    when("creating a trace");

    webTestClient
        .post()
        .uri(BASE_PATH)
        .contentType(APPLICATION_JSON)
        .bodyValue(objectMapper.writeValueAsString(dto))
        .header(AvenirsSecurityHeaders.SIGNED_CONTEXT, studentPayload)
        .header(AvenirsSecurityHeaders.CONTEXT_SIGNATURE, studentSignature)
        .exchange()
        .expectStatus()
        .isCreated()
        .expectBody()
        .jsonPath("$.traceId")
        .exists();

    BddLogger.then("it should create a new trace");
  }

  @Test
  void shouldFilterTracesViewByIsValorized() throws Exception {
    BddLogger.given("a valorized trace and a non-valorized trace");

    UUID valorizedTraceId = createTrace("Trace valorisee");
    updateTraceValorized(valorizedTraceId, "Trace valorisee", true);

    UUID notValorizedTraceId = createTrace("Trace non valorisee");

    when("requesting traces view filtered by isValorized=true");

    TraceFilter valorizedFilter = new TraceFilter(null, null, null, null, true);

    webTestClient
        .post()
        .uri(uriBuilder -> uriBuilder.path(VIEW_BASE_PATH).queryParam("pageSize", 100).build())
        .contentType(APPLICATION_JSON)
        .bodyValue(objectMapper.writeValueAsString(valorizedFilter))
        .header(AvenirsSecurityHeaders.SIGNED_CONTEXT, studentPayload)
        .header(AvenirsSecurityHeaders.CONTEXT_SIGNATURE, studentSignature)
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody()
        .jsonPath("$.data[?(@.id == '" + valorizedTraceId + "')]")
        .exists()
        .jsonPath("$.data[?(@.id == '" + notValorizedTraceId + "')]")
        .doesNotExist();

    BddLogger.then("it should only return the valorized trace");

    when("requesting traces view filtered by isValorized=false");

    TraceFilter notValorizedFilter = new TraceFilter(null, null, null, null, false);

    webTestClient
        .post()
        .uri(uriBuilder -> uriBuilder.path(VIEW_BASE_PATH).queryParam("pageSize", 100).build())
        .contentType(APPLICATION_JSON)
        .bodyValue(objectMapper.writeValueAsString(notValorizedFilter))
        .header(AvenirsSecurityHeaders.SIGNED_CONTEXT, studentPayload)
        .header(AvenirsSecurityHeaders.CONTEXT_SIGNATURE, studentSignature)
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody()
        .jsonPath("$.data[?(@.id == '" + notValorizedTraceId + "')]")
        .exists()
        .jsonPath("$.data[?(@.id == '" + valorizedTraceId + "')]")
        .doesNotExist();

    BddLogger.then("it should only return the non-valorized trace");
  }

  //  TODO: Refacto in #1887 beacause the JSON request is incompatible with H2
  //  @Test
  //  void shouldDeleteTraceAndItsAssociationsAndAttachment() throws Exception {
  //    BddLogger.given("an existing deletable trace with associations and removable attachment");
  //
  //    UUID existingTraceId = UUID.fromString("efb1f0ce-e531-49af-8031-949f3d68b354");
  //
  //    var traceBeforeDelete = traceRepository.findById(existingTraceId).orElseThrow();
  //    var attachmentId =
  // traceBeforeDelete.getAttachment().map(AvenirsBaseModel::getId).orElse(null);
  //
  //    when("deleting trace");
  //
  //    webTestClient
  //            .method(HttpMethod.DELETE)
  //            .uri(BASE_PATH)
  //            .contentType(APPLICATION_JSON)
  //            .bodyValue(objectMapper.writeValueAsString(List.of(existingTraceId)))
  //            .header(AvenirsSecurityHeaders.SIGNED_CONTEXT, studentPayload)
  //            .header(AvenirsSecurityHeaders.CONTEXT_SIGNATURE, studentSignature)
  //            .exchange()
  //            .expectStatus()
  //            .isOk();
  //
  //    BddLogger.then("it should delete the trace from database");
  //
  //    assertThat(traceRepository.findById(existingTraceId)).isEmpty();
  //
  //    BddLogger.then("it should delete associations linked to the trace");
  //
  //    assertThat(
  //            associationRepository.findAllOf(
  //                    existingTraceId,
  //                    Trace.class,
  //                    EAssociationType.getAllBy(Trace.class)))
  //            .isEmpty();
  //
  //    if (attachmentId != null) {
  //      BddLogger.then("it should delete the removable attachment");
  //      assertThat(fileRepository.findById(attachmentId)).isEmpty();
  //    }
  //  }
  //
  //  @Test
  //  void shouldDeleteTraceButKeepAttachmentWhenUsedByFeedbackSnapshot() throws Exception {
  //    BddLogger.given("an existing trace whose attachment is used by a feedback snapshot");
  //
  //    UUID existingTraceId = UUID.fromString("ID_TRACE_AVEC_ATTACHMENT_PROTEGE");
  //    UUID attachmentId =
  //
  // traceRepository.findById(existingTraceId).orElseThrow().getAttachment().orElseThrow().getId();
  //
  //    when("deleting trace");
  //
  //    webTestClient
  //            .method(HttpMethod.DELETE)
  //            .uri(BASE_PATH)
  //            .contentType(APPLICATION_JSON)
  //            .bodyValue(objectMapper.writeValueAsString(List.of(existingTraceId)))
  //            .header(AvenirsSecurityHeaders.SIGNED_CONTEXT, studentPayload)
  //            .header(AvenirsSecurityHeaders.CONTEXT_SIGNATURE, studentSignature)
  //            .exchange()
  //            .expectStatus()
  //            .isOk();
  //
  //    BddLogger.then("it should delete the trace but keep the protected attachment");
  //
  //    assertThat(traceRepository.findById(existingTraceId)).isEmpty();
  //    assertThat(fileRepository.findById(attachmentId)).isPresent();
  //  }

  @Test
  void shouldReturn404IfTraceNotFoundWhenDeleting() throws Exception {
    BddLogger.given("an unknown trace id");
    UUID traceId = UUID.randomUUID();

    when("deleting trace");

    webTestClient
        .method(HttpMethod.DELETE)
        .uri(BASE_PATH)
        .contentType(APPLICATION_JSON)
        .bodyValue(objectMapper.writeValueAsString(List.of(traceId)))
        .header(AvenirsSecurityHeaders.SIGNED_CONTEXT, studentPayload)
        .header(AvenirsSecurityHeaders.CONTEXT_SIGNATURE, studentSignature)
        .exchange()
        .expectStatus()
        .isNotFound();

    BddLogger.then("it should return 404");
  }

  @Test
  void shouldAssociateTraceWithDeclaredSkill() throws Exception {
    BddLogger.given("an existing trace and declared skill");
    UUID traceId = getFirstTraceIdFromOverview();
    UUID skillId = searchFirstAssociationDeclaredSkillId(0);

    AssociationsCreationRequest body = new AssociationsCreationRequest(List.of(skillId));

    when("associating trace with declared skill");

    webTestClient
        .post()
        .uri(BASE_PATH + "/" + traceId + "/associate/declared-skill")
        .contentType(APPLICATION_JSON)
        .bodyValue(objectMapper.writeValueAsString(body))
        .header(AvenirsSecurityHeaders.SIGNED_CONTEXT, studentPayload)
        .header(AvenirsSecurityHeaders.CONTEXT_SIGNATURE, studentSignature)
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody()
        .jsonPath("$.declaredSkillAssociations")
        .exists();

    BddLogger.then("it should associate trace with declared skill");
  }

  @Test
  void shouldUnassociateTraceAssociationsSuccessfully() throws Exception {
    BddLogger.given("an existing trace associated with a declared skill");
    UUID traceId = getFirstTraceIdFromOverview();
    UUID skillId = searchFirstAssociationDeclaredSkillId(1);

    AssociationsCreationRequest associateBody = new AssociationsCreationRequest(List.of(skillId));

    String associateResult =
        webTestClient
            .post()
            .uri(BASE_PATH + "/" + traceId + "/associate/declared-skill")
            .contentType(APPLICATION_JSON)
            .bodyValue(objectMapper.writeValueAsString(associateBody))
            .header(AvenirsSecurityHeaders.SIGNED_CONTEXT, studentPayload)
            .header(AvenirsSecurityHeaders.CONTEXT_SIGNATURE, studentSignature)
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody(String.class)
            .returnResult()
            .getResponseBody();

    JsonNode json = objectMapper.readTree(associateResult);
    UUID associationId =
        UUID.fromString(json.get("declaredSkillAssociations").get(0).get("associationId").asText());

    when("unassociating trace association");

    webTestClient
        .method(HttpMethod.DELETE)
        .uri(BASE_PATH + "/" + traceId + "/associations")
        .contentType(APPLICATION_JSON)
        .bodyValue(objectMapper.writeValueAsString(List.of(associationId)))
        .header(AvenirsSecurityHeaders.SIGNED_CONTEXT, studentPayload)
        .header(AvenirsSecurityHeaders.CONTEXT_SIGNATURE, studentSignature)
        .exchange()
        .expectStatus()
        .isOk();

    BddLogger.then("it should unassociate trace association");
  }

  @Test
  void shouldSearchDeclaredActivitiesForAssociation() throws Exception {
    BddLogger.given("an existing trace");
    UUID traceId = getFirstTraceIdFromOverview();

    when("searching declared activities for association");

    webTestClient
        .get()
        .uri(
            uriBuilder ->
                uriBuilder
                    .path(SEARCH_ASSOCIATION_DECLARED_ACTIVITY_BASE_PATH)
                    .queryParam("page", "0")
                    .queryParam("pageSize", "8")
                    .build(traceId))
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
        .jsonPath("$.data[0].thematic")
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
  void shouldSearchDeclaredActivitiesForAssociationWithKeyword() throws Exception {
    BddLogger.given("an existing trace and a keyword");
    UUID traceId = getFirstTraceIdFromOverview();

    when("searching with a keyword that matches nothing");

    webTestClient
        .get()
        .uri(
            uriBuilder ->
                uriBuilder
                    .path(SEARCH_ASSOCIATION_DECLARED_ACTIVITY_BASE_PATH)
                    .queryParam("keyword", "zzzzzznonexistent")
                    .queryParam("page", "0")
                    .queryParam("pageSize", "8")
                    .build(traceId))
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
  void shouldReturn404WhenSearchingDeclaredActivitiesForNonExistentTrace() {
    BddLogger.given("a non-existent trace ID");
    UUID nonExistentTraceId = UUID.randomUUID();

    when("searching declared activities for association");

    webTestClient
        .get()
        .uri(
            uriBuilder ->
                uriBuilder
                    .path(SEARCH_ASSOCIATION_DECLARED_ACTIVITY_BASE_PATH)
                    .queryParam("page", "0")
                    .queryParam("pageSize", "8")
                    .build(nonExistentTraceId))
        .header(AvenirsSecurityHeaders.SIGNED_CONTEXT, studentPayload)
        .header(AvenirsSecurityHeaders.CONTEXT_SIGNATURE, studentSignature)
        .accept(APPLICATION_JSON)
        .exchange()
        .expectStatus()
        .isNotFound();

    BddLogger.then("it should return 404");
  }

  @Test
  void shouldSearchDeclaredSkillsForAssociation() throws Exception {
    BddLogger.given("an existing trace");
    UUID traceId = getFirstTraceIdFromOverview();

    when("searching declared skills for association");

    webTestClient
        .get()
        .uri(
            uriBuilder ->
                uriBuilder
                    .path(SEARCH_ASSOCIATION_DECLARED_SKILL_BASE_PATH)
                    .queryParam("page", "0")
                    .queryParam("pageSize", "8")
                    .build(traceId))
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
        .jsonPath("$.data[0].type")
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
  void shouldReturn404WhenSearchingDeclaredSkillsForNonExistentTrace() {
    BddLogger.given("a non-existent trace ID");
    UUID nonExistentTraceId = UUID.randomUUID();

    when("searching declared skills for association");

    webTestClient
        .get()
        .uri(
            uriBuilder ->
                uriBuilder
                    .path(SEARCH_ASSOCIATION_DECLARED_SKILL_BASE_PATH)
                    .queryParam("page", "0")
                    .queryParam("pageSize", "8")
                    .build(nonExistentTraceId))
        .header(AvenirsSecurityHeaders.SIGNED_CONTEXT, studentPayload)
        .header(AvenirsSecurityHeaders.CONTEXT_SIGNATURE, studentSignature)
        .accept(APPLICATION_JSON)
        .exchange()
        .expectStatus()
        .isNotFound();

    BddLogger.then("it should return 404");
  }

  @Test
  void shouldAssociateTraceWithDeclaredExperiences() throws Exception {
    BddLogger.given("an existing trace and declared experience");
    UUID traceId = getFirstTraceIdFromOverview();
    UUID experienceId = getFirstDeclaredExperienceIdFromView();

    AssociationsCreationRequest body = new AssociationsCreationRequest(List.of(experienceId));

    when("associating trace with declared experience");

    webTestClient
        .post()
        .uri(BASE_PATH + "/" + traceId + "/associate/declared-experiences")
        .contentType(APPLICATION_JSON)
        .bodyValue(objectMapper.writeValueAsString(body))
        .header(AvenirsSecurityHeaders.SIGNED_CONTEXT, studentPayload)
        .header(AvenirsSecurityHeaders.CONTEXT_SIGNATURE, studentSignature)
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody()
        .jsonPath("$.declaredExperienceAssociations")
        .exists();

    BddLogger.then("it should associate trace with declared experience");
  }

  @Test
  void shouldReturn404WhenAssociatingDeclaredExperiencesWithUnknownTrace() throws Exception {
    BddLogger.given("an unknown trace");
    UUID unknownTraceId = UUID.randomUUID();
    UUID experienceId = searchFirstAssociationDeclaredActivityId();

    AssociationsCreationRequest body = new AssociationsCreationRequest(List.of(experienceId));

    when("associating declared experience with unknown trace");

    webTestClient
        .post()
        .uri(BASE_PATH + "/" + unknownTraceId + "/associate/declared-experiences")
        .contentType(APPLICATION_JSON)
        .bodyValue(objectMapper.writeValueAsString(body))
        .header(AvenirsSecurityHeaders.SIGNED_CONTEXT, studentPayload)
        .header(AvenirsSecurityHeaders.CONTEXT_SIGNATURE, studentSignature)
        .exchange()
        .expectStatus()
        .isNotFound();

    BddLogger.then("it should return 404");
  }

  @Test
  void shouldReturn404WhenAssociatingDeclaredExperiencesWithUnknownDeclaredExperiences()
      throws Exception {
    BddLogger.given("an existing trace and unknown declared experience");
    UUID traceId = getFirstTraceIdFromOverview();

    AssociationsCreationRequest body = new AssociationsCreationRequest(List.of(UUID.randomUUID()));

    when("associating unknown declared experience");

    webTestClient
        .post()
        .uri(BASE_PATH + "/" + traceId + "/associate/declared-experiences")
        .contentType(APPLICATION_JSON)
        .bodyValue(objectMapper.writeValueAsString(body))
        .header(AvenirsSecurityHeaders.SIGNED_CONTEXT, studentPayload)
        .header(AvenirsSecurityHeaders.CONTEXT_SIGNATURE, studentSignature)
        .exchange()
        .expectStatus()
        .isNotFound();

    BddLogger.then("it should return 404");
  }

  @Test
  void shouldSearchDeclaredExperiencesForAssociation() throws Exception {
    BddLogger.given("an existing trace");
    UUID traceId = getFirstTraceIdFromOverview();

    when("searching declared experiences for association");

    webTestClient
        .get()
        .uri(
            uriBuilder ->
                uriBuilder
                    .path(SEARCH_ASSOCIATION_DECLARED_EXPERIENCE_BASE_PATH)
                    .queryParam("page", "0")
                    .queryParam("pageSize", "8")
                    .build(traceId))
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
        .jsonPath("$.data[0].experienceType")
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
  void shouldSearchDeclaredExperiencesForAssociationWithKeyword() throws Exception {
    BddLogger.given("an existing trace and a keyword");
    UUID traceId = getFirstTraceIdFromOverview();

    when("searching with a keyword that matches nothing");

    webTestClient
        .get()
        .uri(
            uriBuilder ->
                uriBuilder
                    .path(SEARCH_ASSOCIATION_DECLARED_EXPERIENCE_BASE_PATH)
                    .queryParam("keyword", "zzzzzznonexistent")
                    .queryParam("page", "0")
                    .queryParam("pageSize", "8")
                    .build(traceId))
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
  void shouldReturn404WhenSearchingDeclaredExperiencesForNonExistentTrace() {
    BddLogger.given("a non-existent trace ID");
    UUID nonExistentTraceId = UUID.randomUUID();

    when("searching declared experiences for association");

    webTestClient
        .get()
        .uri(
            uriBuilder ->
                uriBuilder
                    .path(SEARCH_ASSOCIATION_DECLARED_EXPERIENCE_BASE_PATH)
                    .queryParam("page", "0")
                    .queryParam("pageSize", "8")
                    .build(nonExistentTraceId))
        .header(AvenirsSecurityHeaders.SIGNED_CONTEXT, studentPayload)
        .header(AvenirsSecurityHeaders.CONTEXT_SIGNATURE, studentSignature)
        .accept(APPLICATION_JSON)
        .exchange()
        .expectStatus()
        .isNotFound();

    BddLogger.then("it should return 404");
  }

  @Test
  void shouldReturnLockedDeclaredActivities() throws Exception {
    BddLogger.given("an existing trace");
    UUID traceId = getFirstTraceIdFromOverview();

    when("getting locked declared activities");

    webTestClient
        .method(HttpMethod.POST)
        .uri(LOCKED_DECLARED_ACTIVITIES_BASE_PATH)
        .contentType(APPLICATION_JSON)
        .bodyValue(objectMapper.writeValueAsString(List.of(traceId)))
        .header(AvenirsSecurityHeaders.SIGNED_CONTEXT, studentPayload)
        .header(AvenirsSecurityHeaders.CONTEXT_SIGNATURE, studentSignature)
        .accept(APPLICATION_JSON)
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody()
        .jsonPath("$")
        .isArray()
        .jsonPath("$[0].traceId")
        .isEqualTo(traceId.toString())
        .jsonPath("$[0].traceTitle")
        .exists()
        .jsonPath("$[0].lockedDeclaredActivities")
        .isArray();

    BddLogger.then("it should return locked declared activities grouped by trace");
  }

  @Test
  void shouldReturn404WhenGettingLockedDeclaredActivitiesForUnknownTrace() throws Exception {
    BddLogger.given("an unknown trace");
    UUID unknownTraceId = UUID.randomUUID();

    when("getting locked declared activities");

    webTestClient
        .method(HttpMethod.POST)
        .uri(LOCKED_DECLARED_ACTIVITIES_BASE_PATH)
        .contentType(APPLICATION_JSON)
        .bodyValue(objectMapper.writeValueAsString(List.of(unknownTraceId)))
        .header(AvenirsSecurityHeaders.SIGNED_CONTEXT, studentPayload)
        .header(AvenirsSecurityHeaders.CONTEXT_SIGNATURE, studentSignature)
        .accept(APPLICATION_JSON)
        .exchange()
        .expectStatus()
        .isNotFound();

    BddLogger.then("it should return 404");
  }
}
