package fr.avenirsesr.portfolio.student.progress.application.adapter.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import fr.avenirsesr.portfolio.shared.domain.model.enums.ELanguage;
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
            get("/me/student-progress/overview")
                .header("X-Signed-Context", studentPayload)
                .header("X-Context-Kid", secretKey)
                .header("X-Context-Signature", studentSignature)
                .header("Accept-Language", language.getCode())
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$[0].id").value("dd6d1d9a-da85-4fd3-94d5-cc89deb42028"))
        .andExpect(jsonPath("$[0].programTitle").value("South Associate - 4 [fr_FR]"))
        .andExpect(jsonPath("$[0].skills[0].id").value("1a480f26-03a4-48da-b29f-3b7afddeda4b"))
        .andExpect(jsonPath("$[0].skills[0].name").value("Skill nesciunt - [fr_FR]"))
        .andExpect(jsonPath("$[0].skills[0].currentSkillLevel").exists())
        .andExpect(jsonPath("$[0].skills[1].id").value("2c25cd90-8d22-432e-82df-ff7b82d51a75"))
        .andExpect(jsonPath("$[0].skills[1].name").value("Skill inventore - [fr_FR]"))
        .andExpect(jsonPath("$[0].skills[1].currentSkillLevel").exists());
  }

  @Test
  void shouldReturn404WhenUserNotFoundForOverviewEndpoint() throws Exception {
    mockMvc
        .perform(
            get("/me/student-progress/overview")
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
            get("/me/student-progress/overview")
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
            get("/me/student-progress/overview")
                .header("X-Signed-Context", studentPayload)
                .header("X-Context-Kid", secretKey)
                .header("X-Context-Signature", studentSignature)
                .header("Accept-Language", "invalid_language_code")
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$[0].id").value("dd6d1d9a-da85-4fd3-94d5-cc89deb42028"))
        .andExpect(jsonPath("$[0].programTitle").value("South Associate - 4 [fr_FR]"))
        .andExpect(jsonPath("$[0].skills[0].id").value("1a480f26-03a4-48da-b29f-3b7afddeda4b"))
        .andExpect(jsonPath("$[0].skills[0].name").value("Skill nesciunt - [fr_FR]"))
        .andExpect(jsonPath("$[0].skills[0].currentSkillLevel").exists());
  }

  @Test
  void shouldReturnSkillsViewForStudentForViewEndpoint() throws Exception {
    mockMvc
        .perform(
            get("/me/student-progress/view")
                .header("X-Signed-Context", studentPayload)
                .header("X-Context-Kid", secretKey)
                .header("X-Context-Signature", studentSignature)
                .header("Accept-Language", language.getCode())
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$[0].id").value("dd6d1d9a-da85-4fd3-94d5-cc89deb42028"))
        .andExpect(jsonPath("$[0].name").value("South Associate - 4 [fr_FR]"))
        .andExpect(jsonPath("$[0].skills[0].id").value("1a480f26-03a4-48da-b29f-3b7afddeda4b"))
        .andExpect(jsonPath("$[0].skills[0].name").value("Skill nesciunt - [fr_FR]"))
        .andExpect(jsonPath("$[0].skills[0].traceCount").value(0))
        .andExpect(jsonPath("$[0].skills[0].levelCount").value(3))
        .andExpect(jsonPath("$[0].skills[0].currentSkillLevel").exists());
  }

  @Test
  void shouldReturn404WhenUserNotFoundForViewEndpoint() throws Exception {
    mockMvc
        .perform(
            get("/me/student-progress/view")
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
            get("/me/student-progress/view")
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
            get("/me/student-progress/view")
                .header("X-Signed-Context", studentPayload)
                .header("X-Context-Kid", secretKey)
                .header("X-Context-Signature", studentSignature)
                .header("Accept-Language", "invalid_language_code")
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$[0].id").value("dd6d1d9a-da85-4fd3-94d5-cc89deb42028"))
        .andExpect(jsonPath("$[0].name").value("South Associate - 4 [fr_FR]"))
        .andExpect(jsonPath("$[0].skills[0].id").value("1a480f26-03a4-48da-b29f-3b7afddeda4b"))
        .andExpect(jsonPath("$[0].skills[0].name").value("Skill nesciunt - [fr_FR]"))
        .andExpect(jsonPath("$[0].skills[0].traceCount").value(0))
        .andExpect(jsonPath("$[0].skills[0].levelCount").value(3))
        .andExpect(jsonPath("$[0].skills[0].currentSkillLevel").exists());
  }
}
