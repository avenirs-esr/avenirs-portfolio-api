package fr.avenirsesr.portfolio.api.application.adapter.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import fixtures.UserFixture;
import fr.avenirsesr.portfolio.api.domain.model.User;
import fr.avenirsesr.portfolio.api.domain.port.output.repository.UserRepository;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class NavigationAccessControllerIT {

  @Autowired private MockMvc mockMvc;

  @Autowired private UserRepository userRepository;

  private UUID studentId;
  private UUID teacherId;

  @BeforeEach
  void setUp() {
    User student = UserFixture.createStudent().toModel();
    userRepository.save(student);
    studentId = student.getId();

    User teacher = UserFixture.createTeacher().toModel();
    userRepository.save(teacher);
    teacherId = teacher.getId();
  }

  @Test
  void shouldReturnNavigationAccessForStudent() throws Exception {
    mockMvc
        .perform(
            get("/me/navigation-access")
                .header("X-Signed-Context", studentId.toString())
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.APC").exists())
        .andExpect(jsonPath("$.LIFE_PROJECT").exists());
  }

  @Test
  void shouldReturn404WhenUserNotFound() throws Exception {
    mockMvc
        .perform(
            get("/me/navigation-access").header("X-Signed-Context", UUID.randomUUID().toString()))
        .andExpect(status().isNotFound())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.message").value("User not found"))
        .andExpect(jsonPath("$.code").value("USER_NOT_FOUND"));
  }

  @Test
  void shouldReturn403WhenUserIsNotStudent() throws Exception {
    mockMvc
        .perform(get("/me/navigation-access").header("X-Signed-Context", teacherId.toString()))
        .andExpect(status().isForbidden())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.message").value("User is not student"))
        .andExpect(jsonPath("$.code").value("USER_IS_NOT_STUDENT_EXCEPTION"));
  }
}
