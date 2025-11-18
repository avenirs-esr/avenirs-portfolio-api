package fr.avenirsesr.portfolio.selfknowledge.application.adapter.controller;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.avenirsesr.portfolio.common.language.domain.model.enums.ELanguage;
import fr.avenirsesr.portfolio.common.security.infrastructure.adapter.model.AvenirsSecurityHeaders;
import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import fr.avenirsesr.portfolio.shared.infrastructure.adapter.seeder.SeederRunner;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SelfKnowledgeControllerIT {

  private static final String BASE_PATH = "/me/self-knowledge";
  private static final String CATEGORIES_BASE_PATH = BASE_PATH + "/categories";
  private static final String CATEGORIES_AVAILABLE_BASE_PATH = CATEGORIES_BASE_PATH + "/available";

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
  static void setup(@Autowired SeederRunner seederRunner) {
    seederRunner.run();
  }

  private String getAvailableCategoryId() throws Exception {
    MvcResult availableResult =
        mockMvc
            .perform(
                get(CATEGORIES_AVAILABLE_BASE_PATH)
                    .header(HttpHeaders.ACCEPT_LANGUAGE, ELanguage.FRENCH.getCode())
                    .header(AvenirsSecurityHeaders.SIGNED_CONTEXT, studentPayload)
                    .header(AvenirsSecurityHeaders.CONTEXT_KID, secretKey)
                    .header(AvenirsSecurityHeaders.CONTEXT_SIGNATURE, studentSignature)
                    .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn();

    String availableBody = availableResult.getResponse().getContentAsString();
    JsonNode availableJson = objectMapper.readTree(availableBody);

    return availableJson.get(0).get("id").asText();
  }

  @Test
  void shouldReturnSelfKnowledgeCategoriesForStudent() throws Exception {
    BddLogger.given("the " + CATEGORIES_BASE_PATH + " endpoint");
    BddLogger.when("performing a GET as a student");
    BddLogger.then("it should return the self knowledge categories linked to the student");

    mockMvc
        .perform(
            get(CATEGORIES_BASE_PATH)
                .header(HttpHeaders.ACCEPT_LANGUAGE, ELanguage.FRENCH.getCode())
                .header(AvenirsSecurityHeaders.SIGNED_CONTEXT, studentPayload)
                .header(AvenirsSecurityHeaders.CONTEXT_KID, secretKey)
                .header(AvenirsSecurityHeaders.CONTEXT_SIGNATURE, studentSignature)
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$[0]").exists())
        .andExpect(jsonPath("$[0].id", notNullValue()))
        .andExpect(jsonPath("$[0].title", notNullValue()))
        .andExpect(jsonPath("$[0].description", notNullValue()))
        .andExpect(jsonPath("$[0].type", notNullValue()))
        .andExpect(jsonPath("$[0].type", is("STRENGTHS")));
  }

  @Test
  void shouldReturn404WhenUserNotFoundOnCategoriesEndpoint() throws Exception {
    BddLogger.given("the " + CATEGORIES_BASE_PATH + " endpoint");
    BddLogger.when("performing a GET and the user is not found");
    BddLogger.then("it should return a 404");
    mockMvc
        .perform(
            get(CATEGORIES_BASE_PATH)
                .header("X-Signed-Context", unknownUserPayload)
                .header("X-Context-Kid", secretKey)
                .header("X-Context-Signature", unknownUserSignature))
        .andExpect(status().isNotFound())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.message").value("User not found"))
        .andExpect(jsonPath("$.code").value("USER_NOT_FOUND"));
  }

  @Test
  void shouldReturnSelfKnowledgeCategoriesAvailableForStudent() throws Exception {
    BddLogger.given("the " + CATEGORIES_AVAILABLE_BASE_PATH + " endpoint");
    BddLogger.when("performing a GET as a student");
    BddLogger.then("it should return the self knowledge categories not linked to the student");

    mockMvc
        .perform(
            get(CATEGORIES_AVAILABLE_BASE_PATH)
                .header(HttpHeaders.ACCEPT_LANGUAGE, ELanguage.FRENCH.getCode())
                .header(AvenirsSecurityHeaders.SIGNED_CONTEXT, studentPayload)
                .header(AvenirsSecurityHeaders.CONTEXT_KID, secretKey)
                .header(AvenirsSecurityHeaders.CONTEXT_SIGNATURE, studentSignature)
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$[0]").exists())
        .andExpect(jsonPath("$[0].id", notNullValue()))
        .andExpect(jsonPath("$[0].title", notNullValue()))
        .andExpect(jsonPath("$[0].description", notNullValue()))
        .andExpect(jsonPath("$[0].type", notNullValue()))
        .andExpect(jsonPath("$[0].type", is("MOTIVATION")));
  }

  @Test
  void shouldReturn404WhenUserNotFoundOnCategoriesAvailableEndpoint() throws Exception {
    BddLogger.given("the " + CATEGORIES_AVAILABLE_BASE_PATH + " endpoint");
    BddLogger.when("performing a GET and the user is not found");
    BddLogger.then("it should return a 404");
    mockMvc
        .perform(
            get(CATEGORIES_AVAILABLE_BASE_PATH)
                .header("X-Signed-Context", unknownUserPayload)
                .header("X-Context-Kid", secretKey)
                .header("X-Context-Signature", unknownUserSignature))
        .andExpect(status().isNotFound())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.message").value("User not found"))
        .andExpect(jsonPath("$.code").value("USER_NOT_FOUND"));
  }

  @Test
  void shouldAssociateSelfKnowledgeCategoriesToStudent() throws Exception {
    BddLogger.given("the " + CATEGORIES_BASE_PATH + " POST endpoint");
    BddLogger.and("an available self knowledge category for this student");
    BddLogger.when("performing a POST as a student to associate categories");
    BddLogger.then("it should associate the given categories and return a success message");

    mockMvc
        .perform(
            post(CATEGORIES_BASE_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    objectMapper.writeValueAsString(java.util.List.of(getAvailableCategoryId())))
                .header(HttpHeaders.ACCEPT_LANGUAGE, ELanguage.FRENCH.getCode())
                .header(AvenirsSecurityHeaders.SIGNED_CONTEXT, studentPayload)
                .header(AvenirsSecurityHeaders.CONTEXT_KID, secretKey)
                .header(AvenirsSecurityHeaders.CONTEXT_SIGNATURE, studentSignature))
        .andExpect(status().isOk())
        .andExpect(content().string("Categories successfully associated with user"));
  }

  @Test
  void shouldReturn404WhenUserNotFoundOnCategoriesPostEndpoint() throws Exception {
    BddLogger.given("the " + CATEGORIES_BASE_PATH + " POST endpoint");
    BddLogger.when("performing a POST and the user is not found");
    BddLogger.then("it should return a 404 USER_NOT_FOUND");

    mockMvc
        .perform(
            post(CATEGORIES_BASE_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    objectMapper.writeValueAsString(java.util.List.of(getAvailableCategoryId())))
                .header("X-Signed-Context", unknownUserPayload)
                .header("X-Context-Kid", secretKey)
                .header("X-Context-Signature", unknownUserSignature))
        .andExpect(status().isNotFound())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.message").value("User not found"))
        .andExpect(jsonPath("$.code").value("USER_NOT_FOUND"));
  }

  @Test
  void shouldReturn400WhenCategoryIdNotAvailableOnCategoriesPostEndpoint() throws Exception {
    BddLogger.given("the " + CATEGORIES_BASE_PATH + " POST endpoint");
    BddLogger.when("performing a POST with a category id that is not available for the student");
    BddLogger.then(
        "it should return a 400 SELF_KNOWLEDGE_CATEGORY_NOT_AVAILABLE (business constraint)");

    String randomCategoryId = "00000000-0000-0000-0000-000000000000";

    mockMvc
        .perform(
            post(CATEGORIES_BASE_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(java.util.List.of(randomCategoryId)))
                .header(AvenirsSecurityHeaders.SIGNED_CONTEXT, studentPayload)
                .header(AvenirsSecurityHeaders.CONTEXT_KID, secretKey)
                .header(AvenirsSecurityHeaders.CONTEXT_SIGNATURE, studentSignature))
        .andExpect(status().isBadRequest())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.message").value("Self knowledge category not available"))
        .andExpect(jsonPath("$.code").value("SELF_KNOWLEDGE_CATEGORY_NOT_AVAILABLE"));
  }

  @Test
  void shouldReturn400WhenCategoryListIsEmptyOnCategoriesPostEndpoint() throws Exception {
    BddLogger.given("the " + CATEGORIES_BASE_PATH + " POST endpoint");
    BddLogger.when("performing a POST with an empty category list");
    BddLogger.then("it should return a 400 SELF_KNOWLEDGE_CATEGORY_LIST_EMPTY");

    mockMvc
        .perform(
            post(CATEGORIES_BASE_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(java.util.List.of()))
                .header(AvenirsSecurityHeaders.SIGNED_CONTEXT, studentPayload)
                .header(AvenirsSecurityHeaders.CONTEXT_KID, secretKey)
                .header(AvenirsSecurityHeaders.CONTEXT_SIGNATURE, studentSignature))
        .andExpect(status().isBadRequest())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.message").value("Self knowledge category list is empty"))
        .andExpect(jsonPath("$.code").value("SELF_KNOWLEDGE_CATEGORY_LIST_EMPTY"));
  }
}
