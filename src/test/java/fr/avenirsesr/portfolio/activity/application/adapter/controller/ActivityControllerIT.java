package fr.avenirsesr.portfolio.activity.application.adapter.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.avenirsesr.portfolio.activity.domain.model.enums.EActivityThematic;
import fr.avenirsesr.portfolio.common.language.domain.model.enums.ELanguage;
import fr.avenirsesr.portfolio.common.security.infrastructure.adapter.model.AvenirsSecurityHeaders;
import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import fr.avenirsesr.portfolio.shared.infrastructure.ContainerConfigurationTest;
import fr.avenirsesr.portfolio.shared.infrastructure.adapter.seeder.SeederRunner;
import java.util.UUID;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

class ActivityControllerIT extends ContainerConfigurationTest {

  private static final String BASE_PATH = "/me/activities";
  private static final String NAVIGATION_BASE_PATH = BASE_PATH + "/navigation";
  private static final String DETAIL_BASE_PATH = BASE_PATH + "/{activityId}";

  @Autowired private WebTestClient webTestClient;
  @Autowired private ObjectMapper objectMapper;

  @Value("${hmac.secret-key}")
  private String secretKey;

  @Value("${user.student.payload}")
  private String studentPayload;

  @Value("${user.student.signature}")
  private String studentSignature;

  @BeforeAll
  void setup(@Autowired SeederRunner seederRunner) {
    seederRunner.run();
  }

  @Test
  void shouldReturnActivitiesNavigationAsArrayAndValidateItemShapeWhenPresent() throws Exception {

    BddLogger.given("the " + NAVIGATION_BASE_PATH + " endpoint");
    BddLogger.when("performing a GET");
    BddLogger.then("it should return a JSON array");

    String body =
        webTestClient
            .get()
            .uri(NAVIGATION_BASE_PATH)
            .header(AvenirsSecurityHeaders.SIGNED_CONTEXT, studentPayload)
            .header(AvenirsSecurityHeaders.CONTEXT_KID, secretKey)
            .header(AvenirsSecurityHeaders.CONTEXT_SIGNATURE, studentSignature)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody(String.class)
            .returnResult()
            .getResponseBody();

    JsonNode root = objectMapper.readTree(body);

    if (root == null || !root.isArray()) {
      throw new AssertionError("root should be a JSON array");
    }

    if (root.isEmpty()) {
      return;
    }

    JsonNode firstMenuWithItems = null;
    for (JsonNode menu : root) {
      JsonNode items = menu.get("items");
      if (items != null && items.isArray() && !items.isEmpty()) {
        firstMenuWithItems = menu;
        break;
      }
    }

    if (firstMenuWithItems == null) {
      return;
    }

    if (!firstMenuWithItems.hasNonNull("title") || !firstMenuWithItems.get("title").isTextual()) {
      throw new AssertionError("menu should have textual title");
    }

    JsonNode firstItem = firstMenuWithItems.get("items").get(0);

    if (!firstItem.hasNonNull("id")
        || !firstItem.hasNonNull("title")
        || !firstItem.get("id").isTextual()
        || !firstItem.get("title").isTextual()) {
      throw new AssertionError("invalid item shape");
    }
  }

  @Test
  void shouldReturnActivitiesView() {
    BddLogger.given("the " + BASE_PATH + " endpoint");
    BddLogger.when("performing a GET");
    BddLogger.then("it should return paged activities");

    webTestClient
        .get()
        .uri(
            uriBuilder ->
                uriBuilder
                    .path(BASE_PATH)
                    .queryParam("page", "0")
                    .queryParam("pageSize", "10")
                    .build())
        .header(AvenirsSecurityHeaders.SIGNED_CONTEXT, studentPayload)
        .header(AvenirsSecurityHeaders.CONTEXT_KID, secretKey)
        .header(AvenirsSecurityHeaders.CONTEXT_SIGNATURE, studentSignature)
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody()
        .jsonPath("$.data")
        .exists();
  }

  @Test
  void shouldReturnActivitiesViewFilteredByThematic() {
    BddLogger.given("the " + BASE_PATH + " endpoint with thematic filter");
    BddLogger.when("performing a GET with thematic filter");
    BddLogger.then("it should return filtered paged activities");

    webTestClient
        .get()
        .uri(
            uriBuilder ->
                uriBuilder
                    .path(BASE_PATH)
                    .queryParam("thematic", EActivityThematic.SELF_KNOWLEDGE.name())
                    .queryParam("page", "0")
                    .queryParam("pageSize", "8")
                    .build())
        .header(AvenirsSecurityHeaders.SIGNED_CONTEXT, studentPayload)
        .header(AvenirsSecurityHeaders.CONTEXT_KID, secretKey)
        .header(AvenirsSecurityHeaders.CONTEXT_SIGNATURE, studentSignature)
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody()
        .jsonPath("$.data")
        .isArray()
        .jsonPath("$.page.page")
        .isEqualTo(0)
        .jsonPath("$.page.pageSize")
        .isEqualTo(8)
        .jsonPath("$.page.totalElements")
        .exists();
  }

  @Test
  void shouldGetActivityDetail() throws Exception {
    BddLogger.given("an existing activity");
    UUID activityId = getFirstActivityIdFromOverview();

    webTestClient
        .get()
        .uri(DETAIL_BASE_PATH, activityId)
        .header("Accept-Language", ELanguage.FRENCH.getCode())
        .header(AvenirsSecurityHeaders.SIGNED_CONTEXT, studentPayload)
        .header(AvenirsSecurityHeaders.CONTEXT_KID, secretKey)
        .header(AvenirsSecurityHeaders.CONTEXT_SIGNATURE, studentSignature)
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody()
        .jsonPath("$.id")
        .isEqualTo(activityId.toString())
        .jsonPath("$.title")
        .exists()
        .jsonPath("$.summary")
        .exists()
        .jsonPath("$.createdAt")
        .exists()
        .jsonPath("$.updatedAt")
        .exists();
  }

  private UUID getFirstActivityIdFromOverview() throws Exception {
    String body =
        webTestClient
            .get()
            .uri(BASE_PATH)
            .header(AvenirsSecurityHeaders.SIGNED_CONTEXT, studentPayload)
            .header(AvenirsSecurityHeaders.CONTEXT_KID, secretKey)
            .header(AvenirsSecurityHeaders.CONTEXT_SIGNATURE, studentSignature)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody(String.class)
            .returnResult()
            .getResponseBody();

    JsonNode json = objectMapper.readTree(body);
    JsonNode activities = json.get("data");

    if (activities == null || !activities.isArray() || activities.isEmpty()) {
      throw new IllegalStateException("Seeder returned no activity");
    }

    return UUID.fromString(activities.get(0).get("id").asText());
  }

  @Test
  void shouldReturn404WhenActivityDetailNotFound() {
    UUID unknownId = UUID.randomUUID();

    webTestClient
        .get()
        .uri(DETAIL_BASE_PATH, unknownId)
        .header("Accept-Language", ELanguage.FRENCH.getCode())
        .header(AvenirsSecurityHeaders.SIGNED_CONTEXT, studentPayload)
        .header(AvenirsSecurityHeaders.CONTEXT_KID, secretKey)
        .header(AvenirsSecurityHeaders.CONTEXT_SIGNATURE, studentSignature)
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectStatus()
        .isNotFound()
        .expectBody()
        .jsonPath("$.code")
        .isEqualTo("ACTIVITY_NOT_FOUND");
  }
}
