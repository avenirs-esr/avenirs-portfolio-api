package fr.avenirsesr.portfolio.student.experience.application.adapter.controller;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.avenirsesr.portfolio.common.configuration.domain.model.TraceConfiguration;
import fr.avenirsesr.portfolio.common.data.domain.model.enums.ESortField;
import fr.avenirsesr.portfolio.common.data.domain.model.enums.ESortOrder;
import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import fr.avenirsesr.portfolio.shared.infrastructure.ContainerConfigurationTest;
import fr.avenirsesr.portfolio.shared.infrastructure.adapter.seeder.SeederRunner;
import fr.avenirsesr.portfolio.student.skill.domain.port.output.repository.DeclaredSkillRepository;
import fr.avenirsesr.portfolio.student.trace.infrastructure.adapter.client.TraceConfigurationClient;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.transaction.annotation.Transactional;

public class DeclaredExperienceControllerIT extends ContainerConfigurationTest {

  private static final String BASE_PATH = "/me/declared/experiences";
  private static final String ASSOCIATIONS_PATH = "/me/associations/DECLARED_EXPERIENCE";

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

  private String buildCreateExperienceJson(String experienceType) {
    return "{\n"
        + "  \"title\": \"My Experience\",\n"
        + "  \"experienceType\": \""
        + experienceType
        + "\",\n"
        + "  \"organization\": \"ACME Inc\",\n"
        + "  \"activitySector\": \"IT\",\n"
        + "  \"location\": \"Paris\",\n"
        + "  \"description\": \"Some description\",\n"
        + "  \"sourceOfInformation\": \"SELF_DECLARED\",\n"
        + "  \"summary\": \"Summary text\",\n"
        + "  \"externalLink\": \"https://example.com\",\n"
        + "  \"startDate\": \"2024-01-01\",\n"
        + "  \"endDate\": \"2024-03-01\"\n"
        + "}\n";
  }

  private String extractIdFromResponse(String responseBody) throws Exception {
    JsonNode jsonNode = objectMapper.readTree(responseBody);
    return jsonNode.get("id").asText();
  }

  private String buildCreateExperienceJson(String title, String startDate, String endDate) {
    return "{\n"
        + "  \"title\": \""
        + title
        + "\",\n"
        + "  \"experienceType\": \"PROFESSIONAL\",\n"
        + "  \"organization\": \"ACME Inc\",\n"
        + "  \"activitySector\": \"IT\",\n"
        + "  \"location\": \"Paris\",\n"
        + "  \"description\": \"Some description\",\n"
        + "  \"sourceOfInformation\": \"SELF_DECLARED\",\n"
        + "  \"summary\": \"Summary text\",\n"
        + "  \"externalLink\": \"https://example.com\",\n"
        + "  \"startDate\": \""
        + startDate
        + "\",\n"
        + "  \"endDate\": \""
        + endDate
        + "\"\n"
        + "}\n";
  }

  private String createDeclaredExperience(String title, String startDate, String endDate)
      throws Exception {
    String responseBody =
        webTestClient
            .post()
            .uri(BASE_PATH + "/")
            .header("X-Signed-Context", studentPayload)
            .header("X-Context-Signature", studentSignature)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(buildCreateExperienceJson(title, startDate, endDate))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(String.class)
            .returnResult()
            .getResponseBody();
    return extractIdFromResponse(responseBody);
  }

  private String getDeclaredExperienceView(ESortField sortField, ESortOrder sortOrder) {
    return webTestClient
        .get()
        .uri(
            uriBuilder -> {
              uriBuilder.path(BASE_PATH + "/view").queryParam("pageSize", 100);
              if (sortField != null) {
                uriBuilder.queryParam("sortField", sortField);
              }
              if (sortOrder != null) {
                uriBuilder.queryParam("sortOrder", sortOrder);
              }
              return uriBuilder.build();
            })
        .header("X-Signed-Context", studentPayload)
        .header("X-Context-Signature", studentSignature)
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody(String.class)
        .returnResult()
        .getResponseBody();
  }

