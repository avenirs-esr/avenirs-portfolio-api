package fr.avenirsesr.portfolio.student.progress.declared.program.application.adapter.controller;

import static org.hamcrest.Matchers.matchesPattern;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.avenirsesr.portfolio.common.language.domain.model.enums.ELanguage;
import fr.avenirsesr.portfolio.common.security.infrastructure.adapter.model.AvenirsSecurityHeaders;
import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import fr.avenirsesr.portfolio.shared.infrastructure.ContainerConfigurationTest;
import fr.avenirsesr.portfolio.shared.infrastructure.adapter.seeder.SeederRunner;
import fr.avenirsesr.portfolio.student.progress.declared.program.application.adapter.dto.AddDeclaredProgramDTO;
import fr.avenirsesr.portfolio.student.progress.declared.program.domain.model.enums.EProgramStatus;
import java.time.LocalDate;
import java.util.UUID;
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

  @Value("${user.second.student.payload}")
  private String secondStudentPayload;

  @Value("${user.unknown.payload}")
  private String unknownUserPayload;

  @Value("${user.student.signature}")
  private String studentSignature;

  @Value("${user.second.student.signature}")
  private String secondStudentSignature;

  @Value("${user.unknown.signature}")
  private String unknownUserSignature;

  private String createDeclaredProgramAndReturnId(
      AddDeclaredProgramDTO body, String userPayload, String userSignature) throws Exception {
    var result =
        mockMvc
            .perform(
                post(BASE_PATH)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(body))
                    .header(HttpHeaders.ACCEPT_LANGUAGE, ELanguage.FRENCH.getCode())
                    .header(AvenirsSecurityHeaders.SIGNED_CONTEXT, userPayload)
                    .header(AvenirsSecurityHeaders.CONTEXT_KID, secretKey)
                    .header(AvenirsSecurityHeaders.CONTEXT_SIGNATURE, userSignature))
            .andExpect(status().isCreated())
            .andExpect(header().string(HttpHeaders.LOCATION, notNullValue()))
            .andReturn();

    String location = result.getResponse().getHeader(HttpHeaders.LOCATION);
    assertNotNull(location);
    return location.substring(location.lastIndexOf('/') + 1);
  }

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
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.title").value("Stage d'observation"))
            .andExpect(jsonPath("$.organization").value("Tech4Future"));
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

    @Nested
    class WhenGettingADeclaredProgram {

      @Test
      void shouldReturn200WhenDeclaredProgramExistsAndBelongsToLoggedInStudent() throws Exception {
        BddLogger.given("the " + BASE_PATH + "/{declaredProgramId} GET endpoint");
        BddLogger.when(
            "performing a GET for an existing declared program owned by the logged-in student");
        BddLogger.then("it should return 200 with the declared program dto");

        String id =
            createDeclaredProgramAndReturnId(
                new AddDeclaredProgramDTO(
                    "Stage d'observation",
                    "Participation aux activités d'une équipe technique",
                    "Tech4Future",
                    "Acquisition de premières compétences techniques",
                    "Conseiller d'orientation",
                    "https://tech4future.example.com",
                    LocalDate.now().minusMonths(1),
                    LocalDate.now()),
                studentPayload,
                studentSignature);

        mockMvc
            .perform(
                get(BASE_PATH + "/" + id)
                    .header(HttpHeaders.ACCEPT_LANGUAGE, ELanguage.FRENCH.getCode())
                    .header(AvenirsSecurityHeaders.SIGNED_CONTEXT, studentPayload)
                    .header(AvenirsSecurityHeaders.CONTEXT_KID, secretKey)
                    .header(AvenirsSecurityHeaders.CONTEXT_SIGNATURE, studentSignature))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(id))
            .andExpect(jsonPath("$.status").value(EProgramStatus.IN_PROGRESS.name()))
            .andExpect(jsonPath("$.title").value("Stage d'observation"))
            .andExpect(
                jsonPath("$.description")
                    .value("Participation aux activités d'une équipe technique"))
            .andExpect(jsonPath("$.organization").value("Tech4Future"))
            .andExpect(
                jsonPath("$.result").value("Acquisition de premières compétences techniques"))
            .andExpect(jsonPath("$.sourceOfInformation").value("Conseiller d'orientation"))
            .andExpect(jsonPath("$.link").value("https://tech4future.example.com"))
            .andExpect(jsonPath("$.startDate").isNotEmpty())
            .andExpect(jsonPath("$.endDate").isNotEmpty())
            .andExpect(jsonPath("$.createdAt").isNotEmpty())
            .andExpect(jsonPath("$.updatedAt").isNotEmpty());
      }

      @Test
      void shouldReturn404WhenDeclaredProgramNotFound() throws Exception {
        BddLogger.given("the " + BASE_PATH + "/{declaredProgramId} GET endpoint");
        BddLogger.when("performing a GET for a non-existing declared program");
        BddLogger.then("it should return 404 DECLARED_PROGRAM_NOT_FOUND");

        String unknownId = UUID.randomUUID().toString();

        mockMvc
            .perform(
                get(BASE_PATH + "/" + unknownId)
                    .header(HttpHeaders.ACCEPT_LANGUAGE, ELanguage.FRENCH.getCode())
                    .header(AvenirsSecurityHeaders.SIGNED_CONTEXT, studentPayload)
                    .header(AvenirsSecurityHeaders.CONTEXT_KID, secretKey)
                    .header(AvenirsSecurityHeaders.CONTEXT_SIGNATURE, studentSignature))
            .andExpect(status().isNotFound())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.code").value("DECLARED_PROGRAM_NOT_FOUND"));
      }

      @Test
      void shouldReturn400WhenDeclaredProgramIdIsNotUUID() throws Exception {
        BddLogger.given("the " + BASE_PATH + "/{declaredProgramId} GET endpoint");
        BddLogger.when("performing a GET with an invalid UUID");
        BddLogger.then("it should return 400");

        mockMvc
            .perform(
                get(BASE_PATH + "/not-a-uuid")
                    .header(HttpHeaders.ACCEPT_LANGUAGE, ELanguage.FRENCH.getCode())
                    .header(AvenirsSecurityHeaders.SIGNED_CONTEXT, studentPayload)
                    .header(AvenirsSecurityHeaders.CONTEXT_KID, secretKey)
                    .header(AvenirsSecurityHeaders.CONTEXT_SIGNATURE, studentSignature))
            .andExpect(status().isBadRequest());
      }

      @Test
      void shouldReturn403WhenDeclaredProgramBelongsToAnotherStudent() throws Exception {
        BddLogger.given("the " + BASE_PATH + "/{declaredProgramId} GET endpoint");
        BddLogger.when(
            "performing a GET for an existing declared program owned by another student");
        BddLogger.then("it should return 403 (or 401 depending on your handler)");

        String id =
            createDeclaredProgramAndReturnId(
                new AddDeclaredProgramDTO(
                    "Programme privé",
                    "Description",
                    "Org",
                    "Result",
                    "Source",
                    "https://example.com",
                    LocalDate.now().minusMonths(1),
                    LocalDate.now()),
                secondStudentPayload,
                secondStudentSignature);

        mockMvc
            .perform(
                get(BASE_PATH + "/" + id)
                    .header(HttpHeaders.ACCEPT_LANGUAGE, ELanguage.FRENCH.getCode())
                    .header(AvenirsSecurityHeaders.SIGNED_CONTEXT, studentPayload)
                    .header(AvenirsSecurityHeaders.CONTEXT_KID, secretKey)
                    .header(AvenirsSecurityHeaders.CONTEXT_SIGNATURE, studentSignature))
            .andExpect(status().isForbidden());
      }
    }

    @Nested
    class WhenGettingDeclaredPrograms {

      @Test
      void shouldReturn200WhenDeclaredProgramsExistsAndBelongsToLoggedInStudent() throws Exception {
        BddLogger.given("the " + BASE_PATH + " GET endpoint");
        BddLogger.when(
            "performing a GET for existing declared programs owned by the logged-in student");
        BddLogger.then("it should return 200 with declared programs dto");

        String declaredProgramId1 =
            createDeclaredProgramAndReturnId(
                new AddDeclaredProgramDTO(
                    "1-Title",
                    "1-Description",
                    "1-Organization",
                    "1-Result",
                    "1-Source",
                    "https://link-1.example.com",
                    LocalDate.now().minusMonths(1),
                    LocalDate.now()),
                studentPayload,
                studentSignature);

        String declaredProgramId2 =
            createDeclaredProgramAndReturnId(
                new AddDeclaredProgramDTO(
                    "2-Title",
                    "2-Description",
                    "2-Organization",
                    "2-Result",
                    "2-Source",
                    "https://link-2.example.com",
                    LocalDate.now().minusMonths(1),
                    LocalDate.now()),
                studentPayload,
                studentSignature);

        mockMvc
            .perform(
                get(BASE_PATH)
                    .header(HttpHeaders.ACCEPT_LANGUAGE, ELanguage.FRENCH.getCode())
                    .header(AvenirsSecurityHeaders.SIGNED_CONTEXT, studentPayload)
                    .header(AvenirsSecurityHeaders.CONTEXT_KID, secretKey)
                    .header(AvenirsSecurityHeaders.CONTEXT_SIGNATURE, studentSignature))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.data.length()").value(2))
            .andExpect(jsonPath("$.data[0].id").value(declaredProgramId1))
            .andExpect(jsonPath("$.data[0].status").value(EProgramStatus.IN_PROGRESS.name()))
            .andExpect(jsonPath("$.data[0].title").value("1-Title"))
            .andExpect(jsonPath("$.data[0].organization").value("1-Organization"))
            .andExpect(jsonPath("$.data[0].result").value("1-Result"))
            .andExpect(jsonPath("$.data[1].id").value(declaredProgramId2))
            .andExpect(jsonPath("$.data[1].status").value(EProgramStatus.IN_PROGRESS.name()))
            .andExpect(jsonPath("$.data[1].title").value("2-Title"))
            .andExpect(jsonPath("$.data[1].organization").value("2-Organization"))
            .andExpect(jsonPath("$.data[1].result").value("2-Result"))
            .andExpect(jsonPath("$.page.page").value(0))
            .andExpect(jsonPath("$.page.pageSize").value(8))
            .andExpect(jsonPath("$.page.totalElements").value(2))
            .andExpect(jsonPath("$.page.totalPages").value(1));
      }
    }
  }
}
