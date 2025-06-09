package fr.avenirsesr.portfolio.api.application.adapter.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import fr.avenirsesr.portfolio.api.infrastructure.adapter.seeder.SeederRunner;
import java.util.UUID;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
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

  private UUID studentId;
  private UUID teacherId;

  @BeforeAll
  static void setup(@Autowired SeederRunner seederRunner) {
    seederRunner.run();
  }

  @BeforeEach
  void setUp() {
    studentId = UUID.fromString("9fe9516a-a528-4870-8f15-89187e368610");
    teacherId = UUID.fromString("56a75a55-2f69-456d-a92d-323149a5ab7f");
  }

  @Test
  void shouldReturnSkillsOverviewForStudent() throws Exception {
    mockMvc
        .perform(
            get("/program-progress/overview")
                .header("X-Signed-Context", studentId.toString())
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$[0].id").value("33184f00-80d4-4b7a-ab7b-14b8ac6ca268"))
        .andExpect(jsonPath("$[0].name").value("North Master - 6"))
        .andExpect(jsonPath("$[0].skills[0].id").value("95af3200-e5d6-4e1d-90e8-67c36ea20cf1"))
        .andExpect(jsonPath("$[0].skills[0].name").value("Skill eius"))
        .andExpect(jsonPath("$[0].skills[0].currentSkillLevel").exists())
        .andExpect(
            jsonPath("$[0].skills[0].currentSkillLevel.id")
                .value("7050764a-3195-4ae7-8d43-510f7d7154ff"))
        .andExpect(jsonPath("$[0].skills[0].currentSkillLevel.name").value("Niv. 1"))
        .andExpect(jsonPath("$[0].skills[0].currentSkillLevel.status").value("UNDER_REVIEW"))
        .andExpect(jsonPath("$[0].skills[1].id").value("721f6782-e37e-4767-b6e6-fb4fd7543803"))
        .andExpect(jsonPath("$[0].skills[1].name").value("Skill est"))
        .andExpect(jsonPath("$[0].skills[1].currentSkillLevel").exists())
        .andExpect(
            jsonPath("$[0].skills[1].currentSkillLevel.id")
                .value("91511147-6488-44ac-866e-88db9a2c8a82"))
        .andExpect(jsonPath("$[0].skills[1].currentSkillLevel.name").value("Niv. 4"))
        .andExpect(jsonPath("$[0].skills[1].currentSkillLevel.status").value("TO_BE_EVALUATED"));
  }

  @Test
  void shouldReturn404WhenUserNotFound() throws Exception {
    mockMvc
        .perform(
            get("/program-progress/overview")
                .header("X-Signed-Context", UUID.randomUUID().toString())
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
            get("/program-progress/overview")
                .header("X-Signed-Context", teacherId.toString())
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isForbidden())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.message").value("User is not student"))
        .andExpect(jsonPath("$.code").value("USER_IS_NOT_STUDENT_EXCEPTION"));
  }
}
