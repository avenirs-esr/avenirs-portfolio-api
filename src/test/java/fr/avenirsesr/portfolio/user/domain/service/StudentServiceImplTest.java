package fr.avenirsesr.portfolio.user.domain.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import fr.avenirsesr.portfolio.file.domain.port.input.FileResourceService;
import fr.avenirsesr.portfolio.shared.domain.port.input.LoggedInUserService;
import fr.avenirsesr.portfolio.user.domain.model.Student;
import fr.avenirsesr.portfolio.user.domain.port.output.repository.StudentRepository;
import fr.avenirsesr.portfolio.user.domain.port.output.repository.UserRepository;
import fr.avenirsesr.portfolio.user.infrastructure.fixture.StudentFixture;
import fr.avenirsesr.portfolio.user.infrastructure.fixture.UserFixture;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class StudentServiceImplTest {

  @Mock private StudentRepository studentRepository;
  @Mock private UserRepository userRepository;
  @Mock private LoggedInUserService loggedInUserService;
  @Mock private FileResourceService fileResourceService;

  private StudentServiceImpl studentService;

  @BeforeEach
  void setUp() {
    studentService =
        new StudentServiceImpl(
            studentRepository, userRepository, loggedInUserService, fileResourceService);
  }

  @Nested
  class GetStudentProfile {

    @Test
    void shouldMapStudentFieldsToProfileOverviewData() {
      BddLogger.given("a logged-in student with known field values");
      var user =
          UserFixture.create()
              .withFirstName("Lucas")
              .withLastName("Tessier")
              .withEmail("lucas@university.com")
              .toModel();
      Student student = StudentFixture.create().withUser(user).withBio("My student bio").toModel();
      when(loggedInUserService.getLoggedInStudent()).thenReturn(student);

      BddLogger.when("getting the student profile");
      var result = studentService.getStudentProfile();

      BddLogger.then("all fields should be correctly mapped");
      assertEquals(student.getId(), result.id());
      assertEquals("Lucas", result.firstName());
      assertEquals("Tessier", result.lastName());
      assertEquals("lucas@university.com", result.email());
      assertEquals("My student bio", result.bio());
    }
  }

  @Nested
  class UpdateAffiliations {

    @Test
    void shouldReplaceInstitutionAndGroupIdsAndSaveTheStudent() {
      BddLogger.given("an existing student and new affiliation ids from the back-office");
      Student student = StudentFixture.create().toModel();
      UUID institutionId = UUID.randomUUID();
      UUID groupId = UUID.randomUUID();
      when(studentRepository.findById(student.getId())).thenReturn(Optional.of(student));

      BddLogger.when("updating the student's affiliations");
      studentService.updateAffiliations(student.getId(), List.of(institutionId), List.of(groupId));

      BddLogger.then("the student's institution and group ids are replaced and saved");
      ArgumentCaptor<Student> studentCaptor = ArgumentCaptor.forClass(Student.class);
      verify(studentRepository).save(studentCaptor.capture());
      assertEquals(List.of(institutionId), studentCaptor.getValue().getInstitutionIds());
      assertEquals(List.of(groupId), studentCaptor.getValue().getGroupIds());
    }
  }
}
