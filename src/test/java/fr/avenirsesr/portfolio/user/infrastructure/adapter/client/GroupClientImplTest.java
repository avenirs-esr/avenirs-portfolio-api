package fr.avenirsesr.portfolio.user.infrastructure.adapter.client;

import static org.assertj.core.api.Assertions.assertThat;

import fr.avenirsesr.portfolio.common.group.application.adapter.dto.GroupDTO;
import fr.avenirsesr.portfolio.common.group.domain.model.enums.EGroupType;
import fr.avenirsesr.portfolio.common.security.infrastructure.adapter.model.AvenirsSecurityHeaders;
import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import java.io.IOException;
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

class GroupClientImplTest {

  private static final UUID GROUP_ID = UUID.fromString("4a1b1f8e-3f0a-4f4b-8f9e-6c9a0c0f1a33");
  private static final UUID PARENT_ID = UUID.fromString("5d2c2f8e-3f0a-4f4b-8f9e-6c9a0c0f1a44");

  private MockWebServer mockWebServer;
  private GroupClientImpl client;

  @BeforeEach
  void setUp() throws IOException {
    mockWebServer = new MockWebServer();
    mockWebServer.start();

    WebClient webClient = WebClient.builder().build();
    client = new GroupClientImpl(webClient);

    ReflectionTestUtils.setField(
        client, "groupEndpoint", mockWebServer.url("/back-office/groups").toString());
    ReflectionTestUtils.setField(client, "apiKey", "test-key");
  }

  @AfterEach
  void tearDown() throws IOException {
    mockWebServer.shutdown();
  }

  @Test
  void shouldReturnGroupWhenBackOfficeRespondsSuccessfully() throws InterruptedException {
    BddLogger.given("a back-office knowing the group");
    String responseBody =
        """
        {
            "id": "%s",
            "name": "Licence 3 Informatique",
            "type": "PROGRAM_OPTION",
            "parentId": "%s"
        }
        """
            .formatted(GROUP_ID, PARENT_ID);
    mockWebServer.enqueue(
        new MockResponse().setBody(responseBody).addHeader("Content-Type", "application/json"));

    BddLogger.when("fetching the group by id");
    Optional<GroupDTO> result = client.getById(GROUP_ID);

    BddLogger.then("the group is returned and the api key is sent");
    assertThat(result).isPresent();
    assertThat(result.get().id()).isEqualTo(GROUP_ID);
    assertThat(result.get().name()).isEqualTo("Licence 3 Informatique");
    assertThat(result.get().type()).isEqualTo(EGroupType.PROGRAM_OPTION);
    assertThat(result.get().parentId()).isEqualTo(PARENT_ID);

    RecordedRequest request = mockWebServer.takeRequest();
    assertThat(request.getPath()).isEqualTo("/back-office/groups/" + GROUP_ID);
    assertThat(request.getHeader(AvenirsSecurityHeaders.API_KEY)).isEqualTo("test-key");
  }

  @Test
  void shouldReturnEmptyWhenGroupIsUnknown() {
    BddLogger.given("a back-office answering 404 for the group");
    mockWebServer.enqueue(new MockResponse().setResponseCode(404));

    BddLogger.when("fetching the group by id");
    Optional<GroupDTO> result = client.getById(GROUP_ID);

    BddLogger.then("no group is returned");
    assertThat(result).isEmpty();
  }

  @Test
  void shouldReturnProgramOfGroupWhenBackOfficeRespondsSuccessfully() throws InterruptedException {
    BddLogger.given("a back-office resolving the program of the group");
    String responseBody =
        """
        {
            "id": "%s",
            "name": "Licence Informatique",
            "type": "PROGRAM",
            "parentId": null
        }
        """
            .formatted(PARENT_ID);
    mockWebServer.enqueue(
        new MockResponse().setBody(responseBody).addHeader("Content-Type", "application/json"));

    BddLogger.when("fetching the program of the group");
    Optional<GroupDTO> result = client.getProgramOfGroup(GROUP_ID);

    BddLogger.then("the program is returned and the program sub-resource is called");
    assertThat(result).isPresent();
    assertThat(result.get().id()).isEqualTo(PARENT_ID);
    assertThat(result.get().type()).isEqualTo(EGroupType.PROGRAM);
    assertThat(result.get().parentId()).isNull();

    RecordedRequest request = mockWebServer.takeRequest();
    assertThat(request.getPath()).isEqualTo("/back-office/groups/" + GROUP_ID + "/program");
    assertThat(request.getHeader(AvenirsSecurityHeaders.API_KEY)).isEqualTo("test-key");
  }

  @Test
  void shouldReturnEmptyWhenTheGroupHasNoProgram() {
    BddLogger.given("a back-office answering 404 for the program of the group");
    mockWebServer.enqueue(new MockResponse().setResponseCode(404));

    BddLogger.when("fetching the program of the group");
    Optional<GroupDTO> result = client.getProgramOfGroup(GROUP_ID);

    BddLogger.then("no program is returned");
    assertThat(result).isEmpty();
  }

  @Test
  void shouldReturnEmptyWhenBackOfficeFails() {
    BddLogger.given("a back-office answering 500");
    mockWebServer.enqueue(new MockResponse().setResponseCode(500));

    BddLogger.when("fetching the group by id");
    Optional<GroupDTO> result = client.getById(GROUP_ID);

    BddLogger.then("no group is returned and no exception escapes the client");
    assertThat(result).isEmpty();
  }
}
