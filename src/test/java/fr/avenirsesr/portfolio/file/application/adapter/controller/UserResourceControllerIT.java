package fr.avenirsesr.portfolio.file.application.adapter.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.avenirsesr.portfolio.common.data.domain.model.enums.EUserCategory;
import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import fr.avenirsesr.portfolio.file.domain.model.EUserPhotoType;
import fr.avenirsesr.portfolio.shared.infrastructure.ContainerConfigurationTest;
import fr.avenirsesr.portfolio.shared.infrastructure.adapter.seeder.SeederRunner;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

class UserResourceControllerIT extends ContainerConfigurationTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  @Value("${hmac.secret-key}")
  private String secretKey;

  @Value("${user.student.payload}")
  private String studentPayload;

  @Value("${user.student.signature}")
  private String studentSignature;

  @BeforeAll
  void setup(@Autowired SeederRunner seederRunner) {
    seederRunner.run();
  }

  @Test
  void shouldUploadUserProfilePhotoSuccessfully() throws Exception {
    BddLogger.given("the /me/storage/users/{userCategory}/{photoType} endpoint");
    MockMultipartFile file =
        new MockMultipartFile(
            "file",
            "profile-photo.jpg",
            MediaType.IMAGE_JPEG_VALUE,
            "FakeImageContent".getBytes(StandardCharsets.UTF_8));

    BddLogger.when("performing a MULTIPART");
    BddLogger.then("it should upload user profile photo successfully");
    mockMvc
        .perform(
            multipart(
                    "/me/storage/users/{userCategory}/{photoType}",
                    EUserCategory.STUDENT,
                    EUserPhotoType.PROFILE)
                .file(file)
                .with(
                    request -> {
                      request.setMethod("PUT"); // multipart default is POST, on force PUT
                      return request;
                    })
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                .header("X-Signed-Context", studentPayload)
                .header("X-Context-Kid", secretKey)
                .header("X-Context-Signature", studentSignature))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.id").exists())
        .andExpect(
            jsonPath("$.fileSize")
                .value("FakeImageContent".getBytes(StandardCharsets.UTF_8).length))
        .andExpect(jsonPath("$.version").value(2));
  }

  @Test
  void shouldReturnNotFoundWhenDeletingNonExistingPhoto() throws Exception {
    BddLogger.given("the /me/storage/users/{fileId} endpoint");
    UUID fileId = UUID.randomUUID();

    BddLogger.when("performing a DELETE ob bib existing photo");
    BddLogger.then("it should return the not found status");
    mockMvc
        .perform(
            delete("/me/storage/users/{fileId}", fileId)
                .header("X-Signed-Context", studentPayload)
                .header("X-Context-Kid", secretKey)
                .header("X-Context-Signature", studentSignature))
        .andExpect(status().isNotFound());
  }
}
