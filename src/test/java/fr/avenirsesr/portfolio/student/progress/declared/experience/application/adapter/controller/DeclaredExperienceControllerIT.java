package fr.avenirsesr.portfolio.student.progress.declared.experience.application.adapter.controller;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import fr.avenirsesr.portfolio.shared.infrastructure.ContainerConfigurationTest;
import fr.avenirsesr.portfolio.shared.infrastructure.adapter.seeder.SeederRunner;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

public class DeclaredExperienceControllerIT extends ContainerConfigurationTest {

  private static final String BASE_PATH = "/me/declared/experiences";

  @Autowired private MockMvc mockMvc;

  @Value("${hmac.secret-key}")
  private String secretKey;

  @Value("${user.student.payload}")
  private String studentPayload;

  @Value("${user.student.signature}")
  private String studentSignature;

  private final String notFoundDeclaredExperienceId = "00000000-0000-0000-0000-000000000000";

  @BeforeAll
  void setup(@Autowired SeederRunner seederRunner) {
    seederRunner.run();
  }

  private String buildCreateExperienceJson() {
    return "{\n"
        + "  \"title\": \"My Experience\",\n"
        + "  \"experienceType\": \"PROFESSIONAL\",\n"
        + "  \"organization\": \"ACME Inc\",\n"
        + "  \"activitySector\": \"IT\",\n"
        + "  \"location\": \"Paris\",\n"
        + "  \"description\": \"Some description\",\n"
        + "  \"sourceOfInformation\": \"SELF_DECLARED\",\n"
        + "  \"summary\": \"Summary text\",\n"
        + "  \"externalLink\": \"https://example.com\",\n"
        + "  \"startDate\": \"2024-01-01\",\n"
        + "  \"endDate\": \"2024-03-01\"\n"
        + "}\n";
  }

  @Transactional
  @Test
  void shouldCreateDeclaredExperience() throws Exception {
    BddLogger.given("the " + BASE_PATH + " endpoint");
    BddLogger.when("performing a POST to create a declared experience");
    BddLogger.then("it should return created status and the experience");

    mockMvc
        .perform(
            post(BASE_PATH + "/")
                .header("X-Signed-Context", studentPayload)
                .header("X-Context-Kid", secretKey)
                .header("X-Context-Signature", studentSignature)
                .contentType(MediaType.APPLICATION_JSON)
                .content(buildCreateExperienceJson()))
        .andExpect(status().isCreated())
        .andExpect(header().string("Location", containsString(BASE_PATH)))
        .andExpect(jsonPath("$.id", notNullValue()))
        .andExpect(jsonPath("$.title").value("My Experience"));
  }

