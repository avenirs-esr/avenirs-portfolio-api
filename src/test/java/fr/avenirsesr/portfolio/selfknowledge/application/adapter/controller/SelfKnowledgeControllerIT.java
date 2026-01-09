package fr.avenirsesr.portfolio.selfknowledge.application.adapter.controller;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.avenirsesr.portfolio.common.language.domain.model.enums.ELanguage;
import fr.avenirsesr.portfolio.common.security.infrastructure.adapter.model.AvenirsSecurityHeaders;
import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import fr.avenirsesr.portfolio.shared.infrastructure.ContainerConfigurationTest;
import fr.avenirsesr.portfolio.shared.infrastructure.adapter.seeder.SeederRunner;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

class SelfKnowledgeControllerIT extends ContainerConfigurationTest {

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
  void setup(@Autowired SeederRunner seederRunner) {
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

  private String getLinkedCategoryId() throws Exception {
    String categoryId = getAvailableCategoryId();

    mockMvc
        .perform(
            post(CATEGORIES_BASE_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(List.of(categoryId)))
                .header(HttpHeaders.ACCEPT_LANGUAGE, ELanguage.FRENCH.getCode())
                .header(AvenirsSecurityHeaders.SIGNED_CONTEXT, studentPayload)
                .header(AvenirsSecurityHeaders.CONTEXT_KID, secretKey)
                .header(AvenirsSecurityHeaders.CONTEXT_SIGNATURE, studentSignature))
        .andExpect(status().isOk());

    return categoryId;
  }

  private MvcResult createElement(
      String linkedCategoryId, String title, String description, int rating) throws Exception {
    String body =
        """
        {
          "title": "%s",
          "description": "%s",
          "rating": %d
        }
        """
            .formatted(title, description, rating);

    return mockMvc
        .perform(
            post(BASE_PATH + "/{categoryId}/elements", linkedCategoryId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .header(HttpHeaders.ACCEPT_LANGUAGE, ELanguage.FRENCH.getCode())
                .header(AvenirsSecurityHeaders.SIGNED_CONTEXT, studentPayload)
                .header(AvenirsSecurityHeaders.CONTEXT_KID, secretKey)
                .header(AvenirsSecurityHeaders.CONTEXT_SIGNATURE, studentSignature)
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.id", notNullValue()))
        .andExpect(jsonPath("$.title", is(title)))
        .andExpect(jsonPath("$.description", is(description)))
        .andExpect(jsonPath("$.rating", is(rating)))
        .andReturn();
  }

  private String extractId(MvcResult result) throws Exception {
    JsonNode json = objectMapper.readTree(result.getResponse().getContentAsString());
    return json.get("id").asText();
  }

  @Test
  void shouldReturnSelfKnowledgeCategoriesForStudent() throws Exception {
    BddLogger.given("the " + CATEGORIES_BASE_PATH + " endpoint");
    BddLogger.when("performing a GET as student");
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
                .content(objectMapper.writeValueAsString(List.of(getAvailableCategoryId())))
                .header(HttpHeaders.ACCEPT_LANGUAGE, ELanguage.FRENCH.getCode())
                .header(AvenirsSecurityHeaders.SIGNED_CONTEXT, studentPayload)
                .header(AvenirsSecurityHeaders.CONTEXT_KID, secretKey)
                .header(AvenirsSecurityHeaders.CONTEXT_SIGNATURE, studentSignature))
        .andExpect(status().isOk())
        .andExpect(content().string("Categories successfully associated with user"));
  }

  @Test
  void shouldAssociateMultipleSelfKnowledgeCategoriesToStudent() throws Exception {
    BddLogger.given("the " + CATEGORIES_BASE_PATH + " POST endpoint");
    BddLogger.when("performing a POST with multiple available categories");
    BddLogger.then("it should associate them");

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

    JsonNode availableJson =
        objectMapper.readTree(availableResult.getResponse().getContentAsString());

    if (availableJson.size() >= 2) {
      String id1 = availableJson.get(0).get("id").asText();
      String id2 = availableJson.get(1).get("id").asText();

      mockMvc
          .perform(
              post(CATEGORIES_BASE_PATH)
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(List.of(id1, id2)))
                  .header(HttpHeaders.ACCEPT_LANGUAGE, ELanguage.FRENCH.getCode())
                  .header(AvenirsSecurityHeaders.SIGNED_CONTEXT, studentPayload)
                  .header(AvenirsSecurityHeaders.CONTEXT_KID, secretKey)
                  .header(AvenirsSecurityHeaders.CONTEXT_SIGNATURE, studentSignature))
          .andExpect(status().isOk())
          .andExpect(content().string("Categories successfully associated with user"));
    } else {
      mockMvc
          .perform(
              post(CATEGORIES_BASE_PATH)
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(List.of(getAvailableCategoryId())))
                  .header(HttpHeaders.ACCEPT_LANGUAGE, ELanguage.FRENCH.getCode())
                  .header(AvenirsSecurityHeaders.SIGNED_CONTEXT, studentPayload)
                  .header(AvenirsSecurityHeaders.CONTEXT_KID, secretKey)
                  .header(AvenirsSecurityHeaders.CONTEXT_SIGNATURE, studentSignature))
          .andExpect(status().isOk());
    }
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
                .content(objectMapper.writeValueAsString(List.of(getAvailableCategoryId())))
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
                .content(objectMapper.writeValueAsString(List.of(randomCategoryId)))
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
                .content(objectMapper.writeValueAsString(List.of()))
                .header(AvenirsSecurityHeaders.SIGNED_CONTEXT, studentPayload)
                .header(AvenirsSecurityHeaders.CONTEXT_KID, secretKey)
                .header(AvenirsSecurityHeaders.CONTEXT_SIGNATURE, studentSignature))
        .andExpect(status().isBadRequest())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.message").value("Self knowledge category list is empty"))
        .andExpect(jsonPath("$.code").value("SELF_KNOWLEDGE_CATEGORY_LIST_EMPTY"));
  }

  @Test
  void shouldDeleteSelfKnowledgeCategoryForStudent() throws Exception {
    BddLogger.given("the " + CATEGORIES_BASE_PATH + "/{categoryId} DELETE endpoint");
    BddLogger.and("a self knowledge category currently linked to the student");
    String categoryId = getLinkedCategoryId();
    BddLogger.when("performing a DELETE as a student");
    BddLogger.then("it should delete the category link and return a success message");

    mockMvc
        .perform(
            delete(CATEGORIES_BASE_PATH + "/{categoryId}", categoryId)
                .header(HttpHeaders.ACCEPT_LANGUAGE, ELanguage.FRENCH.getCode())
                .header(AvenirsSecurityHeaders.SIGNED_CONTEXT, studentPayload)
                .header(AvenirsSecurityHeaders.CONTEXT_KID, secretKey)
                .header(AvenirsSecurityHeaders.CONTEXT_SIGNATURE, studentSignature))
        .andExpect(status().isOk())
        .andExpect(content().string("Categories successfully deleted"));

    mockMvc
        .perform(
            get(CATEGORIES_BASE_PATH)
                .header(HttpHeaders.ACCEPT_LANGUAGE, ELanguage.FRENCH.getCode())
                .header(AvenirsSecurityHeaders.SIGNED_CONTEXT, studentPayload)
                .header(AvenirsSecurityHeaders.CONTEXT_KID, secretKey)
                .header(AvenirsSecurityHeaders.CONTEXT_SIGNATURE, studentSignature)
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[?(@.id == '%s')]", categoryId).doesNotExist());
  }

  @Test
  void shouldReturn404WhenUserNotFoundOnCategoriesDeleteEndpoint() throws Exception {
    BddLogger.given("the " + CATEGORIES_BASE_PATH + "/{categoryId} DELETE endpoint");
    BddLogger.when("performing a DELETE and the user is not found");
    BddLogger.then("it should return a 404 USER_NOT_FOUND");

    String someCategoryId = UUID.randomUUID().toString();

    mockMvc
        .perform(
            delete(CATEGORIES_BASE_PATH + "/{categoryId}", someCategoryId)
                .header("X-Signed-Context", unknownUserPayload)
                .header("X-Context-Kid", secretKey)
                .header("X-Context-Signature", unknownUserSignature))
        .andExpect(status().isNotFound())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.message").value("User not found"))
        .andExpect(jsonPath("$.code").value("USER_NOT_FOUND"));
  }

  @Test
  void shouldReturn400WhenCategoryDoesNotExistOnCategoriesDeleteEndpoint() throws Exception {
    BddLogger.given("the " + CATEGORIES_BASE_PATH + "/{categoryId} DELETE endpoint");
    BddLogger.when("performing a DELETE with a non existing category id");
    BddLogger.then(
        "it should return a 400 SELF_KNOWLEDGE_CATEGORY_NOT_FOUND (business constraint)");

    String randomCategoryId = UUID.randomUUID().toString();

    mockMvc
        .perform(
            delete(CATEGORIES_BASE_PATH + "/{categoryId}", randomCategoryId)
                .header(AvenirsSecurityHeaders.SIGNED_CONTEXT, studentPayload)
                .header(AvenirsSecurityHeaders.CONTEXT_KID, secretKey)
                .header(AvenirsSecurityHeaders.CONTEXT_SIGNATURE, studentSignature))
        .andExpect(status().isBadRequest())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.message").value("Self knowledge category not found"))
        .andExpect(jsonPath("$.code").value("SELF_KNOWLEDGE_CATEGORY_NOT_FOUND"));
  }

  @Test
  void shouldReturnSelfKnowledgeElementsOfCategory_withDefaultPagination() throws Exception {
    String categoryId = getLinkedCategoryId();

    MvcResult created = createElement(categoryId, "Titre 1", "Desc 1", 3);

    String elementId = extractId(created);

    mockMvc
        .perform(
            get(BASE_PATH + "/{categoryId}/elements", categoryId)
                .header(HttpHeaders.ACCEPT_LANGUAGE, ELanguage.FRENCH.getCode())
                .header(AvenirsSecurityHeaders.SIGNED_CONTEXT, studentPayload)
                .header(AvenirsSecurityHeaders.CONTEXT_KID, secretKey)
                .header(AvenirsSecurityHeaders.CONTEXT_SIGNATURE, studentSignature)
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.data").isArray())
        .andExpect(jsonPath("$.page").exists())
        .andExpect(jsonPath("$.page.page", notNullValue()))
        .andExpect(jsonPath("$.page.pageSize", notNullValue()))
        .andExpect(jsonPath("$.page.totalElements", notNullValue()))
        .andExpect(jsonPath("$.page.totalPages", notNullValue()))
        .andExpect(jsonPath("$.data[?(@.id == '%s')]", elementId).exists());
  }

  @Test
  void shouldReturnSelfKnowledgeElementsOfCategory_withPaginationParams() throws Exception {
    String categoryId = getLinkedCategoryId();

    createElement(categoryId, "Titre A", "Desc A", 2);
    createElement(categoryId, "Titre B", "Desc B", 4);

    mockMvc
        .perform(
            get(BASE_PATH + "/{categoryId}/elements?page=0&pageSize=1", categoryId)
                .header(HttpHeaders.ACCEPT_LANGUAGE, ELanguage.FRENCH.getCode())
                .header(AvenirsSecurityHeaders.SIGNED_CONTEXT, studentPayload)
                .header(AvenirsSecurityHeaders.CONTEXT_KID, secretKey)
                .header(AvenirsSecurityHeaders.CONTEXT_SIGNATURE, studentSignature)
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.data").isArray())
        .andExpect(jsonPath("$.data.length()", is(1)))
        .andExpect(jsonPath("$.page.page", is(0)))
        .andExpect(jsonPath("$.page.pageSize", is(1)));
  }

  @Test
  void shouldReturn404WhenUserNotFoundOnElementsEndpoint() throws Exception {
    String anyCategoryId = UUID.randomUUID().toString();

    mockMvc
        .perform(
            get(BASE_PATH + "/{categoryId}/elements", anyCategoryId)
                .header("X-Signed-Context", unknownUserPayload)
                .header("X-Context-Kid", secretKey)
                .header("X-Context-Signature", unknownUserSignature))
        .andExpect(status().isNotFound())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.message").value("User not found"))
        .andExpect(jsonPath("$.code").value("USER_NOT_FOUND"));
  }

  @Test
  void shouldGetSelfKnowledgeElementDetails() throws Exception {
    String categoryId = getLinkedCategoryId();
    MvcResult created = createElement(categoryId, "Details", "Desc", 5);
    String elementId = extractId(created);

    mockMvc
        .perform(
            get(BASE_PATH + "/element/{elementId}", elementId)
                .header(HttpHeaders.ACCEPT_LANGUAGE, ELanguage.FRENCH.getCode())
                .header(AvenirsSecurityHeaders.SIGNED_CONTEXT, studentPayload)
                .header(AvenirsSecurityHeaders.CONTEXT_KID, secretKey)
                .header(AvenirsSecurityHeaders.CONTEXT_SIGNATURE, studentSignature)
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.id", notNullValue()))
        .andExpect(jsonPath("$.title", is("Details")))
        .andExpect(jsonPath("$.description", is("Desc")))
        .andExpect(jsonPath("$.rating", is(5)));
  }

  @Test
  void shouldReturn404WhenUserNotFoundOnElementDetailsEndpoint() throws Exception {
    String elementId = UUID.randomUUID().toString();

    mockMvc
        .perform(
            get(BASE_PATH + "/element/{elementId}", elementId)
                .header("X-Signed-Context", unknownUserPayload)
                .header("X-Context-Kid", secretKey)
                .header("X-Context-Signature", unknownUserSignature))
        .andExpect(status().isNotFound())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.message").value("User not found"))
        .andExpect(jsonPath("$.code").value("USER_NOT_FOUND"));
  }

  @Test
  void shouldReturn400Or404WhenElementDetailsNotFound() throws Exception {
    String elementId = UUID.randomUUID().toString();

    mockMvc
        .perform(
            get(BASE_PATH + "/element/{elementId}", elementId)
                .header(HttpHeaders.ACCEPT_LANGUAGE, ELanguage.FRENCH.getCode())
                .header(AvenirsSecurityHeaders.SIGNED_CONTEXT, studentPayload)
                .header(AvenirsSecurityHeaders.CONTEXT_KID, secretKey)
                .header(AvenirsSecurityHeaders.CONTEXT_SIGNATURE, studentSignature))
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().is4xxClientError())
        .andExpect(jsonPath("$.code").exists());
  }

  @Test
  void shouldCreateSelfKnowledgeElement() throws Exception {
    String categoryId = getLinkedCategoryId();

    mockMvc
        .perform(
            post(BASE_PATH + "/{categoryId}/elements", categoryId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                      "title": "New",
                      "description": "New desc",
                      "rating": 4
                    }
                    """)
                .header(HttpHeaders.ACCEPT_LANGUAGE, ELanguage.FRENCH.getCode())
                .header(AvenirsSecurityHeaders.SIGNED_CONTEXT, studentPayload)
                .header(AvenirsSecurityHeaders.CONTEXT_KID, secretKey)
                .header(AvenirsSecurityHeaders.CONTEXT_SIGNATURE, studentSignature)
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.id", notNullValue()))
        .andExpect(jsonPath("$.title", is("New")))
        .andExpect(jsonPath("$.description", is("New desc")))
        .andExpect(jsonPath("$.rating", is(4)));
  }

  @Test
  void shouldReturn400WhenCreateSelfKnowledgeElementRequestInvalid() throws Exception {
    String categoryId = getLinkedCategoryId();

    mockMvc
        .perform(
            post(BASE_PATH + "/{categoryId}/elements", categoryId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                      "title": "",
                      "description": "",
                      "rating": -1
                    }
                    """)
                .header(HttpHeaders.ACCEPT_LANGUAGE, ELanguage.FRENCH.getCode())
                .header(AvenirsSecurityHeaders.SIGNED_CONTEXT, studentPayload)
                .header(AvenirsSecurityHeaders.CONTEXT_KID, secretKey)
                .header(AvenirsSecurityHeaders.CONTEXT_SIGNATURE, studentSignature))
        .andExpect(status().isBadRequest());
  }

  @Test
  void shouldReturn404WhenUserNotFoundOnCreateElementEndpoint() throws Exception {
    String categoryId = UUID.randomUUID().toString();

    mockMvc
        .perform(
            post(BASE_PATH + "/{categoryId}/elements", categoryId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                      "title": "X",
                      "description": "Y",
                      "rating": 3
                    }
                    """)
                .header("X-Signed-Context", unknownUserPayload)
                .header("X-Context-Kid", secretKey)
                .header("X-Context-Signature", unknownUserSignature))
        .andExpect(status().isNotFound())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.message").value("User not found"))
        .andExpect(jsonPath("$.code").value("USER_NOT_FOUND"));
  }

  @Test
  void shouldUpdateSelfKnowledgeElement() throws Exception {
    String categoryId = getLinkedCategoryId();
    MvcResult created = createElement(categoryId, "Old", "Old desc", 2);
    String elementId = extractId(created);

    mockMvc
        .perform(
            put(BASE_PATH + "/element/{elementId}", elementId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                      "title": "Updated",
                      "description": "Updated desc",
                      "rating": 5
                    }
                    """)
                .header(HttpHeaders.ACCEPT_LANGUAGE, ELanguage.FRENCH.getCode())
                .header(AvenirsSecurityHeaders.SIGNED_CONTEXT, studentPayload)
                .header(AvenirsSecurityHeaders.CONTEXT_KID, secretKey)
                .header(AvenirsSecurityHeaders.CONTEXT_SIGNATURE, studentSignature)
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.id", notNullValue()))
        .andExpect(jsonPath("$.title", is("Updated")))
        .andExpect(jsonPath("$.description", is("Updated desc")))
        .andExpect(jsonPath("$.rating", is(5)));
  }

  @Test
  void shouldReturn400WhenUpdateSelfKnowledgeElementRequestInvalid() throws Exception {
    String categoryId = getLinkedCategoryId();
    MvcResult created = createElement(categoryId, "Old", "Old desc", 2);
    String elementId = extractId(created);

    mockMvc
        .perform(
            put(BASE_PATH + "/element/{elementId}", elementId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                      "title": "",
                      "description": "",
                      "rating": -1
                    }
                    """)
                .header(HttpHeaders.ACCEPT_LANGUAGE, ELanguage.FRENCH.getCode())
                .header(AvenirsSecurityHeaders.SIGNED_CONTEXT, studentPayload)
                .header(AvenirsSecurityHeaders.CONTEXT_KID, secretKey)
                .header(AvenirsSecurityHeaders.CONTEXT_SIGNATURE, studentSignature))
        .andExpect(status().isBadRequest());
  }

  @Test
  void shouldReturn404WhenUserNotFoundOnUpdateElementEndpoint() throws Exception {
    String elementId = UUID.randomUUID().toString();

    mockMvc
        .perform(
            put(BASE_PATH + "/element/{elementId}", elementId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                      "title": "Updated",
                      "description": "Updated desc",
                      "rating": 3
                    }
                    """)
                .header("X-Signed-Context", unknownUserPayload)
                .header("X-Context-Kid", secretKey)
                .header("X-Context-Signature", unknownUserSignature))
        .andExpect(status().isNotFound())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.message").value("User not found"))
        .andExpect(jsonPath("$.code").value("USER_NOT_FOUND"));
  }

  @Test
  void shouldReturn400Or404WhenUpdatingElementThatDoesNotExist() throws Exception {
    String elementId = UUID.randomUUID().toString();

    mockMvc
        .perform(
            put(BASE_PATH + "/element/{elementId}", elementId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                      "title": "Updated",
                      "description": "Updated desc",
                      "rating": 3
                    }
                    """)
                .header(HttpHeaders.ACCEPT_LANGUAGE, ELanguage.FRENCH.getCode())
                .header(AvenirsSecurityHeaders.SIGNED_CONTEXT, studentPayload)
                .header(AvenirsSecurityHeaders.CONTEXT_KID, secretKey)
                .header(AvenirsSecurityHeaders.CONTEXT_SIGNATURE, studentSignature))
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().is4xxClientError())
        .andExpect(jsonPath("$.code").exists());
  }

  @Test
  void shouldDeleteSelfKnowledgeElements() throws Exception {
    String categoryId = getLinkedCategoryId();
    String id1 = extractId(createElement(categoryId, "E1", "D1", 1));
    String id2 = extractId(createElement(categoryId, "E2", "D2", 2));

    mockMvc
        .perform(
            delete(BASE_PATH + "/elements")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(List.of(id1, id2)))
                .header(HttpHeaders.ACCEPT_LANGUAGE, ELanguage.FRENCH.getCode())
                .header(AvenirsSecurityHeaders.SIGNED_CONTEXT, studentPayload)
                .header(AvenirsSecurityHeaders.CONTEXT_KID, secretKey)
                .header(AvenirsSecurityHeaders.CONTEXT_SIGNATURE, studentSignature))
        .andExpect(status().isOk())
        .andExpect(content().string("Self knowledge elements successfully deleted"));

    mockMvc
        .perform(
            get(BASE_PATH + "/{categoryId}/elements", categoryId)
                .header(HttpHeaders.ACCEPT_LANGUAGE, ELanguage.FRENCH.getCode())
                .header(AvenirsSecurityHeaders.SIGNED_CONTEXT, studentPayload)
                .header(AvenirsSecurityHeaders.CONTEXT_KID, secretKey)
                .header(AvenirsSecurityHeaders.CONTEXT_SIGNATURE, studentSignature)
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data[?(@.id == '%s')]", id1).doesNotExist())
        .andExpect(jsonPath("$.data[?(@.id == '%s')]", id2).doesNotExist());
  }

  @Test
  void shouldReturn404WhenUserNotFoundOnDeleteElementsEndpoint() throws Exception {
    mockMvc
        .perform(
            delete(BASE_PATH + "/elements")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(List.of(UUID.randomUUID().toString())))
                .header("X-Signed-Context", unknownUserPayload)
                .header("X-Context-Kid", secretKey)
                .header("X-Context-Signature", unknownUserSignature))
        .andExpect(status().isNotFound())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.message").value("User not found"))
        .andExpect(jsonPath("$.code").value("USER_NOT_FOUND"));
  }
}
