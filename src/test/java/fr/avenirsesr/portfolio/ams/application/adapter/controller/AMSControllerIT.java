package fr.avenirsesr.portfolio.ams.application.adapter.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import fr.avenirsesr.portfolio.common.language.domain.model.enums.ELanguage;
import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import fr.avenirsesr.portfolio.shared.infrastructure.ContainerConfigurationTest;
import fr.avenirsesr.portfolio.shared.infrastructure.adapter.seeder.SeederRunner;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

public class AMSControllerIT extends ContainerConfigurationTest {

  private static final String BASE_PATH = "/me/ams/view";
  private static final String VALID_STUDENT_PROGRESS_ID = "96c16cc8-4ae8-4ffe-bb37-d55d0832c36b";
  private static final String UNKNOWN_STUDENT_PROGRESS_ID = "00000000-0000-0000-0000-000000000000";
  private final ELanguage language = ELanguage.FRENCH;
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

  @BeforeAll
  void setup(@Autowired SeederRunner seederRunner) {
    seederRunner.run();
  }

  @Test
  void shouldReturnAmsForStudent() throws Exception {
    BddLogger.given("the " + BASE_PATH + " enpoint");
    BddLogger.when("performing a GET with a valid student");
    BddLogger.then("it should return paged ams");
    mockMvc
        .perform(
            get(BASE_PATH)
                .param("studentProgressId", VALID_STUDENT_PROGRESS_ID)
                .param("page", "0")
                .param("pageSize", "10")
                .header("Accept-Language", language.getCode())
                .header("X-Signed-Context", studentPayload)
                .header("X-Context-Kid", secretKey)
                .header("X-Context-Signature", studentSignature)
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data").isArray())
        .andExpect(jsonPath("$.page").exists());
  }

  @Test
  void shouldReturn404IfUserIsUnknown() throws Exception {
    BddLogger.given("the " + BASE_PATH + " enpoint");
    BddLogger.when("performing a GET with an unknown user");
    BddLogger.then("it should return a 404");
    mockMvc
        .perform(
            get(BASE_PATH)
                .param("studentProgressId", VALID_STUDENT_PROGRESS_ID)
                .header("Accept-Language", language.getCode())
                .header("X-Signed-Context", unknownUserPayload)
                .header("X-Context-Kid", secretKey)
                .header("X-Context-Signature", unknownUserSignature)
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.message").value("User not found"))
        .andExpect(jsonPath("$.code").value("USER_NOT_FOUND"));
  }

  @Test
  void shouldReturn403IfUserIsNotStudent() throws Exception {
    BddLogger.given("the " + BASE_PATH + " enpoint");
    BddLogger.when("performing a GET with a non student user");
    BddLogger.then("it should return a 403");
    mockMvc
        .perform(
            get(BASE_PATH)
                .param("studentProgressId", VALID_STUDENT_PROGRESS_ID)
                .header("Accept-Language", language.getCode())
                .header("X-Signed-Context", teacherPayload)
                .header("X-Context-Kid", secretKey)
                .header("X-Context-Signature", teacherSignature)
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isForbidden())
        .andExpect(jsonPath("$.code").value("USER_IS_NOT_STUDENT_EXCEPTION"));
  }

  @Test
  void shouldReturn400IfstudentProgressIdMissing() throws Exception {
    BddLogger.given("the " + BASE_PATH + " enpoint");
    BddLogger.when("performing a GET without student progress ID");
    BddLogger.then("it should return a 400");
    mockMvc
        .perform(
            get(BASE_PATH)
                .param("page", "0")
                .param("pageSize", "10")
                .header("Accept-Language", language.getCode())
                .header("X-Signed-Context", studentPayload)
                .header("X-Context-Kid", secretKey)
                .header("X-Context-Signature", studentSignature)
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest());
  }
}
