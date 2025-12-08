package fr.avenirsesr.portfolio.student.progress.declared.experience.domain.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import fr.avenirsesr.portfolio.common.security.domain.exception.UserNotAuthorizedException;
import fr.avenirsesr.portfolio.shared.application.adapter.exception.RequestContextNotDefinedException;
import fr.avenirsesr.portfolio.shared.domain.port.input.LoggedInUserService;
import fr.avenirsesr.portfolio.student.progress.declared.experience.domain.model.DeclaredExperience;
import fr.avenirsesr.portfolio.student.progress.declared.experience.domain.model.enums.EExperienceType;
import fr.avenirsesr.portfolio.student.progress.declared.experience.domain.port.output.repository.DeclaredExperienceRepository;
import fr.avenirsesr.portfolio.user.domain.model.Student;
import fr.avenirsesr.portfolio.user.domain.port.output.repository.StudentRepository;
import fr.avenirsesr.portfolio.user.infrastructure.fixture.StudentFixture;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DeclaredExperienceServiceImplTest {

  @Mock private LoggedInUserService loggedInUserService;
  @Mock private DeclaredExperienceRepository experienceRepository;
  @Mock private StudentRepository studentRepository;

  @InjectMocks private DeclaredExperienceServiceImpl service;

  private UUID studentId;
  private Student student;
  private LocalDate start;
  private LocalDate end;

  @BeforeEach
  void setup() {
    studentId = UUID.randomUUID();
    student = StudentFixture.create().withId(studentId).toModel();

    start = LocalDate.of(2024, 1, 1);
    end = LocalDate.of(2024, 6, 1);
  }

  @Test
  void shouldCreateExperienceWhenLoggedInStudentMatches() {
    when(loggedInUserService.getLoggedInStudent()).thenReturn(student);

    DeclaredExperience saved = mock(DeclaredExperience.class);
    when(experienceRepository.save(any())).thenReturn(saved);

    DeclaredExperience result =
        service.create(
            studentId,
            "Titre",
            EExperienceType.PROFESSIONAL,
            "Org",
            "Sector",
            "Paris",
            "Desc",
            "Source",
            "Summary",
            "https://test.fr",
            start,
            end);

    assertNotNull(result);
    verify(experienceRepository).save(any(DeclaredExperience.class));
  }

  @Test
  void shouldLoadStudentFromRepositoryWhenRequestContextNotDefined() {
    when(loggedInUserService.getLoggedInStudent())
        .thenThrow(new RequestContextNotDefinedException());

    when(studentRepository.findById(studentId)).thenReturn(Optional.of(student));

    DeclaredExperience saved = mock(DeclaredExperience.class);
    when(experienceRepository.save(any())).thenReturn(saved);

    DeclaredExperience result =
        service.create(
            studentId,
            "Titre",
            EExperienceType.PROFESSIONAL,
            "Org",
            "Sector",
            "Paris",
            "Desc",
            "Source",
            "Summary",
            "https://test.fr",
            start,
            end);

    assertNotNull(result);
    verify(studentRepository).findById(studentId);
    verify(experienceRepository).save(any());
  }

  @Test
  void shouldThrowUnauthorizedWhenStudentDoesNotMatchLoggedIn() {
    Student differentStudent = mock(Student.class);
    when(differentStudent.getId()).thenReturn(UUID.randomUUID());

    when(loggedInUserService.getLoggedInStudent()).thenReturn(differentStudent);

    assertThrows(
        UserNotAuthorizedException.class,
        () ->
            service.create(
                studentId,
                "Titre",
                EExperienceType.PROFESSIONAL,
                "Org",
                "Sector",
                "Paris",
                "Desc",
                "Source",
                "Summary",
                "https://test.fr",
                start,
                end));
  }

  @Test
  void shouldThrowUnauthorizedWhenStudentNotFoundInFallback() {
    when(loggedInUserService.getLoggedInStudent())
        .thenThrow(new RequestContextNotDefinedException());

    when(studentRepository.findById(studentId)).thenReturn(Optional.empty());

    assertThrows(
        UserNotAuthorizedException.class,
        () ->
            service.create(
                studentId,
                "Titre",
                EExperienceType.PROFESSIONAL,
                "Org",
                "Sector",
                "Paris",
                "Desc",
                "Source",
                "Summary",
                "https://test.fr",
                start,
                end));
  }
}
