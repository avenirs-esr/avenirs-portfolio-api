package fr.avenirsesr.portfolio.student.progress.application.adapter.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import fr.avenirsesr.portfolio.common.language.domain.model.enums.ELanguage;
import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import fr.avenirsesr.portfolio.shared.infrastructure.adapter.seeder.SeederRunner;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class StudentProgressControllerIT {

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
  static void setup(@Autowired SeederRunner seederRunner) {
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
                .header("X-Signed-Context", studentPayload)
                .header("X-Context-Kid", secretKey)
                .header("X-Context-Signature", studentSignature)
                .header("Accept-Language", language.getCode())
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$[0].id").value("96c16cc8-4ae8-4ffe-bb37-d55d0832c36b"))
        .andExpect(jsonPath("$[0].programTitle").value("Northern Associate - 5 [fr-FR]"))
        .andExpect(jsonPath("$[0].skills[0].id").value("baed890a-d90f-4845-9e75-9faefbb8e1ae"))
        .andExpect(jsonPath("$[0].skills[0].name").value("Skill laudantium - [fr-FR]"))
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
                .header("X-Signed-Context", unknownUserPayload)
                .header("X-Context-Kid", secretKey)
                .header("X-Context-Signature", unknownUserSignature)
                .header("Accept-Language", language.getCode())
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
                .header("X-Signed-Context", teacherPayload)
                .header("X-Context-Kid", secretKey)
                .header("X-Context-Signature", teacherSignature)
                .header("Accept-Language", language.getCode())
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
                .header("X-Signed-Context", studentPayload)
                .header("X-Context-Kid", secretKey)
                .header("X-Context-Signature", studentSignature)
                .header("Accept-Language", "invalid_language_code")
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$[0].id").value("96c16cc8-4ae8-4ffe-bb37-d55d0832c36b"))
        .andExpect(jsonPath("$[0].programTitle").value("Northern Associate - 5 [fr-FR]"))
        .andExpect(jsonPath("$[0].skills[0].id").value("baed890a-d90f-4845-9e75-9faefbb8e1ae"))
        .andExpect(jsonPath("$[0].skills[0].name").value("Skill laudantium - [fr-FR]"))
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
                .header("X-Signed-Context", studentPayload)
                .header("X-Context-Kid", secretKey)
                .header("X-Context-Signature", studentSignature)
                .header("Accept-Language", language.getCode())
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$[0].id").value("96c16cc8-4ae8-4ffe-bb37-d55d0832c36b"))
        .andExpect(jsonPath("$[0].name").value("Northern Associate - 5 [fr-FR]"))
        .andExpect(jsonPath("$[0].skills[0].id").value("ef456a4d-0892-49ba-b78f-0e540f50cf7c"))
        .andExpect(jsonPath("$[0].skills[0].name").value("Skill ea - [fr-FR]"))
        .andExpect(jsonPath("$[0].skills[0].levelCount").value(3))
        .andExpect(jsonPath("$[0].skills[0].currentSkillLevel").exists())
        .andExpect(jsonPath("$[0].skills[0].currentSkillLevel.traceCount").value(0))
        .andExpect(jsonPath("$[0].skills[0].currentSkillLevel.activityCount").value(0));
  }

  @Test
  void shouldReturn404WhenUserNotFoundForViewEndpoint() throws Exception {
    BddLogger.given("the " + VIEW_BASE_PATH + " enpoint");
    BddLogger.when("performing a GET with a not found user");
    BddLogger.then("it should return a 404");
    mockMvc
        .perform(
            get(VIEW_BASE_PATH)
                .header("X-Signed-Context", unknownUserPayload)
                .header("X-Context-Kid", secretKey)
                .header("X-Context-Signature", unknownUserSignature)
                .header("Accept-Language", language.getCode())
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
                .header("X-Signed-Context", teacherPayload)
                .header("X-Context-Kid", secretKey)
                .header("X-Context-Signature", teacherSignature)
                .header("Accept-Language", language.getCode())
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
                .header("X-Signed-Context", studentPayload)
                .header("X-Context-Kid", secretKey)
                .header("X-Context-Signature", studentSignature)
                .header("Accept-Language", "invalid_language_code")
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$[0].id").value("96c16cc8-4ae8-4ffe-bb37-d55d0832c36b"))
        .andExpect(jsonPath("$[0].name").value("Northern Associate - 5 [fr-FR]"))
        .andExpect(jsonPath("$[0].skills[0].id").value("ef456a4d-0892-49ba-b78f-0e540f50cf7c"))
        .andExpect(jsonPath("$[0].skills[0].name").value("Skill ea - [fr-FR]"))
        .andExpect(jsonPath("$[0].skills[0].levelCount").value(3))
        .andExpect(jsonPath("$[0].skills[0].currentSkillLevel").exists())
        .andExpect(jsonPath("$[0].skills[0].currentSkillLevel.traceCount").value(0))
        .andExpect(jsonPath("$[0].skills[0].currentSkillLevel.activityCount").value(0));
  }
}
