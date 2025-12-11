package fr.avenirsesr.portfolio.student.progress.application.adapter.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import fr.avenirsesr.portfolio.common.language.domain.model.enums.ELanguage;
import fr.avenirsesr.portfolio.common.security.infrastructure.adapter.model.AvenirsSecurityHeaders;
import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import fr.avenirsesr.portfolio.shared.infrastructure.adapter.seeder.SeederRunner;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Transactional
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
class StudentProgressControllerIT {

  private static final String BASE_PATH = "/me/student-progress";
  private static final String OVERVIEW_BASE_PATH = "/me/student-progress/overview";
  private static final String VIEW_BASE_PATH = "/me/student-progress/view";

  @Autowired private MockMvc mockMvc;

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
  void shouldReturnSkillsOverviewForStudentForOverviewEndpoint() throws Exception {
    BddLogger.given("the " + OVERVIEW_BASE_PATH + " enpoint");
    BddLogger.when("performing a GET");
    BddLogger.then("it should return skills overview");
    mockMvc
        .perform(
            get(OVERVIEW_BASE_PATH)
                .header(AvenirsSecurityHeaders.SIGNED_CONTEXT, studentPayload)
                .header(AvenirsSecurityHeaders.CONTEXT_KID, secretKey)
                .header(AvenirsSecurityHeaders.CONTEXT_SIGNATURE, studentSignature)
                .header(HttpHeaders.ACCEPT_LANGUAGE, language.getCode())
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$[0].id").value("c1206b40-7a05-4488-8496-86cb2acf40ec"))
        .andExpect(jsonPath("$[0].programTitle").value("Program Master Institute"))
        .andExpect(jsonPath("$[0].skills[0].id").value("9cf0d6e7-4736-44c7-bd0a-60e6520ca584"))
        .andExpect(jsonPath("$[0].skills[0].name").value("Skill omnis"))
        .andExpect(jsonPath("$[0].skills[0].currentSkillLevel").exists());
  }

  @Test
  void shouldReturn404WhenUserNotFoundForOverviewEndpoint() throws Exception {
    BddLogger.given("the " + OVERVIEW_BASE_PATH + " enpoint");
    BddLogger.when("performing a GET with a not found user");
    BddLogger.then("it should return a 404");
    mockMvc
        .perform(
            get(OVERVIEW_BASE_PATH)
                .header(AvenirsSecurityHeaders.SIGNED_CONTEXT, unknownUserPayload)
                .header(AvenirsSecurityHeaders.CONTEXT_KID, secretKey)
                .header(AvenirsSecurityHeaders.CONTEXT_SIGNATURE, unknownUserSignature)
                .header(HttpHeaders.ACCEPT_LANGUAGE, language.getCode())
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.message").value("User not found"))
        .andExpect(jsonPath("$.code").value("USER_NOT_FOUND"));
  }

  @Test
  void shouldReturn403WhenUserIsNotStudentForOverviewEndpoint() throws Exception {
    BddLogger.given("the " + OVERVIEW_BASE_PATH + " enpoint");
    BddLogger.when("performing a GET with a non student user");
    BddLogger.then("it should return a 403");
    mockMvc
        .perform(
            get(OVERVIEW_BASE_PATH)
                .header(AvenirsSecurityHeaders.SIGNED_CONTEXT, teacherPayload)
                .header(AvenirsSecurityHeaders.CONTEXT_KID, secretKey)
                .header(AvenirsSecurityHeaders.CONTEXT_SIGNATURE, teacherSignature)
                .header(HttpHeaders.ACCEPT_LANGUAGE, language.getCode())
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isForbidden())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.message").value("User is not student"))
        .andExpect(jsonPath("$.code").value("USER_IS_NOT_STUDENT_EXCEPTION"));
  }

  @Test
  void shouldFallbackInDefaultLanguageWhenLanguageNotSupportedForOverviewEndpoint()
      throws Exception {
    BddLogger.given("the " + OVERVIEW_BASE_PATH + " enpoint");
    BddLogger.when("performing a GET with a not supported language");
    BddLogger.then("it should fallback in default language");
    mockMvc
        .perform(
            get(OVERVIEW_BASE_PATH)
                .header(AvenirsSecurityHeaders.SIGNED_CONTEXT, studentPayload)
                .header(AvenirsSecurityHeaders.CONTEXT_KID, secretKey)
                .header(AvenirsSecurityHeaders.CONTEXT_SIGNATURE, studentSignature)
                .header(HttpHeaders.ACCEPT_LANGUAGE, "invalid_language_code")
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$[0].id").value("c1206b40-7a05-4488-8496-86cb2acf40ec"))
        .andExpect(jsonPath("$[0].programTitle").value("Program Master Institute"))
        .andExpect(jsonPath("$[0].skills[0].id").value("9cf0d6e7-4736-44c7-bd0a-60e6520ca584"))
        .andExpect(jsonPath("$[0].skills[0].name").value("Skill omnis"))
        .andExpect(jsonPath("$[0].skills[0].currentSkillLevel").exists());
  }

  @Test
  void shouldReturnSkillsViewForStudentForViewEndpoint() throws Exception {
    BddLogger.given("the " + VIEW_BASE_PATH + " enpoint");
    BddLogger.when("performing a GET");
    BddLogger.then("it should return the skills view");
    mockMvc
        .perform(
            get(VIEW_BASE_PATH)
                .header(AvenirsSecurityHeaders.SIGNED_CONTEXT, studentPayload)
                .header(AvenirsSecurityHeaders.CONTEXT_KID, secretKey)
                .header(AvenirsSecurityHeaders.CONTEXT_SIGNATURE, studentSignature)
                .header(HttpHeaders.ACCEPT_LANGUAGE, language.getCode())
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$[0].id").value("c1206b40-7a05-4488-8496-86cb2acf40ec"))
        .andExpect(jsonPath("$[0].name").value("Program Master Institute"))
        .andExpect(jsonPath("$[0].skills[0].id").value("4f178261-b7cc-41a0-a1c2-fed8ed234dc3"))
        .andExpect(jsonPath("$[0].skills[0].name").value("Skill culpa"))
        .andExpect(jsonPath("$[0].skills[0].levelCount").value(3))
        .andExpect(jsonPath("$[0].skills[0].currentSkillLevel").exists())
        .andExpect(jsonPath("$[0].skills[0].currentSkillLevel.traceCount").value(0));
  }

  @Test
  void shouldReturn404WhenUserNotFoundForViewEndpoint() throws Exception {
    BddLogger.given("the " + VIEW_BASE_PATH + " enpoint");
    BddLogger.when("performing a GET with a not found user");
    BddLogger.then("it should return a 404");
    mockMvc
        .perform(
            get(VIEW_BASE_PATH)
                .header(AvenirsSecurityHeaders.SIGNED_CONTEXT, unknownUserPayload)
                .header(AvenirsSecurityHeaders.CONTEXT_KID, secretKey)
                .header(AvenirsSecurityHeaders.CONTEXT_SIGNATURE, unknownUserSignature)
                .header(HttpHeaders.ACCEPT_LANGUAGE, language.getCode())
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.message").value("User not found"))
        .andExpect(jsonPath("$.code").value("USER_NOT_FOUND"));
  }

  @Test
  void shouldReturn403WhenUserIsNotStudentForViewEndpoint() throws Exception {
    BddLogger.given("the " + VIEW_BASE_PATH + " enpoint");
    BddLogger.when("performing a GET with a non student user");
    BddLogger.then("it should return a 403");
    mockMvc
        .perform(
            get(VIEW_BASE_PATH)
                .header(AvenirsSecurityHeaders.SIGNED_CONTEXT, teacherPayload)
                .header(AvenirsSecurityHeaders.CONTEXT_KID, secretKey)
                .header(AvenirsSecurityHeaders.CONTEXT_SIGNATURE, teacherSignature)
                .header(HttpHeaders.ACCEPT_LANGUAGE, language.getCode())
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isForbidden())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.message").value("User is not student"))
        .andExpect(jsonPath("$.code").value("USER_IS_NOT_STUDENT_EXCEPTION"));
  }

  @Test
  void shouldFallbackInDefaultLanguageWhenLanguageNotSupportedForViewEndpoint() throws Exception {
    BddLogger.given("the " + VIEW_BASE_PATH + " enpoint");
    BddLogger.when("performing a GET with a not supported language");
    BddLogger.then("it should fallback in default language");
    mockMvc
        .perform(
            get(VIEW_BASE_PATH)
                .header(AvenirsSecurityHeaders.SIGNED_CONTEXT, studentPayload)
                .header(AvenirsSecurityHeaders.CONTEXT_KID, secretKey)
                .header(AvenirsSecurityHeaders.CONTEXT_SIGNATURE, studentSignature)
                .header(HttpHeaders.ACCEPT_LANGUAGE, "invalid_language_code")
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$[0].id").value("c1206b40-7a05-4488-8496-86cb2acf40ec"))
        .andExpect(jsonPath("$[0].name").value("Program Master Institute"))
        .andExpect(jsonPath("$[0].skills[0].id").value("4f178261-b7cc-41a0-a1c2-fed8ed234dc3"))
        .andExpect(jsonPath("$[0].skills[0].name").value("Skill culpa"))
        .andExpect(jsonPath("$[0].skills[0].levelCount").value(3))
        .andExpect(jsonPath("$[0].skills[0].currentSkillLevel").exists())
        .andExpect(jsonPath("$[0].skills[0].currentSkillLevel.traceCount").value(0));
  }

  @Test
  void shouldReturnAllProgramProgressForStudent() throws Exception {
    BddLogger.given("the " + BASE_PATH + " enpoint");
    BddLogger.when("performing a GET with a student user");
    BddLogger.then("it should return all program progresses");
    mockMvc
        .perform(
            get(BASE_PATH)
                .header(AvenirsSecurityHeaders.SIGNED_CONTEXT, studentPayload)
                .header(AvenirsSecurityHeaders.CONTEXT_KID, secretKey)
                .header(AvenirsSecurityHeaders.CONTEXT_SIGNATURE, studentSignature)
                .header(HttpHeaders.ACCEPT_LANGUAGE, language.getCode())
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$[0].id").value("c1206b40-7a05-4488-8496-86cb2acf40ec"))
        .andExpect(jsonPath("$[0].studentId").value("0a8700ab-90b6-4a38-8338-acbdd4fbcd3d"))
        .andExpect(jsonPath("$[0].trainingPath.id").value("430f3ed4-6b38-4602-8bf3-7cccb65ebeac"))
        .andExpect(jsonPath("$[0].trainingPath.name").value("Program Master Institute"))
        .andExpect(jsonPath("$[0].trainingPath.durationUnit").value("TRIMESTER"))
        .andExpect(jsonPath("$[0].trainingPath.durationCount").value(4));
  }

  @Test
  void shouldReturn404WhenUserIsUnknown() throws Exception {
    BddLogger.given("the " + BASE_PATH + " enpoint");
    BddLogger.when("performing a GET with an unknown user");
    BddLogger.then("it should return a 404");
    mockMvc
        .perform(
            get(BASE_PATH)
                .header(AvenirsSecurityHeaders.SIGNED_CONTEXT, unknownUserPayload)
                .header(AvenirsSecurityHeaders.CONTEXT_KID, secretKey)
                .header(AvenirsSecurityHeaders.CONTEXT_SIGNATURE, unknownUserSignature)
                .header(HttpHeaders.ACCEPT_LANGUAGE, language.getCode())
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.message").value("User not found"))
        .andExpect(jsonPath("$.code").value("USER_NOT_FOUND"));
  }

  @Test
  void shouldReturn403WhenUserIsNotStudentForAllProgramProgress() throws Exception {
    BddLogger.given("the " + BASE_PATH + " enpoint");
    BddLogger.when("performing a GET with a non student user");
    BddLogger.then("it should return a 403");
    mockMvc
        .perform(
            get(BASE_PATH)
                .header(AvenirsSecurityHeaders.SIGNED_CONTEXT, teacherPayload)
                .header(AvenirsSecurityHeaders.CONTEXT_KID, secretKey)
                .header(AvenirsSecurityHeaders.CONTEXT_SIGNATURE, teacherSignature)
                .header(HttpHeaders.ACCEPT_LANGUAGE, language.getCode())
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isForbidden())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.message").value("User is not student"))
        .andExpect(jsonPath("$.code").value("USER_IS_NOT_STUDENT_EXCEPTION"));
  }

  @Test
  void shouldFallbackInDefaultLanguageWhenLanguageNotSupportedForAllProgramProgress()
      throws Exception {
    BddLogger.given("the " + BASE_PATH + " enpoint");
    BddLogger.when("performing a GET with a non supported language");
    BddLogger.then("it should fallback in default language");
    mockMvc
        .perform(
            get(BASE_PATH)
                .header(AvenirsSecurityHeaders.SIGNED_CONTEXT, studentPayload)
                .header(AvenirsSecurityHeaders.CONTEXT_KID, secretKey)
                .header(AvenirsSecurityHeaders.CONTEXT_SIGNATURE, studentSignature)
                .header(HttpHeaders.ACCEPT_LANGUAGE, "invalid_language_code")
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$[0].id").value("c1206b40-7a05-4488-8496-86cb2acf40ec"))
        .andExpect(jsonPath("$[0].studentId").value("0a8700ab-90b6-4a38-8338-acbdd4fbcd3d"))
        .andExpect(jsonPath("$[0].trainingPath.id").value("430f3ed4-6b38-4602-8bf3-7cccb65ebeac"))
        .andExpect(jsonPath("$[0].trainingPath.name").value("Program Master Institute"))
        .andExpect(jsonPath("$[0].trainingPath.durationUnit").value("TRIMESTER"))
        .andExpect(jsonPath("$[0].trainingPath.durationCount").value(4));
  }
}
