package fr.avenirsesr.portfolio.student.selfknowledge.application.adapter.controller;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.avenirsesr.portfolio.common.language.domain.model.enums.ELanguage;
import fr.avenirsesr.portfolio.common.security.infrastructure.adapter.model.AvenirsSecurityHeaders;
import fr.avenirsesr.portfolio.student.selfknowledge.domain.model.enums.ESelfKnowledgeCategory;
import fr.avenirsesr.portfolio.shared.infrastructure.ContainerConfigurationTest;
import fr.avenirsesr.portfolio.shared.infrastructure.adapter.seeder.SeederRunner;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

class SelfKnowledgeControllerIT extends ContainerConfigurationTest {

  private static final String BASE_PATH = "/me/self-knowledge";
  private static final String CATEGORIES_BASE_PATH = BASE_PATH + "/categories";
  private static final String CATEGORIES_AVAILABLE_BASE_PATH = CATEGORIES_BASE_PATH + "/available";

  @Autowired private WebTestClient webTestClient;
  @Autowired private ObjectMapper objectMapper;

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
    String body =
        webTestClient
            .get()
            .uri(CATEGORIES_AVAILABLE_BASE_PATH)
            .header("Accept-Language", ELanguage.FRENCH.getCode())
            .header(AvenirsSecurityHeaders.SIGNED_CONTEXT, studentPayload)
            .header(AvenirsSecurityHeaders.CONTEXT_SIGNATURE, studentSignature)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody(String.class)
            .returnResult()
            .getResponseBody();

