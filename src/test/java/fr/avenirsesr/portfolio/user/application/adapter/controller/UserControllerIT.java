package fr.avenirsesr.portfolio.user.application.adapter.controller;

import static fr.avenirsesr.portfolio.common.testutils.infrastructure.adapter.util.TestResourceUtils.loadJson;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import fr.avenirsesr.portfolio.shared.infrastructure.ContainerConfigurationTest;
import fr.avenirsesr.portfolio.shared.infrastructure.adapter.seeder.SeederRunner;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

public class UserControllerIT extends ContainerConfigurationTest {
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
  void setup(@Autowired SeederRunner seederRunner) {
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
        .andExpect(jsonPath("$.firstname").value("Lucas"))
        .andExpect(jsonPath("$.lastname").value("Tessier"))
        .andExpect(jsonPath("$.email").value("lucas.tessier@email.com"))
        .andExpect(jsonPath("$.bio").exists());
  }

  @Test
  void shouldBuildOriginFromReferer_withPort() {
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.setScheme("http");
    request.setServerName("localhost");
    request.setServerPort(10000);
    request.setRequestURI("/me/users/STUDENT/overview");
    request.addHeader("Referer", "https://front.example.com:8443/some/page");

    String origin =
        ReflectionTestUtils.invokeMethod(UserController.class, "extractOrigin", request);

    assertThat(origin).isEqualTo("https://front.example.com:8443/apim");
  }

  @Test
  void shouldBuildOriginFromReferer_withoutPort() {
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.setScheme("http");
    request.setServerName("localhost");
    request.setServerPort(10000);
    request.setRequestURI("/me/users/STUDENT/overview");
    request.addHeader("Referer", "https://front.example.com/some/page");

    String origin =
        ReflectionTestUtils.invokeMethod(UserController.class, "extractOrigin", request);

    assertThat(origin).isEqualTo("https://front.example.com/apim");
  }

  @Test
  void shouldFallbackToRequestSchemeHostPort_whenRefererHasNoSchemeOrHost() {
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.setScheme("http");
    request.setServerName("localhost");
    request.setServerPort(10000);
    request.setRequestURI("/me/users/STUDENT/overview");
    request.addHeader("Referer", "/relative/path");

    String origin =
        ReflectionTestUtils.invokeMethod(UserController.class, "extractOrigin", request);

    assertThat(origin).isEqualTo("http://localhost:10000/apim");
  }

  @Test
  void shouldReturnNull_whenRefererIsMissing() {
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.setScheme("http");
    request.setServerName("localhost");
    request.setServerPort(10000);
    request.setRequestURI("/me/users/STUDENT/overview");

    String origin =
        ReflectionTestUtils.invokeMethod(UserController.class, "extractOrigin", request);

    assertThat(origin).isNull();
  }

  @Test
  void shouldReturnNull_whenRefererIsInvalid() {
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.setScheme("http");
    request.setServerName("localhost");
    request.setServerPort(10000);
    request.setRequestURI("/me/users/STUDENT/overview");
    request.addHeader("Referer", "ht!tp://bad");

    String origin =
        ReflectionTestUtils.invokeMethod(UserController.class, "extractOrigin", request);

    assertThat(origin).isNull();
  }
}
