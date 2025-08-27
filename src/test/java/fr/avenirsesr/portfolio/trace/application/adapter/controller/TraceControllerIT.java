package fr.avenirsesr.portfolio.trace.application.adapter.controller;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.avenirsesr.portfolio.shared.domain.model.enums.ELanguage;
import fr.avenirsesr.portfolio.shared.domain.port.output.utils.UuidGenerator;
import fr.avenirsesr.portfolio.shared.infrastructure.adapter.seeder.SeederRunner;
import fr.avenirsesr.portfolio.shared.infrastructure.adapter.utils.UuidV7Generator;
import fr.avenirsesr.portfolio.trace.application.adapter.dto.CreateTraceDTO;
import fr.avenirsesr.portfolio.trace.domain.model.ETraceStatus;
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
class TraceControllerIT {

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;

  @Value("${hmac.secret-key}")
  private String secretKey;

  @Value("${user.student.payload}")
  private String studentPayload;

  @Value("${user.unknown.payload}")
  private String unknownUserPayload;

  @Value("${user.student.signature}")
  private String studentSignature;

  @Value("${user.unknown.signature}")
  private String unknownUserSignature;

  private final UuidGenerator uuidGenerator = new UuidV7Generator();

  @BeforeAll
  static void setup(@Autowired SeederRunner seederRunner) {
    seederRunner.run();
  }

  @Test
  void shouldReturnTraceOverview() throws Exception {
    mockMvc
        .perform(
            get("/me/traces/overview")
                .header("Accept-Language", ELanguage.FRENCH.getCode())
                .header("X-Signed-Context", studentPayload)
                .header("X-Context-Kid", secretKey)
                .header("X-Context-Signature", studentSignature)
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$[0].traceId").exists())
        .andExpect(jsonPath("$[0].programName").exists())
        .andExpect(jsonPath("$[0].programName").value("LIFE_PROJECT"));
  }

  @Test
  void shouldReturn404WhenUserNotExist() throws Exception {
    mockMvc
        .perform(
            get("/me/traces/overview")
                .header("Accept-Language", ELanguage.FRENCH.getCode())
                .header("X-Signed-Context", unknownUserPayload)
                .header("X-Context-Kid", secretKey)
                .header("X-Context-Signature", unknownUserSignature))
        .andExpect(status().isNotFound())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.code").value("USER_NOT_FOUND"));
  }

  @Test
  void shouldReturnTraceViewUnassociated() throws Exception {
    mockMvc
        .perform(
            get("/me/traces/view")
                .param("status", ETraceStatus.UNASSOCIATED.name())
                .param("page", "0")
                .param("pageSize", "10")
                .header("X-Signed-Context", studentPayload)
                .header("X-Context-Kid", secretKey)
                .header("X-Context-Signature", studentSignature))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.data").exists())
        .andExpect(jsonPath("$.data").isArray())
        .andExpect(jsonPath("$.page").exists());
  }

  @Test
  void shouldReturnUnassociatedSummary() throws Exception {
    mockMvc
        .perform(
            get("/me/traces/unassociated/summary")
                .header("X-Signed-Context", studentPayload)
                .header("X-Context-Kid", secretKey)
                .header("X-Context-Signature", studentSignature))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.total").isNumber())
        .andExpect(jsonPath("$.totalWarnings").isNumber())
        .andExpect(jsonPath("$.totalCriticals").isNumber());
  }

  @Test
  void shouldCreateNewTrace() throws Exception {
    CreateTraceDTO dto =
        new CreateTraceDTO(
            "Nouvelle trace", ELanguage.FRENCH, false, "Note personnelle", "Justification IA");

    mockMvc
        .perform(
            post("/me/traces")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto))
                .header("X-Signed-Context", studentPayload)
                .header("X-Context-Kid", secretKey)
                .header("X-Context-Signature", studentSignature))
        .andExpect(status().isCreated());
  }

  @Test
  void shouldDeleteTrace() throws Exception {
    UUID existingTraceId = UUID.fromString("efb1f0ce-e531-49af-8031-949f3d68b354");

    mockMvc
        .perform(
            delete("/me/traces/{traceId}", existingTraceId)
                .header("X-Signed-Context", studentPayload)
                .header("X-Context-Kid", secretKey)
                .header("X-Context-Signature", studentSignature))
        .andExpect(status().isOk())
        .andExpect(content().string("Resource successfully deleted."));
  }

  @Test
  void shouldReturn403IfTraceNotFoundWhenDeleting() throws Exception {
    UUID traceIdNotOwned = uuidGenerator.generate();

    mockMvc
        .perform(
            delete("/me/traces/{traceId}", traceIdNotOwned)
                .header("X-Signed-Context", studentPayload)
                .header("X-Context-Kid", secretKey)
                .header("X-Context-Signature", studentSignature))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.code", is("TRACE_NOT_FOUND")));
  }

  @Test
  void shouldReturn403IfUserNotOwnerWhenDeleting() throws Exception {
    UUID existingTraceId = UUID.fromString("4b02b225-998a-4996-be52-8d9b2a5ab327");

    mockMvc
        .perform(
            delete("/me/traces/{traceId}", existingTraceId)
                .header("X-Signed-Context", studentPayload)
                .header("X-Context-Kid", secretKey)
                .header("X-Context-Signature", studentSignature))
        .andExpect(status().isForbidden())
        .andExpect(jsonPath("$.code", is("USER_NOT_AUTHORIZED")));
  }
}
