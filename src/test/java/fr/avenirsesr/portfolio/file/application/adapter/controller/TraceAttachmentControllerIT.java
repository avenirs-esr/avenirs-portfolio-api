package fr.avenirsesr.portfolio.file.application.adapter.controller;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.avenirsesr.portfolio.shared.infrastructure.adapter.seeder.SeederRunner;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
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
class TraceAttachmentControllerIT {

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  @Value("${hmac.secret-key}")
  private String secretKey;

  @Value("${user.student.payload}")
  private String studentPayload;

  @Value("${user.student.signature}")
  private String studentSignature;

  @Value("${file.storage.local-path}")
  private String storagePath;

  @BeforeAll
  static void setup(@Autowired SeederRunner seederRunner) {
    seederRunner.run();
  }

  @AfterEach
  void cleanupStorageFolder() throws Exception {
    var folder = Path.of(storagePath);
    if (Files.exists(folder)) {
      Files.list(folder).forEach(path -> path.toFile().delete());
    }
  }

  @Test
  void shouldUploadAttachmentSuccessfully() throws Exception {
    UUID existingTraceId = UUID.fromString("01012cc9-762e-46cc-9b7b-810bca86d427");

    byte[] fileContent = "Contenu du fichier de test".getBytes(StandardCharsets.UTF_8);
    MockMultipartFile file =
        new MockMultipartFile("file", "test-file.txt", MediaType.TEXT_PLAIN_VALUE, fileContent);

    mockMvc
        .perform(
            multipart("/me/traces/upload/{traceId}", existingTraceId)
                .file(file)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                .header("X-Signed-Context", studentPayload)
                .header("X-Context-Kid", secretKey)
                .header("X-Context-Signature", studentSignature))
        .andExpect(status().isCreated())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.id").exists())
        .andExpect(jsonPath("$.fileName").value("test-file.txt"))
        .andExpect(jsonPath("$.fileSize").value(fileContent.length))
        .andExpect(jsonPath("$.version").value(2));
  }

  @Test
  void shouldReturn404WhenTraceNotFound() throws Exception {
    UUID unknownTraceId = UUID.randomUUID();

    MockMultipartFile file =
        new MockMultipartFile(
            "file", "test-file.txt", MediaType.TEXT_PLAIN_VALUE, "Contenu".getBytes());

    mockMvc
        .perform(
            multipart("/me/traces/upload/{traceId}", unknownTraceId)
                .file(file)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                .header("X-Signed-Context", studentPayload)
                .header("X-Context-Kid", secretKey)
                .header("X-Context-Signature", studentSignature))
        .andExpect(status().isNotFound())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.code").value("TRACE_NOT_FOUND"));
  }

  @Test
  void shouldReturn403WhenUserNotAuthorized() throws Exception {
    UUID traceIdNotOwnedByUser = UUID.fromString("efed73b3-a161-4886-87d1-4f19d41dd145");

    MockMultipartFile file =
        new MockMultipartFile(
            "file", "test-file.txt", MediaType.TEXT_PLAIN_VALUE, "Contenu".getBytes());

    mockMvc
        .perform(
            multipart("/me/traces/upload/{traceId}", traceIdNotOwnedByUser)
                .file(file)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                .header("X-Signed-Context", studentPayload)
                .header("X-Context-Kid", secretKey)
                .header("X-Context-Signature", studentSignature))
        .andExpect(status().isForbidden())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.code").value("USER_NOT_AUTHORIZED"));
  }
}
