package fr.avenirsesr.portfolio.api.application.adapter.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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

  @BeforeAll
  static void setup(@Autowired SeederRunner seederRunner) {
    seederRunner.run();
  }

  @Test
  void shouldReturnSkillsOverviewForStudent() throws Exception {
    mockMvc
        .perform(
            get("/program-progress/skills/overview")
                .header("X-Signed-Context", studentPayload)
                .header("X-Context-Kid", secretKey)
                .header("X-Context-Signature", studentSignature)
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$[0].id").value("33184f00-80d4-4b7a-ab7b-14b8ac6ca268"))
        .andExpect(jsonPath("$[0].name").value("North Master - 6"))
        .andExpect(jsonPath("$[0].skills[0].id").value("95af3200-e5d6-4e1d-90e8-67c36ea20cf1"))
        .andExpect(jsonPath("$[0].skills[0].name").value("Skill eius"))
        .andExpect(jsonPath("$[0].skills[0].levels.size()").value(1))
        .andExpect(
            jsonPath("$[0].skills[0].levels[0].id").value("7050764a-3195-4ae7-8d43-510f7d7154ff"))
        .andExpect(jsonPath("$[0].skills[0].levels[0].name").value("Niv. 1"))
        .andExpect(jsonPath("$[0].skills[0].levels[0].status").value("UNDER_REVIEW"))
        .andExpect(jsonPath("$[0].skills[1].id").value("721f6782-e37e-4767-b6e6-fb4fd7543803"))
        .andExpect(jsonPath("$[0].skills[1].name").value("Skill est"))
        .andExpect(jsonPath("$[0].skills[1].levels.size()").value(1))
        .andExpect(
            jsonPath("$[0].skills[1].levels[0].id").value("91511147-6488-44ac-866e-88db9a2c8a82"))
        .andExpect(jsonPath("$[0].skills[1].levels[0].name").value("Niv. 4"))
        .andExpect(jsonPath("$[0].skills[1].levels[0].status").value("TO_BE_EVALUATED"));
  }

  @Test
  void shouldReturn404WhenUserNotFound() throws Exception {
    mockMvc
        .perform(
            get("/program-progress/skills/overview")
                .header("X-Signed-Context", unknownUserPayload)
                .header("X-Context-Kid", secretKey)
                .header("X-Context-Signature", unknownUserSignature)
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.message").value("User not found"))
        .andExpect(jsonPath("$.code").value("USER_NOT_FOUND"));
  }

  @Test
  void shouldReturn403WhenUserIsNotStudent() throws Exception {
    mockMvc
        .perform(
            get("/program-progress/skills/overview")
                .header("X-Signed-Context", teacherPayload)
                .header("X-Context-Kid", secretKey)
                .header("X-Context-Signature", teacherSignature)
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isForbidden())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.message").value("User is not student"))
        .andExpect(jsonPath("$.code").value("USER_IS_NOT_STUDENT_EXCEPTION"));
  }
}