  @Transactional
  @Test
  void shouldGetDeclaredExperience() throws Exception {
    BddLogger.given("an already created declared experience");
    var result =
        mockMvc
            .perform(
                post(BASE_PATH + "/")
                    .header("X-Signed-Context", studentPayload)
                    .header("X-Context-Kid", secretKey)
                    .header("X-Context-Signature", studentSignature)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(buildCreateExperienceJson()))
            .andExpect(status().isCreated())
            .andReturn();

    // Extract ID from creation response
    String responseBody = result.getResponse().getContentAsString();
    String createdId =
        responseBody.substring(responseBody.indexOf(":") + 2, responseBody.indexOf(",") - 1);

    BddLogger.when("performing a GET on the created declared experience");
    BddLogger.then("it should return the declared experience");

    mockMvc
        .perform(
            get(BASE_PATH + "/" + createdId)
                .header("X-Signed-Context", studentPayload)
                .header("X-Context-Kid", secretKey)
                .header("X-Context-Signature", studentSignature))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(createdId))
        .andExpect(jsonPath("$.title").value("My Experience"));
  }

  @Test
  void shouldReturnNotFoundWhenExperienceDoesNotExist() throws Exception {

    BddLogger.given("a declared experience id that does not exist");
    BddLogger.when("performing a GET with unknown id");
    BddLogger.then("it should return not found");

    mockMvc
        .perform(
            get(BASE_PATH + "/" + notFoundDeclaredExperienceId)
                .header("X-Signed-Context", studentPayload)
                .header("X-Context-Kid", secretKey)
                .header("X-Context-Signature", studentSignature))
        .andExpect(status().isNotFound());
  }

  @Transactional
  @Test
  void shouldGetDeclaredExperienceViewWithDefaultPagination() throws Exception {
    BddLogger.given("several declared experiences exist");
    BddLogger.when("performing a GET on /view without pagination params");
    BddLogger.then("it should return a paged list of declared experiences");

    mockMvc
        .perform(
            get(BASE_PATH + "/view")
                .header("X-Signed-Context", studentPayload)
                .header("X-Context-Kid", secretKey)
                .header("X-Context-Signature", studentSignature))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data").isArray())
        .andExpect(jsonPath("$.page").exists())
        .andExpect(jsonPath("$.page.page").exists())
        .andExpect(jsonPath("$.page.pageSize").exists())
        .andExpect(jsonPath("$.page.totalElements").exists())
        .andExpect(jsonPath("$.page.totalPages").exists());
  }

  @Transactional
  @Test
  void shouldGetDeclaredExperienceViewWithPaginationParams() throws Exception {
    BddLogger.given("several declared experiences exist");
    BddLogger.when("performing a GET on /view with pagination params");
    BddLogger.then("it should return a paged list respecting pagination");

    mockMvc
        .perform(
            get(BASE_PATH + "/view")
                .param("page", "0")
                .param("pageSize", "5")
                .header("X-Signed-Context", studentPayload)
                .header("X-Context-Kid", secretKey)
                .header("X-Context-Signature", studentSignature))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data").isArray())
        .andExpect(jsonPath("$.data.length()").value(lessThanOrEqualTo(5)))
        .andExpect(jsonPath("$.page.page").value(0))
        .andExpect(jsonPath("$.page.pageSize").value(5));
  }

  @Transactional
  @Test
  void shouldUpdateDeclaredExperience() throws Exception {
    BddLogger.given("an existing declared experience");
    var result =
        mockMvc
            .perform(
                post(BASE_PATH + "/")
                    .header("X-Signed-Context", studentPayload)
                    .header("X-Context-Kid", secretKey)
                    .header("X-Context-Signature", studentSignature)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(buildCreateExperienceJson()))
            .andExpect(status().isCreated())
            .andReturn();

    String createdId =
        result
            .getResponse()
            .getContentAsString()
            .substring(
                result.getResponse().getContentAsString().indexOf(":") + 2,
                result.getResponse().getContentAsString().indexOf(",") - 1);

    String updateJson =
        "{\n"
            + "  \"title\": \"Updated Experience\",\n"
            + "  \"experienceType\": \"PERSONAL\",\n"
            + "  \"organization\": \"New Org\",\n"
            + "  \"activitySector\": \"Science\",\n"
            + "  \"location\": \"Lyon\",\n"
            + "  \"description\": \"Updated description\",\n"
            + "  \"sourceOfInformation\": \"SELF_DECLARED\",\n"
            + "  \"summary\": \"Updated summary\",\n"
            + "  \"externalLink\": \"https://updated.com\",\n"
            + "  \"startDate\": \"2024-02-01\",\n"
            + "  \"endDate\": \"2024-04-01\"\n"
            + "}";

    BddLogger.when("performing PUT on that declared experience");
    BddLogger.then("it should update and return the new values");

    mockMvc
        .perform(
            put(BASE_PATH + "/" + createdId)
                .header("X-Signed-Context", studentPayload)
                .header("X-Context-Kid", secretKey)
                .header("X-Context-Signature", studentSignature)
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateJson))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(createdId))
        .andExpect(jsonPath("$.title").value("Updated Experience"))
        .andExpect(jsonPath("$.organization").value("New Org"))
        .andExpect(jsonPath("$.location").value("Lyon"))
        .andExpect(jsonPath("$.externalLink").value("https://updated.com"));
  }

  @Test
  void shouldReturnNotFoundWhenUpdatingNonExistingExperience() throws Exception {
    BddLogger.given("a non existing declared experience id");
    BddLogger.when("performing PUT with unknown id");
    BddLogger.then("it should return not found");

    mockMvc
        .perform(
            put(BASE_PATH + "/" + notFoundDeclaredExperienceId)
                .header("X-Signed-Context", studentPayload)
                .header("X-Context-Kid", secretKey)
                .header("X-Context-Signature", studentSignature)
                .contentType(MediaType.APPLICATION_JSON)
                .content(buildCreateExperienceJson()))
        .andExpect(status().isNotFound());
  }

  @Transactional
  @Test
  void shouldReturnBadRequestWhenUpdatingWithInvalidData() throws Exception {
    BddLogger.given("an existing declared experience");
    var result =
        mockMvc
            .perform(
                post(BASE_PATH + "/")
                    .header("X-Signed-Context", studentPayload)
                    .header("X-Context-Kid", secretKey)
                    .header("X-Context-Signature", studentSignature)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(buildCreateExperienceJson()))
            .andExpect(status().isCreated())
            .andReturn();

    String createdId =
        result
            .getResponse()
            .getContentAsString()
            .substring(
                result.getResponse().getContentAsString().indexOf(":") + 2,
                result.getResponse().getContentAsString().indexOf(",") - 1);

    String invalidUpdateJson =
        "{\n"
            + "  \"title\": \"\",\n"
            + "  \"experienceType\": \"PROFESSIONAL\",\n"
            + "  \"organization\": \"ACME Inc\",\n"
            + "  \"activitySector\": \"IT\",\n"
            + "  \"location\": \"Paris\",\n"
            + "  \"description\": \"Some description\",\n"
            + "  \"sourceOfInformation\": \"SELF_DECLARED\",\n"
            + "  \"summary\": \"Summary\",\n"
            + "  \"externalLink\": \"https://example.com\",\n"
            + "  \"startDate\": \"2024-01-01\",\n"
            + "  \"endDate\": \"2024-03-01\"\n"
            + "}";

    BddLogger.when("performing PUT with invalid payload");
    BddLogger.then("it should return 400");

    mockMvc
        .perform(
            put(BASE_PATH + "/" + createdId)
                .header("X-Signed-Context", studentPayload)
                .header("X-Context-Kid", secretKey)
                .header("X-Context-Signature", studentSignature)
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidUpdateJson))
        .andExpect(status().isBadRequest());
  }
}