    JsonNode json = objectMapper.readTree(body);
    return json.get(0).get("type").asText();
  }

  private String createElement(String categoryId, String title, String description, int rating)
      throws Exception {

    String body =
        """
        {
          "title": "%s",
          "description": "%s",
          "rating": %d
        }
        """
            .formatted(title, description, rating);

    String response =
        webTestClient
            .post()
            .uri(BASE_PATH + "/{categoryId}/elements", categoryId)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(body)
            .header("Accept-Language", ELanguage.FRENCH.getCode())
            .header(AvenirsSecurityHeaders.SIGNED_CONTEXT, studentPayload)
            .header(AvenirsSecurityHeaders.CONTEXT_SIGNATURE, studentSignature)
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody(String.class)
            .returnResult()
            .getResponseBody();

    JsonNode json = objectMapper.readTree(response);
    return json.get("id").asText();
  }

  @Test
  void shouldReturnSelfKnowledgeCategoriesForStudent() {
    webTestClient
        .get()
        .uri(CATEGORIES_BASE_PATH)
        .header("Accept-Language", ELanguage.FRENCH.getCode())
        .header(AvenirsSecurityHeaders.SIGNED_CONTEXT, studentPayload)
        .header(AvenirsSecurityHeaders.CONTEXT_SIGNATURE, studentSignature)
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody()
        .jsonPath("$")
        .isArray()
        .jsonPath("$[0].type")
        .exists()
        .jsonPath("$[0].mandatory")
        .exists();
  }

  @Test
  void shouldReturn404WhenUserNotFoundOnCategoriesEndpoint() {
    webTestClient
        .get()
        .uri(CATEGORIES_BASE_PATH)
        .header("X-Signed-Context", unknownUserPayload)
        .header("X-Context-Signature", unknownUserSignature)
        .exchange()
        .expectStatus()
        .isUnauthorized()
        .expectBody()
        .jsonPath("$.code")
        .isEqualTo("USER_NOT_AUTHORIZED");
  }

  @Test
  void shouldAssociateSelfKnowledgeCategoriesToStudent() throws Exception {
    webTestClient
        .post()
        .uri(CATEGORIES_BASE_PATH)
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(objectMapper.writeValueAsString(List.of(getAvailableCategoryId())))
        .header("Accept-Language", ELanguage.FRENCH.getCode())
        .header(AvenirsSecurityHeaders.SIGNED_CONTEXT, studentPayload)
        .header(AvenirsSecurityHeaders.CONTEXT_SIGNATURE, studentSignature)
        .exchange()
        .expectStatus()
        .isOk();
  }

  @Test
  void shouldCreateSelfKnowledgeElement() throws Exception {
    String categoryId = ESelfKnowledgeCategory.STRENGTHS.name();

    webTestClient
        .post()
        .uri(BASE_PATH + "/{categoryId}/elements", categoryId)
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(
            """
            {
              "title": "New",
              "description": "New desc",
              "rating": 4
            }
            """)
        .header("Accept-Language", ELanguage.FRENCH.getCode())
        .header(AvenirsSecurityHeaders.SIGNED_CONTEXT, studentPayload)
        .header(AvenirsSecurityHeaders.CONTEXT_SIGNATURE, studentSignature)
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody()
        .jsonPath("$.id")
        .exists()
        .jsonPath("$.title")
        .isEqualTo("New")
        .jsonPath("$.valorized")
        .isEqualTo(false);
  }

  @Test
  void shouldUpdateSelfKnowledgeElementValorizedFlag() throws Exception {
    String categoryId = ESelfKnowledgeCategory.VALUES.name();
    String elementId = createElement(categoryId, "Test", "Desc", 3);

    webTestClient
        .put()
        .uri(BASE_PATH + "/element/{elementId}", elementId)
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(
            """
            {
              "title": "Test",
              "description": "Desc",
              "rating": 3,
              "valorized": true
            }
            """)
        .header("Accept-Language", ELanguage.FRENCH.getCode())
        .header(AvenirsSecurityHeaders.SIGNED_CONTEXT, studentPayload)
        .header(AvenirsSecurityHeaders.CONTEXT_SIGNATURE, studentSignature)
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody()
        .jsonPath("$.id")
        .isEqualTo(elementId)
        .jsonPath("$.valorized")
        .isEqualTo(true);
  }

  @Test
  void shouldFilterSelfKnowledgeElementsByIsValorized() throws Exception {
    String categoryId = ESelfKnowledgeCategory.ASPIRATIONS.name();
    String elementId = createElement(categoryId, "Filter me", "Desc", 3);

    webTestClient
        .put()
        .uri(BASE_PATH + "/element/{elementId}", elementId)
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(
            """
            {
              "title": "Filter me",
              "description": "Desc",
              "rating": 3,
              "valorized": true
            }
            """)
        .header("Accept-Language", ELanguage.FRENCH.getCode())
        .header(AvenirsSecurityHeaders.SIGNED_CONTEXT, studentPayload)
        .header(AvenirsSecurityHeaders.CONTEXT_SIGNATURE, studentSignature)
        .exchange()
        .expectStatus()
        .isOk();

    String valorizedOnlyResponse =
        webTestClient
            .get()
            .uri(
                BASE_PATH + "/elements?selfKnowledgeCategories={categoryId}&isValorized=true",
                categoryId)
            .header("Accept-Language", ELanguage.FRENCH.getCode())
            .header(AvenirsSecurityHeaders.SIGNED_CONTEXT, studentPayload)
            .header(AvenirsSecurityHeaders.CONTEXT_SIGNATURE, studentSignature)
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody(String.class)
            .returnResult()
            .getResponseBody();

    List<String> valorizedIds = new ArrayList<>();
    objectMapper
        .readTree(valorizedOnlyResponse)
        .get("data")
        .forEach(node -> valorizedIds.add(node.get("id").asText()));
    assertThat(valorizedIds).contains(elementId);

    String nonValorizedOnlyResponse =
        webTestClient
            .get()
            .uri(
                BASE_PATH + "/elements?selfKnowledgeCategories={categoryId}&isValorized=false",
                categoryId)
            .header("Accept-Language", ELanguage.FRENCH.getCode())
            .header(AvenirsSecurityHeaders.SIGNED_CONTEXT, studentPayload)
            .header(AvenirsSecurityHeaders.CONTEXT_SIGNATURE, studentSignature)
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody(String.class)
            .returnResult()
            .getResponseBody();

    List<String> nonValorizedIds = new ArrayList<>();
    objectMapper
        .readTree(nonValorizedOnlyResponse)
        .get("data")
        .forEach(node -> nonValorizedIds.add(node.get("id").asText()));
    assertThat(nonValorizedIds).doesNotContain(elementId);
  }

  @Test
  void shouldFilterSelfKnowledgeElementsByMultipleCategories() throws Exception {
    String firstCategory = ESelfKnowledgeCategory.MOTIVATION.name();
    String secondCategory = ESelfKnowledgeCategory.IMPROVEMENT.name();
    String firstElementId = createElement(firstCategory, "In first category", "Desc", 3);
    String secondElementId = createElement(secondCategory, "In second category", "Desc", 2);
    String otherCategory = ESelfKnowledgeCategory.INTERESTS.name();
    String otherElementId = createElement(otherCategory, "In other category", "Desc", 1);

    String response =
        webTestClient
            .get()
            .uri(
                uriBuilder ->
                    uriBuilder
                        .path(BASE_PATH + "/elements")
                        .queryParam("selfKnowledgeCategories", firstCategory, secondCategory)
                        .build())
            .header("Accept-Language", ELanguage.FRENCH.getCode())
            .header(AvenirsSecurityHeaders.SIGNED_CONTEXT, studentPayload)
            .header(AvenirsSecurityHeaders.CONTEXT_SIGNATURE, studentSignature)
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody(String.class)
            .returnResult()
            .getResponseBody();

    List<String> ids = new ArrayList<>();
    JsonNode dataNode = objectMapper.readTree(response).get("data");
    dataNode.forEach(node -> ids.add(node.get("id").asText()));

    assertThat(ids).contains(firstElementId, secondElementId).doesNotContain(otherElementId);
    assertThat(dataNode.get(0).get("category")).isNotNull();
    assertThat(dataNode.get(0).get("category").get("type")).isNotNull();
  }

  @Test
  void shouldReturn400WhenFilteringByInvalidCategory() {
    webTestClient
        .get()
        .uri(
            uriBuilder ->
                uriBuilder
                    .path(BASE_PATH + "/elements")
                    .queryParam("selfKnowledgeCategories", "NOT_A_CATEGORY")
                    .build())
        .header("Accept-Language", ELanguage.FRENCH.getCode())
        .header(AvenirsSecurityHeaders.SIGNED_CONTEXT, studentPayload)
        .header(AvenirsSecurityHeaders.CONTEXT_SIGNATURE, studentSignature)
        .exchange()
        .expectStatus()
        .isBadRequest();
  }

  @Test
  void shouldGetSelfKnowledgeElementDetails() throws Exception {
    String categoryId = ESelfKnowledgeCategory.INSPIRATIONS.name();
    String elementId = createElement(categoryId, "Test", "Desc", 3);

    webTestClient
        .get()
        .uri(BASE_PATH + "/element/{elementId}", elementId)
        .header("Accept-Language", ELanguage.FRENCH.getCode())
        .header(AvenirsSecurityHeaders.SIGNED_CONTEXT, studentPayload)
        .header(AvenirsSecurityHeaders.CONTEXT_SIGNATURE, studentSignature)
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody()
        .jsonPath("$.id")
        .isEqualTo(elementId)
        .jsonPath("$.title")
        .isEqualTo("Test");
  }

  @Test
  void shouldDeleteSelfKnowledgeElements() throws Exception {
    String categoryId = ESelfKnowledgeCategory.OBLIGATIONS.name();
    String id1 = createElement(categoryId, "E1", "D1", 1);
    String id2 = createElement(categoryId, "E2", "D2", 2);

    webTestClient
        .method(org.springframework.http.HttpMethod.DELETE)
        .uri(BASE_PATH + "/elements")
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(objectMapper.writeValueAsString(List.of(id1, id2)))
        .header("Accept-Language", ELanguage.FRENCH.getCode())
        .header(AvenirsSecurityHeaders.SIGNED_CONTEXT, studentPayload)
        .header(AvenirsSecurityHeaders.CONTEXT_SIGNATURE, studentSignature)
        .exchange()
        .expectStatus()
        .isOk();
  }
}
