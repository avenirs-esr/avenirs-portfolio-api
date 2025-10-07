package fr.avenirsesr.portfolio.trace.infrastructure.adapter.client;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import fr.avenirsesr.portfolio.common.configuration.domain.model.TraceConfiguration;
import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import java.io.IOException;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
class TraceConfigurationClientTest {

  private MockWebServer mockWebServer;
  private TraceConfigurationClient client;

  @BeforeEach
  void setUp() throws IOException {
    mockWebServer = new MockWebServer();
    mockWebServer.start();

    WebClient webClient = WebClient.builder().baseUrl(mockWebServer.url("/").toString()).build();

    client = new TraceConfigurationClient(webClient);

    ReflectionTestUtils.setField(
        client, "traceConfigBackOfficeEndPoint", mockWebServer.url("/config/trace").toString());
  }

  @AfterEach
  void tearDown() throws IOException {
    mockWebServer.shutdown();
  }

  @Test
  void shouldReturnTraceConfigurationWhenBackOfficeRespondsSuccessfully() {
    BddLogger.given(
        "a TraceConfigurationClient with a mock back-office server responding successfully");
    String responseBody =
        """
        {
            "maxRemainingDays": 45,
            "maxRemainingDaysBeforeWarning": 10,
            "maxRemainingDaysBeforeCritical": 5
        }
        """;
    mockWebServer.enqueue(
        new MockResponse().setBody(responseBody).addHeader("Content-Type", "application/json"));

    BddLogger.when("fetching trace configuration from back-office");
    TraceConfiguration result = client.getTraceConfiguration();

    BddLogger.then("it should return the configuration from back-office");
    assertThat(result).isNotNull();
    assertThat(result.maxRemainingDays()).isEqualTo(45);
    assertThat(result.maxRemainingDaysBeforeWarning()).isEqualTo(10);
    assertThat(result.maxRemainingDaysBeforeCritical()).isEqualTo(5);
  }

  @Test
  void shouldThrowResponseStatusExceptionWhenBackOfficeReturns500() {
    BddLogger.given(
        "a TraceConfigurationClient with a mock back-office server returning 500 error");
    mockWebServer.enqueue(new MockResponse().setResponseCode(500));

    BddLogger.when("fetching trace configuration from back-office");
    BddLogger.then("it should throw a ResponseStatusException with 500 status");
    ResponseStatusException exception =
        assertThrows(ResponseStatusException.class, () -> client.getTraceConfiguration());

    assertThat(exception.getStatusCode().value()).isEqualTo(500);
    assertThat(exception.getReason())
        .isEqualTo("Unable to fetch trace configuration from back-office");
  }

  @Test
  void shouldThrowResponseStatusExceptionWhenBackOfficeReturns404() {
    BddLogger.given(
        "a TraceConfigurationClient with a mock back-office server returning 404 error");
    mockWebServer.enqueue(new MockResponse().setResponseCode(404));

    BddLogger.when("fetching trace configuration from back-office");
    BddLogger.then("it should throw a ResponseStatusException with 500 status");
    ResponseStatusException exception =
        assertThrows(ResponseStatusException.class, () -> client.getTraceConfiguration());

    assertThat(exception.getStatusCode().value()).isEqualTo(500);
    assertThat(exception.getReason())
        .isEqualTo("Unable to fetch trace configuration from back-office");
  }

  @Test
  void shouldThrowResponseStatusExceptionWhenBackOfficeReturnsInvalidJson() {
    BddLogger.given(
        "a TraceConfigurationClient with a mock back-office server returning invalid JSON");
    mockWebServer.enqueue(
        new MockResponse().setBody("invalid json").addHeader("Content-Type", "application/json"));

    BddLogger.when("fetching trace configuration from back-office");
    BddLogger.then("it should throw a ResponseStatusException with 500 status");
    ResponseStatusException exception =
        assertThrows(ResponseStatusException.class, () -> client.getTraceConfiguration());

    assertThat(exception.getStatusCode().value()).isEqualTo(500);
    assertThat(exception.getReason())
        .isEqualTo("Unable to fetch trace configuration from back-office");
  }

  @Test
  void shouldThrowResponseStatusExceptionWhenNetworkTimeout() {
    BddLogger.given(
        "a TraceConfigurationClient with a mock back-office server with network timeout");
    mockWebServer.enqueue(
        new MockResponse()
            .setBody("timeout")
            .setBodyDelay(10, java.util.concurrent.TimeUnit.SECONDS));

    BddLogger.when("fetching trace configuration from back-office");
    BddLogger.then("it should throw a ResponseStatusException with 500 status");
    ResponseStatusException exception =
        assertThrows(ResponseStatusException.class, () -> client.getTraceConfiguration());

    assertThat(exception.getStatusCode().value()).isEqualTo(500);
    assertThat(exception.getReason())
        .isEqualTo("Unable to fetch trace configuration from back-office");
  }

  @Test
  void shouldThrowResponseStatusExceptionWhenBackOfficeIsUnavailable() {
    BddLogger.given("a TraceConfigurationClient with an unavailable back-office server");
    mockWebServer.enqueue(new MockResponse().setResponseCode(503));

    BddLogger.when("fetching trace configuration from back-office");
    BddLogger.then("it should throw a ResponseStatusException with 500 status");
    ResponseStatusException exception =
        assertThrows(ResponseStatusException.class, () -> client.getTraceConfiguration());

    assertThat(exception.getStatusCode().value()).isEqualTo(500);
    assertThat(exception.getReason())
        .isEqualTo("Unable to fetch trace configuration from back-office");
  }
}
