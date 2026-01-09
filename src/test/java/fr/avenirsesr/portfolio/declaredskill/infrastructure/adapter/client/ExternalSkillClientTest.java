package fr.avenirsesr.portfolio.declaredskill.infrastructure.adapter.client;

import static org.assertj.core.api.Assertions.assertThat;

import fr.avenirsesr.portfolio.common.data.application.adapter.response.PagedResponse;
import fr.avenirsesr.portfolio.common.data.domain.model.PageCriteria;
import fr.avenirsesr.portfolio.common.externalskill.application.adapter.dto.ExternalSkillDTO;
import fr.avenirsesr.portfolio.common.externalskill.application.adapter.dto.ExternalSkillDetailsDTO;
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

class ExternalSkillClientTest {

  private MockWebServer mockWebServer;
  private ExternalSkillClient client;

  @BeforeEach
  void setUp() throws IOException {
    mockWebServer = new MockWebServer();
    mockWebServer.start();

    WebClient webClient = WebClient.builder().baseUrl(mockWebServer.url("/").toString()).build();
    client = new ExternalSkillClient(webClient);

    ReflectionTestUtils.setField(client, "apiKey", "test-api-key");
    ReflectionTestUtils.setField(
        client, "externalSkillEndpoint", mockWebServer.url("/external-skills").toString());
    ReflectionTestUtils.setField(
        client, "healthEndpoint", mockWebServer.url("/actuator/health").toString());
  }

  @AfterEach
  void tearDown() throws IOException {
    mockWebServer.shutdown();
  }

  @Test
  void shouldReturnExternalSkillWhenInteroperabilityRespondsSuccessfully() throws Exception {
    BddLogger.given("a mock interoperability server responding successfully");
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
        new MockResponse()
            .setResponseCode(200)
            .setBody(responseBody)
            .addHeader("Content-Type", "application/json"));

    BddLogger.when("fetching external skill from interoperability");
    Optional<ExternalSkillDTO> result = client.getById(skillId);

    BddLogger.then("it should return the mapped external skill");
    assertThat(result).isPresent();
    assertThat(result.get().id()).isEqualTo(skillId);
    assertThat(result.get().title()).isEqualTo("Java Programming");
    assertThat(result.get().type().name()).isEqualTo("ROME4");
    assertThat(result.get().pathSegments()).containsExactly("Informatique", "Développement");

