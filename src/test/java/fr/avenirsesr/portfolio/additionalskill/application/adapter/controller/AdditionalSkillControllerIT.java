package fr.avenirsesr.portfolio.additionalskill.application.adapter.controller;

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
import org.springframework.test.web.servlet.MockMvc;

public class AdditionalSkillControllerIT extends ContainerConfigurationTest {

  private static final String BASE_PATH = "/additional-skills";

  @Autowired private MockMvc mockMvc;

  @Value("${hmac.secret-key}")
  private String secretKey;

  @Value("${user.student.payload}")
  private String studentPayload;

  @Value("${user.student.signature}")
  private String studentSignature;

  @BeforeAll
  void setup(@Autowired SeederRunner seederRunner) {
    seederRunner.run();
  }

  @Test
  void shouldSearchAdditionalSkillsByKeyword() throws Exception {
    BddLogger.given("the " + BASE_PATH + " enpoint");
    BddLogger.when("performing a GET with keywords");
    BddLogger.then("it should return paged additional skill progresses filtered by keywords");
    mockMvc
        .perform(
            get(BASE_PATH + "/search")
                .header("X-Signed-Context", studentPayload)
                .header("X-Context-Kid", secretKey)
                .header("X-Context-Signature", studentSignature)
                .param("keyword", "acc")
                .param("page", "0")
                .param("pageSize", "5"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data", notNullValue()))
        .andExpect(jsonPath("$.page.page").value(0))
        .andExpect(jsonPath("$.page.pageSize").value(5));
  }
}
