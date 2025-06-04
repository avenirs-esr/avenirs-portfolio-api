package fr.avenirsesr.portfolio.api.application.adapter.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import fr.avenirsesr.portfolio.api.infrastructure.adapter.seeder.SeederRunner;
import java.util.UUID;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class TrackControllerIT {

  @Autowired private MockMvc mockMvc;

  private UUID studentId;

  @BeforeAll
  static void setup(@Autowired SeederRunner seederRunner) {
    seederRunner.run();
  }

  @BeforeEach
  void setUp() {
    studentId = UUID.fromString("9fe9516a-a528-4870-8f15-89187e368610");
  }

  @Test
  void shouldReturnTrackOverview() throws Exception {
    mockMvc
        .perform(
            get("/me/track/overview")
                .header("X-Signed-Context", studentId.toString())
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$[0].trackId").value("18516fa2-79cf-43e9-8ccb-3be357a5882e"))
        .andExpect(jsonPath("$[0].title").value("Ut accusantium nostrum similique."))
        .andExpect(jsonPath("$[0].isGroup").value(false));
  }

  @Test
  void shouldReturn404WhenUserNotExist() throws Exception {
    mockMvc
        .perform(get("/me/track/overview").header("X-Signed-Context", UUID.randomUUID().toString()))
        .andExpect(status().isNotFound())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.message").value("User not found"))
        .andExpect(jsonPath("$.code").value("USER_NOT_FOUND"));
  }
}
