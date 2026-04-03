package fr.avenirsesr.portfolio.student.progress.imported.application.adapter.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.avenirsesr.portfolio.common.language.domain.model.enums.ELanguage;
import fr.avenirsesr.portfolio.common.security.infrastructure.adapter.model.AvenirsSecurityHeaders;
import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import fr.avenirsesr.portfolio.shared.infrastructure.ContainerConfigurationTest;
import fr.avenirsesr.portfolio.shared.infrastructure.adapter.seeder.SeederRunner;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

class StudentProgressControllerIT extends ContainerConfigurationTest {

  private static final String BASE_PATH = "/me/student-progress";
  private static final String OVERVIEW_BASE_PATH = "/me/student-progress/overview";
  private static final String VIEW_BASE_PATH = "/me/student-progress/view";

  @Autowired private WebTestClient webTestClient;

  @Autowired private ObjectMapper objectMapper;

  @Value("${hmac.secret-key}")
  private String secretKey;

  @Value("${user.student.payload}")
  private String studentPayload;

  @Value("${user.teacher.payload}")
  private String teacherPayload;

  @Value("${user.unknown.payload}")
  private String unknownUserPayload;

  @Value("${user.student.signature}")
  private String studentSignature;

  @Value("${user.teacher.signature}")
  private String teacherSignature;

  @Value("${user.unknown.signature}")
  private String unknownUserSignature;

  private ELanguage language = ELanguage.FRENCH;

  @BeforeAll
  void setup(@Autowired SeederRunner seederRunner) {
    seederRunner.run();
  }

  @Test
  void shouldReturnSkillsOverviewForStudentForOverviewEndpoint() {
    BddLogger.given("the " + OVERVIEW_BASE_PATH + " enpoint");
    BddLogger.when("performing a GET");
    BddLogger.then("it should return skills overview");

    webTestClient
        .get()
        .uri(OVERVIEW_BASE_PATH)
        .header(AvenirsSecurityHeaders.SIGNED_CONTEXT, studentPayload)
        .header(AvenirsSecurityHeaders.CONTEXT_KID, secretKey)
        .header(AvenirsSecurityHeaders.CONTEXT_SIGNATURE, studentSignature)
        .header("Accept-Language", language.getCode())
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody()
        .jsonPath("$")
        .isArray()
        .jsonPath("$[0].id")
        .isEqualTo("96c16cc8-4ae8-4ffe-bb37-d55d0832c36b")
        .jsonPath("$[0].programTitle")
        .isEqualTo("Master Chimie des Matériaux")
        .jsonPath("$[0].skills[0].id")
        .isEqualTo("9dbdbdbf-a4a5-45e7-ab28-5582eb16e0be")
        .jsonPath("$[0].skills[0].name")
        .isEqualTo("Analyser les révolutions politiques")
        .jsonPath("$[0].skills[0].currentSkillLevel")
        .exists();
  }

  @Test
  void shouldReturn404WhenUserNotFoundForOverviewEndpoint() {
    webTestClient
        .get()
        .uri(OVERVIEW_BASE_PATH)
        .header(AvenirsSecurityHeaders.SIGNED_CONTEXT, unknownUserPayload)
        .header(AvenirsSecurityHeaders.CONTEXT_KID, secretKey)
        .header(AvenirsSecurityHeaders.CONTEXT_SIGNATURE, unknownUserSignature)
        .header("Accept-Language", language.getCode())
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectStatus()
        .isNotFound()
        .expectBody()
        .jsonPath("$.message")
        .isEqualTo("User not found")
        .jsonPath("$.code")
        .isEqualTo("USER_NOT_FOUND");
  }

  @Test
  void shouldReturn403WhenUserIsNotStudentForOverviewEndpoint() {
    webTestClient
        .get()
        .uri(OVERVIEW_BASE_PATH)
        .header(AvenirsSecurityHeaders.SIGNED_CONTEXT, teacherPayload)
        .header(AvenirsSecurityHeaders.CONTEXT_KID, secretKey)
        .header(AvenirsSecurityHeaders.CONTEXT_SIGNATURE, teacherSignature)
        .header("Accept-Language", language.getCode())
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectStatus()
        .isForbidden()
        .expectBody()
        .jsonPath("$.message")
        .isEqualTo("User is not student")
        .jsonPath("$.code")
        .isEqualTo("USER_IS_NOT_STUDENT_EXCEPTION");
  }

