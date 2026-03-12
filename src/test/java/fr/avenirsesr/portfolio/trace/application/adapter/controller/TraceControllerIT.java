package fr.avenirsesr.portfolio.trace.application.adapter.controller;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.avenirsesr.portfolio.common.configuration.domain.model.TraceConfiguration;
import fr.avenirsesr.portfolio.common.language.domain.model.enums.ELanguage;
import fr.avenirsesr.portfolio.common.security.infrastructure.adapter.model.AvenirsSecurityHeaders;
import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import fr.avenirsesr.portfolio.shared.application.adapter.dto.AssociationsCreationRequest;
import fr.avenirsesr.portfolio.shared.infrastructure.ContainerConfigurationTest;
import fr.avenirsesr.portfolio.shared.infrastructure.adapter.seeder.SeederRunner;
import fr.avenirsesr.portfolio.trace.application.adapter.dto.*;
import fr.avenirsesr.portfolio.trace.infrastructure.adapter.client.TraceConfigurationClient;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

class TraceControllerIT extends ContainerConfigurationTest {

  private static final String BASE_PATH = "/me/traces";
  private static final String BASE_PATH_WITH_ID = BASE_PATH + "/{traceId}";
  private static final String OVERVIEW_BASE_PATH = BASE_PATH + "/overview";
  private static final String VIEW_BASE_PATH = BASE_PATH + "/view";
  private static final String SUMMARY_BASE_PATH = BASE_PATH + "/summary";
  private static final String DETAIL_BASE_PATH = BASE_PATH + "/{traceId}/detail";
  private static final String SEARCH_ASSOCIATION_BASE_PATH =
      BASE_PATH + "/search-association/{type}";