  private List<String> orderedKnownIds(String responseBody, List<String> knownIds)
      throws Exception {
    List<String> result = new ArrayList<>();
    for (JsonNode node : objectMapper.readTree(responseBody).get("data")) {
      String id = node.get("id").asText();
      if (knownIds.contains(id)) {
        result.add(id);
      }
    }
    return result;
  }

  @Transactional
  @Test
  void shouldCreateDeclaredExperience() throws Exception {
    BddLogger.given("the " + BASE_PATH + " endpoint");
    BddLogger.when("performing a POST to create a declared experience");
    BddLogger.then("it should return created status and the experience");

    webTestClient
        .post()
        .uri(BASE_PATH + "/")
        .header("X-Signed-Context", studentPayload)
        .header("X-Context-Signature", studentSignature)
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(buildCreateExperienceJson())
        .exchange()
        .expectStatus()
        .isCreated()
        .expectBody()
        .jsonPath("$.id")
        .exists()
        .jsonPath("$.title")
        .isEqualTo("My Experience")
        .jsonPath("$.valorized")
        .isEqualTo(false)
        .jsonPath("$.result")
        .isEqualTo("Certified");
  }

  @Transactional
  @Test
  void shouldGetDeclaredExperience() throws Exception {
    BddLogger.given("an already created declared experience");
    String responseBody =
        webTestClient
            .post()
            .uri(BASE_PATH + "/")
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

    String createdId = extractIdFromResponse(responseBody);

    BddLogger.when("performing a GET on the created declared experience");
    BddLogger.then("it should return the declared experience");

    webTestClient
        .get()
        .uri(BASE_PATH + "/" + createdId)
        .header("X-Signed-Context", studentPayload)
        .header("X-Context-Signature", studentSignature)
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody()
        .jsonPath("$.id")
        .isEqualTo(createdId)
        .jsonPath("$.title")
        .isEqualTo("My Experience");
  }

  @Test
  void shouldReturnNotFoundWhenExperienceDoesNotExist() throws Exception {

    BddLogger.given("a declared experience id that does not exist");
    BddLogger.when("performing a GET with unknown id");
    BddLogger.then("it should return not found");

    webTestClient
        .get()
        .uri(BASE_PATH + "/" + notFoundDeclaredExperienceId)
        .header("X-Signed-Context", studentPayload)
        .header("X-Context-Signature", studentSignature)
        .exchange()
        .expectStatus()
        .isNotFound();
  }

  @Transactional
  @Test
  void shouldGetDeclaredExperienceViewWithDefaultPagination() throws Exception {
    BddLogger.given("several declared experiences exist");
    BddLogger.when("performing a GET on /view without pagination params");
    BddLogger.then("it should return a paged list of declared experiences");

    webTestClient
        .get()
        .uri(BASE_PATH + "/view")
        .header("X-Signed-Context", studentPayload)
        .header("X-Context-Signature", studentSignature)
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody()
        .jsonPath("$.data")
        .isArray()
        .jsonPath("$.page")
        .exists()
        .jsonPath("$.page.page")
        .exists()
        .jsonPath("$.page.pageSize")
        .exists()
        .jsonPath("$.page.totalElements")
        .exists()
        .jsonPath("$.page.totalPages")
        .exists();
  }

  @Transactional
  @Test
  void shouldGetDeclaredExperienceViewWithPaginationParams() throws Exception {
    BddLogger.given("several declared experiences exist");
    BddLogger.when("performing a GET on /view with pagination params");
    BddLogger.then("it should return a paged list respecting pagination");

    webTestClient
        .get()
        .uri(
            uriBuilder ->
                uriBuilder
                    .path(BASE_PATH + "/view")
                    .queryParam("page", 0)
                    .queryParam("pageSize", 5)
                    .build())
        .header("X-Signed-Context", studentPayload)
        .header("X-Context-Signature", studentSignature)
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody()
        .jsonPath("$.data")
        .isArray()
        .jsonPath("$.page.page")
        .isEqualTo(0)
        .jsonPath("$.page.pageSize")
        .isEqualTo(5);
  }

