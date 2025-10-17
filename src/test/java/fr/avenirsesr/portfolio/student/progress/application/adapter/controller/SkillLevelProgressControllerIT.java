package fr.avenirsesr.portfolio.student.progress.application.adapter.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import fr.avenirsesr.portfolio.common.language.domain.model.enums.ELanguage;
import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import fr.avenirsesr.portfolio.shared.infrastructure.adapter.seeder.SeederRunner;
import java.util.UUID;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class SkillLevelProgressControllerIT {

  private static final String BASE_PATH = "/me/skill-level-progress";
  private static final String DETAILS_BASE_PATH = BASE_PATH + "/details/{skillId}";

  @Autowired private MockMvc mockMvc;

  @Value("${hmac.secret-key}")
  private String secretKey;

  @Value("${user.student.payload}")
  private String studentPayload;

  @Value("${user.teacher.payload}")
  private String teacherPayload;

  @Value("${user.unknown.payload}")
  private String unknownUserPayload;

  @Value("${user.student.signature}")
  private String studentSignature;

  @Value("${user.teacher.signature}")
  private String teacherSignature;

  @Value("${user.unknown.signature}")
  private String unknownUserSignature;

  private final ELanguage language = ELanguage.FRENCH;

  @BeforeAll
  static void setup(@Autowired SeederRunner seederRunner) {
    seederRunner.run();
  }

  @Nested
  class GivenSkillLevelProgressEndpoint {
    @BeforeEach
    void setupGiven() {
      BddLogger.given("the " + BASE_PATH + " enpoint");
    }

    @Nested
    class WhenPerformingGET {
      @BeforeEach
      void setupWhen() {
        BddLogger.when("performing a GET");
      }

      @Nested
      class AndAStudentUserIsPassed {
        @BeforeEach
        void setupAnd() {
          BddLogger.and("a student user is passed");
        }

        @Test
        void thenItShouldReturnPagedSkillLevelProgress() throws Exception {
          BddLogger.then("it should return paged skill level progresses");
          mockMvc
              .perform(
                  get(BASE_PATH)
                      .param("page", "0")
                      .param("pageSize", "10")
                      .param("sort", "NAME")
                      .header("Accept-Language", language.getCode())
                      .header("X-Signed-Context", studentPayload)
                      .header("X-Context-Kid", secretKey)
                      .header("X-Context-Signature", studentSignature)
                      .accept(MediaType.APPLICATION_JSON))
              .andExpect(status().isOk())
              .andExpect(jsonPath("$.data").isArray())
              .andExpect(jsonPath("$.page").exists());
        }
      }

      @Nested
      class AndNoPaginationParamsArePassed {
        @BeforeEach
        void setupAnd() {
          BddLogger.and("no pagination params are passed");
        }

        @Test
        void thenItShouldReturnDefaultPagination() throws Exception {
          BddLogger.then("it should return default pagination");
          mockMvc
              .perform(
                  get(BASE_PATH)
                      .header("Accept-Language", language.getCode())
                      .header("X-Signed-Context", studentPayload)
                      .header("X-Context-Kid", secretKey)
                      .header("X-Context-Signature", studentSignature)
                      .accept(MediaType.APPLICATION_JSON))
              .andExpect(status().isOk())
              .andExpect(jsonPath("$.data").isArray())
              .andExpect(jsonPath("$.page").exists());
        }
      }

      @Nested
      class AndAnUnknownUserIsPassed {
        @BeforeEach
        void setupAnd() {
          BddLogger.and("an unknown user or an non student user is passed");
        }

        @Test
        void thenItShouldReturn404() throws Exception {
          BddLogger.then("it should return 403");
          mockMvc
              .perform(
                  get(BASE_PATH)
                      .header("Accept-Language", language.getCode())
                      .header("X-Signed-Context", unknownUserPayload)
                      .header("X-Context-Kid", secretKey)
                      .header("X-Context-Signature", unknownUserSignature)
                      .accept(MediaType.APPLICATION_JSON))
              .andExpect(status().isForbidden())
              .andExpect(jsonPath("$.code").value("USER_IS_NOT_STUDENT_EXCEPTION"));
        }
      }

      @Nested
      class AndADateParamIsPassed {
        @BeforeEach
        void setupAnd() {
          BddLogger.and("a date param is passed");
        }

        @Test
        void thenItShouldSupportSortingByDate() throws Exception {
          BddLogger.then("it should return paged skill level progresses sorted by date");
          mockMvc
              .perform(
                  get(BASE_PATH)
                      .param("sort", "DATE")
                      .header("Accept-Language", language.getCode())
                      .header("X-Signed-Context", studentPayload)
                      .header("X-Context-Kid", secretKey)
                      .header("X-Context-Signature", studentSignature)
                      .accept(MediaType.APPLICATION_JSON))
              .andExpect(status().isOk())
              .andExpect(jsonPath("$.data").isArray())
              .andExpect(jsonPath("$.page").exists());
        }
      }
    }
  }

  @Nested
  class GivenSkillLevelProgressDetailsEndpoint {
    @BeforeEach
    void setupGiven() {
      BddLogger.given("the " + DETAILS_BASE_PATH + " enpoint");
    }

    @Nested
    class WhenPerformingGET {
      @BeforeEach
      void setupWhen() {
        BddLogger.when("performing a GET");
      }

      @Nested
      class AndAValidSkillIdIsPassed {
        private static final UUID EXISTING_SKILL_ID =
            UUID.fromString("f5bbedeb-c0f4-4b3c-bcbe-9a96091719e6");

        @BeforeEach
        void setupAnd() {
          BddLogger.and("a valid skill id is passed");
        }

        @Test
        void thenItShouldReturnTheDetailedSkill() throws Exception {
          BddLogger.then("it should return the detailed skill");
          mockMvc
              .perform(
                  get(DETAILS_BASE_PATH, EXISTING_SKILL_ID)
                      .header("Accept-Language", language.getCode())
                      .header("X-Signed-Context", studentPayload)
                      .header("X-Context-Kid", secretKey)
                      .header("X-Context-Signature", studentSignature)
                      .accept(MediaType.APPLICATION_JSON))
              .andExpect(status().isOk())
              .andExpect(jsonPath("$.id").isString())
              .andExpect(jsonPath("$.name").isString())
              .andExpect(jsonPath("$.skillLevels").isArray());
        }
      }

      @Nested
      class AndAnUnknowSkillIdIsPassed {
        private static final UUID UNKNOWN_SKILL_ID = UUID.randomUUID();

        @BeforeEach
        void setupAnd() {
          BddLogger.and("an unknown skill id is passed");
        }

        @Test
        void thenItShouldReturn404() throws Exception {
          BddLogger.then("it should return 404");
          mockMvc
              .perform(
                  get(DETAILS_BASE_PATH, UNKNOWN_SKILL_ID)
                      .header("Accept-Language", language.getCode())
                      .header("X-Signed-Context", studentPayload)
                      .header("X-Context-Kid", secretKey)
                      .header("X-Context-Signature", studentSignature)
                      .accept(MediaType.APPLICATION_JSON))
              .andExpect(status().isNotFound())
              .andExpect(jsonPath("$.code").value("SKILL_NOT_FOUND"));
        }
      }
    }
  }
}
