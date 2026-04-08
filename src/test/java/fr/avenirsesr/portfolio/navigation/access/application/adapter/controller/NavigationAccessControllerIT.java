package fr.avenirsesr.portfolio.navigation.access.application.adapter.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import fr.avenirsesr.portfolio.common.configuration.domain.model.InstitutionConfigurationElements;
import fr.avenirsesr.portfolio.common.language.domain.model.enums.ELanguage;
import fr.avenirsesr.portfolio.common.security.infrastructure.adapter.model.AvenirsSecurityHeaders;
import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import fr.avenirsesr.portfolio.program.domain.port.output.client.InstitutionConfigClient;
import fr.avenirsesr.portfolio.shared.infrastructure.ContainerConfigurationTest;
import fr.avenirsesr.portfolio.shared.infrastructure.adapter.seeder.SeederRunner;
import java.util.UUID;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

class NavigationAccessControllerIT extends ContainerConfigurationTest {

  private static final String BASE_PATH = "/me/navigation-access";

  @Autowired private WebTestClient webTestClient;

  @Value("${hmac.secret-key}")
  private String secretKey;

  @Value("${user.student.payload}")
  private String studentPayload;

  @Value("${user.staff.payload}")
  private String staffPayload;

  @Value("${user.unknown.payload}")
  private String unknownUserPayload;

  @Value("${user.student.signature}")
  private String studentSignature;

  @Value("${user.staff.signature}")
  private String staffSignature;

  @Value("${user.unknown.signature}")
  private String unknownUserSignature;

  private final ELanguage language = ELanguage.FRENCH;

  @BeforeAll
  void setup(@Autowired SeederRunner seederRunner) {
    seederRunner.run();
  }

  @TestConfiguration
  static class TestConfig {

    @Bean
    @Primary
    public InstitutionConfigClient institutionConfigClient() {
      InstitutionConfigClient mock = org.mockito.Mockito.mock(InstitutionConfigClient.class);

      InstitutionConfigurationElements config = new InstitutionConfigurationElements(true, false);
      when(mock.getInstitutionConfigElementsById(any(UUID.class))).thenReturn(config);

      return mock;
    }
  }

  @Test
  void shouldReturnNavigationAccessForStudent() {
    BddLogger.given("the " + BASE_PATH + " enpoint");
    BddLogger.when("performing a GET");
    BddLogger.then("it should return navigation access");

    webTestClient
        .get()
        .uri(BASE_PATH)
        .header(AvenirsSecurityHeaders.SIGNED_CONTEXT, studentPayload)
        .header(AvenirsSecurityHeaders.CONTEXT_KID, secretKey)
        .header(AvenirsSecurityHeaders.CONTEXT_SIGNATURE, studentSignature)
        .header("Accept-Language", language.getCode())
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody()
        .jsonPath("$.APC")
        .exists()
        .jsonPath("$.LIFE_PROJECT")
        .exists()
        .jsonPath("$.APC.enabledByInstitution")
        .isEqualTo(true)
        .jsonPath("$.APC.hasProgram")
        .isEqualTo(true)
        .jsonPath("$.LIFE_PROJECT.enabledByInstitution")
        .isEqualTo(false);
  }

  @Test
  void shouldReturn404WhenUserNotFound() {
    BddLogger.given("the " + BASE_PATH + " enpoint");
    BddLogger.when("performing a GET and the user is not found");
    BddLogger.then("it should return a 404");

    webTestClient
        .get()
        .uri(BASE_PATH)
        .header(AvenirsSecurityHeaders.SIGNED_CONTEXT, unknownUserPayload)
        .header(AvenirsSecurityHeaders.CONTEXT_KID, secretKey)
        .header(AvenirsSecurityHeaders.CONTEXT_SIGNATURE, unknownUserSignature)
        .header("Accept-Language", language.getCode())
        .exchange()
        .expectStatus()
        .isNotFound()
        .expectBody()
        .jsonPath("$.message")
        .isEqualTo("User not found")
        .jsonPath("$.code")
        .isEqualTo("USER_NOT_FOUND");
  }

  @Test
  void shouldReturn403WhenUserIsNotStudent() {
    BddLogger.given("the " + BASE_PATH + " enpoint");
    BddLogger.when("performing a GET with a non student user");
    BddLogger.then("it should return a 403");

    webTestClient
        .get()
        .uri(BASE_PATH)
        .header(AvenirsSecurityHeaders.SIGNED_CONTEXT, staffPayload)
        .header(AvenirsSecurityHeaders.CONTEXT_KID, secretKey)
        .header(AvenirsSecurityHeaders.CONTEXT_SIGNATURE, staffSignature)
        .header("Accept-Language", language.getCode())
        .exchange()
        .expectStatus()
        .isForbidden()
        .expectBody()
        .jsonPath("$.message")
        .isEqualTo("User is not student")
        .jsonPath("$.code")
        .isEqualTo("USER_IS_NOT_STUDENT_EXCEPTION");
  }

  @Test
  void shouldFallbackInDefaultLanguageWhenLanguageNotSupported() {
    BddLogger.given("the " + BASE_PATH + " enpoint");
    BddLogger.when("performing a GET with a non supported language");
    BddLogger.then("it should fallback in default language");

    webTestClient
        .get()
        .uri(BASE_PATH)
        .header(AvenirsSecurityHeaders.SIGNED_CONTEXT, studentPayload)
        .header(AvenirsSecurityHeaders.CONTEXT_KID, secretKey)
        .header(AvenirsSecurityHeaders.CONTEXT_SIGNATURE, studentSignature)
        .header("Accept-Language", "invalid_language_code")
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody()
        .jsonPath("$.APC")
        .exists()
        .jsonPath("$.LIFE_PROJECT")
        .exists()
        .jsonPath("$.APC.enabledByInstitution")
        .isEqualTo(true)
        .jsonPath("$.APC.hasProgram")
        .isEqualTo(true)
        .jsonPath("$.LIFE_PROJECT.enabledByInstitution")
        .isEqualTo(false);
  }
}
