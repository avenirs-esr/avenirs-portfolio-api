package fr.avenirsesr.portfolio.student.progress.declared.program.application.adapter.controller;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.avenirsesr.portfolio.common.language.domain.model.enums.ELanguage;
import fr.avenirsesr.portfolio.common.security.infrastructure.adapter.model.AvenirsSecurityHeaders;
import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import fr.avenirsesr.portfolio.shared.infrastructure.ContainerConfigurationTest;
import fr.avenirsesr.portfolio.shared.infrastructure.adapter.seeder.SeederRunner;
import fr.avenirsesr.portfolio.student.progress.declared.program.application.adapter.dto.DeclaredProgramRequestDTO;
import fr.avenirsesr.portfolio.student.progress.declared.program.domain.model.enums.EProgramStatus;
import fr.avenirsesr.portfolio.student.progress.declared.program.infrastructure.adapter.repository.DeclaredProgramJpaRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

class DeclaredProgramControllerIT extends ContainerConfigurationTest {

  private static final String BASE_PATH = "/me/declared/programs";

  @Autowired private DeclaredProgramJpaRepository declaredProgramJpaRepository;

  @Autowired private WebTestClient webTestClient;

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
      DeclaredProgramRequestDTO body, String userPayload, String userSignature) {
    var result =
        webTestClient
            .post()
            .uri(BASE_PATH)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(writeValueAsString(body))
            .header(HttpHeaders.ACCEPT_LANGUAGE, ELanguage.FRENCH.getCode())
            .header(AvenirsSecurityHeaders.SIGNED_CONTEXT, userPayload)
            .header(AvenirsSecurityHeaders.CONTEXT_KID, secretKey)
            .header(AvenirsSecurityHeaders.CONTEXT_SIGNATURE, userSignature)
            .exchange()
            .expectStatus()
            .isCreated()
            .returnResult(String.class);

    String location = result.getResponseHeaders().getFirst(HttpHeaders.LOCATION);
    assertNotNull(location);
    return location.substring(location.lastIndexOf('/') + 1);
  }

  private String writeValueAsString(Object value) {
    try {
      return objectMapper.writeValueAsString(value);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
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
      void shouldCreateDeclaredProgramSuccessfully() {
        BddLogger.given("the " + BASE_PATH + " POST endpoint");
        BddLogger.when("performing a POST with a valid payload as a student");
        BddLogger.then("it should create a declared program and return 201 with Location header");

        DeclaredProgramRequestDTO body =
            new DeclaredProgramRequestDTO(
                "Stage d'observation",
                "Participation aux activités d'une équipe technique",
                "Tech4Future",
                "Acquisition de premières compétences techniques",
                "Conseiller d'orientation",
                LocalDate.now().minusMonths(1),
                LocalDate.now());

        webTestClient
            .post()
            .uri(BASE_PATH)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(writeValueAsString(body))
            .header(HttpHeaders.ACCEPT_LANGUAGE, ELanguage.FRENCH.getCode())
            .header(AvenirsSecurityHeaders.SIGNED_CONTEXT, studentPayload)
            .header(AvenirsSecurityHeaders.CONTEXT_KID, secretKey)
            .header(AvenirsSecurityHeaders.CONTEXT_SIGNATURE, studentSignature)
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody()
            .jsonPath("$.title")
            .isEqualTo("Stage d'observation")
            .jsonPath("$.organization")
            .isEqualTo("Tech4Future");
      }

      @Test
      void shouldReturn404WhenUserNotFound() {
        BddLogger.given("the " + BASE_PATH + " POST endpoint");
        BddLogger.when("performing a POST with an unknown user");
        BddLogger.then("it should return a 404 USER_NOT_FOUND");

        DeclaredProgramRequestDTO body =
            new DeclaredProgramRequestDTO(
                "Stage d'observation",
                "Description",
                "Tech4Future",
                "Résultat",
                "Source",
                LocalDate.now().minusMonths(1),
                LocalDate.now());

        webTestClient
            .post()
            .uri(BASE_PATH)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(writeValueAsString(body))
            .header(HttpHeaders.ACCEPT_LANGUAGE, ELanguage.FRENCH.getCode())
            .header(AvenirsSecurityHeaders.SIGNED_CONTEXT, unknownUserPayload)
            .header(AvenirsSecurityHeaders.CONTEXT_KID, secretKey)
            .header(AvenirsSecurityHeaders.CONTEXT_SIGNATURE, unknownUserSignature)
            .exchange()
            .expectStatus()
            .isUnauthorized()
            .expectBody()
            .jsonPath("$.code")
            .isEqualTo("USER_NOT_AUTHORIZED")
            .jsonPath("$.message")
            .isEqualTo("User not authorized");
      }

      @Test
      void shouldReturn400WhenTitleTooLong() {
        BddLogger.given("the " + BASE_PATH + " POST endpoint");
        BddLogger.when("performing a POST with a title longer than 80 characters");
        BddLogger.then("it should return a 400 REQUEST_VALIDATION_ERROR");

        String longTitle = "T".repeat(81);

        DeclaredProgramRequestDTO body =
            new DeclaredProgramRequestDTO(
                longTitle,
                "Description",
                "Tech4Future",
                "Résultat",
                "Source",
                LocalDate.now().minusMonths(1),
                LocalDate.now());

        webTestClient
            .post()
            .uri(BASE_PATH)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(writeValueAsString(body))
            .header(HttpHeaders.ACCEPT_LANGUAGE, ELanguage.FRENCH.getCode())
            .header(AvenirsSecurityHeaders.SIGNED_CONTEXT, studentPayload)
            .header(AvenirsSecurityHeaders.CONTEXT_KID, secretKey)
            .header(AvenirsSecurityHeaders.CONTEXT_SIGNATURE, studentSignature)
            .exchange()
            .expectStatus()
            .isBadRequest()
            .expectBody()
            .jsonPath("$.code")
            .isEqualTo("TOO_LONG")
            .jsonPath("$.message")
            .isEqualTo("The field title exceeds the maximum allowed length");
      }

      @Test
      void shouldReturn400WhenOrganizationIsBlank() {
        BddLogger.given("the " + BASE_PATH + " POST endpoint");
        BddLogger.when("performing a POST with a blank organization");
        BddLogger.then("it should return a 400 REQUEST_VALIDATION_ERROR");

        DeclaredProgramRequestDTO body =
            new DeclaredProgramRequestDTO(
                "Stage d'observation",
                "Description",
                "   ",
                "Résultat",
                "Source",
                LocalDate.now().minusMonths(1),
                LocalDate.now());

        webTestClient
            .post()
            .uri(BASE_PATH)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(writeValueAsString(body))
            .header(HttpHeaders.ACCEPT_LANGUAGE, ELanguage.FRENCH.getCode())
            .header(AvenirsSecurityHeaders.SIGNED_CONTEXT, studentPayload)
            .header(AvenirsSecurityHeaders.CONTEXT_KID, secretKey)
            .header(AvenirsSecurityHeaders.CONTEXT_SIGNATURE, studentSignature)
            .exchange()
            .expectStatus()
            .isBadRequest()
            .expectBody()
            .jsonPath("$.code")
            .isEqualTo("NOT_BLANK")
            .jsonPath("$.message")
            .isEqualTo("The field organization cannot be blank");
      }

      @Test
      void shouldReturn400WhenStartDateIsMissing() {
        BddLogger.given("the " + BASE_PATH + " POST endpoint");
        BddLogger.when("performing a POST with a missing startDate");
        BddLogger.then("it should return a 400 REQUEST_VALIDATION_ERROR");

        DeclaredProgramRequestDTO body =
            new DeclaredProgramRequestDTO(
                "Stage d'observation",
                "Participation aux activités d'une équipe technique",
                "Tech4Future",
                "Acquisition de premières compétences techniques",
                "Conseiller d'orientation",
                null,
                LocalDate.now());

        webTestClient
            .post()
            .uri(BASE_PATH)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(writeValueAsString(body))
            .header(HttpHeaders.ACCEPT_LANGUAGE, ELanguage.FRENCH.getCode())
            .header(AvenirsSecurityHeaders.SIGNED_CONTEXT, studentPayload)
            .header(AvenirsSecurityHeaders.CONTEXT_KID, secretKey)
            .header(AvenirsSecurityHeaders.CONTEXT_SIGNATURE, studentSignature)
            .exchange()
            .expectStatus()
            .isBadRequest()
            .expectBody()
            .jsonPath("$.code")
            .isEqualTo("NOT_NULL")
            .jsonPath("$.message")
            .isEqualTo("The field startDate cannot be null");
      }
    }

    @Nested
    class WhenGettingADeclaredProgram {

      @Test
      void shouldReturn200WhenDeclaredProgramExistsAndBelongsToLoggedInStudent() {
        BddLogger.given("the " + BASE_PATH + "/{declaredProgramId} GET endpoint");
        BddLogger.when(
            "performing a GET for an existing declared program owned by the logged-in student");
        BddLogger.then("it should return 200 with the declared program dto");

        String id =
            createDeclaredProgramAndReturnId(
                new DeclaredProgramRequestDTO(
                    "Stage d'observation",
                    "Participation aux activités d'une équipe technique",
                    "Tech4Future",
                    "Acquisition de premières compétences techniques",
                    "Conseiller d'orientation",
                    LocalDate.now().minusMonths(1),
                    LocalDate.now()),
                studentPayload,
                studentSignature);

        webTestClient
            .get()
            .uri(BASE_PATH + "/" + id)
            .header(HttpHeaders.ACCEPT_LANGUAGE, ELanguage.FRENCH.getCode())
            .header(AvenirsSecurityHeaders.SIGNED_CONTEXT, studentPayload)
            .header(AvenirsSecurityHeaders.CONTEXT_KID, secretKey)
            .header(AvenirsSecurityHeaders.CONTEXT_SIGNATURE, studentSignature)
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody()
            .jsonPath("$.id")
            .isEqualTo(id)
            .jsonPath("$.status")
            .isEqualTo(EProgramStatus.IN_PROGRESS.name())
            .jsonPath("$.title")
            .isEqualTo("Stage d'observation")
            .jsonPath("$.description")
            .isEqualTo("Participation aux activités d'une équipe technique")
            .jsonPath("$.organization")
            .isEqualTo("Tech4Future")
            .jsonPath("$.result")
            .isEqualTo("Acquisition de premières compétences techniques")
            .jsonPath("$.sourceOfInformation")
            .isEqualTo("Conseiller d'orientation")
            .jsonPath("$.startDate")
            .isNotEmpty()
            .jsonPath("$.endDate")
            .isNotEmpty()
            .jsonPath("$.createdAt")
            .isNotEmpty()
            .jsonPath("$.updatedAt")
            .isNotEmpty();
      }

      @Test
      void shouldReturn404WhenDeclaredProgramNotFound() {
        BddLogger.given("the " + BASE_PATH + "/{declaredProgramId} GET endpoint");
        BddLogger.when("performing a GET for a non-existing declared program");
        BddLogger.then("it should return 404 DECLARED_PROGRAM_NOT_FOUND");

        String unknownId = UUID.randomUUID().toString();

        webTestClient
            .get()
            .uri(BASE_PATH + "/" + unknownId)
            .header(HttpHeaders.ACCEPT_LANGUAGE, ELanguage.FRENCH.getCode())
            .header(AvenirsSecurityHeaders.SIGNED_CONTEXT, studentPayload)
            .header(AvenirsSecurityHeaders.CONTEXT_KID, secretKey)
            .header(AvenirsSecurityHeaders.CONTEXT_SIGNATURE, studentSignature)
            .exchange()
            .expectStatus()
            .isNotFound()
            .expectBody()
            .jsonPath("$.code")
            .isEqualTo("DECLARED_PROGRAM_NOT_FOUND");
      }

      @Test
      void shouldReturn400WhenDeclaredProgramIdIsNotUUID() {
        BddLogger.given("the " + BASE_PATH + "/{declaredProgramId} GET endpoint");
        BddLogger.when("performing a GET with an invalid UUID");
        BddLogger.then("it should return 400");

        webTestClient
            .get()
            .uri(BASE_PATH + "/not-a-uuid")
            .header(HttpHeaders.ACCEPT_LANGUAGE, ELanguage.FRENCH.getCode())
            .header(AvenirsSecurityHeaders.SIGNED_CONTEXT, studentPayload)
            .header(AvenirsSecurityHeaders.CONTEXT_KID, secretKey)
            .header(AvenirsSecurityHeaders.CONTEXT_SIGNATURE, studentSignature)
            .exchange()
            .expectStatus()
            .isBadRequest();
      }

      @Test
      void shouldReturn403WhenDeclaredProgramBelongsToAnotherStudent() {
        BddLogger.given("the " + BASE_PATH + "/{declaredProgramId} GET endpoint");
        BddLogger.when(
            "performing a GET for an existing declared program owned by another student");
        BddLogger.then("it should return 403");

        String id =
            createDeclaredProgramAndReturnId(
                new DeclaredProgramRequestDTO(
                    "Programme privé",
                    "Description",
                    "Org",
                    "Result",
                    "Source",
                    LocalDate.now().minusMonths(1),
                    LocalDate.now()),
                secondStudentPayload,
                secondStudentSignature);

        webTestClient
            .get()
            .uri(BASE_PATH + "/" + id)
            .header(HttpHeaders.ACCEPT_LANGUAGE, ELanguage.FRENCH.getCode())
            .header(AvenirsSecurityHeaders.SIGNED_CONTEXT, studentPayload)
            .header(AvenirsSecurityHeaders.CONTEXT_KID, secretKey)
            .header(AvenirsSecurityHeaders.CONTEXT_SIGNATURE, studentSignature)
            .exchange()
            .expectStatus()
            .isForbidden();
      }
    }

    @Nested
    class WhenGettingDeclaredPrograms {

      @Test
      void shouldReturn200WhenDeclaredProgramsExistsAndBelongsToLoggedInStudent() {
        BddLogger.given("the " + BASE_PATH + " GET endpoint");
        BddLogger.when(
            "performing a GET for existing declared programs owned by the logged-in student");
        BddLogger.then("it should return 200 with declared programs dto");

        webTestClient
            .get()
            .uri(BASE_PATH)
            .header(HttpHeaders.ACCEPT_LANGUAGE, ELanguage.FRENCH.getCode())
            .header(AvenirsSecurityHeaders.SIGNED_CONTEXT, studentPayload)
            .header(AvenirsSecurityHeaders.CONTEXT_KID, secretKey)
            .header(AvenirsSecurityHeaders.CONTEXT_SIGNATURE, studentSignature)
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody()
            .jsonPath("$.data[?(@.title == 'Stage développeur web')].status")
            .isEqualTo(EProgramStatus.COMPLETED.name())
            .jsonPath("$.data[?(@.title == 'Stage développeur web')].organization")
            .isEqualTo("TechNova")
            .jsonPath("$.data[?(@.title == 'Stage développeur web')].result")
            .isEqualTo("Mise en production de plusieurs fonctionnalités.")
            .jsonPath("$.data[?(@.title == 'Séminaire leadership et communication')].status")
            .isEqualTo(EProgramStatus.COMPLETED.name())
            .jsonPath("$.data[?(@.title == 'Séminaire leadership et communication')].organization")
            .isEqualTo("LeadPro")
            .jsonPath("$.data[?(@.title == 'Séminaire leadership et communication')].result")
            .isEqualTo("Certificat obtenu");
      }
    }

    @Nested
    class WhenUpdatingADeclaredProgram {

      @Test
      void shouldUpdateDeclaredProgramSuccessfully() {
        BddLogger.given("the " + BASE_PATH + "/{declaredProgramId} PUT endpoint");
        BddLogger.when("performing a PUT with a valid payload as the owner student");
        BddLogger.then("it should return 200 and the updated declared program");

        // Arrange: create a program owned by the logged-in student
        String id =
            createDeclaredProgramAndReturnId(
                new DeclaredProgramRequestDTO(
                    "Stage d'observation",
                    "Participation aux activités d'une équipe technique",
                    "Tech4Future",
                    "Acquisition de premières compétences techniques",
                    "Conseiller d'orientation",
                    LocalDate.now().minusMonths(1),
                    LocalDate.now()),
                studentPayload,
                studentSignature);

        // Act: update it
        DeclaredProgramRequestDTO updateBody =
            new DeclaredProgramRequestDTO(
                "Stage d'observation - UPDATE",
                "Nouvelle description",
                "Tech4Future UPDATED",
                "Nouveau résultat",
                "Nouvelle source",
                LocalDate.now().minusDays(5),
                LocalDate.now().plusDays(5));

        webTestClient
            .put()
            .uri(BASE_PATH + "/" + id)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(writeValueAsString(updateBody))
            .header(HttpHeaders.ACCEPT_LANGUAGE, ELanguage.FRENCH.getCode())
            .header(AvenirsSecurityHeaders.SIGNED_CONTEXT, studentPayload)
            .header(AvenirsSecurityHeaders.CONTEXT_KID, secretKey)
            .header(AvenirsSecurityHeaders.CONTEXT_SIGNATURE, studentSignature)
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody()
            .jsonPath("$.id")
            .isEqualTo(id)
            .jsonPath("$.title")
            .isEqualTo("Stage d'observation - UPDATE")
            .jsonPath("$.description")
            .isEqualTo("Nouvelle description")
            .jsonPath("$.organization")
            .isEqualTo("Tech4Future UPDATED")
            .jsonPath("$.result")
            .isEqualTo("Nouveau résultat")
            .jsonPath("$.sourceOfInformation")
            .isEqualTo("Nouvelle source")
            .jsonPath("$.status")
            .isEqualTo(EProgramStatus.IN_PROGRESS.name())
            .jsonPath("$.startDate")
            .isNotEmpty()
            .jsonPath("$.endDate")
            .isNotEmpty()
            .jsonPath("$.updatedAt")
            .isNotEmpty();

        // Bonus: GET after update to ensure persistence and returned object match
        webTestClient
            .get()
            .uri(BASE_PATH + "/" + id)
            .header(HttpHeaders.ACCEPT_LANGUAGE, ELanguage.FRENCH.getCode())
            .header(AvenirsSecurityHeaders.SIGNED_CONTEXT, studentPayload)
            .header(AvenirsSecurityHeaders.CONTEXT_KID, secretKey)
            .header(AvenirsSecurityHeaders.CONTEXT_SIGNATURE, studentSignature)
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody()
            .jsonPath("$.title")
            .isEqualTo("Stage d'observation - UPDATE")
            .jsonPath("$.organization")
            .isEqualTo("Tech4Future UPDATED")
            .jsonPath("$.status")
            .isEqualTo(EProgramStatus.IN_PROGRESS.name());
      }

      @Test
      void shouldReturn404WhenDeclaredProgramNotFound() {
        BddLogger.given("the " + BASE_PATH + "/{declaredProgramId} PUT endpoint");
        BddLogger.when("performing a PUT for a non-existing declared program");
        BddLogger.then("it should return 404 DECLARED_PROGRAM_NOT_FOUND");

        String unknownId = UUID.randomUUID().toString();

        DeclaredProgramRequestDTO updateBody =
            new DeclaredProgramRequestDTO(
                "Title",
                "Description",
                "Org",
                "Result",
                "Source",
                LocalDate.now().minusDays(10),
                LocalDate.now().minusDays(5));

        webTestClient
            .put()
            .uri(BASE_PATH + "/" + unknownId)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(writeValueAsString(updateBody))
            .header(HttpHeaders.ACCEPT_LANGUAGE, ELanguage.FRENCH.getCode())
            .header(AvenirsSecurityHeaders.SIGNED_CONTEXT, studentPayload)
            .header(AvenirsSecurityHeaders.CONTEXT_KID, secretKey)
            .header(AvenirsSecurityHeaders.CONTEXT_SIGNATURE, studentSignature)
            .exchange()
            .expectStatus()
            .isNotFound()
            .expectBody()
            .jsonPath("$.code")
            .isEqualTo("DECLARED_PROGRAM_NOT_FOUND");
      }

      @Test
      void shouldReturn403WhenDeclaredProgramBelongsToAnotherStudent() {
        BddLogger.given("the " + BASE_PATH + "/{declaredProgramId} PUT endpoint");
        BddLogger.when("performing a PUT on a declared program owned by another student");
        BddLogger.then("it should return 403");

        String id =
            createDeclaredProgramAndReturnId(
                new DeclaredProgramRequestDTO(
                    "Programme privé",
                    "Description",
                    "Org",
                    "Result",
                    "Source",
                    LocalDate.now().minusMonths(1),
                    LocalDate.now()),
                secondStudentPayload,
                secondStudentSignature);

        DeclaredProgramRequestDTO updateBody =
            new DeclaredProgramRequestDTO(
                "Update attempt",
                "Description",
                "Org",
                "Result",
                "Source",
                LocalDate.now().minusDays(10),
                LocalDate.now().minusDays(5));

        webTestClient
            .put()
            .uri(BASE_PATH + "/" + id)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(writeValueAsString(updateBody))
            .header(HttpHeaders.ACCEPT_LANGUAGE, ELanguage.FRENCH.getCode())
            .header(AvenirsSecurityHeaders.SIGNED_CONTEXT, studentPayload)
            .header(AvenirsSecurityHeaders.CONTEXT_KID, secretKey)
            .header(AvenirsSecurityHeaders.CONTEXT_SIGNATURE, studentSignature)
            .exchange()
            .expectStatus()
            .isForbidden();
      }

      @Test
      void shouldReturn400WhenDeclaredProgramIdIsNotUUID() {
        BddLogger.given("the " + BASE_PATH + "/{declaredProgramId} PUT endpoint");
        BddLogger.when("performing a PUT with an invalid UUID");
        BddLogger.then("it should return 400");

        DeclaredProgramRequestDTO updateBody =
            new DeclaredProgramRequestDTO(
                "Title",
                "Description",
                "Org",
                "Result",
                "Source",
                LocalDate.now().minusDays(10),
                LocalDate.now().minusDays(5));

        webTestClient
            .put()
            .uri(BASE_PATH + "/not-a-uuid")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(writeValueAsString(updateBody))
            .header(HttpHeaders.ACCEPT_LANGUAGE, ELanguage.FRENCH.getCode())
            .header(AvenirsSecurityHeaders.SIGNED_CONTEXT, studentPayload)
            .header(AvenirsSecurityHeaders.CONTEXT_KID, secretKey)
            .header(AvenirsSecurityHeaders.CONTEXT_SIGNATURE, studentSignature)
            .exchange()
            .expectStatus()
            .isBadRequest();
      }

      @Test
      void shouldReturn400WhenStartDateIsMissing() {
        BddLogger.given("the " + BASE_PATH + "/{declaredProgramId} PUT endpoint");
        BddLogger.when("performing a PUT with a missing startDate");
        BddLogger.then("it should return 400 NOT_NULL for startDate");

        String id =
            createDeclaredProgramAndReturnId(
                new DeclaredProgramRequestDTO(
                    "Stage d'observation",
                    "Description",
                    "Tech4Future",
                    "Result",
                    "Source",
                    LocalDate.now().minusMonths(1),
                    LocalDate.now()),
                studentPayload,
                studentSignature);

        DeclaredProgramRequestDTO updateBody =
            new DeclaredProgramRequestDTO(
                "Title", "Description", "Org", "Result", "Source", null, LocalDate.now());

        webTestClient
            .put()
            .uri(BASE_PATH + "/" + id)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(writeValueAsString(updateBody))
            .header(HttpHeaders.ACCEPT_LANGUAGE, ELanguage.FRENCH.getCode())
            .header(AvenirsSecurityHeaders.SIGNED_CONTEXT, studentPayload)
            .header(AvenirsSecurityHeaders.CONTEXT_KID, secretKey)
            .header(AvenirsSecurityHeaders.CONTEXT_SIGNATURE, studentSignature)
            .exchange()
            .expectStatus()
            .isBadRequest()
            .expectBody()
            .jsonPath("$.code")
            .isEqualTo("NOT_NULL")
            .jsonPath("$.message")
            .isEqualTo("The field startDate cannot be null");
      }

      @Test
      void shouldReturn400WhenTitleTooLong() {
        BddLogger.given("the " + BASE_PATH + "/{declaredProgramId} PUT endpoint");
        BddLogger.when("performing a PUT with a title longer than 80 characters");
        BddLogger.then("it should return 400 TOO_LONG");

        String id =
            createDeclaredProgramAndReturnId(
                new DeclaredProgramRequestDTO(
                    "Stage d'observation",
                    "Description",
                    "Tech4Future",
                    "Result",
                    "Source",
                    LocalDate.now().minusMonths(1),
                    LocalDate.now()),
                studentPayload,
                studentSignature);

        String longTitle = "T".repeat(81);

        DeclaredProgramRequestDTO updateBody =
            new DeclaredProgramRequestDTO(
                longTitle,
                "Description",
                "Tech4Future",
                "Result",
                "Source",
                LocalDate.now().minusDays(10),
                LocalDate.now().minusDays(5));

        webTestClient
            .put()
            .uri(BASE_PATH + "/" + id)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(writeValueAsString(updateBody))
            .header(HttpHeaders.ACCEPT_LANGUAGE, ELanguage.FRENCH.getCode())
            .header(AvenirsSecurityHeaders.SIGNED_CONTEXT, studentPayload)
            .header(AvenirsSecurityHeaders.CONTEXT_KID, secretKey)
            .header(AvenirsSecurityHeaders.CONTEXT_SIGNATURE, studentSignature)
            .exchange()
            .expectStatus()
            .isBadRequest()
            .expectBody()
            .jsonPath("$.code")
            .isEqualTo("TOO_LONG");
      }

      @Test
      void shouldReturn404WhenUserNotFound() {
        BddLogger.given("the " + BASE_PATH + "/{declaredProgramId} PUT endpoint");
        BddLogger.when("performing a PUT with an unknown user");
        BddLogger.then("it should return 404 USER_NOT_FOUND");

        String id =
            createDeclaredProgramAndReturnId(
                new DeclaredProgramRequestDTO(
                    "Stage d'observation",
                    "Description",
                    "Tech4Future",
                    "Result",
                    "Source",
                    LocalDate.now().minusMonths(1),
                    LocalDate.now()),
                studentPayload,
                studentSignature);

        DeclaredProgramRequestDTO updateBody =
            new DeclaredProgramRequestDTO(
                "Title",
                "Description",
                "Org",
                "Result",
                "Source",
                LocalDate.now().minusDays(10),
                LocalDate.now().minusDays(5));

        webTestClient
            .put()
            .uri(BASE_PATH + "/" + id)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(writeValueAsString(updateBody))
            .header(HttpHeaders.ACCEPT_LANGUAGE, ELanguage.FRENCH.getCode())
            .header(AvenirsSecurityHeaders.SIGNED_CONTEXT, unknownUserPayload)
            .header(AvenirsSecurityHeaders.CONTEXT_KID, secretKey)
            .header(AvenirsSecurityHeaders.CONTEXT_SIGNATURE, unknownUserSignature)
            .exchange()
            .expectStatus()
            .isUnauthorized()
            .expectBody()
            .jsonPath("$.code")
            .isEqualTo("USER_NOT_AUTHORIZED");
      }
    }

    @Nested
    class WhenDeletingDeclaredPrograms {

      @Test
      void shouldDeleteDeclaredProgramsSuccessfully() {
        BddLogger.given("the " + BASE_PATH + " DELETE endpoint");
        BddLogger.when("performing a DELETE with ids owned by the logged-in student");
        BddLogger.then("it should return 200 and the programs should be deleted");

        String id1 =
            createDeclaredProgramAndReturnId(
                new DeclaredProgramRequestDTO(
                    "Prog 1",
                    "Desc",
                    "Org",
                    "Result",
                    "Source",
                    LocalDate.now().minusMonths(1),
                    LocalDate.now()),
                studentPayload,
                studentSignature);

        String id2 =
            createDeclaredProgramAndReturnId(
                new DeclaredProgramRequestDTO(
                    "Prog 2",
                    "Desc",
                    "Org",
                    "Result",
                    "Source",
                    LocalDate.now().minusMonths(2),
                    LocalDate.now().minusMonths(1)),
                studentPayload,
                studentSignature);

        webTestClient
            .method(HttpMethod.DELETE)
            .uri(BASE_PATH)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(writeValueAsString(List.of(UUID.fromString(id1), UUID.fromString(id2))))
            .header(HttpHeaders.ACCEPT_LANGUAGE, ELanguage.FRENCH.getCode())
            .header(AvenirsSecurityHeaders.SIGNED_CONTEXT, studentPayload)
            .header(AvenirsSecurityHeaders.CONTEXT_KID, secretKey)
            .header(AvenirsSecurityHeaders.CONTEXT_SIGNATURE, studentSignature)
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody(String.class)
            .isEqualTo("Declared programs deleted successfully.");

        // Assert deletion: each GET should now be 404
        webTestClient
            .get()
            .uri(BASE_PATH + "/" + id1)
            .header(HttpHeaders.ACCEPT_LANGUAGE, ELanguage.FRENCH.getCode())
            .header(AvenirsSecurityHeaders.SIGNED_CONTEXT, studentPayload)
            .header(AvenirsSecurityHeaders.CONTEXT_KID, secretKey)
            .header(AvenirsSecurityHeaders.CONTEXT_SIGNATURE, studentSignature)
            .exchange()
            .expectStatus()
            .isNotFound()
            .expectBody()
            .jsonPath("$.code")
            .isEqualTo("DECLARED_PROGRAM_NOT_FOUND");

        webTestClient
            .get()
            .uri(BASE_PATH + "/" + id2)
            .header(HttpHeaders.ACCEPT_LANGUAGE, ELanguage.FRENCH.getCode())
            .header(AvenirsSecurityHeaders.SIGNED_CONTEXT, studentPayload)
            .header(AvenirsSecurityHeaders.CONTEXT_KID, secretKey)
            .header(AvenirsSecurityHeaders.CONTEXT_SIGNATURE, studentSignature)
            .exchange()
            .expectStatus()
            .isNotFound()
            .expectBody()
            .jsonPath("$.code")
            .isEqualTo("DECLARED_PROGRAM_NOT_FOUND");
      }

      @Test
      void shouldReturn403WhenTryingToDeleteProgramsOfAnotherStudent() {
        BddLogger.given("the " + BASE_PATH + " DELETE endpoint");
        BddLogger.when("performing a DELETE including an id owned by another student");
        BddLogger.then("it should return 403 and not delete anything");

        String ownedByOtherStudent =
            createDeclaredProgramAndReturnId(
                new DeclaredProgramRequestDTO(
                    "Other student program",
                    "Desc",
                    "Org",
                    "Result",
                    "Source",
                    LocalDate.now().minusMonths(1),
                    LocalDate.now()),
                secondStudentPayload,
                secondStudentSignature);

        webTestClient
            .method(HttpMethod.DELETE)
            .uri(BASE_PATH)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(writeValueAsString(List.of(UUID.fromString(ownedByOtherStudent))))
            .header(HttpHeaders.ACCEPT_LANGUAGE, ELanguage.FRENCH.getCode())
            .header(AvenirsSecurityHeaders.SIGNED_CONTEXT, studentPayload)
            .header(AvenirsSecurityHeaders.CONTEXT_KID, secretKey)
            .header(AvenirsSecurityHeaders.CONTEXT_SIGNATURE, studentSignature)
            .exchange()
            .expectStatus()
            .isForbidden();
      }

      @Test
      void shouldReturn404WhenUserNotFound() {
        BddLogger.given("the " + BASE_PATH + " DELETE endpoint");
        BddLogger.when("performing a DELETE with an unknown user");
        BddLogger.then("it should return 404 USER_NOT_FOUND");

        webTestClient
            .method(HttpMethod.DELETE)
            .uri(BASE_PATH)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(writeValueAsString(List.of(UUID.randomUUID())))
            .header(HttpHeaders.ACCEPT_LANGUAGE, ELanguage.FRENCH.getCode())
            .header(AvenirsSecurityHeaders.SIGNED_CONTEXT, unknownUserPayload)
            .header(AvenirsSecurityHeaders.CONTEXT_KID, secretKey)
            .header(AvenirsSecurityHeaders.CONTEXT_SIGNATURE, unknownUserSignature)
            .exchange()
            .expectStatus()
            .isUnauthorized()
            .expectBody()
            .jsonPath("$.code")
            .isEqualTo("USER_NOT_AUTHORIZED");
      }

      @Test
      void shouldReturn400WhenBodyContainsInvalidUUID() {
        BddLogger.given("the " + BASE_PATH + " DELETE endpoint");
        BddLogger.when("performing a DELETE with invalid UUID in body");
        BddLogger.then("it should return 400");

        webTestClient
            .method(HttpMethod.DELETE)
            .uri(BASE_PATH)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue("[\"not-a-uuid\"]")
            .header(HttpHeaders.ACCEPT_LANGUAGE, ELanguage.FRENCH.getCode())
            .header(AvenirsSecurityHeaders.SIGNED_CONTEXT, studentPayload)
            .header(AvenirsSecurityHeaders.CONTEXT_KID, secretKey)
            .header(AvenirsSecurityHeaders.CONTEXT_SIGNATURE, studentSignature)
            .exchange()
            .expectStatus()
            .isBadRequest();
      }

      @Test
      void shouldReturn400WhenBodyIsMissing() {
        BddLogger.given("the " + BASE_PATH + " DELETE endpoint");
        BddLogger.when("performing a DELETE without body");
        BddLogger.then("it should return 400");

        webTestClient
            .delete()
            .uri(BASE_PATH)
            .accept(MediaType.APPLICATION_JSON)
            .header(HttpHeaders.ACCEPT_LANGUAGE, ELanguage.FRENCH.getCode())
            .header(AvenirsSecurityHeaders.SIGNED_CONTEXT, studentPayload)
            .header(AvenirsSecurityHeaders.CONTEXT_KID, secretKey)
            .header(AvenirsSecurityHeaders.CONTEXT_SIGNATURE, studentSignature)
            .exchange()
            .expectStatus()
            .isBadRequest();
      }
    }
  }
}
