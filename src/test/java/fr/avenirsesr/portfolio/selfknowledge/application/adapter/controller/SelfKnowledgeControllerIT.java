package fr.avenirsesr.portfolio.selfknowledge.application.adapter.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.avenirsesr.portfolio.common.language.domain.model.enums.ELanguage;
import fr.avenirsesr.portfolio.common.security.infrastructure.adapter.model.AvenirsSecurityHeaders;
import fr.avenirsesr.portfolio.shared.infrastructure.ContainerConfigurationTest;
import fr.avenirsesr.portfolio.shared.infrastructure.adapter.seeder.SeederRunner;
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
    String body =
        webTestClient
            .get()
            .uri(CATEGORIES_AVAILABLE_BASE_PATH)
            .header("Accept-Language", ELanguage.FRENCH.getCode())
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
    return json.get(0).get("id").asText();
  }

  private String getLinkedCategoryId() throws Exception {
    String categoryId = getAvailableCategoryId();

    webTestClient
        .post()
        .uri(CATEGORIES_BASE_PATH)
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(objectMapper.writeValueAsString(List.of(categoryId)))
        .header("Accept-Language", ELanguage.FRENCH.getCode())
        .header(AvenirsSecurityHeaders.SIGNED_CONTEXT, studentPayload)
        .header(AvenirsSecurityHeaders.CONTEXT_KID, secretKey)
        .header(AvenirsSecurityHeaders.CONTEXT_SIGNATURE, studentSignature)
        .exchange()
        .expectStatus()
        .isOk();

    return categoryId;
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
            .header(AvenirsSecurityHeaders.CONTEXT_KID, secretKey)
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
        .header(AvenirsSecurityHeaders.CONTEXT_KID, secretKey)
        .header(AvenirsSecurityHeaders.CONTEXT_SIGNATURE, studentSignature)
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody()
        .jsonPath("$")
        .isArray()
        .jsonPath("$[0].id")
        .exists()
        .jsonPath("$[0].title")
        .exists();
  }

  @Test
  void shouldReturn404WhenUserNotFoundOnCategoriesEndpoint() {
    webTestClient
        .get()
        .uri(CATEGORIES_BASE_PATH)
        .header("X-Signed-Context", unknownUserPayload)
        .header("X-Context-Kid", secretKey)
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
        .header(AvenirsSecurityHeaders.CONTEXT_KID, secretKey)
        .header(AvenirsSecurityHeaders.CONTEXT_SIGNATURE, studentSignature)
        .exchange()
        .expectStatus()
        .isOk();
  }

  @Test
  void shouldCreateSelfKnowledgeElement() throws Exception {
    String categoryId = getLinkedCategoryId();

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
        .header(AvenirsSecurityHeaders.CONTEXT_KID, secretKey)
        .header(AvenirsSecurityHeaders.CONTEXT_SIGNATURE, studentSignature)
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody()
        .jsonPath("$.id")
        .exists()
        .jsonPath("$.title")
        .isEqualTo("New");
  }

  @Test
  void shouldGetSelfKnowledgeElementDetails() throws Exception {
    String categoryId = getLinkedCategoryId();
    String elementId = createElement(categoryId, "Test", "Desc", 3);

    webTestClient
        .get()
        .uri(BASE_PATH + "/element/{elementId}", elementId)
        .header("Accept-Language", ELanguage.FRENCH.getCode())
        .header(AvenirsSecurityHeaders.SIGNED_CONTEXT, studentPayload)
        .header(AvenirsSecurityHeaders.CONTEXT_KID, secretKey)
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
    String categoryId = getLinkedCategoryId();
    String id1 = createElement(categoryId, "E1", "D1", 1);
    String id2 = createElement(categoryId, "E2", "D2", 2);

    webTestClient
        .method(org.springframework.http.HttpMethod.DELETE)
        .uri(BASE_PATH + "/elements")
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(objectMapper.writeValueAsString(List.of(id1, id2)))
        .header("Accept-Language", ELanguage.FRENCH.getCode())
        .header(AvenirsSecurityHeaders.SIGNED_CONTEXT, studentPayload)
        .header(AvenirsSecurityHeaders.CONTEXT_KID, secretKey)
        .header(AvenirsSecurityHeaders.CONTEXT_SIGNATURE, studentSignature)
        .exchange()
        .expectStatus()
        .isOk();
  }
}
