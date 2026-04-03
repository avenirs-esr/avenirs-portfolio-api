package fr.avenirsesr.portfolio.student.progress.declared.experience.application.adapter.controller;

import static org.hamcrest.Matchers.*;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import fr.avenirsesr.portfolio.shared.infrastructure.ContainerConfigurationTest;
import fr.avenirsesr.portfolio.shared.infrastructure.adapter.seeder.SeederRunner;
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
        .isEqualTo("My Experience");
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
}
