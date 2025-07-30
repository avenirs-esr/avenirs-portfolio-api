package fr.avenirsesr.portfolio.file.application.adapter.controller;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.avenirsesr.portfolio.file.domain.model.EUserPhotoType;
import fr.avenirsesr.portfolio.shared.infrastructure.adapter.seeder.SeederRunner;
import fr.avenirsesr.portfolio.user.domain.model.enums.EUserCategory;
import java.nio.charset.StandardCharsets;
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
class UserResourceControllerIT {

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

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
  void shouldUploadUserProfilePhotoSuccessfully() throws Exception {
    MockMultipartFile file =
        new MockMultipartFile(
            "file",
            "profile-photo.jpg",
            MediaType.IMAGE_JPEG_VALUE,
            "FakeImageContent".getBytes(StandardCharsets.UTF_8));

    mockMvc
        .perform(
            multipart(
                    "/me/users/upload/{userCategory}/{photoType}",
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
}
