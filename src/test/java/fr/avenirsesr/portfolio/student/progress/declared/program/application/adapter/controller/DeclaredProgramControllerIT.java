package fr.avenirsesr.portfolio.student.progress.declared.program.application.adapter.controller;

import static org.hamcrest.Matchers.matchesPattern;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.avenirsesr.portfolio.common.language.domain.model.enums.ELanguage;
import fr.avenirsesr.portfolio.common.security.infrastructure.adapter.model.AvenirsSecurityHeaders;
import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import fr.avenirsesr.portfolio.shared.infrastructure.ContainerConfigurationTest;
import fr.avenirsesr.portfolio.shared.infrastructure.adapter.seeder.SeederRunner;
import fr.avenirsesr.portfolio.student.progress.declared.program.application.adapter.dto.AddDeclaredProgramDTO;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

class DeclaredProgramControllerIT extends ContainerConfigurationTest {

  private static final String BASE_PATH = "/me/declared/programs";

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

  @Nested
  class GivenDeclaredProgramEndpoint {

    @Nested
    class WhenCreatingADeclaredProgram {

      @Test
      void shouldCreateDeclaredProgramSuccessfully() throws Exception {
        BddLogger.given("the " + BASE_PATH + " POST endpoint");
        BddLogger.when("performing a POST with a valid payload as a student");
        BddLogger.then("it should create a declared program and return 201 with Location header");

        AddDeclaredProgramDTO body =
            new AddDeclaredProgramDTO(
                "Stage d'observation",
                "Participation aux activités d'une équipe technique",
                "Tech4Future",
                "Acquisition de premières compétences techniques",
                "Conseiller d'orientation",
                "https://tech4future.example.com",
                LocalDate.now().minusMonths(1),
                LocalDate.now());

        mockMvc
            .perform(
                post(BASE_PATH)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(body))
                    .header(HttpHeaders.ACCEPT_LANGUAGE, ELanguage.FRENCH.getCode())
                    .header(AvenirsSecurityHeaders.SIGNED_CONTEXT, studentPayload)
                    .header(AvenirsSecurityHeaders.CONTEXT_KID, secretKey)
                    .header(AvenirsSecurityHeaders.CONTEXT_SIGNATURE, studentSignature))
            .andExpect(status().isCreated())
            .andExpect(header().string(HttpHeaders.LOCATION, notNullValue()))
            .andExpect(
                header()
                    .string(
                        HttpHeaders.LOCATION,
                        matchesPattern(".*/me/declared/programs/[0-9a-fA-F\\-]{36}")))
            .andExpect(content().string("Declared program created successfully"));
      }

      @Test
      void shouldReturn404WhenUserNotFound() throws Exception {
        BddLogger.given("the " + BASE_PATH + " POST endpoint");
        BddLogger.when("performing a POST with an unknown user");
        BddLogger.then("it should return a 404 USER_NOT_FOUND");

        AddDeclaredProgramDTO body =
            new AddDeclaredProgramDTO(
                "Stage d'observation",
                "Description",
                "Tech4Future",
                "Résultat",
                "Source",
                "https://example.com",
                LocalDate.now().minusMonths(1),
                LocalDate.now());

        mockMvc
            .perform(
                post(BASE_PATH)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(body))
                    .header(HttpHeaders.ACCEPT_LANGUAGE, ELanguage.FRENCH.getCode())
                    .header(AvenirsSecurityHeaders.SIGNED_CONTEXT, unknownUserPayload)
                    .header(AvenirsSecurityHeaders.CONTEXT_KID, secretKey)
                    .header(AvenirsSecurityHeaders.CONTEXT_SIGNATURE, unknownUserSignature))
            .andExpect(status().isNotFound())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.code").value("USER_NOT_FOUND"))
            .andExpect(jsonPath("$.message").value("User not found"));
      }

      @Test
      void shouldReturn400WhenTitleTooLong() throws Exception {
        BddLogger.given("the " + BASE_PATH + " POST endpoint");
        BddLogger.when("performing a POST with a title longer than 80 characters");
        BddLogger.then("it should return a 400 REQUEST_VALIDATION_ERROR");

        String longTitle = "T".repeat(81);

        AddDeclaredProgramDTO body =
            new AddDeclaredProgramDTO(
                longTitle,
                "Description",
                "Tech4Future",
                "Résultat",
                "Source",
                "https://example.com",
                LocalDate.now().minusMonths(1),
                LocalDate.now());

        mockMvc
            .perform(
                post(BASE_PATH)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(body))
                    .header(HttpHeaders.ACCEPT_LANGUAGE, ELanguage.FRENCH.getCode())
                    .header(AvenirsSecurityHeaders.SIGNED_CONTEXT, studentPayload)
                    .header(AvenirsSecurityHeaders.CONTEXT_KID, secretKey)
                    .header(AvenirsSecurityHeaders.CONTEXT_SIGNATURE, studentSignature))
            .andExpect(status().isBadRequest())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.code").value("TOO_LONG"))
            .andExpect(
                jsonPath("$.message").value("The field title exceeds the maximum allowed length"));
      }

      @Test
      void shouldReturn400WhenOrganizationIsBlank() throws Exception {
        BddLogger.given("the " + BASE_PATH + " POST endpoint");
        BddLogger.when("performing a POST with a blank organization");
        BddLogger.then("it should return a 400 REQUEST_VALIDATION_ERROR");

        AddDeclaredProgramDTO body =
            new AddDeclaredProgramDTO(
                "Stage d'observation",
                "Description",
                "   ",
                "Résultat",
                "Source",
                "https://example.com",
                LocalDate.now().minusMonths(1),
                LocalDate.now());

        mockMvc
            .perform(
                post(BASE_PATH)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(body))
                    .header(HttpHeaders.ACCEPT_LANGUAGE, ELanguage.FRENCH.getCode())
                    .header(AvenirsSecurityHeaders.SIGNED_CONTEXT, studentPayload)
                    .header(AvenirsSecurityHeaders.CONTEXT_KID, secretKey)
                    .header(AvenirsSecurityHeaders.CONTEXT_SIGNATURE, studentSignature))
            .andExpect(status().isBadRequest())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.code").value("NOT_BLANK"))
            .andExpect(jsonPath("$.message").value("The field organization cannot be blank"));
      }

      @Test
      void shouldReturn400WhenStartDateIsMissing() throws Exception {
        BddLogger.given("the " + BASE_PATH + " POST endpoint");
        BddLogger.when("performing a POST with a missing startDate");
        BddLogger.then("it should return a 400 REQUEST_VALIDATION_ERROR");

        AddDeclaredProgramDTO body =
            new AddDeclaredProgramDTO(
                "Stage d'observation",
                "Participation aux activités d'une équipe technique",
                "Tech4Future",
                "Acquisition de premières compétences techniques",
                "Conseiller d'orientation",
                "https://tech4future.example.com",
                null,
                LocalDate.now());

        mockMvc
            .perform(
                post(BASE_PATH)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(body))
                    .header(HttpHeaders.ACCEPT_LANGUAGE, ELanguage.FRENCH.getCode())
                    .header(AvenirsSecurityHeaders.SIGNED_CONTEXT, studentPayload)
                    .header(AvenirsSecurityHeaders.CONTEXT_KID, secretKey)
                    .header(AvenirsSecurityHeaders.CONTEXT_SIGNATURE, studentSignature))
            .andExpect(status().isBadRequest())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.code").value("NOT_NULL"))
            .andExpect(jsonPath("$.message").value("The field startDate cannot be null"));
      }
    }
  }
}
