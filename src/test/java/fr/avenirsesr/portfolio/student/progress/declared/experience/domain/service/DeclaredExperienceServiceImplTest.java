package fr.avenirsesr.portfolio.student.progress.declared.experience.domain.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import fr.avenirsesr.portfolio.common.error.domain.exception.FieldValidationException;
import fr.avenirsesr.portfolio.common.security.domain.exception.UserNotAuthorizedException;
import fr.avenirsesr.portfolio.shared.domain.port.input.LoggedInUserService;
import fr.avenirsesr.portfolio.student.progress.declared.experience.domain.exception.DeclaredExperienceNotFoundException;
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
    DeclaredExperience saved = mock(DeclaredExperience.class);
    when(experienceRepository.save(any())).thenReturn(saved);
    when(studentRepository.findById(studentId)).thenReturn(Optional.of(student));

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
    DeclaredExperience saved = mock(DeclaredExperience.class);
    when(experienceRepository.save(any())).thenReturn(saved);
    when(studentRepository.findById(studentId)).thenReturn(Optional.of(student));

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
  void shouldThrowWhenStudentNotFound() {
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

  @Test
  void shouldCreateUsingLoggedInStudent() {
    DeclaredExperience saved = mock(DeclaredExperience.class);
    when(experienceRepository.save(any())).thenReturn(saved);
    when(loggedInUserService.getLoggedInStudent()).thenReturn(student);

    DeclaredExperience result =
        service.create(
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
    verify(loggedInUserService).getLoggedInStudent();
    verify(experienceRepository).save(any());
  }

  @Test
  void shouldThrowWhenTitleMissing() {
    when(loggedInUserService.getLoggedInStudent()).thenReturn(student);
    assertThrows(
        FieldValidationException.class,
        () ->
            service.create(
                null,
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
  void shouldThrowWhenTitleTooLong() {
    when(loggedInUserService.getLoggedInStudent()).thenReturn(student);
    String tooLong = "A".repeat(81);

    assertThrows(
        FieldValidationException.class,
        () ->
            service.create(
                tooLong,
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
  void shouldThrowWhenOrganizationMissing() {
    when(loggedInUserService.getLoggedInStudent()).thenReturn(student);
    assertThrows(
        FieldValidationException.class,
        () ->
            service.create(
                "Titre",
                EExperienceType.PROFESSIONAL,
                "",
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
  void shouldThrowWhenOrganizationTooLong() {
    when(loggedInUserService.getLoggedInStudent()).thenReturn(student);
    String tooLong = "A".repeat(81);

    assertThrows(
        FieldValidationException.class,
        () ->
            service.create(
                "Titre",
                EExperienceType.PROFESSIONAL,
                tooLong,
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
  void shouldThrowWhenActivitySectorTooLong() {
    when(loggedInUserService.getLoggedInStudent()).thenReturn(student);
    String tooLong = "A".repeat(51);

    assertThrows(
        FieldValidationException.class,
        () ->
            service.create(
                "Titre",
                EExperienceType.PROFESSIONAL,
                "Org",
                tooLong,
                "Paris",
                "Desc",
                "Source",
                "Summary",
                "https://test.fr",
                start,
                end));
  }

  @Test
  void shouldThrowWhenLocationTooLong() {
    when(loggedInUserService.getLoggedInStudent()).thenReturn(student);
    String tooLong = "A".repeat(51);

    assertThrows(
        FieldValidationException.class,
        () ->
            service.create(
                "Titre",
                EExperienceType.PROFESSIONAL,
                "Org",
                "Sector",
                tooLong,
                "Desc",
                "Source",
                "Summary",
                "https://test.fr",
                start,
                end));
  }

  @Test
  void shouldThrowWhenSourceTooLong() {
    when(loggedInUserService.getLoggedInStudent()).thenReturn(student);
    String tooLong = "A".repeat(201);

    assertThrows(
        FieldValidationException.class,
        () ->
            service.create(
                "Titre",
                EExperienceType.PROFESSIONAL,
                "Org",
                "Sector",
                "Paris",
                "Desc",
                tooLong,
                "Summary",
                "https://test.fr",
                start,
                end));
  }

  @Test
  void shouldThrowWhenDescriptionTooLong() {
    when(loggedInUserService.getLoggedInStudent()).thenReturn(student);
    String tooLong = "A".repeat(401);

    assertThrows(
        FieldValidationException.class,
        () ->
            service.create(
                "Titre",
                EExperienceType.PROFESSIONAL,
                "Org",
                "Sector",
                "Paris",
                tooLong,
                "Source",
                "Summary",
                "https://test.fr",
                start,
                end));
  }

  @Test
  void shouldThrowWhenSummaryTooLong() {
    when(loggedInUserService.getLoggedInStudent()).thenReturn(student);
    String tooLong = "A".repeat(401);

    assertThrows(
        FieldValidationException.class,
        () ->
            service.create(
                "Titre",
                EExperienceType.PROFESSIONAL,
                "Org",
                "Sector",
                "Paris",
                "Desc",
                "Source",
                tooLong,
                "https://test.fr",
                start,
                end));
  }

  @Test
  void shouldThrowWhenStartDateNull() {
    when(loggedInUserService.getLoggedInStudent()).thenReturn(student);
    assertThrows(
        FieldValidationException.class,
        () ->
            service.create(
                "Titre",
                EExperienceType.PROFESSIONAL,
                "Org",
                "Sector",
                "Paris",
                "Desc",
                "Source",
                "Summary",
                "https://test.fr",
                null,
                end));
  }

  @Test
  void shouldThrowWhenEndDateBeforeStartDate() {
    when(loggedInUserService.getLoggedInStudent()).thenReturn(student);
    LocalDate wrongEnd = start.minusDays(1);

    assertThrows(
        FieldValidationException.class,
        () ->
            service.create(
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
                wrongEnd));
  }

  @Test
  void shouldReturnExperienceWhenStudentIsOwner() {
    Student loggedIn = student;
    DeclaredExperience experience = mock(DeclaredExperience.class);

    when(loggedInUserService.getLoggedInStudent()).thenReturn(loggedIn);
    when(experience.getStudent()).thenReturn(loggedIn);
    when(experienceRepository.findById(any())).thenReturn(Optional.of(experience));

    DeclaredExperience result = service.get(UUID.randomUUID());

    assertNotNull(result);
    verify(loggedInUserService).getLoggedInStudent();
    verify(experienceRepository).findById(any());
  }

  @Test
  void shouldThrowWhenExperienceNotFound() {
    when(loggedInUserService.getLoggedInStudent()).thenReturn(student);
    when(experienceRepository.findById(any())).thenReturn(Optional.empty());

    assertThrows(DeclaredExperienceNotFoundException.class, () -> service.get(UUID.randomUUID()));
  }

  @Test
  void shouldThrowWhenStudentIsNotOwner() {
    Student loggedIn = student;
    Student other = StudentFixture.create().withId(UUID.randomUUID()).toModel();
    DeclaredExperience experience = mock(DeclaredExperience.class);

    when(loggedInUserService.getLoggedInStudent()).thenReturn(loggedIn);
    when(experienceRepository.findById(any())).thenReturn(Optional.of(experience));
    when(experience.getStudent()).thenReturn(other);

    assertThrows(UserNotAuthorizedException.class, () -> service.get(UUID.randomUUID()));
  }
}