  @Transactional
  @Test
  void shouldSortDeclaredExperienceViewByNameAscending() throws Exception {
    BddLogger.given("three declared experiences with distinct titles");
    String bananeId = createDeclaredExperience("Banane", "2024-01-01", "2024-01-15");
    String cerieId = createDeclaredExperience("Cerise", "2024-03-01", "2024-03-15");
    String ananasId = createDeclaredExperience("Ananas", "2024-06-01", "2024-06-15");
    List<String> knownIds = List.of(bananeId, cerieId, ananasId);

    BddLogger.when("performing a GET on /view with sortField=NAME, sortOrder=ASC");
    BddLogger.then("experiences should be ordered alphabetically from A to Z by title");

    String response = getDeclaredExperienceView(ESortField.NAME, ESortOrder.ASC);
    assertThat(orderedKnownIds(response, knownIds)).containsExactly(ananasId, bananeId, cerieId);
  }

  @Transactional
  @Test
  void shouldSortDeclaredExperienceViewByNameDescending() throws Exception {
    BddLogger.given("three declared experiences with distinct titles");
    String bananeId = createDeclaredExperience("Banane", "2024-01-01", "2024-01-15");
    String cerieId = createDeclaredExperience("Cerise", "2024-03-01", "2024-03-15");
    String ananasId = createDeclaredExperience("Ananas", "2024-06-01", "2024-06-15");
    List<String> knownIds = List.of(bananeId, cerieId, ananasId);

    BddLogger.when("performing a GET on /view with sortField=NAME, sortOrder=DESC");
    BddLogger.then("experiences should be ordered alphabetically from Z to A by title");

    String response = getDeclaredExperienceView(ESortField.NAME, ESortOrder.DESC);
    assertThat(orderedKnownIds(response, knownIds)).containsExactly(cerieId, bananeId, ananasId);
  }

  @Transactional
  @Test
  void shouldSortDeclaredExperienceViewByMostRecentStartDateFirst() throws Exception {
    BddLogger.given("three declared experiences with distinct start dates");
    String oldestId = createDeclaredExperience("Old experience", "2024-01-01", "2024-01-15");
    String middleId = createDeclaredExperience("Middle experience", "2024-03-01", "2024-03-15");
    String mostRecentId = createDeclaredExperience("Recent experience", "2024-06-01", "2024-06-15");
    List<String> knownIds = List.of(oldestId, middleId, mostRecentId);

    BddLogger.when("performing a GET on /view with sortField=DATE, sortOrder=DESC");
    BddLogger.then("experiences should be ordered from the most recent to the oldest start date");

    String response = getDeclaredExperienceView(ESortField.DATE, ESortOrder.DESC);
    assertThat(orderedKnownIds(response, knownIds))
        .containsExactly(mostRecentId, middleId, oldestId);
  }

  @Transactional
  @Test
  void shouldSortDeclaredExperienceViewByOldestStartDateFirst() throws Exception {
    BddLogger.given("three declared experiences with distinct start dates");
    String oldestId = createDeclaredExperience("Old experience", "2024-01-01", "2024-01-15");
    String middleId = createDeclaredExperience("Middle experience", "2024-03-01", "2024-03-15");
    String mostRecentId = createDeclaredExperience("Recent experience", "2024-06-01", "2024-06-15");
    List<String> knownIds = List.of(oldestId, middleId, mostRecentId);

    BddLogger.when("performing a GET on /view with sortField=DATE, sortOrder=ASC");
    BddLogger.then("experiences should be ordered from the oldest to the most recent start date");

    String response = getDeclaredExperienceView(ESortField.DATE, ESortOrder.ASC);
    assertThat(orderedKnownIds(response, knownIds))
        .containsExactly(oldestId, middleId, mostRecentId);
  }

