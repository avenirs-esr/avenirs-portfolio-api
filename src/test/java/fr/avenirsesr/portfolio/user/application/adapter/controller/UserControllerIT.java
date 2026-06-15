package fr.avenirsesr.portfolio.user.application.adapter.controller;

import static fr.avenirsesr.portfolio.common.testutils.infrastructure.adapter.util.TestResourceUtils.loadJson;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import fr.avenirsesr.portfolio.shared.application.adapter.Utils;
import fr.avenirsesr.portfolio.shared.infrastructure.ContainerConfigurationTest;
import fr.avenirsesr.portfolio.shared.infrastructure.adapter.seeder.SeederRunner;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.reactive.server.WebTestClient;

public class UserControllerIT extends ContainerConfigurationTest {

  @Autowired private WebTestClient webTestClient;

  @Value("${hmac.secret-key}")
  private String secretKey;

  @Value("${user.student.payload}")
  private String studentPayload;

  @Value("${user.student.signature}")
  private String studentSignature;

  @Value("${user.staff.payload}")
  private String staffPayload;

  @Value("${user.staff.signature}")
  private String staffSignature;

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

    webTestClient
        .put()
        .uri("/me/users/STUDENT/update")
        .header("X-Signed-Context", studentPayload)
        .header("X-Context-Kid", secretKey)
        .header("X-Context-Signature", studentSignature)
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(payloadJson)
        .exchange()
        .expectStatus()
        .isNoContent();
  }

  @Test
  void shouldUpdateStaffProfileSuccessfully() throws Exception {
    BddLogger.given("the /me/users/STAFF/update endpoint");
    String payloadJson = loadJson("user/mock-update-user.json");

    BddLogger.when("performing a PUT");
    BddLogger.then("it should update the staff profile successfully");

    webTestClient
        .put()
        .uri("/me/users/STAFF/update")
        .header("X-Signed-Context", staffPayload)
        .header("X-Context-Kid", secretKey)
        .header("X-Context-Signature", staffSignature)
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(payloadJson)
        .exchange()
        .expectStatus()
        .isNoContent();
  }

  @Test
  void shouldFailOnUpdateStaffProfileWithEmail() throws Exception {
    BddLogger.given("the /me/users/STAFF/update endpoint");
    String payloadJson = loadJson("user/mock-update-user-with-email.json");

    BddLogger.when("performing a PUT");
    BddLogger.then("it should update the staff profile successfully");

    webTestClient
        .put()
        .uri("/me/users/STAFF/update")
        .header("X-Signed-Context", staffPayload)
        .header("X-Context-Kid", secretKey)
        .header("X-Context-Signature", staffSignature)
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(payloadJson)
        .exchange()
        .expectStatus()
        .isForbidden();
  }

  @Test
  void shouldReturnNotFoundForUnknownUser() {
    BddLogger.given("the /me/users/STUDENT/overview endpoint");
    BddLogger.when("performing a GET with an unknown user");
    BddLogger.then("it should return a 404");

    webTestClient
        .get()
        .uri("/me/users/STUDENT/overview")
        .header("X-Signed-Context", unknownPayload)
        .header("X-Context-Kid", secretKey)
        .header("X-Context-Signature", unknownSignature)
        .exchange()
        .expectStatus()
        .isUnauthorized()
        .expectBody()
        .jsonPath("$.code")
        .isEqualTo("USER_NOT_AUTHORIZED");
  }

  @Test
  void shouldGetStudentProfile() {
    BddLogger.given("the /me/users/STUDENT/overview endpoint");
    BddLogger.when("performing a GET");
    BddLogger.then("it should return the student profile");

    webTestClient
        .get()
        .uri("/me/users/STUDENT/overview")
        .header("X-Signed-Context", studentPayload)
        .header("X-Context-Kid", secretKey)
        .header("X-Context-Signature", studentSignature)
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody()
        .jsonPath("$.email")
        .isEqualTo("lucas.tessier@university.com")
        .jsonPath("$.bio")
        .exists()
        .jsonPath("$.hasUnseenNotification")
        .isEqualTo(false);
  }

  @Test
  void shouldGetStaffProfile() {
    BddLogger.given("the /me/users/STAFF/overview endpoint");
    BddLogger.when("performing a GET");
    BddLogger.then("it should return the staff profile");

    webTestClient
        .get()
        .uri("/me/users/STAFF/overview")
        .header("X-Signed-Context", staffPayload)
        .header("X-Context-Kid", secretKey)
        .header("X-Context-Signature", staffSignature)
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody()
        .jsonPath("$.hasUnseenNotification")
        .isEqualTo(false);
  }

  @Test
  void shouldBuildOriginFromReferer_withPort() {
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.setScheme("http");
    request.setServerName("localhost");
    request.setServerPort(10000);
    request.setRequestURI("/me/users/STUDENT/overview");
    request.addHeader("Referer", "https://front.example.com:8443/some/page");

    String origin = ReflectionTestUtils.invokeMethod(Utils.class, "extractOrigin", request);

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

    String origin = ReflectionTestUtils.invokeMethod(Utils.class, "extractOrigin", request);

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

    String origin = ReflectionTestUtils.invokeMethod(Utils.class, "extractOrigin", request);

    assertThat(origin).isEqualTo("http://localhost:10000/apim");
  }

  @Test
  void shouldReturnNull_whenRefererIsMissing() {
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.setScheme("http");
    request.setServerName("localhost");
    request.setServerPort(10000);
    request.setRequestURI("/me/users/STUDENT/overview");

    String origin = ReflectionTestUtils.invokeMethod(Utils.class, "extractOrigin", request);

    assertThat(origin).isNull();
  }

  @Nested
  class UpdateNotificationPreferences {

    @Test
    void shouldUpdateNotificationPreferencesForStudent() throws Exception {
      BddLogger.given("the /me/users/preferences/notification endpoint");
      String payloadJson = loadJson("user/mock-update-notification-preferences.json");

      BddLogger.when("performing a PATCH as a student");
      BddLogger.then("it should update the notification preferences successfully");

      webTestClient
          .patch()
          .uri("/me/users/preferences/notification")
          .header("X-Signed-Context", studentPayload)
          .header("X-Context-Kid", secretKey)
          .header("X-Context-Signature", studentSignature)
          .contentType(MediaType.APPLICATION_JSON)
          .bodyValue(payloadJson)
          .exchange()
          .expectStatus()
          .isNoContent();
    }

    @Test
    void shouldUpdateNotificationPreferencesForStaff() throws Exception {
      BddLogger.given("the /me/users/preferences/notification endpoint");
      String payloadJson = loadJson("user/mock-update-notification-preferences.json");

      BddLogger.when("performing a PATCH as a staff member");
      BddLogger.then("it should update the notification preferences successfully");

      webTestClient
          .patch()
          .uri("/me/users/preferences/notification")
          .header("X-Signed-Context", staffPayload)
          .header("X-Context-Kid", secretKey)
          .header("X-Context-Signature", staffSignature)
          .contentType(MediaType.APPLICATION_JSON)
          .bodyValue(payloadJson)
          .exchange()
          .expectStatus()
          .isNoContent();
    }

    @Test
    void shouldReturnUnauthorizedForUnknownUser() throws Exception {
      BddLogger.given("the /me/users/preferences/notification endpoint");
      String payloadJson = loadJson("user/mock-update-notification-preferences.json");

      BddLogger.when("performing a PATCH with an unknown user");
      BddLogger.then("it should return 401");

      webTestClient
          .patch()
          .uri("/me/users/preferences/notification")
          .header("X-Signed-Context", unknownPayload)
          .header("X-Context-Kid", secretKey)
          .header("X-Context-Signature", unknownSignature)
          .contentType(MediaType.APPLICATION_JSON)
          .bodyValue(payloadJson)
          .exchange()
          .expectStatus()
          .isUnauthorized()
          .expectBody()
          .jsonPath("$.code")
          .isEqualTo("USER_NOT_AUTHORIZED");
    }
  }

  @Test
  void shouldReturnNull_whenRefererIsInvalid() {
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.setScheme("http");
    request.setServerName("localhost");
    request.setServerPort(10000);
    request.setRequestURI("/me/users/STUDENT/overview");
    request.addHeader("Referer", "ht!tp://bad");

    String origin = ReflectionTestUtils.invokeMethod(Utils.class, "extractOrigin", request);

    assertThat(origin).isNull();
  }
}
