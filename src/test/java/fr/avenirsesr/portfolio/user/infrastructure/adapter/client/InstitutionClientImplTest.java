package fr.avenirsesr.portfolio.user.infrastructure.adapter.client;

import static org.assertj.core.api.Assertions.assertThat;

import fr.avenirsesr.portfolio.common.institution.application.adapter.dto.InstitutionDTO;
import fr.avenirsesr.portfolio.common.institution.domain.model.enums.EInstitutionType;
import fr.avenirsesr.portfolio.common.security.infrastructure.adapter.model.AvenirsSecurityHeaders;
import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.WebClient;

class InstitutionClientImplTest {

  private static final UUID INSTITUTION_ID =
      UUID.fromString("6b1b1f8e-3f0a-4f4b-8f9e-6c9a0c0f1a11");
  private static final UUID PARENT_ID = UUID.fromString("9c2c2f8e-3f0a-4f4b-8f9e-6c9a0c0f1a22");

  private MockWebServer mockWebServer;
  private InstitutionClientImpl client;

  @BeforeEach
  void setUp() throws IOException {
    mockWebServer = new MockWebServer();
    mockWebServer.start();

    WebClient webClient = WebClient.builder().build();
    client = new InstitutionClientImpl(webClient);

    ReflectionTestUtils.setField(
        client, "institutionEndpoint", mockWebServer.url("/back-office/institutions").toString());
    ReflectionTestUtils.setField(client, "apiKey", "test-key");
  }

  @AfterEach
  void tearDown() throws IOException {
    mockWebServer.shutdown();
  }

  @Test
  void shouldReturnInstitutionWhenBackOfficeRespondsSuccessfully() throws InterruptedException {
    BddLogger.given("a back-office knowing the institution");
    String responseBody =
        """
        {
            "id": "%s",
            "name": "Université de Lorraine",
            "type": "SECONDARY",
            "parentId": "%s"
        }
        """
            .formatted(INSTITUTION_ID, PARENT_ID);
    mockWebServer.enqueue(
        new MockResponse().setBody(responseBody).addHeader("Content-Type", "application/json"));

    BddLogger.when("fetching the institution by id");
    Optional<InstitutionDTO> result = client.getById(INSTITUTION_ID);

    BddLogger.then("the institution is returned and the api key is sent");
    assertThat(result).isPresent();
    assertThat(result.get().id()).isEqualTo(INSTITUTION_ID);
    assertThat(result.get().name()).isEqualTo("Université de Lorraine");
    assertThat(result.get().type()).isEqualTo(EInstitutionType.SECONDARY);
    assertThat(result.get().parentId()).isEqualTo(PARENT_ID);

    RecordedRequest request = mockWebServer.takeRequest();
    assertThat(request.getPath()).isEqualTo("/back-office/institutions/" + INSTITUTION_ID);
    assertThat(request.getHeader(AvenirsSecurityHeaders.API_KEY)).isEqualTo("test-key");
  }

  @Test
  void shouldReturnEmptyWhenInstitutionIsUnknown() {
    BddLogger.given("a back-office answering 404 for the institution");
    mockWebServer.enqueue(new MockResponse().setResponseCode(404));

    BddLogger.when("fetching the institution by id");
    Optional<InstitutionDTO> result = client.getById(INSTITUTION_ID);

    BddLogger.then("no institution is returned");
    assertThat(result).isEmpty();
  }

  @Test
  void shouldReturnEmptyWhenBackOfficeFails() {
    BddLogger.given("a back-office answering 500");
    mockWebServer.enqueue(new MockResponse().setResponseCode(500));

    BddLogger.when("fetching the institution by id");
    Optional<InstitutionDTO> result = client.getById(INSTITUTION_ID);

    BddLogger.then("no institution is returned and no exception escapes the client");
    assertThat(result).isEmpty();
  }

