package fr.avenirsesr.portfolio.trace.application.adapter.controller;

import static org.springframework.http.MediaType.APPLICATION_JSON;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.avenirsesr.portfolio.common.configuration.domain.model.TraceConfiguration;
import fr.avenirsesr.portfolio.common.language.domain.model.enums.ELanguage;
import fr.avenirsesr.portfolio.common.security.infrastructure.adapter.model.AvenirsSecurityHeaders;
import fr.avenirsesr.portfolio.shared.application.adapter.dto.AssociationsCreationRequest;
import fr.avenirsesr.portfolio.shared.infrastructure.ContainerConfigurationTest;
import fr.avenirsesr.portfolio.shared.infrastructure.adapter.seeder.SeederRunner;
import fr.avenirsesr.portfolio.trace.application.adapter.dto.CreateTraceDTO;
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
  private static final String BASE_PATH_WITH_ID = BASE_PATH + "/{traceId}";
  private static final String OVERVIEW_BASE_PATH = BASE_PATH + "/overview";
  private static final String VIEW_BASE_PATH = BASE_PATH + "/view";
  private static final String SUMMARY_BASE_PATH = BASE_PATH + "/summary";
  private static final String DETAIL_BASE_PATH = BASE_PATH + "/{traceId}/detail";

  private static final String SEARCH_ASSOCIATION_DECLARED_SKILL_BASE_PATH =
      BASE_PATH + "/{traceId}/search-for-association/declared-skills";
  private static final String SEARCH_ASSOCIATION_DECLARED_ACTIVITY_BASE_PATH =
      BASE_PATH + "/{traceId}/search-for-association/declared-activities";

  @Autowired private WebTestClient webTestClient;
  @Autowired private ObjectMapper objectMapper;

  @Value("${hmac.secret-key}")
  private String secretKey;

  @Value("${user.student.payload}")
  private String studentPayload;

  @Value("${user.unknown.payload}")
  private String unknownUserPayload;

  @Value("${user.student.signature}")
  private String studentSignature;

  @Value("${user.unknown.signature}")
  private String unknownUserSignature;

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
            .header(AvenirsSecurityHeaders.CONTEXT_KID, secretKey)
            .header(AvenirsSecurityHeaders.CONTEXT_SIGNATURE, studentSignature)
            .accept(APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody(String.class)
            .returnResult()
            .getResponseBody();

    JsonNode json = objectMapper.readTree(body);

    if (!json.isArray() || json.size() == 0) {
      throw new IllegalStateException("Seeder returned no traces in /overview");
    }

    return UUID.fromString(json.get(0).get("traceId").asText());
  }

  private UUID searchFirstAssociationDeclaredSkillId(int i) throws Exception {
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
            .header(AvenirsSecurityHeaders.CONTEXT_KID, secretKey)
            .header(AvenirsSecurityHeaders.CONTEXT_SIGNATURE, studentSignature)
            .accept(APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody(String.class)
            .returnResult()
            .getResponseBody();

    JsonNode json = objectMapper.readTree(body);
    JsonNode data = json.get("data");

    if (data == null || !data.isArray() || data.size() == 0) {
      throw new IllegalStateException("No declared skill association data");
    }

    return UUID.fromString(data.get(i).get("id").asText());
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
            .header(AvenirsSecurityHeaders.CONTEXT_KID, secretKey)
            .header(AvenirsSecurityHeaders.CONTEXT_SIGNATURE, studentSignature)
            .accept(APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody(String.class)
            .returnResult()
            .getResponseBody();

    JsonNode json = objectMapper.readTree(body);
    JsonNode data = json.get("data");

    if (data == null || !data.isArray() || data.size() == 0) {
      throw new IllegalStateException("No declared activity association data");
    }

    return UUID.fromString(data.get(0).get("id").asText());
  }

  @Test
  void shouldReturnTraceOverview() {
    webTestClient
        .get()
        .uri(OVERVIEW_BASE_PATH)
        .header(AvenirsSecurityHeaders.SIGNED_CONTEXT, studentPayload)
        .header(AvenirsSecurityHeaders.CONTEXT_KID, secretKey)
        .header(AvenirsSecurityHeaders.CONTEXT_SIGNATURE, studentSignature)
        .accept(APPLICATION_JSON)
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody()
        .jsonPath("$[0].traceId")
        .exists();
  }

  @Test
  void shouldCreateNewTrace() throws Exception {
    CreateTraceDTO dto =
        new CreateTraceDTO("Nouvelle trace", ELanguage.FRENCH, false, "Note", "Justification");

    webTestClient
        .post()
        .uri(BASE_PATH)
        .contentType(APPLICATION_JSON)
        .bodyValue(objectMapper.writeValueAsString(dto))
        .header(AvenirsSecurityHeaders.SIGNED_CONTEXT, studentPayload)
        .header(AvenirsSecurityHeaders.CONTEXT_KID, secretKey)
        .header(AvenirsSecurityHeaders.CONTEXT_SIGNATURE, studentSignature)
        .exchange()
        .expectStatus()
        .isCreated()
        .expectBody()
        .jsonPath("$.traceId")
        .exists();
  }

  @Test
  void shouldDeleteTrace() {
    UUID existingTraceId = UUID.fromString("efb1f0ce-e531-49af-8031-949f3d68b354");

    webTestClient
        .delete()
        .uri(BASE_PATH_WITH_ID, existingTraceId)
        .header(AvenirsSecurityHeaders.SIGNED_CONTEXT, studentPayload)
        .header(AvenirsSecurityHeaders.CONTEXT_KID, secretKey)
        .header(AvenirsSecurityHeaders.CONTEXT_SIGNATURE, studentSignature)
        .exchange()
        .expectStatus()
        .isOk();
  }

  @Test
  void shouldReturn404IfTraceNotFoundWhenDeleting() {
    UUID traceId = UUID.randomUUID();

    webTestClient
        .delete()
        .uri(BASE_PATH_WITH_ID, traceId)
        .header(AvenirsSecurityHeaders.SIGNED_CONTEXT, studentPayload)
        .header(AvenirsSecurityHeaders.CONTEXT_KID, secretKey)
        .header(AvenirsSecurityHeaders.CONTEXT_SIGNATURE, studentSignature)
        .exchange()
        .expectStatus()
        .isNotFound();
  }

  @Test
  void shouldAssociateTraceWithDeclaredSkill() throws Exception {
    UUID traceId = getFirstTraceIdFromOverview();
    UUID skillId = searchFirstAssociationDeclaredSkillId(0);

    AssociationsCreationRequest body = new AssociationsCreationRequest(List.of(skillId));

    webTestClient
        .post()
        .uri(BASE_PATH + "/" + traceId + "/associate/declared-skill")
        .contentType(APPLICATION_JSON)
        .bodyValue(objectMapper.writeValueAsString(body))
        .header(AvenirsSecurityHeaders.SIGNED_CONTEXT, studentPayload)
        .header(AvenirsSecurityHeaders.CONTEXT_KID, secretKey)
        .header(AvenirsSecurityHeaders.CONTEXT_SIGNATURE, studentSignature)
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody()
        .jsonPath("$.declaredSkillAssociations")
        .exists();
  }

  @Test
  void shouldUnassociateTraceAssociationsSuccessfully() throws Exception {
    UUID traceId = getFirstTraceIdFromOverview();
    UUID skillId = searchFirstAssociationDeclaredSkillId(1);

    AssociationsCreationRequest associateBody = new AssociationsCreationRequest(List.of(skillId));
    var associateResult =
        webTestClient
            .post()
            .uri(BASE_PATH + "/" + traceId + "/associate/declared-skill")
            .contentType(APPLICATION_JSON)
            .bodyValue(objectMapper.writeValueAsString(associateBody))
            .header(AvenirsSecurityHeaders.SIGNED_CONTEXT, studentPayload)
            .header(AvenirsSecurityHeaders.CONTEXT_KID, secretKey)
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

    webTestClient
        .method(HttpMethod.DELETE)
        .uri(BASE_PATH + "/" + traceId + "/associations")
        .contentType(APPLICATION_JSON)
        .bodyValue(objectMapper.writeValueAsString(List.of(associationId)))
        .header(AvenirsSecurityHeaders.SIGNED_CONTEXT, studentPayload)
        .header(AvenirsSecurityHeaders.CONTEXT_KID, secretKey)
        .header(AvenirsSecurityHeaders.CONTEXT_SIGNATURE, studentSignature)
        .exchange()
        .expectStatus()
        .isOk();
  }
}
