package fr.avenirsesr.portfolio.user.domain.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import fr.avenirsesr.portfolio.selfknowledge.domain.port.input.SelfKnowledgeService;
import fr.avenirsesr.portfolio.shared.domain.port.input.LoggedInUserService;
import fr.avenirsesr.portfolio.user.domain.model.Student;
import fr.avenirsesr.portfolio.user.domain.port.output.repository.StudentRepository;
import fr.avenirsesr.portfolio.user.domain.port.output.repository.UserRepository;
import fr.avenirsesr.portfolio.user.infrastructure.fixture.StudentFixture;
import fr.avenirsesr.portfolio.user.infrastructure.fixture.UserFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class StudentServiceImplTest {

  @Mock private StudentRepository studentRepository;
  @Mock private UserRepository userRepository;
  @Mock private SelfKnowledgeService selfKnowledgeService;
  @Mock private LoggedInUserService loggedInUserService;

  private StudentServiceImpl studentService;

  @BeforeEach
  void setUp() {
    studentService =
        new StudentServiceImpl(
            studentRepository, userRepository, selfKnowledgeService, loggedInUserService);
  }

  @Nested
  class GetStudentProfile {

    private Student createStudent(boolean hasUnseenNotification) {
      return StudentFixture.create().withHasUnseenNotification(hasUnseenNotification).toModel();
    }

    @Test
    void shouldReturnHasUnseenNotificationFalse() {
      BddLogger.given("a logged-in student with no unseen notifications");
      when(loggedInUserService.getLoggedInStudent()).thenReturn(createStudent(false));

      BddLogger.when("getting the student profile");
      var result = studentService.getStudentProfile();

      BddLogger.then("hasUnseenNotification should be false");
      assertFalse(result.hasUnseenNotification());
    }

    @Test
    void shouldReturnHasUnseenNotificationTrue() {
      BddLogger.given("a logged-in student with unseen notifications");
      when(loggedInUserService.getLoggedInStudent()).thenReturn(createStudent(true));

      BddLogger.when("getting the student profile");
      var result = studentService.getStudentProfile();

      BddLogger.then("hasUnseenNotification should be true");
      assertTrue(result.hasUnseenNotification());
    }

    @Test
    void shouldMapStudentFieldsToProfileOverviewData() {
      BddLogger.given("a logged-in student with known field values");
      var user =
          UserFixture.create()
              .withFirstName("Lucas")
              .withLastName("Tessier")
              .withEmail("lucas@university.com")
              .toModel();
      Student student =
          StudentFixture.create()
              .withUser(user)
              .withBio("My student bio")
              .withHasUnseenNotification(false)
              .toModel();
      when(loggedInUserService.getLoggedInStudent()).thenReturn(student);

      BddLogger.when("getting the student profile");
      var result = studentService.getStudentProfile();

      BddLogger.then("all fields should be correctly mapped");
      assertEquals(student.getId(), result.id());
      assertEquals("Lucas", result.firstName());
      assertEquals("Tessier", result.lastName());
      assertEquals("lucas@university.com", result.email());
      assertEquals("My student bio", result.bio());
      assertFalse(result.hasUnseenNotification());
    }
  }
}