  @Autowired private MockMvc mockMvc;
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
      TraceConfiguration mockConfig =
          new TraceConfiguration(
              30, // maxRemainingDays
              7, // maxRemainingDaysBeforeWarning
              3 // maxRemainingDaysBeforeCritical
              );
      when(mock.getTraceConfiguration()).thenReturn(mockConfig);
      return mock;
    }
  }

  private UUID getFirstTraceIdFromOverview() throws Exception {
    var mvc =
        mockMvc
            .perform(
                get(OVERVIEW_BASE_PATH)
                    .header(AvenirsSecurityHeaders.SIGNED_CONTEXT, studentPayload)
                    .header(AvenirsSecurityHeaders.CONTEXT_KID, secretKey)
                    .header(AvenirsSecurityHeaders.CONTEXT_SIGNATURE, studentSignature)
                    .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn();

    JsonNode json = objectMapper.readTree(mvc.getResponse().getContentAsString());
    if (!json.isArray() || json.size() == 0) {
      throw new IllegalStateException("Seeder returned no traces in /overview");
    }
    return UUID.fromString(json.get(0).get("traceId").asText());
  }

  private UUID searchFirstAssociationId(ETraceAssociationType type) throws Exception {
    var mvcResult =
        mockMvc
            .perform(
                get(SEARCH_ASSOCIATION_BASE_PATH, type.name())
                    .param("page", "0")
                    .param("pageSize", "8")
                    .header(AvenirsSecurityHeaders.SIGNED_CONTEXT, studentPayload)
                    .header(AvenirsSecurityHeaders.CONTEXT_KID, secretKey)
                    .header(AvenirsSecurityHeaders.CONTEXT_SIGNATURE, studentSignature)
                    .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.data", isA(List.class)))
            .andReturn();

    JsonNode json = objectMapper.readTree(mvcResult.getResponse().getContentAsString());
    JsonNode data = json.get("data");

    if (data == null || !data.isArray() || data.size() == 0) {
      throw new IllegalStateException("No association data returned for type " + type);
    }

    return UUID.fromString(data.get(0).get("id").asText());
  }

  @Test
  void shouldReturnTraceOverview() throws Exception {
    BddLogger.given("the " + OVERVIEW_BASE_PATH + " endpoint");
    BddLogger.when("performing a GET");
    BddLogger.then("it should return the trace overview");
    mockMvc
        .perform(
            get(OVERVIEW_BASE_PATH)
                .header(HttpHeaders.ACCEPT_LANGUAGE, ELanguage.FRENCH.getCode())
                .header(AvenirsSecurityHeaders.SIGNED_CONTEXT, studentPayload)
                .header(AvenirsSecurityHeaders.CONTEXT_KID, secretKey)
                .header(AvenirsSecurityHeaders.CONTEXT_SIGNATURE, studentSignature)
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$[0].traceId").exists())
        .andExpect(jsonPath("$[0].programName").exists())
        .andExpect(jsonPath("$[0].programName").value("LIFE_PROJECT"));
  }

  @Test
  void shouldReturnTraceViewUnassociated() throws Exception {
    BddLogger.given("the " + VIEW_BASE_PATH + " endpoint");
    BddLogger.when("performing a POST");
    BddLogger.then("it should return the trace view unassociated");
    mockMvc
        .perform(
            post(VIEW_BASE_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"isAssociated\": false}")
                .param("page", "0")
                .param("pageSize", "10")
                .header(AvenirsSecurityHeaders.SIGNED_CONTEXT, studentPayload)
                .header(AvenirsSecurityHeaders.CONTEXT_KID, secretKey)
                .header(AvenirsSecurityHeaders.CONTEXT_SIGNATURE, studentSignature))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.data").exists())
        .andExpect(jsonPath("$.data").isArray())
        .andExpect(jsonPath("$.page").exists());
  }

  @Test
  void shouldReturnTraceSummary() throws Exception {
    BddLogger.given("the " + SUMMARY_BASE_PATH + " endpoint");
    BddLogger.when("performing a GET");
    BddLogger.then("it should return the trace summary");
    mockMvc
        .perform(
            get(SUMMARY_BASE_PATH)
                .header(AvenirsSecurityHeaders.SIGNED_CONTEXT, studentPayload)
                .header(AvenirsSecurityHeaders.CONTEXT_KID, secretKey)
                .header(AvenirsSecurityHeaders.CONTEXT_SIGNATURE, studentSignature))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.associated").isNumber())
        .andExpect(jsonPath("$.unassociated").isNumber())
        .andExpect(jsonPath("$.totalWarnings").isNumber())
        .andExpect(jsonPath("$.totalCriticals").isNumber());
  }

  @Test
  void shouldCreateNewTrace() throws Exception {
    BddLogger.given("the " + BASE_PATH + " endpoint");
    CreateTraceDTO dto =
        new CreateTraceDTO(
            "Nouvelle trace", ELanguage.FRENCH, false, "Note personnelle", "Justification IA");

    BddLogger.when("performing a POST");
    BddLogger.then("it should create a new trace");
    mockMvc
        .perform(
            post(BASE_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto))
                .header(AvenirsSecurityHeaders.SIGNED_CONTEXT, studentPayload)
                .header(AvenirsSecurityHeaders.CONTEXT_KID, secretKey)
                .header(AvenirsSecurityHeaders.CONTEXT_SIGNATURE, studentSignature))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.traceId", notNullValue()));
  }

  @Test
  void shouldDeleteTrace() throws Exception {
    BddLogger.given("the " + BASE_PATH_WITH_ID + " endpoint");
    UUID existingTraceId = UUID.fromString("efb1f0ce-e531-49af-8031-949f3d68b354");

    BddLogger.when("performing a DELETE with trace id");
    BddLogger.then("it should delete the corresponding trace");
    mockMvc
        .perform(
            delete(BASE_PATH_WITH_ID, existingTraceId)
                .header(AvenirsSecurityHeaders.SIGNED_CONTEXT, studentPayload)
                .header(AvenirsSecurityHeaders.CONTEXT_KID, secretKey)
                .header(AvenirsSecurityHeaders.CONTEXT_SIGNATURE, studentSignature))
        .andExpect(status().isOk())
        .andExpect(content().string("Resource successfully deleted."));
  }

  @Test
  void shouldReturn404IfTraceNotFoundWhenDeleting() throws Exception {
    BddLogger.given("the " + BASE_PATH_WITH_ID + " endpoint");
    UUID traceIdNotOwned = UUID.randomUUID();

    BddLogger.when("performing a DELETE with a not found trace id");
    BddLogger.then("it should return a 404");
    mockMvc
        .perform(
            delete(BASE_PATH_WITH_ID, traceIdNotOwned)
                .header(AvenirsSecurityHeaders.SIGNED_CONTEXT, studentPayload)
                .header(AvenirsSecurityHeaders.CONTEXT_KID, secretKey)
                .header(AvenirsSecurityHeaders.CONTEXT_SIGNATURE, studentSignature))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.code", is("TRACE_NOT_FOUND")));
  }

  @Test
  void shouldReturn403IfUserNotOwnerWhenDeleting() throws Exception {
    BddLogger.given("the " + BASE_PATH_WITH_ID + " endpoint");
    UUID existingTraceId = UUID.fromString("4b02b225-998a-4996-be52-8d9b2a5ab327");

    BddLogger.when("performing a DELETE with trace id but the user is not its owner");
    BddLogger.then("it should return a 403");
    mockMvc
        .perform(
            delete(BASE_PATH_WITH_ID, existingTraceId)
                .header(AvenirsSecurityHeaders.SIGNED_CONTEXT, studentPayload)
                .header(AvenirsSecurityHeaders.CONTEXT_KID, secretKey)
                .header(AvenirsSecurityHeaders.CONTEXT_SIGNATURE, studentSignature))
        .andExpect(status().isForbidden())
        .andExpect(jsonPath("$.code", is("USER_NOT_AUTHORIZED")));
  }

  @Test
  void shouldReturn404WhenUnknownUserRequestsOverview() throws Exception {
    BddLogger.given("the " + OVERVIEW_BASE_PATH + " endpoint");
    BddLogger.when("performing a GET with an unknown user context");
    BddLogger.then("it should return 404 USER_NOT_FOUND");

    mockMvc
        .perform(
            get(OVERVIEW_BASE_PATH)
                .header(AvenirsSecurityHeaders.SIGNED_CONTEXT, unknownUserPayload)
                .header(AvenirsSecurityHeaders.CONTEXT_KID, secretKey)
                .header(AvenirsSecurityHeaders.CONTEXT_SIGNATURE, unknownUserSignature)
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.code", is("USER_NOT_FOUND")));
  }

  @Test
  void shouldReturnTraceViewWithKeywordAndDateFilter() throws Exception {
    BddLogger.given("the " + VIEW_BASE_PATH + " endpoint");
    BddLogger.when("performing a POST with keyword and date filters");
    BddLogger.then("it should return a paged trace view");

    mockMvc
        .perform(
            post(VIEW_BASE_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"isAssociated\": true}")
                .param("keyword", "trace")
                .param("fromDate", LocalDate.now().minusDays(30).toString())
                .param("toDate", LocalDate.now().toString())
                .param("page", "0")
                .param("pageSize", "8")
                .header(AvenirsSecurityHeaders.SIGNED_CONTEXT, studentPayload)
                .header(AvenirsSecurityHeaders.CONTEXT_KID, secretKey)
                .header(AvenirsSecurityHeaders.CONTEXT_SIGNATURE, studentSignature))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.data").isArray())
        .andExpect(jsonPath("$.page.page").value(0))
        .andExpect(jsonPath("$.page.pageSize").value(8))
        .andExpect(jsonPath("$.page.totalElements", greaterThanOrEqualTo(0)));
  }

  @Test
  void shouldReturn400WhenCreatingTraceWithBlankTitle() throws Exception {
    BddLogger.given("the " + BASE_PATH + " endpoint");
    BddLogger.when("performing a POST with a blank title");
    BddLogger.then("it should return 400");

    CreateTraceDTO dto = new CreateTraceDTO("   ", ELanguage.FRENCH, false, "Note", "Justif");

    mockMvc
        .perform(
            post(BASE_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto))
                .header(AvenirsSecurityHeaders.SIGNED_CONTEXT, studentPayload)
                .header(AvenirsSecurityHeaders.CONTEXT_KID, secretKey)
                .header(AvenirsSecurityHeaders.CONTEXT_SIGNATURE, studentSignature))
        .andExpect(status().isBadRequest());
  }

  @Test
  void shouldReturn400WhenCreatingTraceWithTooLongTitle() throws Exception {
    BddLogger.given("the " + BASE_PATH + " endpoint");
    BddLogger.when("performing a POST with a title too long");
    BddLogger.then("it should return 400");

    String tooLongTitle = "T".repeat(300);
    CreateTraceDTO dto =
        new CreateTraceDTO(tooLongTitle, ELanguage.FRENCH, false, "Note", "Justif");

    mockMvc
        .perform(
            post(BASE_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto))
                .header(AvenirsSecurityHeaders.SIGNED_CONTEXT, studentPayload)
                .header(AvenirsSecurityHeaders.CONTEXT_KID, secretKey)
                .header(AvenirsSecurityHeaders.CONTEXT_SIGNATURE, studentSignature))
        .andExpect(status().isBadRequest());
  }

  @Test
  void shouldGetTraceDetail() throws Exception {
    BddLogger.given("an existing trace created by the logged-in student");
    UUID traceId = getFirstTraceIdFromOverview();

    BddLogger.when("performing GET /detail");
    BddLogger.then("it should return trace detail");

    mockMvc
        .perform(
            get(DETAIL_BASE_PATH, traceId)
                .header(HttpHeaders.ACCEPT_LANGUAGE, ELanguage.FRENCH.getCode())
                .header(AvenirsSecurityHeaders.SIGNED_CONTEXT, studentPayload)
                .header(AvenirsSecurityHeaders.CONTEXT_KID, secretKey)
                .header(AvenirsSecurityHeaders.CONTEXT_SIGNATURE, studentSignature)
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.id").value(traceId.toString()))
        .andExpect(jsonPath("$.title", notNullValue()))
        .andExpect(jsonPath("$.programName", notNullValue()))
        .andExpect(jsonPath("$.createdAt", notNullValue()))
        .andExpect(jsonPath("$.updatedAt", notNullValue()));
  }

  @Test
  void shouldReturn404WhenTraceDetailNotFound() throws Exception {
    BddLogger.given("a non-existing trace id");
    UUID unknownId = UUID.randomUUID();

    BddLogger.when("performing GET /detail");
    BddLogger.then("it should return 404 TRACE_NOT_FOUND");

    mockMvc
        .perform(
            get(DETAIL_BASE_PATH, unknownId)
                .header(HttpHeaders.ACCEPT_LANGUAGE, ELanguage.FRENCH.getCode())
                .header(AvenirsSecurityHeaders.SIGNED_CONTEXT, studentPayload)
                .header(AvenirsSecurityHeaders.CONTEXT_KID, secretKey)
                .header(AvenirsSecurityHeaders.CONTEXT_SIGNATURE, studentSignature)
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.code", is("TRACE_NOT_FOUND")));
  }

  @Test
  void shouldUpdateTrace() throws Exception {
    BddLogger.given("an existing trace created by the logged-in student");
    UUID traceId = getFirstTraceIdFromOverview();

    UpdateTraceDTO update =
        new UpdateTraceDTO(
            "Updated title", ELanguage.ENGLISH, true, "Updated note", "Updated AI justification");

    BddLogger.when("performing PUT /{traceId}");
    BddLogger.then("it should update and return the updated trace detail");

    mockMvc
        .perform(
            put(BASE_PATH_WITH_ID, traceId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(update))
                .header(HttpHeaders.ACCEPT_LANGUAGE, ELanguage.FRENCH.getCode())
                .header(AvenirsSecurityHeaders.SIGNED_CONTEXT, studentPayload)
                .header(AvenirsSecurityHeaders.CONTEXT_KID, secretKey)
                .header(AvenirsSecurityHeaders.CONTEXT_SIGNATURE, studentSignature))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.id").value(traceId.toString()))
        .andExpect(jsonPath("$.title").value("Updated title"))
        .andExpect(jsonPath("$.isGroup").value(true));
  }

  @Test
  void shouldReturn404WhenUpdatingUnknownTrace() throws Exception {
    BddLogger.given("a non-existing trace id");
    UUID unknownId = UUID.randomUUID();

    UpdateTraceDTO update =
        new UpdateTraceDTO("Updated title", ELanguage.FRENCH, false, null, null);

    BddLogger.when("performing PUT /{traceId}");
    BddLogger.then("it should return 404 TRACE_NOT_FOUND");

    mockMvc
        .perform(
            put(BASE_PATH_WITH_ID, unknownId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(update))
                .header(AvenirsSecurityHeaders.SIGNED_CONTEXT, studentPayload)
                .header(AvenirsSecurityHeaders.CONTEXT_KID, secretKey)
                .header(AvenirsSecurityHeaders.CONTEXT_SIGNATURE, studentSignature))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.code", is("TRACE_NOT_FOUND")));
  }

  @Test
  void shouldSearchAssociationsForEachType() throws Exception {
    BddLogger.given("the " + SEARCH_ASSOCIATION_BASE_PATH + " endpoint");
    BddLogger.when("performing GET search for AMS, SKILL_LEVEL and DECLARED_SKILL");
    BddLogger.then("it should return paged responses");

    for (ETraceAssociationType type : ETraceAssociationType.values()) {
      mockMvc
          .perform(
              get(SEARCH_ASSOCIATION_BASE_PATH, type.name())
                  .param("keyword", "")
                  .param("page", "0")
                  .param("pageSize", "8")
                  .header(AvenirsSecurityHeaders.SIGNED_CONTEXT, studentPayload)
                  .header(AvenirsSecurityHeaders.CONTEXT_KID, secretKey)
                  .header(AvenirsSecurityHeaders.CONTEXT_SIGNATURE, studentSignature)
                  .accept(MediaType.APPLICATION_JSON))
          .andExpect(status().isOk())
          .andExpect(content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(jsonPath("$.data").isArray())
          .andExpect(jsonPath("$.page.page").value(0))
          .andExpect(jsonPath("$.page.pageSize").value(8))
          .andExpect(jsonPath("$.page.totalElements", greaterThanOrEqualTo(0)));
    }
  }

  @Test
  void shouldReturn404WhenUnknownUserAssociatesTrace() throws Exception {
    BddLogger.given("unknown user context");

    UUID traceId = getFirstTraceIdFromOverview();
    UUID activityId = UUID.randomUUID();

    AssociationsCreationRequest body = new AssociationsCreationRequest(List.of(activityId));

    BddLogger.when("performing POST with unknown user");
    BddLogger.then("it should return 404 USER_NOT_FOUND");

    mockMvc
        .perform(
            post(BASE_PATH + "/" + traceId + "/associate/activities")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body))
                .header(AvenirsSecurityHeaders.SIGNED_CONTEXT, unknownUserPayload)
                .header(AvenirsSecurityHeaders.CONTEXT_KID, secretKey)
                .header(AvenirsSecurityHeaders.CONTEXT_SIGNATURE, unknownUserSignature))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.code", is("USER_NOT_FOUND")));
  }
}
