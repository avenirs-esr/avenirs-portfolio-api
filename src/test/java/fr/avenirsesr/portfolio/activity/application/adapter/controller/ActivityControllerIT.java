package fr.avenirsesr.portfolio.activity.application.adapter.controller;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

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
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

class ActivityControllerIT extends ContainerConfigurationTest {

  private static final String BASE_PATH = "/me/activities";
  private static final String NAVIGATION_BASE_PATH = BASE_PATH + "/navigation";

  private static final String DETAIL_BASE_PATH = BASE_PATH + "/{activityId}";

  @Autowired private MockMvc mockMvc;
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
    BddLogger.then(
        "it should return a JSON array of menus and validate item shape if at least one activity"
            + " exists");

    var mvcResult =
        mockMvc
            .perform(
                get(NAVIGATION_BASE_PATH)
                    .header(AvenirsSecurityHeaders.SIGNED_CONTEXT, studentPayload)
                    .header(AvenirsSecurityHeaders.CONTEXT_KID, secretKey)
                    .header(AvenirsSecurityHeaders.CONTEXT_SIGNATURE, studentSignature)
                    .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$").isArray())
            .andReturn();

    JsonNode root = objectMapper.readTree(mvcResult.getResponse().getContentAsString());

    assertNotNull(root);
    assertTrue(root.isArray(), "root should be a JSON array");

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

    assertTrue(firstMenuWithItems.hasNonNull("title"), "menu should have non-null 'title'");
    assertTrue(firstMenuWithItems.get("title").isTextual(), "'title' should be a string");

    JsonNode firstItem = firstMenuWithItems.get("items").get(0);
    assertNotNull(firstItem, "first item should exist");

    assertTrue(firstItem.hasNonNull("id"), "item should have non-null 'id'");
    assertTrue(firstItem.hasNonNull("title"), "item should have non-null 'title'");
    assertTrue(firstItem.get("id").isTextual(), "'id' should be a string");
    assertTrue(firstItem.get("title").isTextual(), "'title' should be a string");
  }

  @Test
  void shouldReturnActivitiesView() throws Exception {
    BddLogger.given("the " + BASE_PATH + " endpoint");
    BddLogger.when("performing a GET");
    BddLogger.then("it should return paged activities");

    mockMvc
        .perform(
            get(BASE_PATH)
                .param("page", "0")
                .param("pageSize", "10")
                .header(AvenirsSecurityHeaders.SIGNED_CONTEXT, studentPayload)
                .header(AvenirsSecurityHeaders.CONTEXT_KID, secretKey)
                .header(AvenirsSecurityHeaders.CONTEXT_SIGNATURE, studentSignature)
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON));
  }

  @Test
  void shouldReturnActivitiesViewFilteredByThematic() throws Exception {
    BddLogger.given("the " + BASE_PATH + " endpoint with thematic filter");
    BddLogger.when("performing a GET with thematic filter");
    BddLogger.then("it should return filtered paged activities");

    mockMvc
        .perform(
            get(BASE_PATH)
                .param("thematic", EActivityThematic.SELF_KNOWLEDGE.name())
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

  @Test
  void shouldGetActivityDetail() throws Exception {
    BddLogger.given("an existing activity created by the logged-in student");
    UUID activityId = getFirstActivityIdFromOverview();

    BddLogger.when("performing GET /detail");
    BddLogger.then("it should return activity detail");

    mockMvc
        .perform(
            get(DETAIL_BASE_PATH, activityId)
                .header(HttpHeaders.ACCEPT_LANGUAGE, ELanguage.FRENCH.getCode())
                .header(AvenirsSecurityHeaders.SIGNED_CONTEXT, studentPayload)
                .header(AvenirsSecurityHeaders.CONTEXT_KID, secretKey)
                .header(AvenirsSecurityHeaders.CONTEXT_SIGNATURE, studentSignature)
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.id").value(activityId.toString()))
        .andExpect(jsonPath("$.title", notNullValue()))
        .andExpect(jsonPath("$.summary", notNullValue()))
        .andExpect(jsonPath("$.createdAt", notNullValue()))
        .andExpect(jsonPath("$.updatedAt", notNullValue()));
  }

  private UUID getFirstActivityIdFromOverview() throws Exception {
    var mvc =
        mockMvc
            .perform(
                get(BASE_PATH)
                    .header(AvenirsSecurityHeaders.SIGNED_CONTEXT, studentPayload)
                    .header(AvenirsSecurityHeaders.CONTEXT_KID, secretKey)
                    .header(AvenirsSecurityHeaders.CONTEXT_SIGNATURE, studentSignature)
                    .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn();

    JsonNode json = objectMapper.readTree(mvc.getResponse().getContentAsString());
    JsonNode activities = json.get("data");
    if (activities == null || !activities.isArray() || activities.size() == 0) {
      throw new IllegalStateException("Seeder returned no activity in /overview");
    }
    return UUID.fromString(activities.get(0).get("id").asText());
  }

  @Test
  void shouldReturn404WhenActivityDetailNotFound() throws Exception {
    BddLogger.given("a non-existing activity id");
    UUID unknownId = UUID.randomUUID();

    BddLogger.when("performing GET /detail");
    BddLogger.then("it should return 404 ACTIVITY_NOT_FOUND");

    mockMvc
        .perform(
            get(DETAIL_BASE_PATH, unknownId)
                .header(HttpHeaders.ACCEPT_LANGUAGE, ELanguage.FRENCH.getCode())
                .header(AvenirsSecurityHeaders.SIGNED_CONTEXT, studentPayload)
                .header(AvenirsSecurityHeaders.CONTEXT_KID, secretKey)
                .header(AvenirsSecurityHeaders.CONTEXT_SIGNATURE, studentSignature)
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.code", is("ACTIVITY_NOT_FOUND")));
  }
}
