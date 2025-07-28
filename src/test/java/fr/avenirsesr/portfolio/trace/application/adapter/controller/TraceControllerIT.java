package fr.avenirsesr.portfolio.trace.application.adapter.controller;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.avenirsesr.portfolio.shared.domain.model.enums.ELanguage;
import fr.avenirsesr.portfolio.shared.infrastructure.adapter.seeder.SeederRunner;
import fr.avenirsesr.portfolio.trace.application.adapter.dto.CreateTraceDTO;
import fr.avenirsesr.portfolio.trace.domain.model.enums.ETraceStatus;
import java.util.UUID;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
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
        .andExpect(jsonPath("$.data.criticalCount").exists())
        .andExpect(jsonPath("$.data.traces").isArray())
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

    MockMultipartFile multipartJson =
        new MockMultipartFile(
            "trace", "", MediaType.APPLICATION_JSON_VALUE, objectMapper.writeValueAsBytes(dto));

    mockMvc
        .perform(
            multipart("/me/traces")
                .file(multipartJson)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                .header("X-Signed-Context", studentPayload)
                .header("X-Context-Kid", secretKey)
                .header("X-Context-Signature", studentSignature))
        .andExpect(status().isCreated());
  }

  @Test
  void shouldDeleteTrace() throws Exception {
    UUID existingTraceId = UUID.fromString("f905db82-9b0f-4a97-a1c8-4a7f7998031d");

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
    UUID traceIdNotOwned = UUID.randomUUID();

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
    UUID existingTraceId = UUID.fromString("a14f172f-8045-4ad0-9eed-f2867d187bb8");

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
