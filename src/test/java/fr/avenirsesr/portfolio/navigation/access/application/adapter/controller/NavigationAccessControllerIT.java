package fr.avenirsesr.portfolio.navigation.access.application.adapter.controller;

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

class NavigationAccessControllerIT extends ContainerConfigurationTest {

  private static final String BASE_PATH = "/me/navigation-access";

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
  void setup(@Autowired SeederRunner seederRunner) {
    seederRunner.run();
  }

  @Test
  void shouldReturnNavigationAccessForStudent() throws Exception {
    BddLogger.given("the " + BASE_PATH + " enpoint");
    BddLogger.when("performing a GET");
    BddLogger.then("it should return navigation access");
    mockMvc
        .perform(
            get(BASE_PATH)
                .header("X-Signed-Context", studentPayload)
                .header("X-Context-Kid", secretKey)
                .header("X-Context-Signature", studentSignature)
                .header("Accept-Language", language.getCode())
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.APC").exists())
        .andExpect(jsonPath("$.LIFE_PROJECT").exists())
        .andExpect(jsonPath("$.APC.enabledByInstitution").value(true))
        .andExpect(jsonPath("$.APC.hasProgram").value(true))
        .andExpect(jsonPath("$.LIFE_PROJECT.enabledByInstitution").value(false));
  }

  @Test
  void shouldReturn404WhenUserNotFound() throws Exception {
    BddLogger.given("the " + BASE_PATH + " enpoint");
    BddLogger.when("performing a GET and the user is not found");
    BddLogger.then("it should return a 404");
    mockMvc
        .perform(
            get(BASE_PATH)
                .header("X-Signed-Context", unknownUserPayload)
                .header("X-Context-Kid", secretKey)
                .header("X-Context-Signature", unknownUserSignature)
                .header("Accept-Language", language.getCode()))
        .andExpect(status().isNotFound())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.message").value("User not found"))
        .andExpect(jsonPath("$.code").value("USER_NOT_FOUND"));
  }

  @Test
  void shouldReturn403WhenUserIsNotStudent() throws Exception {
    BddLogger.given("the " + BASE_PATH + " enpoint");
    BddLogger.when("performing a GET with a non student user");
    BddLogger.then("it should return a 403");
    mockMvc
        .perform(
            get(BASE_PATH)
                .header("X-Signed-Context", teacherPayload)
                .header("X-Context-Kid", secretKey)
                .header("X-Context-Signature", teacherSignature)
                .header("Accept-Language", language.getCode()))
        .andExpect(status().isForbidden())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.message").value("User is not student"))
        .andExpect(jsonPath("$.code").value("USER_IS_NOT_STUDENT_EXCEPTION"));
  }

  @Test
  void shouldFallbackInDefaultLanguageWhenLanguageNotSupported() throws Exception {
    BddLogger.given("the " + BASE_PATH + " enpoint");
    BddLogger.when("performing a GET with a non supported language");
    BddLogger.then("it should fallback in default language");
    mockMvc
        .perform(
            get(BASE_PATH)
                .header("X-Signed-Context", studentPayload)
                .header("X-Context-Kid", secretKey)
                .header("X-Context-Signature", studentSignature)
                .header("Accept-Language", "invalid_language_code")
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.APC").exists())
        .andExpect(jsonPath("$.LIFE_PROJECT").exists())
        .andExpect(jsonPath("$.APC.enabledByInstitution").value(true))
        .andExpect(jsonPath("$.APC.hasProgram").value(true))
        .andExpect(jsonPath("$.LIFE_PROJECT.enabledByInstitution").value(false));
  }
}