  @Test
  void shouldReturnTrueWhenBackOfficeGrantsAccess() throws InterruptedException {
    BddLogger.given("a back-office granting access to the targeted institution");
    mockWebServer.enqueue(
        new MockResponse().setBody("true").addHeader("Content-Type", "application/json"));

    BddLogger.when("checking access to an institution");
    boolean result = client.hasAccess(List.of(PARENT_ID), List.of(INSTITUTION_ID));

    BddLogger.then("access is granted and the affiliated/target ids are sent");
    assertThat(result).isTrue();

    RecordedRequest request = mockWebServer.takeRequest();
    assertThat(request.getPath()).isEqualTo("/back-office/institutions/staff/access-check");
    assertThat(request.getMethod()).isEqualTo("POST");
    assertThat(request.getHeader(AvenirsSecurityHeaders.API_KEY)).isEqualTo("test-key");
    assertThat(request.getBody().readUtf8())
        .contains(PARENT_ID.toString())
        .contains(INSTITUTION_ID.toString());
  }

  @Test
  void shouldReturnFalseWhenBackOfficeDeniesAccess() {
    BddLogger.given("a back-office denying access to the targeted institution");
    mockWebServer.enqueue(
        new MockResponse().setBody("false").addHeader("Content-Type", "application/json"));

    BddLogger.when("checking access to an institution");
    boolean result = client.hasAccess(List.of(PARENT_ID), List.of(INSTITUTION_ID));

    BddLogger.then("access is denied");
    assertThat(result).isFalse();
  }

  @Test
  void shouldReturnFalseWhenCheckingAccessAndBackOfficeFails() {
    BddLogger.given("a back-office answering 500 to the access check");
    mockWebServer.enqueue(new MockResponse().setResponseCode(500));

    BddLogger.when("checking access to an institution");
    boolean result = client.hasAccess(List.of(PARENT_ID), List.of(INSTITUTION_ID));

    BddLogger.then("access is denied by default and no exception escapes the client");
    assertThat(result).isFalse();
  }

  @Test
  void shouldReturnAccessibleIdsResolvedByTheBackOffice() throws InterruptedException {
    BddLogger.given("a back-office resolving an institution together with its ancestors");
    mockWebServer.enqueue(
        new MockResponse()
            .setBody("[\"%s\",\"%s\"]".formatted(INSTITUTION_ID, PARENT_ID))
            .addHeader("Content-Type", "application/json"));

    BddLogger.when("resolving the institution with its ancestors");
    List<UUID> result = client.getStudentAccessibleIds(List.of(INSTITUTION_ID));

    BddLogger.then("the accessible ids returned by the back-office are forwarded");
    assertThat(result).containsExactly(INSTITUTION_ID, PARENT_ID);

    RecordedRequest request = mockWebServer.takeRequest();
    assertThat(request.getPath()).isEqualTo("/back-office/institutions/student/accessible-ids");
    assertThat(request.getMethod()).isEqualTo("POST");
    assertThat(request.getHeader(AvenirsSecurityHeaders.API_KEY)).isEqualTo("test-key");
    assertThat(request.getBody().readUtf8()).contains(INSTITUTION_ID.toString());
  }

  @Test
  void shouldReturnTheOriginalIdsWhenBackOfficeFails() {
    BddLogger.given("a back-office failing to resolve accessible institution ids");
    mockWebServer.enqueue(new MockResponse().setResponseCode(500));

    BddLogger.when("resolving the institution with its ancestors");
    List<UUID> result = client.getStudentAccessibleIds(List.of(INSTITUTION_ID));

    BddLogger.then("the requested institution is returned and no exception escapes the client");
    assertThat(result).containsExactly(INSTITUTION_ID);
  }

  @Test
  void shouldReturnEmptyListWhenNoIdsAreProvided() {
    BddLogger.when("resolving ancestors for an empty list of institutions");
    List<UUID> result = client.getStudentAccessibleIds(List.of());

    BddLogger.then("an empty list is returned and the back-office is not called");
    assertThat(result).isEmpty();
    assertThat(mockWebServer.getRequestCount()).isZero();
  }
}