    RecordedRequest request = mockWebServer.takeRequest();
    assertThat(request.getMethod()).isEqualTo("GET");
    assertThat(request.getPath()).isEqualTo("/external-skills/" + skillId);
    assertThat(request.getHeader(AvenirsSecurityHeaders.API_KEY)).isEqualTo("test-api-key");
  }

  @Test
  void shouldReturnEmptyOptionalWhenInteroperabilityReturns404() {
    BddLogger.given("a mock interoperability server returning 404");
    UUID skillId = UUID.randomUUID();
    mockWebServer.enqueue(new MockResponse().setResponseCode(404));

    BddLogger.when("fetching external skill from interoperability");
    Optional<ExternalSkillDTO> result = client.getById(skillId);

    BddLogger.then("it should return an empty Optional");
    assertThat(result).isEmpty();
  }

  @Test
  void shouldReturnEmptyOptionalWhenInteroperabilityReturns500() {
    BddLogger.given("a mock interoperability server returning 500");
    UUID skillId = UUID.randomUUID();
    mockWebServer.enqueue(new MockResponse().setResponseCode(500));

    BddLogger.when("fetching external skill from interoperability");
    Optional<ExternalSkillDTO> result = client.getById(skillId);

    BddLogger.then("it should return an empty Optional");
    assertThat(result).isEmpty();
  }

  @Test
  void shouldReturnEmptyOptionalWhenInteroperabilityReturnsInvalidJson() {
    BddLogger.given("a mock interoperability server returning invalid JSON");
    UUID skillId = UUID.randomUUID();
    mockWebServer.enqueue(
        new MockResponse()
            .setResponseCode(200)
            .setBody("invalid json")
            .addHeader("Content-Type", "application/json"));

    BddLogger.when("fetching external skill from interoperability");
    Optional<ExternalSkillDTO> result = client.getById(skillId);

    BddLogger.then("it should return an empty Optional");
    assertThat(result).isEmpty();
  }

  @Test
  void shouldReturnEmptyOptionalWhenResponseBodyIsEmpty() {
    BddLogger.given("a mock interoperability server returning 200 with empty body");
    UUID skillId = UUID.randomUUID();
    mockWebServer.enqueue(new MockResponse().setResponseCode(200));

    BddLogger.when("fetching external skill from interoperability");
    Optional<ExternalSkillDTO> result = client.getById(skillId);

    BddLogger.then("it should return an empty Optional");
    assertThat(result).isEmpty();
  }

  @Test
  void shouldReturnExternalSkillDetailsWhenInteroperabilityRespondsSuccessfully() throws Exception {
    BddLogger.given("a mock interoperability server responding with details");
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
        new MockResponse()
            .setResponseCode(200)
            .setBody(responseBody)
            .addHeader("Content-Type", "application/json"));

    BddLogger.when("fetching external skill details from interoperability");
    Optional<ExternalSkillDetailsDTO> result = client.getExternalSkillDetails(skillId);

    BddLogger.then("it should return the external skill details");
    assertThat(result).isPresent();
    assertThat(result.get().id()).isEqualTo(skillId);
    assertThat(result.get().title()).isEqualTo("Java Programming");
    assertThat(result.get().type().name()).isEqualTo("ROME4");
    assertThat(result.get().categoryPath()).hasSize(2);
    assertThat(result.get().categoryPath().get(0).libelle()).isEqualTo("Informatique");
    assertThat(result.get().categoryPath().get(0).type().name()).isEqualTo("DOMAIN");

    RecordedRequest request = mockWebServer.takeRequest();
    assertThat(request.getMethod()).isEqualTo("GET");
    assertThat(request.getPath()).isEqualTo("/external-skills/" + skillId);
    assertThat(request.getHeader(AvenirsSecurityHeaders.API_KEY)).isEqualTo("test-api-key");
  }

  @Test
  void shouldReturnEmptyOptionalWhenFetchingDetailsReturns404() {
    BddLogger.given("a mock interoperability server returning 404 for details");
    UUID skillId = UUID.randomUUID();
    mockWebServer.enqueue(new MockResponse().setResponseCode(404));

    BddLogger.when("fetching external skill details from interoperability");
    Optional<ExternalSkillDetailsDTO> result = client.getExternalSkillDetails(skillId);

    BddLogger.then("it should return an empty Optional");
    assertThat(result).isEmpty();
  }

  @Test
  void shouldReturnEmptyOptionalWhenFetchingDetailsReturns500() {
    BddLogger.given("a mock interoperability server returning 500 for details");
    UUID skillId = UUID.randomUUID();
    mockWebServer.enqueue(new MockResponse().setResponseCode(500));

    BddLogger.when("fetching external skill details from interoperability");
    Optional<ExternalSkillDetailsDTO> result = client.getExternalSkillDetails(skillId);

    BddLogger.then("it should return an empty Optional");
    assertThat(result).isEmpty();
  }

  @Test
  void shouldReturnEmptyOptionalWhenFetchingDetailsReturnsInvalidJson() {
    BddLogger.given("a mock interoperability server returning invalid JSON for details");
    UUID skillId = UUID.randomUUID();
    mockWebServer.enqueue(
        new MockResponse()
            .setResponseCode(200)
            .setBody("invalid json")
            .addHeader("Content-Type", "application/json"));

    BddLogger.when("fetching external skill details from interoperability");
    Optional<ExternalSkillDetailsDTO> result = client.getExternalSkillDetails(skillId);

    BddLogger.then("it should return an empty Optional");
    assertThat(result).isEmpty();
  }

  @Test
  void shouldReturnEmptyOptionalWhenFetchingDetailsReturnsEmptyBody() {
    BddLogger.given("a mock interoperability server returning 200 with empty body for details");
    UUID skillId = UUID.randomUUID();
    mockWebServer.enqueue(new MockResponse().setResponseCode(200));

    BddLogger.when("fetching external skill details from interoperability");
    Optional<ExternalSkillDetailsDTO> result = client.getExternalSkillDetails(skillId);

    BddLogger.then("it should return an empty Optional");
    assertThat(result).isEmpty();
  }

  @Test
  void shouldReturnRandomSkillsWhenInteroperabilityRespondsSuccessfully() throws Exception {
    BddLogger.given("a mock interoperability server responding with random skills");
    String responseBody =
        """
        [
          {
            "id": "%s",
            "title": "Skill 1",
            "pathSegments": ["Domaine", "Issue", "Target"],
            "type": "ROME4"
          },
          {
            "id": "%s",
            "title": "Skill 2",
            "pathSegments": ["Domaine", "Issue", "Target"],
            "type": "ROME4"
          }
        ]
        """
            .formatted(UUID.randomUUID(), UUID.randomUUID());

    mockWebServer.enqueue(
        new MockResponse()
            .setResponseCode(200)
            .setBody(responseBody)
            .addHeader("Content-Type", "application/json"));

    BddLogger.when("fetching random skills from interoperability");
    List<ExternalSkillDTO> result = client.getRandomSkills(2);

    BddLogger.then("it should return the list of skills");
    assertThat(result).hasSize(2);
    assertThat(result.get(0).title()).isEqualTo("Skill 1");
    assertThat(result.get(1).title()).isEqualTo("Skill 2");

    RecordedRequest request = mockWebServer.takeRequest();
    assertThat(request.getMethod()).isEqualTo("GET");
    assertThat(request.getPath()).isEqualTo("/external-skills/random?count=2");
    assertThat(request.getHeader(AvenirsSecurityHeaders.API_KEY)).isEqualTo("test-api-key");
  }

  @Test
  void shouldReturnEmptyListWhenRandomSkillsReturnsEmptyBody() {
    BddLogger.given(
        "a mock interoperability server returning 200 with empty body for random skills");
    mockWebServer.enqueue(new MockResponse().setResponseCode(200));

    BddLogger.when("fetching random skills from interoperability");
    List<ExternalSkillDTO> result = client.getRandomSkills(3);

    BddLogger.then("it should return an empty list");
    assertThat(result).isEmpty();
  }

  @Test
  void shouldReturnEmptyListWhenRandomSkillsReturns500() {
    BddLogger.given("a mock interoperability server returning 500 for random skills");
    mockWebServer.enqueue(new MockResponse().setResponseCode(500));

    BddLogger.when("fetching random skills from interoperability");
    List<ExternalSkillDTO> result = client.getRandomSkills(3);

    BddLogger.then("it should return an empty list");
    assertThat(result).isEmpty();
  }

  @Test
  void shouldReturnEmptyListWhenRandomSkillsReturnsInvalidJson() {
    BddLogger.given("a mock interoperability server returning invalid JSON for random skills");
    mockWebServer.enqueue(
        new MockResponse()
            .setResponseCode(200)
            .setBody("invalid json")
            .addHeader("Content-Type", "application/json"));

    BddLogger.when("fetching random skills from interoperability");
    List<ExternalSkillDTO> result = client.getRandomSkills(3);

    BddLogger.then("it should return an empty list");
    assertThat(result).isEmpty();
  }

  @Test
  void shouldReturnPagedResponseWhenSearchRespondsSuccessfully() throws Exception {
    BddLogger.given("a mock interoperability server responding successfully for search");
    PageCriteria pageCriteria = new PageCriteria(1, 10);

    String responseBody =
        """
        {
          "data": [
            {
              "id": "%s",
              "title": "Java",
              "pathSegments": ["Informatique", "Développement"],
              "type": "ROME4"
            }
          ],
          "page": {
            "page": 1,
            "pageSize": 10,
            "totalElements": 1,
            "totalPages": 1
          }
        }
        """
            .formatted(UUID.randomUUID());

    mockWebServer.enqueue(
        new MockResponse()
            .setResponseCode(200)
            .setBody(responseBody)
            .addHeader("Content-Type", "application/json"));

    BddLogger.when("searching external skills");
    PagedResponse<ExternalSkillDTO> result = client.search("java", pageCriteria);

    BddLogger.then("it should return the paged response");
    assertThat(result).isNotNull();
    assertThat(result.data()).hasSize(1);
    assertThat(result.data().get(0).title()).isEqualTo("Java");
    assertThat(result.page().page()).isEqualTo(1);
    assertThat(result.page().pageSize()).isEqualTo(10);
    assertThat(result.page().totalElements()).isEqualTo(1);
    assertThat(result.page().totalPages()).isEqualTo(1);

    RecordedRequest request = mockWebServer.takeRequest();
    assertThat(request.getMethod()).isEqualTo("GET");
    assertThat(request.getPath())
        .isEqualTo("/external-skills/search?keyword=java&page=1&pageSize=10");
    assertThat(request.getHeader(AvenirsSecurityHeaders.API_KEY)).isEqualTo("test-api-key");
  }

  @Test
  void shouldReturnEmptyPagedResponseWhenSearchReturnsEmptyBody() {
    BddLogger.given("a mock interoperability server returning 200 with empty body for search");
    PageCriteria pageCriteria = new PageCriteria(2, 5);
    mockWebServer.enqueue(new MockResponse().setResponseCode(200));

    BddLogger.when("searching external skills");
    PagedResponse<ExternalSkillDTO> result = client.search("spring", pageCriteria);

    BddLogger.then("it should return an empty paged response");
    assertThat(result).isNotNull();
    assertThat(result.data()).isEmpty();
    assertThat(result.page().page()).isEqualTo(2);
    assertThat(result.page().pageSize()).isEqualTo(5);
    assertThat(result.page().totalElements()).isZero();
    assertThat(result.page().totalPages()).isZero();
  }

  @Test
  void shouldReturnEmptyPagedResponseWhenSearchReturns500() {
    BddLogger.given("a mock interoperability server returning 500 for search");
    PageCriteria pageCriteria = new PageCriteria(0, 20);
    mockWebServer.enqueue(new MockResponse().setResponseCode(500));

    BddLogger.when("searching external skills");
    PagedResponse<ExternalSkillDTO> result = client.search("kotlin", pageCriteria);

    BddLogger.then("it should return an empty paged response");
    assertThat(result).isNotNull();
    assertThat(result.data()).isEmpty();
    assertThat(result.page().page()).isEqualTo(pageCriteria.page());
    assertThat(result.page().pageSize()).isEqualTo(pageCriteria.pageSize());
    assertThat(result.page().totalElements()).isZero();
    assertThat(result.page().totalPages()).isZero();
  }

  @Test
  void shouldReturnTrueWhenHealthIsUp() throws Exception {
    BddLogger.given("a mock interoperability health endpoint returning UP");
    mockWebServer.enqueue(
        new MockResponse()
            .setResponseCode(200)
            .setBody("{\"status\":\"UP\"}")
            .addHeader("Content-Type", "application/json"));

    BddLogger.when("checking interoperability health");
    boolean result = client.checkInteroperabilityMicroservice();

    BddLogger.then("it should return true");
    assertThat(result).isTrue();

    RecordedRequest request = mockWebServer.takeRequest();
    assertThat(request.getMethod()).isEqualTo("GET");
    assertThat(request.getPath()).isEqualTo("/actuator/health");
  }

  @Test
  void shouldReturnFalseWhenHealthIsDown() {
    BddLogger.given("a mock interoperability health endpoint returning DOWN");
    mockWebServer.enqueue(
        new MockResponse()
            .setResponseCode(200)
            .setBody("{\"status\":\"DOWN\"}")
            .addHeader("Content-Type", "application/json"));

    BddLogger.when("checking interoperability health");
    boolean result = client.checkInteroperabilityMicroservice();

    BddLogger.then("it should return false");
    assertThat(result).isFalse();
  }

  @Test
  void shouldReturnFalseWhenHealthBodyIsEmpty() {
    BddLogger.given("a mock interoperability health endpoint returning 200 with empty body");
    mockWebServer.enqueue(new MockResponse().setResponseCode(200));

    BddLogger.when("checking interoperability health");
    boolean result = client.checkInteroperabilityMicroservice();

    BddLogger.then("it should return false");
    assertThat(result).isFalse();
  }

  @Test
  void shouldReturnFalseWhenHealthEndpointReturns500() {
    BddLogger.given("a mock interoperability health endpoint returning 500");
    mockWebServer.enqueue(new MockResponse().setResponseCode(500));

    BddLogger.when("checking interoperability health");
    boolean result = client.checkInteroperabilityMicroservice();

    BddLogger.then("it should return false");
    assertThat(result).isFalse();
  }
}