  @Transactional
  @Test
  void shouldSortDeclaredExperienceViewByMostRecentStartDateFirstByDefault() throws Exception {
    BddLogger.given("three declared experiences with distinct start dates");
    String oldestId = createDeclaredExperience("Old experience", "2024-01-01", "2024-01-15");
    String middleId = createDeclaredExperience("Middle experience", "2024-03-01", "2024-03-15");
    String mostRecentId = createDeclaredExperience("Recent experience", "2024-06-01", "2024-06-15");
    List<String> knownIds = List.of(oldestId, middleId, mostRecentId);

    BddLogger.when("performing a GET on /view without sortField/sortOrder params");
    BddLogger.then("experiences should default to the most recent to the oldest start date order");

    String response = getDeclaredExperienceView(null, null);
    assertThat(orderedKnownIds(response, knownIds))
        .containsExactly(mostRecentId, middleId, oldestId);
  }

  @Transactional
  @Test
  void shouldUpdateDeclaredExperience() throws Exception {
    BddLogger.given("an existing declared experience");
    String responseBody =
        webTestClient
            .post()
            .uri(BASE_PATH + "/")
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

    String createdId = extractIdFromResponse(responseBody);

    String updateJson =
        "{\n"
            + "  \"title\": \"Updated Experience\",\n"
            + "  \"experienceType\": \"PERSONAL\",\n"
            + "  \"organization\": \"New Org\",\n"
            + "  \"activitySector\": \"Science\",\n"
            + "  \"location\": \"Lyon\",\n"
            + "  \"description\": \"Updated description\",\n"
            + "  \"sourceOfInformation\": \"SELF_DECLARED\",\n"
            + "  \"summary\": \"Updated summary\",\n"
            + "  \"externalLink\": \"https://updated.com\",\n"
            + "  \"startDate\": \"2024-02-01\",\n"
            + "  \"endDate\": \"2024-04-01\",\n"
            + "  \"result\": \"Updated Result\"\n"
            + "}";

    BddLogger.when("performing PUT on that declared experience");
    BddLogger.then("it should update and return the new values");

    webTestClient
        .put()
        .uri(BASE_PATH + "/" + createdId)
        .header("X-Signed-Context", studentPayload)
        .header("X-Context-Signature", studentSignature)
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(updateJson)
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody()
        .jsonPath("$.id")
        .isEqualTo(createdId)
        .jsonPath("$.title")
        .isEqualTo("Updated Experience")
        .jsonPath("$.organization")
        .isEqualTo("New Org")
        .jsonPath("$.location")
        .isEqualTo("Lyon")
        .jsonPath("$.externalLink")
        .isEqualTo("https://updated.com")
        .jsonPath("$.result")
        .isEqualTo("Updated Result");
  }

  @Transactional
  @Test
  void shouldUpdateDeclaredExperienceValorizedFlag() throws Exception {
    BddLogger.given("an existing declared experience");
    String responseBody =
        webTestClient
            .post()
            .uri(BASE_PATH + "/")
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

    String createdId = extractIdFromResponse(responseBody);

    String updateJson =
        "{\n"
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
            + "  \"valorized\": true\n"
            + "}\n";

    BddLogger.when("performing PUT with valorized set to true");
    BddLogger.then("it should return the experience marked as valorized");

    webTestClient
        .put()
        .uri(BASE_PATH + "/" + createdId)
        .header("X-Signed-Context", studentPayload)
        .header("X-Context-Signature", studentSignature)
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(updateJson)
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody()
        .jsonPath("$.id")
        .isEqualTo(createdId)
        .jsonPath("$.valorized")
        .isEqualTo(true);
  }

