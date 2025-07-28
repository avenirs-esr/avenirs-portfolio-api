package fr.avenirsesr.portfolio.additionalskill.application.adapter.controller;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import fr.avenirsesr.portfolio.shared.infrastructure.adapter.seeder.SeederRunner;
import java.util.UUID;
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
public class AdditionalSkillControllerIT {

  @Autowired private MockMvc mockMvc;

  @Value("${hmac.secret-key}")
  private String secretKey;

  @Value("${user.student.payload}")
  private String studentPayload;

  @Value("${user.student.signature}")
  private String studentSignature;

  @BeforeAll
  static void setup(@Autowired SeederRunner seederRunner) {
    seederRunner.run();
  }

  @Test
  void shouldReturnPagedAdditionalSkillProgresses() throws Exception {
    mockMvc
        .perform(
            get("/me/additional-skills")
                .header("X-Signed-Context", studentPayload)
                .header("X-Context-Kid", secretKey)
                .header("X-Context-Signature", studentSignature)
                .param("page", "0")
                .param("pageSize", "5"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data", notNullValue()))
        .andExpect(jsonPath("$.page.totalElements", greaterThanOrEqualTo(0)))
        .andExpect(jsonPath("$.page.page").value(0))
        .andExpect(jsonPath("$.page.pageSize").value(5));
  }

  @Test
  void shouldSearchAdditionalSkillsByKeyword() throws Exception {
    mockMvc
        .perform(
            get("/me/additional-skills")
                .header("X-Signed-Context", studentPayload)
                .header("X-Context-Kid", secretKey)
                .header("X-Context-Signature", studentSignature)
                .param("keyword", "acc")
                .param("page", "0")
                .param("pageSize", "5"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data", notNullValue()))
        .andExpect(jsonPath("$.page.page").value(0))
        .andExpect(jsonPath("$.page.pageSize").value(5));
  }

  @Test
  void shouldCreateAdditionalSkillProgress() throws Exception {
    String payloadJson =
        """
      {
        "id": "3f024a1c-5429-43f6-bb2e-ac5a3ca662e6",
        "level": "BEGINNER",
        "type": "ROME4.0"
      }
      """;

    mockMvc
        .perform(
            post("/me/additional-skills")
                .header("X-Signed-Context", studentPayload)
                .header("X-Context-Kid", secretKey)
                .header("X-Context-Signature", studentSignature)
                .contentType(MediaType.APPLICATION_JSON)
                .content(payloadJson))
        .andExpect(status().isCreated());
  }

  @Test
  void shouldReturnConflictWhenAdditionalSkillAlreadyExists() throws Exception {
    String payloadJson =
        """
      {
        "id": "cce0bde1-f45c-4607-a5df-3083aa643dff",
        "level": "BEGINNER",
        "type": "ROME4.0"
      }
      """;

    mockMvc
        .perform(
            post("/me/additional-skills")
                .header("X-Signed-Context", studentPayload)
                .header("X-Context-Kid", secretKey)
                .header("X-Context-Signature", studentSignature)
                .contentType(MediaType.APPLICATION_JSON)
                .content(payloadJson))
        .andExpect(status().isCreated());

    mockMvc
        .perform(
            post("/me/additional-skills")
                .header("X-Signed-Context", studentPayload)
                .header("X-Context-Kid", secretKey)
                .header("X-Context-Signature", studentSignature)
                .contentType(MediaType.APPLICATION_JSON)
                .content(payloadJson))
        .andExpect(status().isConflict());
  }

  @Test
  void shouldReturnNotFoundWhenSkillDoesNotExist() throws Exception {
    String skillId = UUID.randomUUID().toString();
    String payloadJson =
        """
      {
        "id": "%s",
        "level": "BEGINNER",
        "type": "ROME4.0"
      }
      """
            .formatted(skillId);

    mockMvc
        .perform(
            post("/me/additional-skills")
                .header("X-Signed-Context", studentPayload)
                .header("X-Context-Kid", secretKey)
                .header("X-Context-Signature", studentSignature)
                .contentType(MediaType.APPLICATION_JSON)
                .content(payloadJson))
        .andExpect(status().isNotFound());
  }
}
