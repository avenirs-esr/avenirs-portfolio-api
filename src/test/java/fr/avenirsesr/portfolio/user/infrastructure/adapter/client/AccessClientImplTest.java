package fr.avenirsesr.portfolio.user.infrastructure.adapter.client;

import static org.assertj.core.api.Assertions.assertThat;

import fr.avenirsesr.portfolio.common.security.infrastructure.adapter.model.AvenirsSecurityHeaders;
import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import fr.avenirsesr.portfolio.user.domain.port.output.client.StudentAccessScope;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.WebClient;

class AccessClientImplTest {

  private static final String EPPN = "lucas.tessier@university.com";
  private static final UUID INSTITUTION_ID =
      UUID.fromString("6b1b1f8e-3f0a-4f4b-8f9e-6c9a0c0f1a11");
  private static final UUID GROUP_ID = UUID.fromString("4a1b1f8e-3f0a-4f4b-8f9e-6c9a0c0f1a33");

  private MockWebServer mockWebServer;
  private AccessClientImpl client;

  @BeforeEach
  void setUp() throws IOException {
    mockWebServer = new MockWebServer();
    mockWebServer.start();

    WebClient webClient = WebClient.builder().build();
    client = new AccessClientImpl(webClient);

    ReflectionTestUtils.setField(
        client, "accessEndpoint", mockWebServer.url("/back-office/access").toString());
    ReflectionTestUtils.setField(client, "apiKey", "test-key");
  }

  @AfterEach
  void tearDown() throws IOException {
    mockWebServer.shutdown();
  }

  @Test
  void shouldReturnTrueWhenBackOfficeGrantsAccess() throws InterruptedException {
    BddLogger.given("a back-office granting staff access to the targeted institution/group");
    mockWebServer.enqueue(
        new MockResponse().setBody("true").addHeader("Content-Type", "application/json"));

    BddLogger.when("checking staff access by eppn");
    boolean result = client.staffHasAccess(EPPN, List.of(INSTITUTION_ID), List.of(GROUP_ID));

    BddLogger.then("access is granted and the eppn/target ids are sent, never affiliation ids");
    assertThat(result).isTrue();

    RecordedRequest request = mockWebServer.takeRequest();
    assertThat(request.getPath()).isEqualTo("/back-office/access/staff/check");
    assertThat(request.getMethod()).isEqualTo("POST");
    assertThat(request.getHeader(AvenirsSecurityHeaders.API_KEY)).isEqualTo("test-key");
    assertThat(request.getBody().readUtf8())
        .contains(EPPN)
        .contains(INSTITUTION_ID.toString())
        .contains(GROUP_ID.toString());
  }

  @Test
  void shouldReturnFalseWhenBackOfficeDeniesAccess() {
    BddLogger.given("a back-office denying staff access");
    mockWebServer.enqueue(
        new MockResponse().setBody("false").addHeader("Content-Type", "application/json"));

    BddLogger.when("checking staff access by eppn");
    boolean result = client.staffHasAccess(EPPN, List.of(INSTITUTION_ID), List.of(GROUP_ID));

    BddLogger.then("access is denied");
    assertThat(result).isFalse();
  }

  @Test
  void shouldDenyAccessByDefault_whenBackOfficeFails() {
    BddLogger.given("a back-office answering 500 to the staff access check");
    mockWebServer.enqueue(new MockResponse().setResponseCode(500));

    BddLogger.when("checking staff access by eppn");
    boolean result = client.staffHasAccess(EPPN, List.of(INSTITUTION_ID), List.of(GROUP_ID));

    BddLogger.then(
        "access is denied by default: an unreachable back-office must never grant implicit"
            + " access");
    assertThat(result).isFalse();
  }

  @Test
  void shouldReturnTheStudentScopeResolvedByTheBackOffice() throws InterruptedException {
    BddLogger.given("a back-office resolving the student's effective scope");
    mockWebServer.enqueue(
        new MockResponse()
            .setBody(
                "{\"institutionIds\":[\"%s\"],\"groupIds\":[\"%s\"]}"
                    .formatted(INSTITUTION_ID, GROUP_ID))
            .addHeader("Content-Type", "application/json"));

    BddLogger.when("resolving the student scope by eppn");
    StudentAccessScope result = client.getStudentScope(EPPN);

    BddLogger.then("the scope returned by the back-office is forwarded");
    assertThat(result.institutionIds()).containsExactly(INSTITUTION_ID);
    assertThat(result.groupIds()).containsExactly(GROUP_ID);

    RecordedRequest request = mockWebServer.takeRequest();
    assertThat(request.getPath()).isEqualTo("/back-office/access/student/" + EPPN + "/scope");
    assertThat(request.getMethod()).isEqualTo("GET");
    assertThat(request.getHeader(AvenirsSecurityHeaders.API_KEY)).isEqualTo("test-key");
  }

  @Test
  void shouldReturnEmptyScope_whenBackOfficeFails() {
    BddLogger.given("a back-office answering 500 to the student scope resolution");
    mockWebServer.enqueue(new MockResponse().setResponseCode(500));

    BddLogger.when("resolving the student scope by eppn");
    StudentAccessScope result = client.getStudentScope(EPPN);

    BddLogger.then(
        "an empty scope is returned: the API keeps no local affiliation copy to fall back on");
    assertThat(result.institutionIds()).isEmpty();
    assertThat(result.groupIds()).isEmpty();
  }
}