  @Transactional
  @Test
  void shouldFilterDeclaredExperienceViewByIsValorized() throws Exception {
    BddLogger.given("a declared experience marked as valorized");

    String createResponse =
        webTestClient
            .post()
            .uri(BASE_PATH + "/")
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

    String createdId = extractIdFromResponse(createResponse);

    String updateJson =
        "{\n"
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
            + "  \"valorized\": true\n"
            + "}\n";

    webTestClient
        .put()
        .uri(BASE_PATH + "/" + createdId)
        .header("X-Signed-Context", studentPayload)
        .header("X-Context-Signature", studentSignature)
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(updateJson)
        .exchange()
        .expectStatus()
        .isOk();

    BddLogger.when("performing a GET on /view with isValorized=true");

    String valorizedOnlyResponse =
        webTestClient
            .get()
            .uri(
                uriBuilder ->
                    uriBuilder.path(BASE_PATH + "/view").queryParam("isValorized", true).build())
            .header("X-Signed-Context", studentPayload)
            .header("X-Context-Signature", studentSignature)
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody(String.class)
            .returnResult()
            .getResponseBody();

    BddLogger.then("it should contain the valorized declared experience");
    List<String> valorizedIds = new ArrayList<>();
    objectMapper
        .readTree(valorizedOnlyResponse)
        .get("data")
        .forEach(node -> valorizedIds.add(node.get("id").asText()));
    assertThat(valorizedIds).contains(createdId);

    BddLogger.when("performing a GET on /view with isValorized=false");

    String nonValorizedOnlyResponse =
        webTestClient
            .get()
            .uri(
                uriBuilder ->
                    uriBuilder.path(BASE_PATH + "/view").queryParam("isValorized", false).build())
            .header("X-Signed-Context", studentPayload)
            .header("X-Context-Signature", studentSignature)
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody(String.class)
            .returnResult()
            .getResponseBody();

    BddLogger.then("it should not contain the valorized declared experience");
    List<String> nonValorizedIds = new ArrayList<>();
    objectMapper
        .readTree(nonValorizedOnlyResponse)
        .get("data")
        .forEach(node -> nonValorizedIds.add(node.get("id").asText()));
    assertThat(nonValorizedIds).doesNotContain(createdId);
  }

