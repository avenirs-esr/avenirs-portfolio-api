package fr.avenirsesr.portfolio.user.application.adapter.controller;

import static fr.avenirsesr.portfolio.common.testutils.infrastructure.adapter.util.TestResourceUtils.loadJson;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import fr.avenirsesr.portfolio.common.seeder.infrastructure.adapter.SeederRunner;
import fr.avenirsesr.portfolio.common.testutils.BddLogger;
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
  void shouldUpdateStudentProfileSuccessfully() throws Exception {
    BddLogger.given("the /me/users/STUDENT/update endpoint");
    String payloadJson = loadJson("user/mock-update-user.json");

    BddLogger.when("performing a PUT");
    BddLogger.then("it should update the student profile successfully");
    mockMvc
        .perform(
            put("/me/users/STUDENT/update")
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
    BddLogger.given("the /me/users/TEACHER/update endpoint");
    String payloadJson = loadJson("user/mock-update-user.json");

    BddLogger.when("performing a PUT");
    BddLogger.then("it should update the teacher profile successfully");
    mockMvc
        .perform(
            put("/me/users/TEACHER/update")
                .header("X-Signed-Context", teacherPayload)
                .header("X-Context-Kid", secretKey)
                .header("X-Context-Signature", teacherSignature)
                .contentType(MediaType.APPLICATION_JSON)
                .content(payloadJson))
        .andExpect(status().isOk())
        .andExpect(content().string("Mise à jour faite."));
  }

  @Test
  void shouldReturnNotFoundForUnknownUser() throws Exception {
    BddLogger.given("the /me/users/STUDENT/overview endpoint");
    BddLogger.when("performing a GET with an unknown user");
    BddLogger.then("it should return a 404");
    mockMvc
        .perform(
            get("/me/users/STUDENT/overview")
                .header("X-Signed-Context", unknownPayload)
                .header("X-Context-Kid", secretKey)
                .header("X-Context-Signature", unknownSignature))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.code").value("USER_NOT_FOUND"));
  }

  @Test
  void shouldGetStudentProfile() throws Exception {
    BddLogger.given("the /me/users/STUDENT/overview endpoint");
    BddLogger.when("performing a GET");
    BddLogger.then("it should return the student profile");
    mockMvc
        .perform(
            get("/me/users/STUDENT/overview")
                .header("X-Signed-Context", studentPayload)
                .header("X-Context-Kid", secretKey)
                .header("X-Context-Signature", studentSignature))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.firstname").value("Updated"))
        .andExpect(jsonPath("$.lastname").value("Name"))
        .andExpect(jsonPath("$.email").value("new.email@example.com"))
        .andExpect(jsonPath("$.bio").exists());
  }
}
