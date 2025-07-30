package fr.avenirsesr.portfolio.user.application.adapter.controller;

import static fr.avenirsesr.portfolio.shared.application.adapter.util.TestResourceUtils.loadJson;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import fr.avenirsesr.portfolio.shared.infrastructure.adapter.seeder.SeederRunner;
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
public class UserControllerIT {
  @Autowired private MockMvc mockMvc;

  @Value("${hmac.secret-key}")
  private String secretKey;

  @Value("${user.student.payload}")
  private String studentPayload;

  @Value("${user.student.signature}")
  private String studentSignature;

  @Value("${user.teacher.payload}")
  private String teacherPayload;

  @Value("${user.teacher.signature}")
  private String teacherSignature;

  @Value("${user.unknown.payload}")
  private String unknownPayload;

  @Value("${user.unknown.signature}")
  private String unknownSignature;

  @BeforeAll
  static void setup(@Autowired SeederRunner seederRunner) {
    seederRunner.run();
  }

  @Test
  void shouldGetStudentProfile() throws Exception {
    mockMvc
        .perform(
            get("/me/user/student/overview")
                .header(
                    "X-Signed-Context",
                    "{\"sub\":\"b5216586-0aee-4c39-ac43-423ef46774e3\","
                        + " \"iat\":\"2019-01-21T05:47:29.886Z\","
                        + " \"exp\":\"2027-01-01T05:47:29.886Z\"}")
                .header("X-Context-Kid", secretKey)
                .header("X-Context-Signature", "Vz6AvMMOapppgUu05I+biTBT/jtuJ9mDuASLh4Gf2iE="))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.firstname").value("Mathilde"))
        .andExpect(jsonPath("$.lastname").value("Aubry"))
        .andExpect(jsonPath("$.email").value("quentin.renard@hotmail.fr"))
        .andExpect(jsonPath("$.bio").exists());
  }

  @Test
  void shouldUpdateStudentProfileSuccessfully() throws Exception {
    String payloadJson = loadJson("user/mock-update-user.json");

    mockMvc
        .perform(
            put("/me/user/student/update")
                .header("X-Signed-Context", studentPayload)
                .header("X-Context-Kid", secretKey)
                .header("X-Context-Signature", studentSignature)
                .contentType(MediaType.APPLICATION_JSON)
                .content(payloadJson))
        .andExpect(status().isOk())
        .andExpect(content().string("Mise à jour faite."));
  }

  @Test
  void shouldUpdateTeacherProfileSuccessfully() throws Exception {
    String payloadJson = loadJson("user/mock-update-user.json");

    mockMvc
        .perform(
            put("/me/user/teacher/update")
                .header("X-Signed-Context", teacherPayload)
                .header("X-Context-Kid", secretKey)
                .header("X-Context-Signature", teacherSignature)
                .contentType(MediaType.APPLICATION_JSON)
                .content(payloadJson))
        .andExpect(status().isOk())
        .andExpect(content().string("Mise à jour faite."));
  }

  @Test
  void shouldUploadProfilePhoto() throws Exception {
    MockMultipartFile file =
        new MockMultipartFile("file", "photo.jpg", "image/jpeg", "dummy content".getBytes());

    mockMvc
        .perform(
            multipart("/me/user/student/update/photo")
                .file(file)
                .with(
                    request -> {
                      request.setMethod("PUT");
                      return request;
                    })
                .header("X-Signed-Context", studentPayload)
                .header("X-Context-Kid", secretKey)
                .header("X-Context-Signature", studentSignature))
        .andExpect(status().isOk());
  }

  @Test
  void shouldUploadCoverPhoto() throws Exception {
    MockMultipartFile file =
        new MockMultipartFile("file", "cover.png", "image/png", "dummy content".getBytes());

    mockMvc
        .perform(
            multipart("/me/user/student/update/cover")
                .file(file)
                .with(
                    request -> {
                      request.setMethod("PUT");
                      return request;
                    })
                .header("X-Signed-Context", studentPayload)
                .header("X-Context-Kid", secretKey)
                .header("X-Context-Signature", studentSignature))
        .andExpect(status().isOk());
  }

  @Test
  void shouldReturnNotFoundForUnknownUser() throws Exception {
    mockMvc
        .perform(
            get("/me/user/student/overview")
                .header("X-Signed-Context", unknownPayload)
                .header("X-Context-Kid", secretKey)
                .header("X-Context-Signature", unknownSignature))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.code").value("USER_NOT_FOUND"));
  }

  @Test
  void shouldUploadTeacherProfilePhoto() throws Exception {
    MockMultipartFile file =
        new MockMultipartFile("file", "photo.jpg", "image/jpeg", "dummy content".getBytes());

    mockMvc
        .perform(
            multipart("/me/user/teacher/update/photo")
                .file(file)
                .with(
                    request -> {
                      request.setMethod("PUT");
                      return request;
                    })
                .header("X-Signed-Context", teacherPayload)
                .header("X-Context-Kid", secretKey)
                .header("X-Context-Signature", teacherSignature))
        .andExpect(status().isOk());
  }

  @Test
  void shouldUploadTeacherCoverPhoto() throws Exception {
    MockMultipartFile file =
        new MockMultipartFile("file", "cover.jpg", "image/jpeg", "dummy content".getBytes());

    mockMvc
        .perform(
            multipart("/me/user/teacher/update/cover")
                .file(file)
                .with(
                    request -> {
                      request.setMethod("PUT");
                      return request;
                    })
                .header("X-Signed-Context", teacherPayload)
                .header("X-Context-Kid", secretKey)
                .header("X-Context-Signature", teacherSignature))
        .andExpect(status().isOk());
  }
}