  @Transactional
  @Test
  void shouldFilterDeclaredExperienceViewByExperienceTypes() throws Exception {
    BddLogger.given("a professional declared experience and a personal declared experience");

    String professionalResponse =
        webTestClient
            .post()
            .uri(BASE_PATH + "/")
            .header("X-Signed-Context", studentPayload)
            .header("X-Context-Signature", studentSignature)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(buildCreateExperienceJson("PROFESSIONAL"))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(String.class)
            .returnResult()
            .getResponseBody();
    String professionalId = extractIdFromResponse(professionalResponse);

    String personalResponse =
        webTestClient
            .post()
            .uri(BASE_PATH + "/")
            .header("X-Signed-Context", studentPayload)
            .header("X-Context-Signature", studentSignature)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(buildCreateExperienceJson("PERSONAL"))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(String.class)
            .returnResult()
            .getResponseBody();
    String personalId = extractIdFromResponse(personalResponse);

    String volunteerResponse =
        webTestClient
            .post()
            .uri(BASE_PATH + "/")
            .header("X-Signed-Context", studentPayload)
            .header("X-Context-Signature", studentSignature)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(buildCreateExperienceJson("VOLUNTEER"))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(String.class)
            .returnResult()
            .getResponseBody();
    String volunteerId = extractIdFromResponse(volunteerResponse);

    BddLogger.when("performing a GET on /view with experienceTypes=[PROFESSIONAL]");

    String professionalOnlyResponse =
        webTestClient
            .get()
            .uri(
                uriBuilder ->
                    uriBuilder
                        .path(BASE_PATH + "/view")
                        .queryParam("experienceTypes", List.of("PROFESSIONAL"))
                        .queryParam("pageSize", 100)
                        .build())
            .header("X-Signed-Context", studentPayload)
            .header("X-Context-Signature", studentSignature)
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody(String.class)
            .returnResult()
            .getResponseBody();

    BddLogger.then("it should only contain the professional declared experience");
    List<String> professionalIds = new ArrayList<>();
    objectMapper
        .readTree(professionalOnlyResponse)
        .get("data")
        .forEach(node -> professionalIds.add(node.get("id").asText()));
    assertThat(professionalIds)
        .contains(professionalId)
        .doesNotContain(personalId)
        .doesNotContain(volunteerId);

    BddLogger.when("performing a GET on /view with experienceTypes=[PERSONAL]");

    String personalOnlyResponse =
        webTestClient
            .get()
            .uri(
                uriBuilder ->
                    uriBuilder
                        .path(BASE_PATH + "/view")
                        .queryParam("experienceTypes", List.of("PERSONAL"))
                        .queryParam("pageSize", 100)
                        .build())
            .header("X-Signed-Context", studentPayload)
            .header("X-Context-Signature", studentSignature)
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody(String.class)
            .returnResult()
            .getResponseBody();

    BddLogger.then("it should only contain the personal declared experience");
    List<String> personalIds = new ArrayList<>();
    objectMapper
        .readTree(personalOnlyResponse)
        .get("data")
        .forEach(node -> personalIds.add(node.get("id").asText()));
    assertThat(personalIds)
        .contains(personalId)
        .doesNotContain(professionalId)
        .doesNotContain(volunteerId);

    BddLogger.when("performing a GET on /view with experienceTypes=[VOLUNTEER]");

    String volunteerOnlyResponse =
        webTestClient
            .get()
            .uri(
                uriBuilder ->
                    uriBuilder
                        .path(BASE_PATH + "/view")
                        .queryParam("experienceTypes", List.of("VOLUNTEER"))
                        .queryParam("pageSize", 100)
                        .build())
            .header("X-Signed-Context", studentPayload)
            .header("X-Context-Signature", studentSignature)
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody(String.class)
            .returnResult()
            .getResponseBody();

    BddLogger.then("it should only contain the volunteer declared experience");
    List<String> volunteerIds = new ArrayList<>();
    objectMapper
        .readTree(volunteerOnlyResponse)
        .get("data")
        .forEach(node -> volunteerIds.add(node.get("id").asText()));
    assertThat(volunteerIds)
        .contains(volunteerId)
        .doesNotContain(professionalId)
        .doesNotContain(personalId);

    BddLogger.when("performing a GET on /view with experienceTypes=[PROFESSIONAL, PERSONAL]");

    String professionalAndPersonalOnlyResponse =
        webTestClient
            .get()
            .uri(
                uriBuilder ->
                    uriBuilder
                        .path(BASE_PATH + "/view")
                        .queryParam("experienceTypes", List.of("PROFESSIONAL", "PERSONAL"))
                        .queryParam("pageSize", 100)
                        .build())
            .header("X-Signed-Context", studentPayload)
            .header("X-Context-Signature", studentSignature)
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody(String.class)
            .returnResult()
            .getResponseBody();

    BddLogger.then("it should only contain the professional and personal declared experience");
    List<String> professionalAndPersonalIds = new ArrayList<>();
    objectMapper
        .readTree(professionalAndPersonalOnlyResponse)
        .get("data")
        .forEach(node -> professionalAndPersonalIds.add(node.get("id").asText()));
    assertThat(professionalAndPersonalIds)
        .contains(professionalId)
        .contains(personalId)
        .doesNotContain(volunteerId);
  }

