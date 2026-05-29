package fr.avenirsesr.portfolio.student.progress.imported.application.adapter.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.avenirsesr.portfolio.common.language.domain.model.enums.ELanguage;
import fr.avenirsesr.portfolio.common.security.infrastructure.adapter.model.AvenirsSecurityHeaders;
import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import fr.avenirsesr.portfolio.shared.infrastructure.ContainerConfigurationTest;
import fr.avenirsesr.portfolio.shared.infrastructure.adapter.seeder.SeederRunner;
import java.util.UUID;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

public class SkillLevelProgressControllerIT extends ContainerConfigurationTest {

  private static final String BASE_PATH = "/me/skill-level-progress";
  private static final String DETAILS_BASE_PATH = BASE_PATH + "/details/{skillId}";

  @Autowired private WebTestClient webTestClient;

  @Autowired private ObjectMapper objectMapper;

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

  @Nested
  class GivenSkillLevelProgressEndpoint {

    @Nested
    class WhenPerformingGET {

      @Nested
      class AndAStudentUserIsPassed {

        @Test
        void thenItShouldReturnPagedSkillLevelProgress() {
          BddLogger.given("the " + BASE_PATH + " enpoint");
          BddLogger.when("performing a GET");
          BddLogger.and("a student user is passed");
          BddLogger.then("it should return paged skill level progresses");

          webTestClient
              .get()
              .uri(
                  uriBuilder ->
                      uriBuilder
                          .path(BASE_PATH)
                          .queryParam("page", "0")
                          .queryParam("pageSize", "10")
                          .queryParam("sort", "NAME")
                          .build())
              .header("Accept-Language", language.getCode())
              .header(AvenirsSecurityHeaders.SIGNED_CONTEXT, studentPayload)
              .header(AvenirsSecurityHeaders.CONTEXT_KID, secretKey)
              .header(AvenirsSecurityHeaders.CONTEXT_SIGNATURE, studentSignature)
              .accept(MediaType.APPLICATION_JSON)
              .exchange()
              .expectStatus()
              .isOk()
              .expectBody()
              .jsonPath("$.data")
              .isArray()
              .jsonPath("$.page")
              .exists();
        }
      }

      @Nested
      class AndNoPaginationParamsArePassed {

        @Test
        void thenItShouldReturnDefaultPagination() {
          BddLogger.given("the " + BASE_PATH + " enpoint");
          BddLogger.when("performing a GET");
          BddLogger.and("no pagination params are passed");
          BddLogger.then("it should return default pagination");

          webTestClient
              .get()
              .uri(BASE_PATH)
              .header("Accept-Language", language.getCode())
              .header(AvenirsSecurityHeaders.SIGNED_CONTEXT, studentPayload)
              .header(AvenirsSecurityHeaders.CONTEXT_KID, secretKey)
              .header(AvenirsSecurityHeaders.CONTEXT_SIGNATURE, studentSignature)
              .accept(MediaType.APPLICATION_JSON)
              .exchange()
              .expectStatus()
              .isOk()
              .expectBody()
              .jsonPath("$.data")
              .isArray()
              .jsonPath("$.page")
              .exists();
        }
      }

      @Nested
      class AndAnUnknownUserIsPassed {

        @Test
        void thenItShouldReturn404() {
          BddLogger.given("the " + BASE_PATH + " enpoint");
          BddLogger.when("performing a GET");
          BddLogger.and("an unknown user is passed");
          BddLogger.then("it should return 404");

          webTestClient
              .get()
              .uri(BASE_PATH)
              .header("Accept-Language", language.getCode())
              .header(AvenirsSecurityHeaders.SIGNED_CONTEXT, unknownUserPayload)
              .header(AvenirsSecurityHeaders.CONTEXT_KID, secretKey)
              .header(AvenirsSecurityHeaders.CONTEXT_SIGNATURE, unknownUserSignature)
              .accept(MediaType.APPLICATION_JSON)
              .exchange()
              .expectStatus()
              .isUnauthorized()
              .expectBody()
              .jsonPath("$.message")
              .isEqualTo("User not authorized")
              .jsonPath("$.code")
              .isEqualTo("USER_NOT_AUTHORIZED");
        }
      }

      @Nested
      class AndADateParamIsPassed {

        @Test
        void thenItShouldSupportSortingByDate() {
          BddLogger.given("the " + BASE_PATH + " enpoint");
          BddLogger.when("performing a GET");
          BddLogger.and("a date param is passed");
          BddLogger.then("it should return paged skill level progresses sorted by date");

          webTestClient
              .get()
              .uri(uriBuilder -> uriBuilder.path(BASE_PATH).queryParam("sort", "DATE").build())
              .header("Accept-Language", language.getCode())
              .header(AvenirsSecurityHeaders.SIGNED_CONTEXT, studentPayload)
              .header(AvenirsSecurityHeaders.CONTEXT_KID, secretKey)
              .header(AvenirsSecurityHeaders.CONTEXT_SIGNATURE, studentSignature)
              .accept(MediaType.APPLICATION_JSON)
              .exchange()
              .expectStatus()
              .isOk()
              .expectBody()
              .jsonPath("$.data")
              .isArray()
              .jsonPath("$.page")
              .exists();
        }
      }
    }
  }

  @Nested
  class GivenSkillLevelProgressDetailsEndpoint {

    @Nested
    class WhenPerformingGET {

      @Nested
      class AndAnUnknownSkillIdIsPassed {

        private final UUID UNKNOWN_SKILL_ID = UUID.randomUUID();

        @Test
        void thenItShouldReturn404() {
          BddLogger.given("the " + DETAILS_BASE_PATH + " enpoint");
          BddLogger.when("performing a GET");
          BddLogger.and("an unknown skill id is passed");
          BddLogger.then("it should return 404");

          webTestClient
              .get()
              .uri(DETAILS_BASE_PATH, UNKNOWN_SKILL_ID)
              .header("Accept-Language", language.getCode())
              .header(AvenirsSecurityHeaders.SIGNED_CONTEXT, studentPayload)
              .header(AvenirsSecurityHeaders.CONTEXT_KID, secretKey)
              .header(AvenirsSecurityHeaders.CONTEXT_SIGNATURE, studentSignature)
              .accept(MediaType.APPLICATION_JSON)
              .exchange()
              .expectStatus()
              .isNotFound()
              .expectBody()
              .jsonPath("$.code")
              .isEqualTo("SKILL_NOT_FOUND");
        }
      }
    }
  }
}
