package fr.avenirsesr.portfolio.student.progress.declared.experience.application.adapter.controller;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.transaction.annotation.Transactional;

public class DeclaredExperienceControllerIT extends ContainerConfigurationTest {

  private static final String BASE_PATH = "/me/declared/experiences";

  @Autowired private WebTestClient webTestClient;
  @Autowired private ObjectMapper objectMapper;
  @Autowired private DeclaredSkillRepository declaredSkillRepository;

  @Value("${hmac.secret-key}")
  private String secretKey;

  @Value("${user.student.payload}")
  private String studentPayload;

  @Value("${user.student.signature}")
  private String studentSignature;

  @Value("${user.second.student.payload}")
  private String otherStudentPayload;

  @Value("${user.second.student.signature}")
  private String otherStudentSignature;

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
        + "  \"endDate\": \"2024-03-01\"\n"
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
        .header("X-Context-Kid", secretKey)
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
        .isEqualTo(false);
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
            .header("X-Context-Kid", secretKey)
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
        .header("X-Context-Kid", secretKey)
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
        .header("X-Context-Kid", secretKey)
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
        .header("X-Context-Kid", secretKey)
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
        .header("X-Context-Kid", secretKey)
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
  void shouldUpdateDeclaredExperience() throws Exception {
    BddLogger.given("an existing declared experience");
    String responseBody =
        webTestClient
            .post()
            .uri(BASE_PATH + "/")
            .header("X-Signed-Context", studentPayload)
            .header("X-Context-Kid", secretKey)
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
            + "  \"endDate\": \"2024-04-01\"\n"
            + "}";

    BddLogger.when("performing PUT on that declared experience");
    BddLogger.then("it should update and return the new values");

    webTestClient
        .put()
        .uri(BASE_PATH + "/" + createdId)
        .header("X-Signed-Context", studentPayload)
        .header("X-Context-Kid", secretKey)
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
        .isEqualTo("https://updated.com");
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
            .header("X-Context-Kid", secretKey)
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
        .header("X-Context-Kid", secretKey)
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
            .header("X-Context-Kid", secretKey)
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
        .header("X-Context-Kid", secretKey)
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
            .header("X-Context-Kid", secretKey)
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
            .header("X-Context-Kid", secretKey)
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
  void shouldFilterDeclaredExperienceViewByExperienceType() throws Exception {
    BddLogger.given("a professional declared experience and a personal declared experience");

    String professionalResponse =
        webTestClient
            .post()
            .uri(BASE_PATH + "/")
            .header("X-Signed-Context", studentPayload)
            .header("X-Context-Kid", secretKey)
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
            .header("X-Context-Kid", secretKey)
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

    BddLogger.when("performing a GET on /view with experienceType=PROFESSIONAL");

    String professionalOnlyResponse =
        webTestClient
            .get()
            .uri(
                uriBuilder ->
                    uriBuilder
                        .path(BASE_PATH + "/view")
                        .queryParam("experienceType", "PROFESSIONAL")
                        .build())
            .header("X-Signed-Context", studentPayload)
            .header("X-Context-Kid", secretKey)
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
    assertThat(professionalIds).contains(professionalId).doesNotContain(personalId);

    BddLogger.when("performing a GET on /view with experienceType=PERSONAL");

    String personalOnlyResponse =
        webTestClient
            .get()
            .uri(
                uriBuilder ->
                    uriBuilder
                        .path(BASE_PATH + "/view")
                        .queryParam("experienceType", "PERSONAL")
                        .build())
            .header("X-Signed-Context", studentPayload)
            .header("X-Context-Kid", secretKey)
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
    assertThat(personalIds).contains(personalId).doesNotContain(professionalId);
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
        .header("X-Context-Kid", secretKey)
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
            .header("X-Context-Kid", secretKey)
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
        .header("X-Context-Kid", secretKey)
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
            .header("X-Context-Kid", secretKey)
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
        .header("X-Context-Kid", secretKey)
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
        .header("X-Context-Kid", secretKey)
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
        .header("X-Context-Kid", secretKey)
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
            .header("X-Context-Kid", secretKey)
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
        .header("X-Context-Kid", secretKey)
        .header("X-Context-Signature", studentSignature)
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(deleteJson)
        .exchange()
        .expectStatus()
        .isForbidden();
  }

  @Test
  void shouldGetDeclaredExperienceAssociations() throws Exception {
    BddLogger.given("an existing declared experience");

    String responseBody =
        webTestClient
            .post()
            .uri(BASE_PATH + "/")
            .header("X-Signed-Context", studentPayload)
            .header("X-Context-Kid", secretKey)
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
        .uri(BASE_PATH + "/" + experienceId + "/associations")
        .header("X-Signed-Context", studentPayload)
        .header("X-Context-Kid", secretKey)
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
        .uri(BASE_PATH + "/" + notFoundDeclaredExperienceId + "/associations")
        .header("X-Signed-Context", studentPayload)
        .header("X-Context-Kid", secretKey)
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
            .uri(BASE_PATH + "/")
            .header("X-Signed-Context", otherStudentPayload)
            .header("X-Context-Kid", secretKey)
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
        .uri(BASE_PATH + "/" + otherExperienceId + "/associations")
        .header("X-Signed-Context", studentPayload)
        .header("X-Context-Kid", secretKey)
        .header("X-Context-Signature", studentSignature)
        .exchange()
        .expectStatus()
        .isForbidden();

    BddLogger.then("it should return forbidden");
  }

  private String createDeclaredExperienceAs(String payload, String signature) throws Exception {
    String responseBody =
        webTestClient
            .post()
            .uri(BASE_PATH + "/")
            .header("X-Signed-Context", payload)
            .header("X-Context-Kid", secretKey)
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

  private UUID createDeclaredSkillProgressAs(int skipIndex, String payload, String signature)
      throws Exception {
    UUID declaredSkillId =
        declaredSkillRepository.findAll().stream()
            .skip(skipIndex)
            .findFirst()
            .orElseThrow()
            .getId();

    String responseBody =
        webTestClient
            .post()
            .uri("/me/declared/skill-progress")
            .header("X-Signed-Context", payload)
            .header("X-Context-Kid", secretKey)
            .header("X-Context-Signature", signature)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(
                "{\n"
                    + "  \"id\": \"%s\",\n".formatted(declaredSkillId)
                    + "  \"level\": \"BEGINNER\",\n"
                    + "  \"type\": \"ROME4\"\n"
                    + "}\n")
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(String.class)
            .returnResult()
            .getResponseBody();

    return UUID.fromString(extractIdFromResponse(responseBody));
  }

  private UUID createTraceAs(String title, String payload, String signature) throws Exception {
    String responseBody =
        webTestClient
            .post()
            .uri("/me/traces")
            .header("X-Signed-Context", payload)
            .header("X-Context-Kid", secretKey)
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
  void shouldAssociateDeclaredExperienceWithDeclaredSkillsSuccessfully() throws Exception {
    BddLogger.given("a declared experience and two declared skill progresses of the student");

    String experienceId = createDeclaredExperienceAs(studentPayload, studentSignature);
    UUID skill1 = createDeclaredSkillProgressAs(10, studentPayload, studentSignature);
    UUID skill2 = createDeclaredSkillProgressAs(11, studentPayload, studentSignature);

    BddLogger.when(
        "performing a POST to associate the experience with both declared skill progresses");

    String responseBody =
        webTestClient
            .post()
            .uri(BASE_PATH + "/" + experienceId + "/associate/declared-skills")
            .header("X-Signed-Context", studentPayload)
            .header("X-Context-Kid", secretKey)
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

    UUID skillId = createDeclaredSkillProgressAs(12, studentPayload, studentSignature);

    BddLogger.when("performing a POST to associate declared skills");

    webTestClient
        .post()
        .uri(BASE_PATH + "/" + notFoundDeclaredExperienceId + "/associate/declared-skills")
        .header("X-Signed-Context", studentPayload)
        .header("X-Context-Kid", secretKey)
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
    UUID skillId = createDeclaredSkillProgressAs(13, studentPayload, studentSignature);

    BddLogger.when("performing a POST to associate declared skills");

    webTestClient
        .post()
        .uri(BASE_PATH + "/" + otherExperienceId + "/associate/declared-skills")
        .header("X-Signed-Context", studentPayload)
        .header("X-Context-Kid", secretKey)
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
        .uri(BASE_PATH + "/" + experienceId + "/associate/declared-skills")
        .header("X-Signed-Context", studentPayload)
        .header("X-Context-Kid", secretKey)
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
        .uri(BASE_PATH + "/" + experienceId + "/associate/declared-skills")
        .header("X-Signed-Context", studentPayload)
        .header("X-Context-Kid", secretKey)
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
        .uri(BASE_PATH + "/" + experienceId + "/associate/declared-skills")
        .header("X-Signed-Context", studentPayload)
        .header("X-Context-Kid", secretKey)
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
    UUID skillId = createDeclaredSkillProgressAs(14, studentPayload, studentSignature);

    BddLogger.when("performing a POST with the duplicated declared skill id");

    String responseBody =
        webTestClient
            .post()
            .uri(BASE_PATH + "/" + experienceId + "/associate/declared-skills")
            .header("X-Signed-Context", studentPayload)
            .header("X-Context-Kid", secretKey)
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
    UUID skillId = createDeclaredSkillProgressAs(15, studentPayload, studentSignature);

    webTestClient
        .post()
        .uri(BASE_PATH + "/" + experienceId + "/associate/declared-skills")
        .header("X-Signed-Context", studentPayload)
        .header("X-Context-Kid", secretKey)
        .header("X-Context-Signature", studentSignature)
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(objectMapper.writeValueAsString(Map.of("idsToAssociate", List.of(skillId))))
        .exchange()
        .expectStatus()
        .isOk();

    BddLogger.when("performing the same association request again");

    webTestClient
        .post()
        .uri(BASE_PATH + "/" + experienceId + "/associate/declared-skills")
        .header("X-Signed-Context", studentPayload)
        .header("X-Context-Kid", secretKey)
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
            .uri(BASE_PATH + "/" + experienceId + "/associate/traces")
            .header("X-Signed-Context", studentPayload)
            .header("X-Context-Kid", secretKey)
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
        .uri(BASE_PATH + "/" + notFoundDeclaredExperienceId + "/associate/traces")
        .header("X-Signed-Context", studentPayload)
        .header("X-Context-Kid", secretKey)
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
        .uri(BASE_PATH + "/" + otherExperienceId + "/associate/traces")
        .header("X-Signed-Context", studentPayload)
        .header("X-Context-Kid", secretKey)
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
        .uri(BASE_PATH + "/" + experienceId + "/associate/traces")
        .header("X-Signed-Context", studentPayload)
        .header("X-Context-Kid", secretKey)
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
        .uri(BASE_PATH + "/" + experienceId + "/associate/traces")
        .header("X-Signed-Context", studentPayload)
        .header("X-Context-Kid", secretKey)
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
        .uri(BASE_PATH + "/" + experienceId + "/associate/traces")
        .header("X-Signed-Context", studentPayload)
        .header("X-Context-Kid", secretKey)
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
            .uri(BASE_PATH + "/" + experienceId + "/associate/traces")
            .header("X-Signed-Context", studentPayload)
            .header("X-Context-Kid", secretKey)
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
        .uri(BASE_PATH + "/" + experienceId + "/associate/traces")
        .header("X-Signed-Context", studentPayload)
        .header("X-Context-Kid", secretKey)
        .header("X-Context-Signature", studentSignature)
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(objectMapper.writeValueAsString(Map.of("idsToAssociate", List.of(traceId))))
        .exchange()
        .expectStatus()
        .isOk();

    BddLogger.when("performing the same association request again");

    webTestClient
        .post()
        .uri(BASE_PATH + "/" + experienceId + "/associate/traces")
        .header("X-Signed-Context", studentPayload)
        .header("X-Context-Kid", secretKey)
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

  private UUID associateExperienceWithTraceAndGetAssociationId(String experienceId, UUID traceId)
      throws Exception {
    String responseBody =
        webTestClient
            .post()
            .uri(BASE_PATH + "/" + experienceId + "/associate/traces")
            .header("X-Signed-Context", studentPayload)
            .header("X-Context-Kid", secretKey)
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

  @Test
  void shouldDeleteSingleTraceAssociationSuccessfully() throws Exception {
    BddLogger.given("a declared experience associated with a trace");

    String experienceId = createDeclaredExperienceAs(studentPayload, studentSignature);
    UUID traceId = createTraceAs("Trace to unassociate", studentPayload, studentSignature);
    UUID associationId = associateExperienceWithTraceAndGetAssociationId(experienceId, traceId);

    BddLogger.when("performing a DELETE to unassociate the trace");

    webTestClient
        .method(HttpMethod.DELETE)
        .uri(BASE_PATH + "/" + experienceId + "/associations")
        .header("X-Signed-Context", studentPayload)
        .header("X-Context-Kid", secretKey)
        .header("X-Context-Signature", studentSignature)
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(objectMapper.writeValueAsString(Map.of("idsToDelete", List.of(associationId))))
        .exchange()
        .expectStatus()
        .isNoContent();

    BddLogger.then("the trace association should no longer appear on the experience");

    webTestClient
        .get()
        .uri(BASE_PATH + "/" + experienceId + "/associations")
        .header("X-Signed-Context", studentPayload)
        .header("X-Context-Kid", secretKey)
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
        .uri(BASE_PATH + "/" + experienceId + "/associations")
        .header("X-Signed-Context", studentPayload)
        .header("X-Context-Kid", secretKey)
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
            .uri(BASE_PATH + "/" + experienceId + "/associations")
            .header("X-Signed-Context", studentPayload)
            .header("X-Context-Kid", secretKey)
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
    UUID skillId = createDeclaredSkillProgressAs(16, studentPayload, studentSignature);

    UUID traceAssociationId =
        associateExperienceWithTraceAndGetAssociationId(experienceId, traceId);

    webTestClient
        .post()
        .uri(BASE_PATH + "/" + experienceId + "/associate/declared-skills")
        .header("X-Signed-Context", studentPayload)
        .header("X-Context-Kid", secretKey)
        .header("X-Context-Signature", studentSignature)
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(objectMapper.writeValueAsString(Map.of("idsToAssociate", List.of(skillId))))
        .exchange()
        .expectStatus()
        .isOk();

    BddLogger.when("performing a DELETE to unassociate only the trace");

    webTestClient
        .method(HttpMethod.DELETE)
        .uri(BASE_PATH + "/" + experienceId + "/associations")
        .header("X-Signed-Context", studentPayload)
        .header("X-Context-Kid", secretKey)
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
        .uri(BASE_PATH + "/" + experienceId + "/associations")
        .header("X-Signed-Context", studentPayload)
        .header("X-Context-Kid", secretKey)
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
        .uri(BASE_PATH + "/" + experience1Id + "/associations")
        .header("X-Signed-Context", studentPayload)
        .header("X-Context-Kid", secretKey)
        .header("X-Context-Signature", studentSignature)
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(objectMapper.writeValueAsString(Map.of("idsToDelete", List.of(association1))))
        .exchange()
        .expectStatus()
        .isNoContent();

    BddLogger.then("the trace should still be associated with the second experience");

    webTestClient
        .get()
        .uri(BASE_PATH + "/" + experience2Id + "/associations")
        .header("X-Signed-Context", studentPayload)
        .header("X-Context-Kid", secretKey)
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
        .uri(BASE_PATH + "/" + notFoundDeclaredExperienceId + "/associations")
        .header("X-Signed-Context", studentPayload)
        .header("X-Context-Kid", secretKey)
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
        .uri(BASE_PATH + "/" + otherExperienceId + "/associations")
        .header("X-Signed-Context", studentPayload)
        .header("X-Context-Kid", secretKey)
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
  void shouldReturn403WhenAssociationIdDoesNotBelongToExperience() throws Exception {
    BddLogger.given("a declared experience with no matching association for the given id");

    String experienceId = createDeclaredExperienceAs(studentPayload, studentSignature);

    BddLogger.when("performing a DELETE with an unknown association id");

    webTestClient
        .method(HttpMethod.DELETE)
        .uri(BASE_PATH + "/" + experienceId + "/associations")
        .header("X-Signed-Context", studentPayload)
        .header("X-Context-Kid", secretKey)
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

    BddLogger.then("it should return forbidden, since the association does not belong to it");
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
        .uri(BASE_PATH + "/" + experienceId + "/associations")
        .header("X-Signed-Context", studentPayload)
        .header("X-Context-Kid", secretKey)
        .header("X-Context-Signature", studentSignature)
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(
            objectMapper.writeValueAsString(
                Map.of("idsToDelete", List.of(associationId, UUID.randomUUID()))))
        .exchange()
        .expectStatus()
        .isForbidden();

    BddLogger.then("the valid association should not have been deleted");

    webTestClient
        .get()
        .uri(BASE_PATH + "/" + experienceId + "/associations")
        .header("X-Signed-Context", studentPayload)
        .header("X-Context-Kid", secretKey)
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
        .uri(BASE_PATH + "/" + experienceId + "/associations")
        .header("X-Signed-Context", studentPayload)
        .header("X-Context-Kid", secretKey)
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
        .uri(BASE_PATH + "/" + experienceId + "/associations")
        .header("X-Signed-Context", studentPayload)
        .header("X-Context-Kid", secretKey)
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
        .uri(BASE_PATH + "/" + experienceId + "/associations")
        .header("X-Signed-Context", studentPayload)
        .header("X-Context-Kid", secretKey)
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
        .uri(BASE_PATH + "/" + experienceId + "/associations")
        .header("X-Signed-Context", studentPayload)
        .header("X-Context-Kid", secretKey)
        .header("X-Context-Signature", studentSignature)
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(objectMapper.writeValueAsString(Map.of("idsToDelete", List.of(associationId))))
        .exchange()
        .expectStatus()
        .isNoContent();

    BddLogger.then("the trace should no longer show the experience in its own associations");

    webTestClient
        .get()
        .uri("/me/traces/" + traceId + "/associations")
        .header("X-Signed-Context", studentPayload)
        .header("X-Context-Kid", secretKey)
        .header("X-Context-Signature", studentSignature)
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody()
        .jsonPath("$.declaredExperienceAssociations")
        .isEmpty();
  }
}