  @Test
  void shouldFallbackInDefaultLanguageWhenLanguageNotSupportedForOverviewEndpoint() {
    webTestClient
        .get()
        .uri(OVERVIEW_BASE_PATH)
        .header(AvenirsSecurityHeaders.SIGNED_CONTEXT, studentPayload)
        .header(AvenirsSecurityHeaders.CONTEXT_KID, secretKey)
        .header(AvenirsSecurityHeaders.CONTEXT_SIGNATURE, studentSignature)
        .header("Accept-Language", "invalid_language_code")
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody()
        .jsonPath("$")
        .isArray()
        .jsonPath("$[0].id")
        .isEqualTo("96c16cc8-4ae8-4ffe-bb37-d55d0832c36b")
        .jsonPath("$[0].programTitle")
        .isEqualTo("Master Chimie des Matériaux")
        .jsonPath("$[0].skills[0].id")
        .isEqualTo("9dbdbdbf-a4a5-45e7-ab28-5582eb16e0be")
        .jsonPath("$[0].skills[0].name")
        .isEqualTo("Analyser les révolutions politiques")
        .jsonPath("$[0].skills[0].currentSkillLevel")
        .exists();
  }

  @Test
  void shouldReturnSkillsViewForStudentForViewEndpoint() {
    webTestClient
        .get()
        .uri(VIEW_BASE_PATH)
        .header(AvenirsSecurityHeaders.SIGNED_CONTEXT, studentPayload)
        .header(AvenirsSecurityHeaders.CONTEXT_KID, secretKey)
        .header(AvenirsSecurityHeaders.CONTEXT_SIGNATURE, studentSignature)
        .header("Accept-Language", language.getCode())
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody()
        .jsonPath("$")
        .isArray()
        .jsonPath("$[0].id")
        .isEqualTo("96c16cc8-4ae8-4ffe-bb37-d55d0832c36b")
        .jsonPath("$[0].name")
        .isEqualTo("Master Chimie des Matériaux")
        .jsonPath("$[0].skills[0].id")
        .isEqualTo("9dbdbdbf-a4a5-45e7-ab28-5582eb16e0be")
        .jsonPath("$[0].skills[0].name")
        .isEqualTo("Analyser les révolutions politiques")
        .jsonPath("$[0].skills[0].levelCount")
        .isEqualTo(3)
        .jsonPath("$[0].skills[0].currentSkillLevel")
        .exists()
        .jsonPath("$[0].skills[0].currentSkillLevel.traceCount")
        .isEqualTo(0);
  }

  @Test
  void shouldReturn404WhenUserNotFoundForViewEndpoint() {
    webTestClient
        .get()
        .uri(VIEW_BASE_PATH)
        .header(AvenirsSecurityHeaders.SIGNED_CONTEXT, unknownUserPayload)
        .header(AvenirsSecurityHeaders.CONTEXT_KID, secretKey)
        .header(AvenirsSecurityHeaders.CONTEXT_SIGNATURE, unknownUserSignature)
        .header("Accept-Language", language.getCode())
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectStatus()
        .isNotFound()
        .expectBody()
        .jsonPath("$.message")
        .isEqualTo("User not found")
        .jsonPath("$.code")
        .isEqualTo("USER_NOT_FOUND");
  }

  @Test
  void shouldReturn403WhenUserIsNotStudentForViewEndpoint() {
    webTestClient
        .get()
        .uri(VIEW_BASE_PATH)
        .header(AvenirsSecurityHeaders.SIGNED_CONTEXT, teacherPayload)
        .header(AvenirsSecurityHeaders.CONTEXT_KID, secretKey)
        .header(AvenirsSecurityHeaders.CONTEXT_SIGNATURE, teacherSignature)
        .header("Accept-Language", language.getCode())
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectStatus()
        .isForbidden()
        .expectBody()
        .jsonPath("$.message")
        .isEqualTo("User is not student")
        .jsonPath("$.code")
        .isEqualTo("USER_IS_NOT_STUDENT_EXCEPTION");
  }

  @Test
  void shouldFallbackInDefaultLanguageWhenLanguageNotSupportedForViewEndpoint() {
    webTestClient
        .get()
        .uri(VIEW_BASE_PATH)
        .header(AvenirsSecurityHeaders.SIGNED_CONTEXT, studentPayload)
        .header(AvenirsSecurityHeaders.CONTEXT_KID, secretKey)
        .header(AvenirsSecurityHeaders.CONTEXT_SIGNATURE, studentSignature)
        .header("Accept-Language", "invalid_language_code")
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody()
        .jsonPath("$")
        .isArray()
        .jsonPath("$[0].id")
        .isEqualTo("96c16cc8-4ae8-4ffe-bb37-d55d0832c36b")
        .jsonPath("$[0].name")
        .isEqualTo("Master Chimie des Matériaux")
        .jsonPath("$[0].skills[0].id")
        .isEqualTo("9dbdbdbf-a4a5-45e7-ab28-5582eb16e0be")
        .jsonPath("$[0].skills[0].name")
        .isEqualTo("Analyser les révolutions politiques")
        .jsonPath("$[0].skills[0].levelCount")
        .isEqualTo(3)
        .jsonPath("$[0].skills[0].currentSkillLevel")
        .exists()
        .jsonPath("$[0].skills[0].currentSkillLevel.traceCount")
        .isEqualTo(0);
  }