  @Transactional
  @Test
  void shouldReturnDeclaredExperienceAssociationCountsInView() throws Exception {
    BddLogger.given("a declared experience associated with a trace and a declared skill");

    String experienceId = createDeclaredExperienceAs(studentPayload, studentSignature);
    UUID traceId = createTraceAs("My trace", studentPayload, studentSignature);
    UUID skillId = createAnyAvailableDeclaredSkillProgressAs(studentPayload, studentSignature);

    webTestClient
        .post()
        .uri(ASSOCIATIONS_PATH + "/" + experienceId + "/TRACE")
        .header("X-Signed-Context", studentPayload)
        .header("X-Context-Signature", studentSignature)
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(objectMapper.writeValueAsString(Map.of("idsToAssociate", List.of(traceId))))
        .exchange()
        .expectStatus()
        .isOk();

    webTestClient
        .post()
        .uri(ASSOCIATIONS_PATH + "/" + experienceId + "/DECLARED_SKILL")
        .header("X-Signed-Context", studentPayload)
        .header("X-Context-Signature", studentSignature)
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(objectMapper.writeValueAsString(Map.of("idsToAssociate", List.of(skillId))))
        .exchange()
        .expectStatus()
        .isOk();

    BddLogger.when("performing a GET on /view");

    String viewResponse =
        webTestClient
            .get()
            .uri(
                uriBuilder ->
                    uriBuilder.path(BASE_PATH + "/view").queryParam("pageSize", 100).build())
            .header("X-Signed-Context", studentPayload)
            .header("X-Context-Signature", studentSignature)
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody(String.class)
            .returnResult()
            .getResponseBody();

    BddLogger.then("it should return the correct association counts for the declared experience");
    JsonNode experienceNode = null;
    for (JsonNode node : objectMapper.readTree(viewResponse).get("data")) {
      if (node.get("id").asText().equals(experienceId)) {
        experienceNode = node;
        break;
      }
    }
    assertThat(experienceNode).isNotNull();
    assertThat(experienceNode.get("declaredExperienceAssociationCountDTO")).isNotNull();
    assertThat(
            experienceNode
                .get("declaredExperienceAssociationCountDTO")
                .get("traceAssociationsCount")
                .asInt())
        .isEqualTo(1);
    assertThat(
            experienceNode
                .get("declaredExperienceAssociationCountDTO")
                .get("declaredSkillAssociationsCount")
                .asInt())
        .isEqualTo(1);
  }

  @Test
  void shouldReturnNotFoundWhenUpdatingNonExistingExperience() throws Exception {
    BddLogger.given("a non existing declared experience id");
    BddLogger.when("performing PUT with unknown id");
    BddLogger.then("it should return not found");

    webTestClient
        .put()
        .uri(BASE_PATH + "/" + notFoundDeclaredExperienceId)
        .header("X-Signed-Context", studentPayload)
        .header("X-Context-Signature", studentSignature)
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(buildCreateExperienceJson())
        .exchange()
        .expectStatus()
        .isNotFound();
  }

  @Transactional
  @Test
  void shouldReturnBadRequestWhenUpdatingWithInvalidData() throws Exception {
    BddLogger.given("an existing declared experience");
    String responseBody =
        webTestClient
            .post()
            .uri(BASE_PATH + "/")
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

    String createdId = extractIdFromResponse(responseBody);

    String invalidUpdateJson =
        "{\n"
            + "  \"title\": \"\",\n"
            + "  \"experienceType\": \"PROFESSIONAL\",\n"
            + "  \"organization\": \"ACME Inc\",\n"
            + "  \"activitySector\": \"IT\",\n"
            + "  \"location\": \"Paris\",\n"
            + "  \"description\": \"Some description\",\n"
            + "  \"sourceOfInformation\": \"SELF_DECLARED\",\n"
            + "  \"summary\": \"Summary\",\n"
            + "  \"externalLink\": \"https://example.com\",\n"
            + "  \"startDate\": \"2024-01-01\",\n"
            + "  \"endDate\": \"2024-03-01\"\n"
            + "}";

    BddLogger.when("performing PUT with invalid payload");
    BddLogger.then("it should return 400");

    webTestClient
        .put()
        .uri(BASE_PATH + "/" + createdId)
        .header("X-Signed-Context", studentPayload)
        .header("X-Context-Signature", studentSignature)
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(invalidUpdateJson)
        .exchange()
        .expectStatus()
        .isBadRequest();
  }

