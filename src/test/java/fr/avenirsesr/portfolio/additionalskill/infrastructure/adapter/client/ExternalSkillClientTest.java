package fr.avenirsesr.portfolio.additionalskill.infrastructure.adapter.client;

import static org.assertj.core.api.Assertions.assertThat;

import fr.avenirsesr.portfolio.common.externalskill.application.adapter.dto.ExternalSkillDTO;
import fr.avenirsesr.portfolio.common.externalskill.application.adapter.dto.ExternalSkillDetailsDTO;
import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.WebClient;

@ExtendWith(MockitoExtension.class)
class ExternalSkillClientTest {

  private MockWebServer mockWebServer;
  private ExternalSkillClient client;

  @BeforeEach
  void setUp() throws IOException {
    mockWebServer = new MockWebServer();
    mockWebServer.start();

    WebClient webClient = WebClient.builder().baseUrl(mockWebServer.url("/").toString()).build();

    client = new ExternalSkillClient(webClient);

    ReflectionTestUtils.setField(
        client, "externalSkillEndpoint", mockWebServer.url("/external-skills").toString());
  }

  @AfterEach
  void tearDown() throws IOException {
    mockWebServer.shutdown();
  }

  @Test
  void shouldReturnExternalSkillWhenInteroperabilityRespondsSuccessfully() {
    BddLogger.given(
        "an ExternalSkillClient with a mock interoperability server responding successfully");
    UUID skillId = UUID.randomUUID();
    String responseBody =
        """
        {
            "id": "%s",
            "title": "Java Programming",
            "categoryPath": [
                {"libelle": "Informatique", "type": "DOMAIN"},
                {"libelle": "Développement", "type": "ISSUE"}
            ],
            "type": "ROME4"
        }
        """
            .formatted(skillId);
    mockWebServer.enqueue(
        new MockResponse().setBody(responseBody).addHeader("Content-Type", "application/json"));

    BddLogger.when("fetching external skill from interoperability");
    Optional<ExternalSkillDTO> result = client.getById(skillId);

    BddLogger.then("it should return the external skill");
    assertThat(result).isPresent();
    assertThat(result.get().id()).isEqualTo(skillId);
    assertThat(result.get().title()).isEqualTo("Java Programming");
    assertThat(result.get().type().name()).isEqualTo("ROME4");
  }

  @Test
  void shouldReturnEmptyOptionalWhenInteroperabilityReturns404() {
    BddLogger.given(
        "an ExternalSkillClient with a mock interoperability server returning 404 error");
    UUID skillId = UUID.randomUUID();
    mockWebServer.enqueue(new MockResponse().setResponseCode(404));

    BddLogger.when("fetching external skill from interoperability");
    Optional<ExternalSkillDTO> result = client.getById(skillId);

    BddLogger.then("it should return an empty Optional");
    assertThat(result).isEmpty();
  }

  @Test
  void shouldReturnEmptyOptionalWhenInteroperabilityReturns500() {
    BddLogger.given(
        "an ExternalSkillClient with a mock interoperability server returning 500 error");
    UUID skillId = UUID.randomUUID();
    mockWebServer.enqueue(new MockResponse().setResponseCode(500));

    BddLogger.when("fetching external skill from interoperability");
    Optional<ExternalSkillDTO> result = client.getById(skillId);

    BddLogger.then("it should return an empty Optional");
    assertThat(result).isEmpty();
  }

  @Test
  void shouldReturnEmptyOptionalWhenInteroperabilityReturnsInvalidJson() {
    BddLogger.given(
        "an ExternalSkillClient with a mock interoperability server returning invalid JSON");
    UUID skillId = UUID.randomUUID();
    mockWebServer.enqueue(
        new MockResponse().setBody("invalid json").addHeader("Content-Type", "application/json"));

    BddLogger.when("fetching external skill from interoperability");
    Optional<ExternalSkillDTO> result = client.getById(skillId);

    BddLogger.then("it should return an empty Optional");
    assertThat(result).isEmpty();
  }

  @Test
  void shouldReturnEmptyOptionalWhenInteroperabilityIsUnavailable() {
    BddLogger.given("an ExternalSkillClient with an unavailable interoperability server");
    UUID skillId = UUID.randomUUID();
    mockWebServer.enqueue(new MockResponse().setResponseCode(503));

    BddLogger.when("fetching external skill from interoperability");
    Optional<ExternalSkillDTO> result = client.getById(skillId);

    BddLogger.then("it should return an empty Optional");
    assertThat(result).isEmpty();
  }

  @Test
  void shouldReturnEmptyOptionalWhenResponseBodyIsNull() {
    BddLogger.given("an ExternalSkillClient with a mock server returning null body");
    UUID skillId = UUID.randomUUID();
    mockWebServer.enqueue(new MockResponse().setResponseCode(200));

    BddLogger.when("fetching external skill from interoperability");
    Optional<ExternalSkillDTO> result = client.getById(skillId);

    BddLogger.then("it should return an empty Optional");
    assertThat(result).isEmpty();
  }

  @Test
  void shouldReturnExternalSkillDetailsWhenInteroperabilityRespondsSuccessfully() {
    BddLogger.given(
        "an ExternalSkillClient with a mock interoperability server responding with details");
    UUID skillId = UUID.randomUUID();
    String responseBody =
        """
        {
            "id": "%s",
            "title": "Java Programming",
            "categoryPath": [
                {"libelle": "Informatique", "type": "DOMAIN"},
                {"libelle": "Développement", "type": "ISSUE"}
            ],
            "type": "ROME4"
        }
        """
            .formatted(skillId);
    mockWebServer.enqueue(
        new MockResponse().setBody(responseBody).addHeader("Content-Type", "application/json"));

    BddLogger.when("fetching external skill details from interoperability");
    Optional<ExternalSkillDetailsDTO> result = client.getExternalSkillDetails(skillId);

    BddLogger.then("it should return the external skill details with category path");
    assertThat(result).isPresent();
    assertThat(result.get().id()).isEqualTo(skillId);
    assertThat(result.get().title()).isEqualTo("Java Programming");
    assertThat(result.get().type().name()).isEqualTo("ROME4");
    assertThat(result.get().categoryPath()).hasSize(2);
    assertThat(result.get().categoryPath().get(0).libelle()).isEqualTo("Informatique");
    assertThat(result.get().categoryPath().get(0).type().name()).isEqualTo("DOMAIN");
  }

  @Test
  void shouldReturnEmptyOptionalWhenFetchingDetailsReturns404() {
    BddLogger.given(
        "an ExternalSkillClient with a mock interoperability server returning 404 for details");
    UUID skillId = UUID.randomUUID();
    mockWebServer.enqueue(new MockResponse().setResponseCode(404));

    BddLogger.when("fetching external skill details from interoperability");
    Optional<ExternalSkillDetailsDTO> result = client.getExternalSkillDetails(skillId);

    BddLogger.then("it should return an empty Optional");
    assertThat(result).isEmpty();
  }
}