  @Test
  void shouldReturnAllProgramProgressForStudent() {
    webTestClient
        .get()
        .uri(BASE_PATH)
        .header(AvenirsSecurityHeaders.SIGNED_CONTEXT, studentPayload)
        .header(AvenirsSecurityHeaders.CONTEXT_KID, secretKey)
        .header(AvenirsSecurityHeaders.CONTEXT_SIGNATURE, studentSignature)
        .header("Accept-Language", language.getCode())
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody()
        .jsonPath("$")
        .isArray()
        .jsonPath("$[0].id")
        .isEqualTo("96c16cc8-4ae8-4ffe-bb37-d55d0832c36b")
        .jsonPath("$[0].studentId")
        .isEqualTo("0a8700ab-90b6-4a38-8338-acbdd4fbcd3d")
        .jsonPath("$[0].trainingPath.id")
        .isEqualTo("9c1bf1a0-85cc-4e28-9f56-845032d2450f")
        .jsonPath("$[0].trainingPath.name")
        .isEqualTo("Master Chimie des Matériaux")
        .jsonPath("$[0].trainingPath.durationUnit")
        .isEqualTo("WEEK")
        .jsonPath("$[0].trainingPath.durationCount")
        .isEqualTo(2);
  }

  @Test
  void shouldReturn404WhenUserIsUnknown() {
    webTestClient
        .get()
        .uri(BASE_PATH)
        .header(AvenirsSecurityHeaders.SIGNED_CONTEXT, unknownUserPayload)
        .header(AvenirsSecurityHeaders.CONTEXT_KID, secretKey)
        .header(AvenirsSecurityHeaders.CONTEXT_SIGNATURE, unknownUserSignature)
        .header("Accept-Language", language.getCode())
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectStatus()
        .isNotFound()
        .expectBody()
        .jsonPath("$.message")
        .isEqualTo("User not found")
        .jsonPath("$.code")
        .isEqualTo("USER_NOT_FOUND");
  }

  @Test
  void shouldReturn403WhenUserIsNotStudentForAllProgramProgress() {
    webTestClient
        .get()
        .uri(BASE_PATH)
        .header(AvenirsSecurityHeaders.SIGNED_CONTEXT, teacherPayload)
        .header(AvenirsSecurityHeaders.CONTEXT_KID, secretKey)
        .header(AvenirsSecurityHeaders.CONTEXT_SIGNATURE, teacherSignature)
        .header("Accept-Language", language.getCode())
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectStatus()
        .isForbidden()
        .expectBody()
        .jsonPath("$.message")
        .isEqualTo("User is not student")
        .jsonPath("$.code")
        .isEqualTo("USER_IS_NOT_STUDENT_EXCEPTION");
  }

  @Test
  void shouldFallbackInDefaultLanguageWhenLanguageNotSupportedForAllProgramProgress() {
    webTestClient
        .get()
        .uri(BASE_PATH)
        .header(AvenirsSecurityHeaders.SIGNED_CONTEXT, studentPayload)
        .header(AvenirsSecurityHeaders.CONTEXT_KID, secretKey)
        .header(AvenirsSecurityHeaders.CONTEXT_SIGNATURE, studentSignature)
        .header("Accept-Language", "invalid_language_code")
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody()
        .jsonPath("$")
        .isArray()
        .jsonPath("$[0].id")
        .isEqualTo("96c16cc8-4ae8-4ffe-bb37-d55d0832c36b")
        .jsonPath("$[0].studentId")
        .isEqualTo("0a8700ab-90b6-4a38-8338-acbdd4fbcd3d")
        .jsonPath("$[0].trainingPath.id")
        .isEqualTo("9c1bf1a0-85cc-4e28-9f56-845032d2450f")
        .jsonPath("$[0].trainingPath.name")
        .isEqualTo("Master Chimie des Matériaux")
        .jsonPath("$[0].trainingPath.durationUnit")
        .isEqualTo("WEEK")
        .jsonPath("$[0].trainingPath.durationCount")
        .isEqualTo(2);
  }
}
