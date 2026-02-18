package fr.avenirsesr.portfolio.activity.application.adapter.controller;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.avenirsesr.portfolio.activity.domain.model.enums.EActivityThematic;
import fr.avenirsesr.portfolio.common.security.infrastructure.adapter.model.AvenirsSecurityHeaders;
import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import fr.avenirsesr.portfolio.shared.infrastructure.ContainerConfigurationTest;
import fr.avenirsesr.portfolio.shared.infrastructure.adapter.seeder.SeederRunner;
import java.util.Iterator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

class ActivityControllerIT extends ContainerConfigurationTest {

  private static final String BASE_PATH = "/me/activities";
  private static final String NAVIGATION_BASE_PATH = BASE_PATH + "/navigation";

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

  @Test
  void shouldReturnActivitiesNavigationAsMapAndValidateItemShapeWhenPresent() throws Exception {
    BddLogger.given("the " + NAVIGATION_BASE_PATH + " endpoint");
    BddLogger.when("performing a GET");
    BddLogger.then(
        "it should return a JSON object (map) and validate item shape if at least one activity"
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
            .andExpect(jsonPath("$").isMap())
            .andReturn();

    JsonNode root = objectMapper.readTree(mvcResult.getResponse().getContentAsString());

    assertNotNull(root);
    assertTrue(root.isObject(), "root should be a JSON object (map)");

    Iterator<String> fieldNames = root.fieldNames();
    JsonNode firstItem = null;

    while (fieldNames.hasNext() && firstItem == null) {
      String thematicKey = fieldNames.next();
      JsonNode listNode = root.get(thematicKey);

      if (listNode != null && listNode.isArray() && !listNode.isEmpty()) {
        firstItem = listNode.get(0);
      }
    }

    if (firstItem == null) {
      return;
    }

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
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.data").isArray())
        .andExpect(jsonPath("$.page").exists())
        .andExpect(jsonPath("$.page.page").value(0))
        .andExpect(jsonPath("$.page.pageSize").value(10))
        .andExpect(jsonPath("$.page.totalElements", greaterThanOrEqualTo(0)));
  }

  @Test
  void shouldReturnActivitiesViewFilteredByThematic() throws Exception {
    BddLogger.given("the " + BASE_PATH + " endpoint with thematic filter");
    BddLogger.when("performing a GET with thematic filter");
    BddLogger.then("it should return filtered paged activities");

    mockMvc
        .perform(
            get(BASE_PATH)
                .param("thematic", EActivityThematic.ABOUT_ME.name())
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
  void shouldReturnLatestActivitiesView() throws Exception {
    BddLogger.given("the /me/activities/latest endpoint");
    BddLogger.when("performing a GET request for latest activities");
    BddLogger.then("it should return paged latest activities");

    mockMvc
        .perform(
            get("/me/activities/latest")
                .param("page", "0")
                .param("pageSize", "10")
                .header(AvenirsSecurityHeaders.SIGNED_CONTEXT, studentPayload)
                .header(AvenirsSecurityHeaders.CONTEXT_KID, secretKey)
                .header(AvenirsSecurityHeaders.CONTEXT_SIGNATURE, studentSignature)
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.data").isArray())
        .andExpect(jsonPath("$.page").exists())
        .andExpect(jsonPath("$.page.page").value(0))
        .andExpect(jsonPath("$.page.pageSize").value(10))
        .andExpect(jsonPath("$.page.totalElements", greaterThanOrEqualTo(0)));
  }
}
