package fr.avenirsesr.portfolio.selfknowledge.application.adapter.controller;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SelfKnowledgeControllerIT {

  private static final String BASE_PATH = "/me/self-knowledge";
  private static final String CATEGORIES_BASE_PATH = BASE_PATH + "/categories";

  @Autowired private MockMvc mockMvc;

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
  void shouldReturn404WhenUserNotFound() throws Exception {
    BddLogger.given("the " + CATEGORIES_BASE_PATH + " enpoint");
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
}
