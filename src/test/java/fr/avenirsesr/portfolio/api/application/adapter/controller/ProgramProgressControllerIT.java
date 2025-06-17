package fr.avenirsesr.portfolio.api.application.adapter.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import fr.avenirsesr.portfolio.api.domain.model.enums.ELanguage;
import fr.avenirsesr.portfolio.api.infrastructure.adapter.seeder.SeederRunner;
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
class ProgramProgressControllerIT {

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
    mockMvc
        .perform(
            get("/me/program-progress/overview")
                .header("X-Signed-Context", studentPayload)
                .header("X-Context-Kid", secretKey)
                .header("X-Context-Signature", studentSignature)
                .header("Accept-Language", language.getCode())
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$[0].id").value("f01fe339-f9d4-4fbc-9fac-4a4b73a84702"))
        .andExpect(jsonPath("$[0].name").value("Western Master - 8"))
        .andExpect(jsonPath("$[0].skills[0].id").value("18516fa2-79cf-43e9-8ccb-3be357a5882e"))
        .andExpect(jsonPath("$[0].skills[0].name").value("Skill amet"))
        .andExpect(jsonPath("$[0].skills[0].currentSkillLevel").exists())
        .andExpect(
            jsonPath("$[0].skills[0].currentSkillLevel.id")
                .value("802fde10-2cd0-47cb-9660-ecd1634ab506"))
        .andExpect(jsonPath("$[0].skills[0].currentSkillLevel.name").value("Niv. 1"))
        .andExpect(jsonPath("$[0].skills[0].currentSkillLevel.status").value("UNDER_REVIEW"))
        .andExpect(jsonPath("$[0].skills[1].id").value("7a9be554-3616-44f5-a49a-07ed4755bc0a"))
        .andExpect(jsonPath("$[0].skills[1].name").value("Skill distinctio"))
        .andExpect(jsonPath("$[0].skills[1].currentSkillLevel").exists())
        .andExpect(
            jsonPath("$[0].skills[1].currentSkillLevel.id")
                .value("0395bac3-9cd5-4df2-ae24-6b053b09bad2"))
        .andExpect(jsonPath("$[0].skills[1].currentSkillLevel.name").value("Niv. 2"))
        .andExpect(jsonPath("$[0].skills[1].currentSkillLevel.status").value("TO_BE_EVALUATED"));
  }

  @Test
  void shouldReturn404WhenUserNotFoundForOverviewEndpoint() throws Exception {
    mockMvc
        .perform(
            get("/me/program-progress/overview")
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
    mockMvc
        .perform(
            get("/me/program-progress/overview")
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
    mockMvc
        .perform(
            get("/me/program-progress/overview")
                .header("X-Signed-Context", studentPayload)
                .header("X-Context-Kid", secretKey)
                .header("X-Context-Signature", studentSignature)
                .header("Accept-Language", "invalid_language_code")
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$[0].id").value("f01fe339-f9d4-4fbc-9fac-4a4b73a84702"))
        .andExpect(jsonPath("$[0].name").value("Western Master - 8"))
        .andExpect(jsonPath("$[0].skills[0].id").value("18516fa2-79cf-43e9-8ccb-3be357a5882e"))
        .andExpect(jsonPath("$[0].skills[0].name").value("Skill amet"))
        .andExpect(jsonPath("$[0].skills[0].currentSkillLevel").exists());
  }

  @Test
  void shouldReturnSkillsOverviewForStudentForViewEndpoint() throws Exception {
    mockMvc
        .perform(
            get("/me/program-progress/view")
                .header("X-Signed-Context", studentPayload)
                .header("X-Context-Kid", secretKey)
                .header("X-Context-Signature", studentSignature)
                .header("Accept-Language", language.getCode())
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$[0].id").value("f01fe339-f9d4-4fbc-9fac-4a4b73a84702"))
        .andExpect(jsonPath("$[0].name").value("Western Master - 8"))
        .andExpect(jsonPath("$[0].skills[0].id").value("18516fa2-79cf-43e9-8ccb-3be357a5882e"))
        .andExpect(jsonPath("$[0].skills[0].name").value("Skill amet"))
        .andExpect(jsonPath("$[0].skills[0].traceCount").value(1))
        .andExpect(jsonPath("$[0].skills[0].levelCount").value(3))
        .andExpect(jsonPath("$[0].skills[0].currentSkillLevel").exists())
        .andExpect(
            jsonPath("$[0].skills[0].currentSkillLevel.id")
                .value("802fde10-2cd0-47cb-9660-ecd1634ab506"))
        .andExpect(jsonPath("$[0].skills[0].currentSkillLevel.name").value("Niv. 1"))
        .andExpect(
            jsonPath("$[0].skills[0].currentSkillLevel.shortDescription")
                .value("Perspiciatis nihil quos."))
        .andExpect(jsonPath("$[0].skills[0].currentSkillLevel.status").value("UNDER_REVIEW"));
  }

  @Test
  void shouldReturn404WhenUserNotFoundForViewEndpoint() throws Exception {
    mockMvc
        .perform(
            get("/me/program-progress/view")
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
    mockMvc
        .perform(
            get("/me/program-progress/view")
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
    mockMvc
        .perform(
            get("/me/program-progress/view")
                .header("X-Signed-Context", studentPayload)
                .header("X-Context-Kid", secretKey)
                .header("X-Context-Signature", studentSignature)
                .header("Accept-Language", "invalid_language_code")
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$[0].id").value("f01fe339-f9d4-4fbc-9fac-4a4b73a84702"))
        .andExpect(jsonPath("$[0].name").value("Western Master - 8"))
        .andExpect(jsonPath("$[0].skills[0].id").value("18516fa2-79cf-43e9-8ccb-3be357a5882e"))
        .andExpect(jsonPath("$[0].skills[0].name").value("Skill amet"))
        .andExpect(jsonPath("$[0].skills[0].traceCount").value(1))
        .andExpect(jsonPath("$[0].skills[0].levelCount").value(3))
        .andExpect(jsonPath("$[0].skills[0].currentSkillLevel").exists());
  }

  @Test
  void shouldReturnAllProgramProgressForStudent() throws Exception {
    mockMvc
        .perform(
            get("/me/program-progress")
                .header("X-Signed-Context", studentPayload)
                .header("X-Context-Kid", secretKey)
                .header("X-Context-Signature", studentSignature)
                .header("Accept-Language", language.getCode())
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$[0].id").value("f01fe339-f9d4-4fbc-9fac-4a4b73a84702"))
        .andExpect(jsonPath("$[0].name").value("Western Master - 8"));
  }

  @Test
  void shouldReturn404WhenUserIsUnknown() throws Exception {
    mockMvc
        .perform(
            get("/me/program-progress")
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
  void shouldReturn403WhenUserIsNotStudentForAllProgramProgress() throws Exception {
    mockMvc
        .perform(
            get("/me/program-progress")
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
  void shouldFallbackInDefaultLanguageWhenLanguageNotSupportedForAllProgramProgress()
      throws Exception {
    mockMvc
        .perform(
            get("/me/program-progress")
                .header("X-Signed-Context", studentPayload)
                .header("X-Context-Kid", secretKey)
                .header("X-Context-Signature", studentSignature)
                .header("Accept-Language", "invalid_language_code")
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$[0].id").value("f01fe339-f9d4-4fbc-9fac-4a4b73a84702"))
        .andExpect(jsonPath("$[0].name").value("Western Master - 8"));
  }
}