  @Transactional
  @Test
  void shouldDeleteDeclaredExperiences() throws Exception {
    BddLogger.given("an existing declared experience");
    String responseBody =
        webTestClient
            .post()
            .uri(BASE_PATH + "/")
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

    String createdId = extractIdFromResponse(responseBody);

    BddLogger.when("performing DELETE on that declared experience");
    BddLogger.then("it should delete successfully");

    String deleteJson = "[\"" + createdId + "\"]";

    webTestClient
        .method(HttpMethod.DELETE)
        .uri(BASE_PATH + "/")
        .header("X-Signed-Context", studentPayload)
        .header("X-Context-Signature", studentSignature)
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(deleteJson)
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody(String.class)
        .consumeWith(
            result -> {
              String body = new String(result.getResponseBody());
              assert body.contains("successfully deleted");
            });

    BddLogger.then("trying to GET the deleted experience should return 404");

    webTestClient
        .get()
        .uri(BASE_PATH + "/" + createdId)
        .header("X-Signed-Context", studentPayload)
        .header("X-Context-Signature", studentSignature)
        .exchange()
        .expectStatus()
        .isNotFound();
  }

  @Test
  void shouldReturnNotFoundWhenDeletingNonExistingExperience() throws Exception {
    BddLogger.given("a non existing declared experience id");
    BddLogger.when("performing DELETE with unknown id");
    BddLogger.then("it should return not found");

    String deleteJson = "[\"" + notFoundDeclaredExperienceId + "\"]";

    webTestClient
        .method(HttpMethod.DELETE)
        .uri(BASE_PATH + "/")
        .header("X-Signed-Context", studentPayload)
        .header("X-Context-Signature", studentSignature)
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(deleteJson)
        .exchange()
        .expectStatus()
        .isNotFound();
  }

  @Transactional
  @Test
  void shouldReturnUnauthorizedWhenDeletingOtherStudentsExperience() throws Exception {
    BddLogger.given("an existing declared experience for another student");

    String responseBody =
        webTestClient
            .post()
            .uri(BASE_PATH + "/")
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

    String otherId = extractIdFromResponse(responseBody);

    BddLogger.when("performing DELETE on that experience as another student");
    BddLogger.then("it should return forbidden");

    String deleteJson = "[\"" + otherId + "\"]";

    webTestClient
        .method(HttpMethod.DELETE)
        .uri(BASE_PATH + "/")
        .header("X-Signed-Context", studentPayload)
        .header("X-Context-Signature", studentSignature)
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(deleteJson)
        .exchange()
        .expectStatus()
        .isForbidden();
  }

  private String createDeclaredExperienceAs(String payload, String signature) throws Exception {
    String responseBody =
        webTestClient
            .post()
            .uri(BASE_PATH + "/")
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
  void shouldReturn403WhenCreatingDeclaredExperienceWithoutPermission() {
    BddLogger.given("an authenticated user without the declared-experience:create:own permission");
    BddLogger.when("performing a POST on " + BASE_PATH);
    BddLogger.then("it should return 403");

    webTestClient
        .post()
        .uri(BASE_PATH + "/")
        .header("X-Signed-Context", noPermissionPayload)
        .header("X-Context-Signature", noPermissionSignature)
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(buildCreateExperienceJson())
        .exchange()
        .expectStatus()
        .isForbidden();
  }

  @Test
  void shouldReturn403WhenGettingDeclaredExperienceViewWithoutPermission() {
    BddLogger.given("an authenticated user without the declared-experience:list:own permission");
    BddLogger.when("performing a GET on " + BASE_PATH + "/view");
    BddLogger.then("it should return 403");

    webTestClient
        .get()
        .uri(BASE_PATH + "/view")
        .header("X-Signed-Context", noPermissionPayload)
        .header("X-Context-Signature", noPermissionSignature)
        .exchange()
        .expectStatus()
        .isForbidden();
  }
}
