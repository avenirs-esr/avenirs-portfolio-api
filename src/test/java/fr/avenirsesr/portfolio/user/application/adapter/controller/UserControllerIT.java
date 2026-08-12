package fr.avenirsesr.portfolio.user.application.adapter.controller;

import static fr.avenirsesr.portfolio.common.testutils.infrastructure.adapter.util.TestResourceUtils.loadJson;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import fr.avenirsesr.portfolio.shared.application.adapter.Utils;
import fr.avenirsesr.portfolio.shared.infrastructure.ContainerConfigurationTest;
import fr.avenirsesr.portfolio.shared.infrastructure.adapter.seeder.SeederRunner;
import java.util.Collections;
import org.hamcrest.Matchers;
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

  @Value("${user.no-permission.payload}")
  private String noPermissionPayload;

  @Value("${user.no-permission.signature}")
  private String noPermissionSignature;

  @BeforeAll
  void setup(@Autowired SeederRunner seederRunner) {
    seederRunner.run();
  }

  @Nested
  class GetMe {

    @Test
    void shouldGetStudentMe() {
      BddLogger.given("the /me endpoint");
      BddLogger.when("performing a GET as a student");
      BddLogger.then("it should return the student's firstname, lastname and roles");

      webTestClient
          .get()
          .uri("/me")
          .header("X-Signed-Context", studentPayload)
          .header("X-Context-Signature", studentSignature)
          .exchange()
          .expectStatus()
          .isOk()
          .expectBody()
          .jsonPath("$.firstname")
          .exists()
          .jsonPath("$.lastname")
          .exists()
          .jsonPath("$.roles")
          .isArray()
          .jsonPath("$.roles")
          .value(Matchers.hasItem("ROLE_STUDENT"));
    }

    @Test
    void shouldGetMeWithEmptyRolesForUserWithoutPermission() {
      BddLogger.given("the /me endpoint");
      BddLogger.when("performing a GET as a user without any authority");
      BddLogger.then("it should return an empty roles list");

      webTestClient
          .get()
          .uri("/me")
          .header("X-Signed-Context", noPermissionPayload)
          .header("X-Context-Signature", noPermissionSignature)
          .exchange()
          .expectStatus()
          .isOk()
          .expectBody()
          .jsonPath("$.roles")
          .isEqualTo(Collections.emptyList());
    }
  }

  @Test
  void shouldUpdateStudentProfileSuccessfully() throws Exception {
    BddLogger.given("the /me/STUDENT/update endpoint");
    String payloadJson = loadJson("user/mock-update-user.json");

    BddLogger.when("performing a PUT");
    BddLogger.then("it should update the student profile successfully");

    webTestClient
        .put()
        .uri("/me/STUDENT/update")
        .header("X-Signed-Context", studentPayload)
        .header("X-Context-Signature", studentSignature)
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(payloadJson)
        .exchange()
        .expectStatus()
        .isNoContent();
  }

  @Test
  void shouldUpdateStaffProfileSuccessfully() throws Exception {
    BddLogger.given("the /me/STAFF/update endpoint");
    String payloadJson = loadJson("user/mock-update-user.json");

    BddLogger.when("performing a PUT");
    BddLogger.then("it should update the staff profile successfully");

    webTestClient
        .put()
        .uri("/me/STAFF/update")
        .header("X-Signed-Context", staffPayload)
        .header("X-Context-Signature", staffSignature)
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(payloadJson)
        .exchange()
        .expectStatus()
        .isNoContent();
  }

  @Test
  void shouldFailOnUpdateStaffProfileWithEmail() throws Exception {
    BddLogger.given("the /me/STAFF/update endpoint");
    String payloadJson = loadJson("user/mock-update-user-with-email.json");

    BddLogger.when("performing a PUT");
    BddLogger.then("it should update the staff profile successfully");

    webTestClient
        .put()
        .uri("/me/STAFF/update")
        .header("X-Signed-Context", staffPayload)
        .header("X-Context-Signature", staffSignature)
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(payloadJson)
        .exchange()
        .expectStatus()
        .isForbidden();
  }

  @Test
  void shouldReturnNotFoundForUnknownUser() {
    BddLogger.given("the /me/STUDENT/overview endpoint");
    BddLogger.when("performing a GET with an unknown user");
    BddLogger.then("it should return a 404");

    webTestClient
        .get()
        .uri("/me/STUDENT/overview")
        .header("X-Signed-Context", unknownPayload)
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
    BddLogger.given("the /me/STUDENT/overview endpoint");
    BddLogger.when("performing a GET");
    BddLogger.then("it should return the student profile");

    webTestClient
        .get()
        .uri("/me/STUDENT/overview")
        .header("X-Signed-Context", studentPayload)
        .header("X-Context-Signature", studentSignature)
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody()
        .jsonPath("$.email")
        .isEqualTo("lucas.tessier@university.com")
        .jsonPath("$.bio")
        .exists();
  }

  @Test
  void shouldGetStaffProfile() {
    BddLogger.given("the /me/STAFF/overview endpoint");
    BddLogger.when("performing a GET");
    BddLogger.then("it should return the staff profile");

    webTestClient
        .get()
        .uri("/me/STAFF/overview")
        .header("X-Signed-Context", staffPayload)
        .header("X-Context-Signature", staffSignature)
        .exchange()
        .expectStatus()
        .isOk();
  }

  @Test
  void shouldBuildOriginFromReferer_withPort() {
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.setScheme("http");
    request.setServerName("localhost");
    request.setServerPort(10000);
    request.setRequestURI("/me/STUDENT/overview");
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
    request.setRequestURI("/me/STUDENT/overview");
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
    request.setRequestURI("/me/STUDENT/overview");
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
    request.setRequestURI("/me/STUDENT/overview");

    String origin = ReflectionTestUtils.invokeMethod(Utils.class, "extractOrigin", request);

    assertThat(origin).isNull();
  }

  @Nested
  class GetQuickLinks {

    @Test
    void shouldGetStudentQuickLinks() {
      BddLogger.given(
          "the /me/STUDENT/quick-links endpoint, after the student has read their"
              + " notifications");
      BddLogger.when("performing a GET as a student");
      BddLogger.then("it should return the student quick links data");

      webTestClient
          .get()
          .uri("/me/notifications/STUDENT")
          .header("X-Signed-Context", studentPayload)
          .header("X-Context-Signature", studentSignature)
          .exchange()
          .expectStatus()
          .isOk();

      webTestClient
          .get()
          .uri("/me/STUDENT/quick-links")
          .header("X-Signed-Context", studentPayload)
          .header("X-Context-Signature", studentSignature)
          .exchange()
          .expectStatus()
          .isOk()
          .expectBody()
          .jsonPath("$.userId")
          .exists()
          .jsonPath("$.firstname")
          .exists()
          .jsonPath("$.lastname")
          .exists()
          .jsonPath("$.hasUnseenNotification")
          .isEqualTo(false)
          .jsonPath("$.unreadNotifications")
          .isNumber()
          .jsonPath("$.notificationEnabled")
          .isBoolean();
    }

    @Test
    void shouldGetStaffQuickLinks() {
      BddLogger.given("the /me/STAFF/quick-links endpoint");
      BddLogger.when("performing a GET as a staff member");
      BddLogger.then("it should return the staff quick links data");

      webTestClient
          .get()
          .uri("/me/STAFF/quick-links")
          .header("X-Signed-Context", staffPayload)
          .header("X-Context-Signature", staffSignature)
          .exchange()
          .expectStatus()
          .isOk()
          .expectBody()
          .jsonPath("$.userId")
          .exists()
          .jsonPath("$.hasUnseenNotification")
          .isEqualTo(false)
          .jsonPath("$.unreadNotifications")
          .isNumber()
          .jsonPath("$.notificationEnabled")
          .isBoolean();
    }

    @Test
    void shouldReturnUnauthorizedForUnknownUserOnQuickLinks() {
      BddLogger.given("the /me/STUDENT/quick-links endpoint");
      BddLogger.when("performing a GET with an unknown user");
      BddLogger.then("it should return 401");

      webTestClient
          .get()
          .uri("/me/STUDENT/quick-links")
          .header("X-Signed-Context", unknownPayload)
          .header("X-Context-Signature", unknownSignature)
          .exchange()
          .expectStatus()
          .isUnauthorized()
          .expectBody()
          .jsonPath("$.code")
          .isEqualTo("USER_NOT_AUTHORIZED");
    }
  }

  @Nested
  class UpdateNotificationPreferences {

    @Test
    void shouldUpdateNotificationPreferencesForStudent() throws Exception {
      BddLogger.given("the /me/preferences/notification endpoint");
      String payloadJson = loadJson("user/mock-update-notification-preferences.json");

      BddLogger.when("performing a PATCH as a student");
      BddLogger.then("it should update the notification preferences successfully");

      webTestClient
          .patch()
          .uri("/me/preferences/notification")
          .header("X-Signed-Context", studentPayload)
          .header("X-Context-Signature", studentSignature)
          .contentType(MediaType.APPLICATION_JSON)
          .bodyValue(payloadJson)
          .exchange()
          .expectStatus()
          .isNoContent();
    }

    @Test
    void shouldUpdateNotificationPreferencesForStaff() throws Exception {
      BddLogger.given("the /me/preferences/notification endpoint");
      String payloadJson = loadJson("user/mock-update-notification-preferences.json");

      BddLogger.when("performing a PATCH as a staff member");
      BddLogger.then("it should update the notification preferences successfully");

      webTestClient
          .patch()
          .uri("/me/preferences/notification")
          .header("X-Signed-Context", staffPayload)
          .header("X-Context-Signature", staffSignature)
          .contentType(MediaType.APPLICATION_JSON)
          .bodyValue(payloadJson)
          .exchange()
          .expectStatus()
          .isNoContent();
    }

    @Test
    void shouldReturnUnauthorizedForUnknownUser() throws Exception {
      BddLogger.given("the /me/preferences/notification endpoint");
      String payloadJson = loadJson("user/mock-update-notification-preferences.json");

      BddLogger.when("performing a PATCH with an unknown user");
      BddLogger.then("it should return 401");

      webTestClient
          .patch()
          .uri("/me/preferences/notification")
          .header("X-Signed-Context", unknownPayload)
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
    request.setRequestURI("/me/STUDENT/overview");
    request.addHeader("Referer", "ht!tp://bad");

    String origin = ReflectionTestUtils.invokeMethod(Utils.class, "extractOrigin", request);

    assertThat(origin).isNull();
  }

  @Test
  void shouldReturn403WhenGettingProfileWithoutPermission() {
    BddLogger.given("an authenticated user without the profile:read:own permission");
    BddLogger.when("performing a GET on /me/STUDENT/overview");
    BddLogger.then("it should return 403");

    webTestClient
        .get()
        .uri("/me/STUDENT/overview")
        .header("X-Signed-Context", noPermissionPayload)
        .header("X-Context-Signature", noPermissionSignature)
        .exchange()
        .expectStatus()
        .isForbidden();
  }

  @Test
  void shouldReturn403WhenUpdatingProfileWithoutPermission() throws Exception {
    BddLogger.given("an authenticated user without the profile:update:own permission");
    String payloadJson = loadJson("user/mock-update-user.json");

    BddLogger.when("performing a PUT on /me/STUDENT/update");
    BddLogger.then("it should return 403");

    webTestClient
        .put()
        .uri("/me/STUDENT/update")
        .header("X-Signed-Context", noPermissionPayload)
        .header("X-Context-Signature", noPermissionSignature)
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(payloadJson)
        .exchange()
        .expectStatus()
        .isForbidden();
  }
}
