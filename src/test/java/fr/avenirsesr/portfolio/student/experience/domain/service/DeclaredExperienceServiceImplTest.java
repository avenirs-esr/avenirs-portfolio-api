package fr.avenirsesr.portfolio.student.experience.domain.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import fr.avenirsesr.portfolio.common.data.domain.model.PageCriteria;
import fr.avenirsesr.portfolio.common.data.domain.model.PageInfo;
import fr.avenirsesr.portfolio.common.data.domain.model.PagedResult;
import fr.avenirsesr.portfolio.common.error.domain.exception.FieldValidationException;
import fr.avenirsesr.portfolio.common.security.domain.exception.UserNotAuthorizedException;
import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import fr.avenirsesr.portfolio.shared.domain.port.input.LoggedInUserService;
import fr.avenirsesr.portfolio.student.association.domain.exception.AssociationAlreadyExistException;
import fr.avenirsesr.portfolio.student.association.domain.model.Association;
import fr.avenirsesr.portfolio.student.association.domain.model.EAssociationType;
import fr.avenirsesr.portfolio.student.association.domain.port.input.AssociationService;
import fr.avenirsesr.portfolio.student.experience.domain.data.DeclaredExperienceAssociationCount;
import fr.avenirsesr.portfolio.student.experience.domain.data.DeclaredExperienceData;
import fr.avenirsesr.portfolio.student.experience.domain.exception.DeclaredExperienceNotFoundException;
import fr.avenirsesr.portfolio.student.experience.domain.model.DeclaredExperience;
import fr.avenirsesr.portfolio.student.experience.domain.model.enums.EExperienceType;
import fr.avenirsesr.portfolio.student.experience.domain.port.output.repository.DeclaredExperienceRepository;
import fr.avenirsesr.portfolio.student.skill.domain.exception.DeclaredSkillProgressNotFoundException;
import fr.avenirsesr.portfolio.student.skill.domain.model.DeclaredSkillProgress;
import fr.avenirsesr.portfolio.student.skill.domain.port.input.DeclaredSkillProgressService;
import fr.avenirsesr.portfolio.student.trace.domain.exception.TraceNotFoundException;
import fr.avenirsesr.portfolio.student.trace.domain.model.Trace;
import fr.avenirsesr.portfolio.student.trace.domain.port.input.TraceService;
import fr.avenirsesr.portfolio.student.trace.infrastructure.fixture.TraceFixture;
import fr.avenirsesr.portfolio.user.domain.exception.UserIsNotStudentException;
import fr.avenirsesr.portfolio.user.domain.model.Student;
import fr.avenirsesr.portfolio.user.domain.port.input.StudentService;
import fr.avenirsesr.portfolio.user.infrastructure.fixture.StudentFixture;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DeclaredExperienceServiceImplTest {

  @Mock private LoggedInUserService loggedInUserService;
  @Mock private AssociationService associationService;
  @Mock private TraceService traceService;
  @Mock private DeclaredExperienceRepository experienceRepository;
  @Mock private StudentService studentService;
  @Mock private DeclaredSkillProgressService declaredSkillProgressService;

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

  private DeclaredSkillProgress mockDeclaredSkillProgress(Student owner) {
    DeclaredSkillProgress skill = mock(DeclaredSkillProgress.class);
    lenient().when(skill.getId()).thenReturn(UUID.randomUUID());
    lenient().when(skill.getStudent()).thenReturn(owner);
    return skill;
  }

  @Test
  void shouldCreateExperienceWhenLoggedInStudentMatches() {
    DeclaredExperience saved = mock(DeclaredExperience.class);
    when(experienceRepository.save(any())).thenReturn(saved);
    when(studentService.getStudentById(studentId)).thenReturn(student);

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
    when(studentService.getStudentById(studentId)).thenReturn(student);

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
    verify(studentService).getStudentById(studentId);
    verify(experienceRepository).save(any());
  }

  @Test
  void shouldThrowWhenStudentNotFound() {
    when(studentService.getStudentById(studentId)).thenThrow(new UserIsNotStudentException());

    assertThrows(
        UserIsNotStudentException.class,
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

  @Test
  void shouldUpdateExperienceWhenStudentIsOwner() {
    Student loggedIn = student;
    DeclaredExperience experience = mock(DeclaredExperience.class);
    DeclaredExperience saved = mock(DeclaredExperience.class);
    UUID expId = UUID.randomUUID();

    when(loggedInUserService.getLoggedInStudent()).thenReturn(loggedIn);
    when(experienceRepository.findById(expId)).thenReturn(Optional.of(experience));
    when(experience.getStudent()).thenReturn(loggedIn);
    when(experienceRepository.save(any())).thenReturn(saved);

    DeclaredExperience result =
        service.update(
            expId,
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
            end,
            false);

    assertNotNull(result);
    verify(loggedInUserService).getLoggedInStudent();
    verify(experienceRepository).findById(expId);
    verify(experienceRepository).save(any());
  }

  @Test
  void shouldThrowWhenExperienceNotFoundOnUpdate() {
    when(loggedInUserService.getLoggedInStudent()).thenReturn(student);
    when(experienceRepository.findById(any())).thenReturn(Optional.empty());

    assertThrows(
        DeclaredExperienceNotFoundException.class,
        () ->
            service.update(
                UUID.randomUUID(),
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
                end,
                false));
  }

  @Test
  void shouldThrowWhenStudentIsNotOwnerOnUpdate() {
    Student loggedIn = student;
    Student other = StudentFixture.create().withId(UUID.randomUUID()).toModel();
    DeclaredExperience experience = mock(DeclaredExperience.class);

    when(loggedInUserService.getLoggedInStudent()).thenReturn(loggedIn);
    when(experienceRepository.findById(any())).thenReturn(Optional.of(experience));
    when(experience.getStudent()).thenReturn(other);

    assertThrows(
        UserNotAuthorizedException.class,
        () ->
            service.update(
                UUID.randomUUID(),
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
                end,
                false));
  }

  @Test
  void shouldThrowOnUpdateWhenInvalidFields() {
    Student loggedIn = student;
    DeclaredExperience experience = mock(DeclaredExperience.class);

    when(loggedInUserService.getLoggedInStudent()).thenReturn(loggedIn);
    when(experienceRepository.findById(any())).thenReturn(Optional.of(experience));
    when(experience.getStudent()).thenReturn(loggedIn);

    assertThrows(
        FieldValidationException.class,
        () ->
            service.update(
                UUID.randomUUID(),
                "", // invalid blank title
                EExperienceType.PROFESSIONAL,
                "Org",
                "Sector",
                "Paris",
                "Desc",
                "Source",
                "Summary",
                "https://test.fr",
                start,
                end,
                false));
  }

  @Test
  void shouldThrowOnUpdateWhenDatesAreInvalid() {
    Student loggedIn = student;
    DeclaredExperience experience = mock(DeclaredExperience.class);
    LocalDate wrongEnd = start.minusDays(1);

    when(loggedInUserService.getLoggedInStudent()).thenReturn(loggedIn);
    when(experienceRepository.findById(any())).thenReturn(Optional.of(experience));
    when(experience.getStudent()).thenReturn(loggedIn);

    assertThrows(
        FieldValidationException.class,
        () ->
            service.update(
                UUID.randomUUID(),
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
                wrongEnd,
                false));
  }

  @Test
  void shouldThrowOnUpdateWhenOrganizationMissing() {
    Student loggedIn = student;
    DeclaredExperience experience = mock(DeclaredExperience.class);

    when(loggedInUserService.getLoggedInStudent()).thenReturn(loggedIn);
    when(experienceRepository.findById(any())).thenReturn(Optional.of(experience));
    when(experience.getStudent()).thenReturn(loggedIn);

    assertThrows(
        FieldValidationException.class,
        () ->
            service.update(
                UUID.randomUUID(),
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
                end,
                false));
  }

  @Test
  void shouldThrowOnUpdateWhenOrganizationTooLong() {
    Student loggedIn = student;
    DeclaredExperience experience = mock(DeclaredExperience.class);
    String tooLong = "A".repeat(81);

    when(loggedInUserService.getLoggedInStudent()).thenReturn(loggedIn);
    when(experienceRepository.findById(any())).thenReturn(Optional.of(experience));
    when(experience.getStudent()).thenReturn(loggedIn);

    assertThrows(
        FieldValidationException.class,
        () ->
            service.update(
                UUID.randomUUID(),
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
                end,
                false));
  }

  @Test
  void shouldThrowOnUpdateWhenActivitySectorTooLong() {
    Student loggedIn = student;
    DeclaredExperience experience = mock(DeclaredExperience.class);
    String tooLong = "A".repeat(51);

    when(loggedInUserService.getLoggedInStudent()).thenReturn(loggedIn);
    when(experienceRepository.findById(any())).thenReturn(Optional.of(experience));
    when(experience.getStudent()).thenReturn(loggedIn);

    assertThrows(
        FieldValidationException.class,
        () ->
            service.update(
                UUID.randomUUID(),
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
                end,
                false));
  }

  @Test
  void shouldThrowOnUpdateWhenLocationTooLong() {
    Student loggedIn = student;
    DeclaredExperience experience = mock(DeclaredExperience.class);
    String tooLong = "A".repeat(51);

    when(loggedInUserService.getLoggedInStudent()).thenReturn(loggedIn);
    when(experienceRepository.findById(any())).thenReturn(Optional.of(experience));
    when(experience.getStudent()).thenReturn(loggedIn);

    assertThrows(
        FieldValidationException.class,
        () ->
            service.update(
                UUID.randomUUID(),
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
                end,
                false));
  }

  @Test
  void shouldThrowOnUpdateWhenSourceTooLong() {
    Student loggedIn = student;
    DeclaredExperience experience = mock(DeclaredExperience.class);
    String tooLong = "A".repeat(201);

    when(loggedInUserService.getLoggedInStudent()).thenReturn(loggedIn);
    when(experienceRepository.findById(any())).thenReturn(Optional.of(experience));
    when(experience.getStudent()).thenReturn(loggedIn);

    assertThrows(
        FieldValidationException.class,
        () ->
            service.update(
                UUID.randomUUID(),
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
                end,
                false));
  }

  @Test
  void shouldThrowOnUpdateWhenDescriptionTooLong() {
    Student loggedIn = student;
    DeclaredExperience experience = mock(DeclaredExperience.class);
    String tooLong = "A".repeat(401);

    when(loggedInUserService.getLoggedInStudent()).thenReturn(loggedIn);
    when(experienceRepository.findById(any())).thenReturn(Optional.of(experience));
    when(experience.getStudent()).thenReturn(loggedIn);

    assertThrows(
        FieldValidationException.class,
        () ->
            service.update(
                UUID.randomUUID(),
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
                end,
                false));
  }

  @Test
  void shouldThrowOnUpdateWhenSummaryTooLong() {
    Student loggedIn = student;
    DeclaredExperience experience = mock(DeclaredExperience.class);
    String tooLong = "A".repeat(401);

    when(loggedInUserService.getLoggedInStudent()).thenReturn(loggedIn);
    when(experienceRepository.findById(any())).thenReturn(Optional.of(experience));
    when(experience.getStudent()).thenReturn(loggedIn);

    assertThrows(
        FieldValidationException.class,
        () ->
            service.update(
                UUID.randomUUID(),
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
                end,
                false));
  }

  @Test
  void shouldThrowOnUpdateWhenInvalidUrl() {
    Student loggedIn = student;
    DeclaredExperience experience = mock(DeclaredExperience.class);

    when(loggedInUserService.getLoggedInStudent()).thenReturn(loggedIn);
    when(experienceRepository.findById(any())).thenReturn(Optional.of(experience));
    when(experience.getStudent()).thenReturn(loggedIn);

    assertThrows(
        FieldValidationException.class,
        () ->
            service.update(
                UUID.randomUUID(),
                "Titre",
                EExperienceType.PROFESSIONAL,
                "Org",
                "Sector",
                "Paris",
                "Desc",
                "Source",
                "Summary",
                "not-an-url",
                start,
                end,
                false));
  }

  @Test
  void shouldUpdateAllFieldsOnExperience() {
    // GIVEN
    Student loggedIn = student;
    DeclaredExperience experience = mock(DeclaredExperience.class);
    DeclaredExperience saved = mock(DeclaredExperience.class);
    UUID expId = UUID.randomUUID();

    when(loggedInUserService.getLoggedInStudent()).thenReturn(loggedIn);
    when(experienceRepository.findById(expId)).thenReturn(Optional.of(experience));
    when(experience.getStudent()).thenReturn(loggedIn);
    when(experienceRepository.save(any())).thenReturn(saved);

    // WHEN
    DeclaredExperience result =
        service.update(
            expId,
            "New Title",
            EExperienceType.PROFESSIONAL,
            "New Org",
            "New Sector",
            "New City",
            "New Description",
            "New Source",
            "New Summary",
            "https://new.link",
            start,
            end,
            true);

    // THEN
    assertNotNull(result);

    // Verify all values are set on the existing entity
    verify(experience).setTitle("New Title");
    verify(experience).setExperienceType(EExperienceType.PROFESSIONAL);
    verify(experience).setOrganization("New Org");
    verify(experience).setActivitySector("New Sector");
    verify(experience).setLocation("New City");
    verify(experience).setDescription("New Description");
    verify(experience).setSourceOfInformation("New Source");
    verify(experience).setSummary("New Summary");
    verify(experience).setExternalLink("https://new.link");
    verify(experience).setStartDate(start);
    verify(experience).setEndDate(end);
    verify(experience).setValorized(true);

    verify(experienceRepository).save(experience);
  }

  @Test
  void thenItShouldUpdateValorizedFieldToTrue() {
    Student loggedIn = student;
    DeclaredExperience experience = mock(DeclaredExperience.class);
    DeclaredExperience saved = mock(DeclaredExperience.class);
    UUID expId = UUID.randomUUID();

    when(loggedInUserService.getLoggedInStudent()).thenReturn(loggedIn);
    when(experienceRepository.findById(expId)).thenReturn(Optional.of(experience));
    when(experience.getStudent()).thenReturn(loggedIn);
    when(experienceRepository.save(any())).thenReturn(saved);

    service.update(
        expId,
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
        end,
        true);

    verify(experience).setValorized(true);
  }

  @Test
  void thenItShouldUpdateValorizedFieldToFalse() {
    Student loggedIn = student;
    DeclaredExperience experience = mock(DeclaredExperience.class);
    DeclaredExperience saved = mock(DeclaredExperience.class);
    UUID expId = UUID.randomUUID();

    when(loggedInUserService.getLoggedInStudent()).thenReturn(loggedIn);
    when(experienceRepository.findById(expId)).thenReturn(Optional.of(experience));
    when(experience.getStudent()).thenReturn(loggedIn);
    when(experienceRepository.save(any())).thenReturn(saved);

    service.update(
        expId,
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
        end,
        false);

    verify(experience).setValorized(false);
  }

  @Test
  void shouldDeleteExperiencesWhenStudentIsOwner() {
    Student loggedIn = student;
    UUID id1 = UUID.randomUUID();
    UUID id2 = UUID.randomUUID();
    DeclaredExperience exp1 = mock(DeclaredExperience.class);
    DeclaredExperience exp2 = mock(DeclaredExperience.class);

    when(loggedInUserService.getLoggedInStudent()).thenReturn(loggedIn);
    when(exp1.getStudent()).thenReturn(loggedIn);
    when(exp2.getStudent()).thenReturn(loggedIn);
    when(exp1.getId()).thenReturn(id1);
    when(exp2.getId()).thenReturn(id2);
    when(experienceRepository.findAllById(List.of(id1, id2))).thenReturn(List.of(exp1, exp2));
    doNothing().when(associationService).deleteAllOf(List.of(id1, id2), DeclaredExperience.class);

    service.delete(List.of(id1, id2));

    verify(experienceRepository).findAllById(List.of(id1, id2));
    verify(experienceRepository).removeAllFromDatabase(List.of(exp1, exp2));
    verify(associationService).deleteAllOf(List.of(id1, id2), DeclaredExperience.class);
  }

  @Test
  void shouldThrowWhenExperienceNotFoundOnDelete() {
    Student loggedIn = student;
    UUID id1 = UUID.randomUUID();
    UUID id2 = UUID.randomUUID();
    DeclaredExperience exp1 = mock(DeclaredExperience.class);

    when(loggedInUserService.getLoggedInStudent()).thenReturn(loggedIn);
    when(exp1.getStudent()).thenReturn(loggedIn);

    when(experienceRepository.findAllById(List.of(id1, id2))).thenReturn(List.of(exp1));

    assertThrows(
        DeclaredExperienceNotFoundException.class, () -> service.delete(List.of(id1, id2)));
  }

  @Test
  void shouldThrowWhenOneExperienceBelongsToAnotherStudent() {
    Student loggedIn = student;
    Student other = StudentFixture.create().withId(UUID.randomUUID()).toModel();
    UUID id1 = UUID.randomUUID();
    UUID id2 = UUID.randomUUID();
    DeclaredExperience exp1 = mock(DeclaredExperience.class);
    DeclaredExperience exp2 = mock(DeclaredExperience.class);

    when(loggedInUserService.getLoggedInStudent()).thenReturn(loggedIn);
    when(exp1.getStudent()).thenReturn(loggedIn);
    when(exp2.getStudent()).thenReturn(other);

    when(experienceRepository.findAllById(List.of(id1, id2))).thenReturn(List.of(exp1, exp2));

    assertThrows(UserNotAuthorizedException.class, () -> service.delete(List.of(id1, id2)));
  }

  @Test
  void shouldThrowWhenNoExperienceFoundAtAll() {
    when(loggedInUserService.getLoggedInStudent()).thenReturn(student);
    when(experienceRepository.findAllById(anyList())).thenReturn(List.of());

    assertThrows(
        DeclaredExperienceNotFoundException.class,
        () -> service.delete(List.of(UUID.randomUUID())));
  }

  @Test
  void shouldHandleSingleIdDeletion() {
    Student loggedIn = student;
    UUID id = UUID.randomUUID();
    DeclaredExperience exp = mock(DeclaredExperience.class);

    when(loggedInUserService.getLoggedInStudent()).thenReturn(loggedIn);
    when(exp.getStudent()).thenReturn(loggedIn);
    when(exp.getId()).thenReturn(id);
    when(experienceRepository.findAllById(List.of(id))).thenReturn(List.of(exp));

    service.delete(List.of(id));

    verify(experienceRepository).removeAllFromDatabase(List.of(exp));
  }

  @Test
  void getView_shouldDelegateToRepositoryAndReturnResult() {
    Student loggedIn = student;
    PageCriteria criteria = new PageCriteria(1, 8);
    DeclaredExperience experience = mock(DeclaredExperience.class);
    UUID experienceId = UUID.randomUUID();
    when(experience.getId()).thenReturn(experienceId);
    PagedResult<DeclaredExperience> repositoryResult =
        new PagedResult<>(List.of(experience), new PageInfo(1, 8, 1));

    when(loggedInUserService.getLoggedInStudent()).thenReturn(loggedIn);
    when(experienceRepository.findAllByStudent(loggedIn, criteria, (Boolean) null, null))
        .thenReturn(repositoryResult);

    PagedResult<DeclaredExperienceData> result = service.getView(criteria, null, null);

    assertSame(repositoryResult.pageInfo(), result.pageInfo());
    assertEquals(
        List.of(
            new DeclaredExperienceData(experience, new DeclaredExperienceAssociationCount(0, 0))),
        result.content());
    verify(experienceRepository).findAllByStudent(loggedIn, criteria, (Boolean) null, null);
  }

  @Test
  void getView_shouldDelegateIsValorizedFilterToRepository() {
    Student loggedIn = student;
    PageCriteria criteria = new PageCriteria(1, 8);
    DeclaredExperience experience = mock(DeclaredExperience.class);
    UUID experienceId = UUID.randomUUID();
    when(experience.getId()).thenReturn(experienceId);
    PagedResult<DeclaredExperience> repositoryResult =
        new PagedResult<>(List.of(experience), new PageInfo(1, 8, 1));

    when(loggedInUserService.getLoggedInStudent()).thenReturn(loggedIn);
    when(experienceRepository.findAllByStudent(loggedIn, criteria, true, null))
        .thenReturn(repositoryResult);

    PagedResult<DeclaredExperienceData> result = service.getView(criteria, true, null);

    assertSame(repositoryResult.pageInfo(), result.pageInfo());
    assertEquals(
        List.of(
            new DeclaredExperienceData(experience, new DeclaredExperienceAssociationCount(0, 0))),
        result.content());
    verify(experienceRepository).findAllByStudent(loggedIn, criteria, true, null);
  }

  @Test
  void getAssociations_should_return_trace_associations() {
    BddLogger.given("a declared experience with trace associations");

    UUID experienceId = UUID.randomUUID();

    Student loggedIn = student;
    DeclaredExperience experience = mock(DeclaredExperience.class);
    when(experience.getId()).thenReturn(experienceId);
    when(experience.getStudent()).thenReturn(loggedIn);

    when(loggedInUserService.getLoggedInStudent()).thenReturn(loggedIn);
    when(experienceRepository.findById(experienceId)).thenReturn(Optional.of(experience));

    Association association = mock(Association.class);
    UUID traceId = UUID.randomUUID();

    when(association.getId()).thenReturn(UUID.randomUUID());
    when(association.getId1()).thenReturn(traceId);
    when(association.getAssociationType()).thenReturn(EAssociationType.TRACE_DECLARED_EXPERIENCE);

    when(associationService.getAllOf(
            experienceId,
            DeclaredExperience.class,
            EAssociationType.getAllBy(DeclaredExperience.class)))
        .thenReturn(List.of(association));

    Trace trace = TraceFixture.create().withId(traceId).toModel();
    when(traceService.findAllTracesById(List.of(traceId))).thenReturn(List.of(trace));

    BddLogger.when("getting associations");

    var result = service.getAssociations(experienceId);

    BddLogger.then("it should return mapped trace associations");

    assertNotNull(result);
    assertEquals(1, result.traceAssociations().size());
  }

  @Test
  void getAssociations_should_throw_DeclaredExperienceNotFoundException() {
    BddLogger.given("an unknown declared experience");

    UUID experienceId = UUID.randomUUID();

    when(loggedInUserService.getLoggedInStudent()).thenReturn(student);
    when(experienceRepository.findById(experienceId)).thenReturn(Optional.empty());

    BddLogger.when("getting associations");

    assertThrows(
        DeclaredExperienceNotFoundException.class, () -> service.getAssociations(experienceId));

    BddLogger.then("it should throw DeclaredExperienceNotFoundException");
  }

  @Test
  void getAssociations_should_throw_UserNotAuthorizedException() {
    BddLogger.given("a declared experience owned by another student");

    UUID experienceId = UUID.randomUUID();

    Student loggedIn = student;
    Student other = StudentFixture.create().toModel();

    DeclaredExperience experience = mock(DeclaredExperience.class);
    when(experience.getStudent()).thenReturn(other);

    when(loggedInUserService.getLoggedInStudent()).thenReturn(loggedIn);
    when(experienceRepository.findById(experienceId)).thenReturn(Optional.of(experience));

    BddLogger.when("getting associations");

    assertThrows(UserNotAuthorizedException.class, () -> service.getAssociations(experienceId));

    BddLogger.then("it should throw UserNotAuthorizedException");
  }

  @Test
  void getAssociations_should_return_empty_when_no_trace_associations() {
    BddLogger.given("a declared experience without trace associations");

    UUID experienceId = UUID.randomUUID();

    DeclaredExperience experience = mock(DeclaredExperience.class);
    when(experience.getId()).thenReturn(experienceId);
    when(experience.getStudent()).thenReturn(student);

    when(loggedInUserService.getLoggedInStudent()).thenReturn(student);
    when(experienceRepository.findById(experienceId)).thenReturn(Optional.of(experience));

    when(associationService.getAllOf(
            experienceId,
            DeclaredExperience.class,
            EAssociationType.getAllBy(DeclaredExperience.class)))
        .thenReturn(List.of());

    when(traceService.findAllTracesById(any())).thenReturn(List.of());

    BddLogger.when("getting associations");

    var result = service.getAssociations(experienceId);

    BddLogger.then("it should return empty list");

    assertNotNull(result);
    assertTrue(result.traceAssociations().isEmpty());
  }

  @Nested
  class WhenAssociatingDeclaredExperienceWithDeclaredSkills {

    @Test
    void shouldCreateAssociationsSuccessfully() {
      BddLogger.given("a declared experience and two declared skill progresses of the student");

      UUID experienceId = UUID.randomUUID();
      DeclaredExperience experience = mock(DeclaredExperience.class);
      when(experience.getId()).thenReturn(experienceId);
      when(experience.getStudent()).thenReturn(student);

      DeclaredSkillProgress skill1 = mockDeclaredSkillProgress(student);
      DeclaredSkillProgress skill2 = mockDeclaredSkillProgress(student);

      when(loggedInUserService.getLoggedInStudent()).thenReturn(student);
      when(experienceRepository.findById(experienceId)).thenReturn(Optional.of(experience));
      when(declaredSkillProgressService.findAllDeclaredSkillProgressesByIds(
              List.of(skill1.getId(), skill2.getId())))
          .thenReturn(List.of(skill1, skill2));

      when(associationService.getAllOf(
              experienceId,
              DeclaredExperience.class,
              EAssociationType.getAllBy(DeclaredExperience.class)))
          .thenReturn(List.of());
      when(traceService.findAllTracesById(List.of())).thenReturn(List.of());
      when(declaredSkillProgressService.findAllDeclaredSkillProgressesByIds(List.of()))
          .thenReturn(List.of());

      BddLogger.when("associating the experience with both declared skill progresses");

      var result =
          service.associateDeclaredExperienceWithDeclaredSkills(
              experienceId, List.of(skill1.getId(), skill2.getId()));

      BddLogger.then("it should create both DECLARED_EXPERIENCE_DECLARED_SKILL associations");

      verify(associationService)
          .createAll(
              argThat(
                  list ->
                      list.size() == 2
                          && list.stream()
                              .allMatch(
                                  assocData ->
                                      assocData.associationType()
                                              == EAssociationType.DECLARED_EXPERIENCE_DECLARED_SKILL
                                          && assocData.id1().equals(experienceId)
                                          && (assocData.id2().equals(skill1.getId())
                                              || assocData.id2().equals(skill2.getId())))));
      assertNotNull(result);
    }

    @Test
    void shouldThrowDeclaredExperienceNotFoundException() {
      BddLogger.given("a non-existent declared experience");

      UUID experienceId = UUID.randomUUID();
      UUID skillId = UUID.randomUUID();

      when(loggedInUserService.getLoggedInStudent()).thenReturn(student);
      when(experienceRepository.findById(experienceId)).thenReturn(Optional.empty());

      BddLogger.when("associating with a declared skill progress");

      BddLogger.then("it should throw DeclaredExperienceNotFoundException");

      assertThrows(
          DeclaredExperienceNotFoundException.class,
          () ->
              service.associateDeclaredExperienceWithDeclaredSkills(
                  experienceId, List.of(skillId)));

      verifyNoInteractions(declaredSkillProgressService);
      verifyNoInteractions(associationService);
    }

    @Test
    void shouldThrowUserNotAuthorizedException_whenExperienceBelongsToAnotherStudent() {
      BddLogger.given("a declared experience belonging to another student");

      UUID experienceId = UUID.randomUUID();
      UUID skillId = UUID.randomUUID();
      Student anotherStudent = StudentFixture.create().toModel();

      DeclaredExperience experience = mock(DeclaredExperience.class);
      when(experience.getStudent()).thenReturn(anotherStudent);

      when(loggedInUserService.getLoggedInStudent()).thenReturn(student);
      when(experienceRepository.findById(experienceId)).thenReturn(Optional.of(experience));

      BddLogger.when("associating with a declared skill progress");

      BddLogger.then("it should throw UserNotAuthorizedException");

      assertThrows(
          UserNotAuthorizedException.class,
          () ->
              service.associateDeclaredExperienceWithDeclaredSkills(
                  experienceId, List.of(skillId)));

      verifyNoInteractions(declaredSkillProgressService);
      verifyNoInteractions(associationService);
    }

    @Test
    void shouldThrowDeclaredSkillProgressNotFoundException_whenSomeIdsAreMissing() {
      BddLogger.given("a declared experience and some non-existing declared skill progress ids");

      UUID experienceId = UUID.randomUUID();
      DeclaredExperience experience = mock(DeclaredExperience.class);
      when(experience.getStudent()).thenReturn(student);

      DeclaredSkillProgress skill1 = mockDeclaredSkillProgress(student);
      UUID missingSkillId = UUID.randomUUID();

      when(loggedInUserService.getLoggedInStudent()).thenReturn(student);
      when(experienceRepository.findById(experienceId)).thenReturn(Optional.of(experience));
      when(declaredSkillProgressService.findAllDeclaredSkillProgressesByIds(
              List.of(skill1.getId(), missingSkillId)))
          .thenReturn(List.of(skill1));

      BddLogger.when("associating with a mix of valid and non-existing declared skill ids");

      BddLogger.then("it should throw DeclaredSkillProgressNotFoundException");

      assertThrows(
          DeclaredSkillProgressNotFoundException.class,
          () ->
              service.associateDeclaredExperienceWithDeclaredSkills(
                  experienceId, List.of(skill1.getId(), missingSkillId)));

      verifyNoInteractions(associationService);
    }

    @Test
    void shouldThrowUserNotAuthorizedException_whenDeclaredSkillBelongsToAnotherStudent() {
      BddLogger.given("a declared skill progress belonging to another student");

      UUID experienceId = UUID.randomUUID();
      DeclaredExperience experience = mock(DeclaredExperience.class);
      when(experience.getStudent()).thenReturn(student);

      Student anotherStudent = StudentFixture.create().toModel();
      DeclaredSkillProgress skill = mockDeclaredSkillProgress(anotherStudent);

      when(loggedInUserService.getLoggedInStudent()).thenReturn(student);
      when(experienceRepository.findById(experienceId)).thenReturn(Optional.of(experience));
      when(declaredSkillProgressService.findAllDeclaredSkillProgressesByIds(List.of(skill.getId())))
          .thenReturn(List.of(skill));

      BddLogger.when("associating with a declared skill progress owned by another student");

      BddLogger.then("it should throw UserNotAuthorizedException");

      assertThrows(
          UserNotAuthorizedException.class,
          () ->
              service.associateDeclaredExperienceWithDeclaredSkills(
                  experienceId, List.of(skill.getId())));

      verifyNoInteractions(associationService);
    }

    @Test
    void shouldHandleEmptyDeclaredSkillProgressList() {
      BddLogger.given("a declared experience and an empty declared skill progress list");

      UUID experienceId = UUID.randomUUID();
      DeclaredExperience experience = mock(DeclaredExperience.class);
      when(experience.getId()).thenReturn(experienceId);
      when(experience.getStudent()).thenReturn(student);

      when(loggedInUserService.getLoggedInStudent()).thenReturn(student);
      when(experienceRepository.findById(experienceId)).thenReturn(Optional.of(experience));
      when(declaredSkillProgressService.findAllDeclaredSkillProgressesByIds(List.of()))
          .thenReturn(List.of());
      when(associationService.getAllOf(
              experienceId,
              DeclaredExperience.class,
              EAssociationType.getAllBy(DeclaredExperience.class)))
          .thenReturn(List.of());
      when(traceService.findAllTracesById(List.of())).thenReturn(List.of());

      BddLogger.when("associating with an empty list");

      var result = service.associateDeclaredExperienceWithDeclaredSkills(experienceId, List.of());

      BddLogger.then("it should create no association and return the current associations");

      verify(associationService).createAll(argThat(List::isEmpty));
      assertNotNull(result);
    }

    @Test
    void shouldCreateOnlyOneAssociation_whenRequestContainsDuplicatedId() {
      BddLogger.given("a request containing the same declared skill progress id twice");

      UUID experienceId = UUID.randomUUID();
      DeclaredExperience experience = mock(DeclaredExperience.class);
      when(experience.getId()).thenReturn(experienceId);
      when(experience.getStudent()).thenReturn(student);

      DeclaredSkillProgress skill = mockDeclaredSkillProgress(student);

      when(loggedInUserService.getLoggedInStudent()).thenReturn(student);
      when(experienceRepository.findById(experienceId)).thenReturn(Optional.of(experience));
      when(declaredSkillProgressService.findAllDeclaredSkillProgressesByIds(List.of(skill.getId())))
          .thenReturn(List.of(skill));
      when(associationService.getAllOf(
              experienceId,
              DeclaredExperience.class,
              EAssociationType.getAllBy(DeclaredExperience.class)))
          .thenReturn(List.of());
      when(traceService.findAllTracesById(List.of())).thenReturn(List.of());
      when(declaredSkillProgressService.findAllDeclaredSkillProgressesByIds(List.of()))
          .thenReturn(List.of());

      BddLogger.when("associating with the duplicated id");

      service.associateDeclaredExperienceWithDeclaredSkills(
          experienceId, List.of(skill.getId(), skill.getId()));

      BddLogger.then("it should create a single association, not two");

      verify(declaredSkillProgressService)
          .findAllDeclaredSkillProgressesByIds(List.of(skill.getId()));
      verify(associationService)
          .createAll(
              argThat(
                  list ->
                      list.size() == 1
                          && list.get(0).id1().equals(experienceId)
                          && list.get(0).id2().equals(skill.getId())));
    }

    @Test
    void shouldPropagateAssociationAlreadyExistException() {
      BddLogger.given("a declared skill progress already associated with the experience");

      UUID experienceId = UUID.randomUUID();
      DeclaredExperience experience = mock(DeclaredExperience.class);
      when(experience.getId()).thenReturn(experienceId);
      when(experience.getStudent()).thenReturn(student);

      DeclaredSkillProgress skill = mockDeclaredSkillProgress(student);

      when(loggedInUserService.getLoggedInStudent()).thenReturn(student);
      when(experienceRepository.findById(experienceId)).thenReturn(Optional.of(experience));
      when(declaredSkillProgressService.findAllDeclaredSkillProgressesByIds(List.of(skill.getId())))
          .thenReturn(List.of(skill));
      when(associationService.createAll(anyList()))
          .thenThrow(new AssociationAlreadyExistException());

      BddLogger.when("associating with an already associated declared skill progress");

      BddLogger.then("it should propagate AssociationAlreadyExistException");

      assertThrows(
          AssociationAlreadyExistException.class,
          () ->
              service.associateDeclaredExperienceWithDeclaredSkills(
                  experienceId, List.of(skill.getId())));
    }
  }

  @Nested
  class WhenAssociatingDeclaredExperienceWithTraces {

    @Test
    void shouldCreateAssociationsSuccessfully() {
      BddLogger.given("a declared experience and two traces of the student");

      UUID experienceId = UUID.randomUUID();
      DeclaredExperience experience = mock(DeclaredExperience.class);
      when(experience.getId()).thenReturn(experienceId);
      when(experience.getStudent()).thenReturn(student);

      Trace trace1 = TraceFixture.create().withUser(student.getUser()).toModel();
      Trace trace2 = TraceFixture.create().withUser(student.getUser()).toModel();

      when(loggedInUserService.getLoggedInStudent()).thenReturn(student);
      when(experienceRepository.findById(experienceId)).thenReturn(Optional.of(experience));
      when(traceService.findAllTracesById(List.of(trace1.getId(), trace2.getId())))
          .thenReturn(List.of(trace1, trace2));

      when(associationService.getAllOf(
              experienceId,
              DeclaredExperience.class,
              EAssociationType.getAllBy(DeclaredExperience.class)))
          .thenReturn(List.of());
      when(traceService.findAllTracesById(List.of())).thenReturn(List.of());
      when(declaredSkillProgressService.findAllDeclaredSkillProgressesByIds(List.of()))
          .thenReturn(List.of());

      BddLogger.when("associating the experience with both traces");

      var result =
          service.associateDeclaredExperienceWithTraces(
              experienceId, List.of(trace1.getId(), trace2.getId()));

      BddLogger.then("it should create both TRACE_DECLARED_EXPERIENCE associations");

      verify(associationService)
          .createAll(
              argThat(
                  list ->
                      list.size() == 2
                          && list.stream()
                              .allMatch(
                                  assocData ->
                                      assocData.associationType()
                                              == EAssociationType.TRACE_DECLARED_EXPERIENCE
                                          && assocData.id2().equals(experienceId)
                                          && (assocData.id1().equals(trace1.getId())
                                              || assocData.id1().equals(trace2.getId())))));
      assertNotNull(result);
    }

    @Test
    void shouldThrowDeclaredExperienceNotFoundException() {
      BddLogger.given("a non-existent declared experience");

      UUID experienceId = UUID.randomUUID();
      UUID traceId = UUID.randomUUID();

      when(loggedInUserService.getLoggedInStudent()).thenReturn(student);
      when(experienceRepository.findById(experienceId)).thenReturn(Optional.empty());

      BddLogger.when("associating with a trace");

      BddLogger.then("it should throw DeclaredExperienceNotFoundException");

      assertThrows(
          DeclaredExperienceNotFoundException.class,
          () -> service.associateDeclaredExperienceWithTraces(experienceId, List.of(traceId)));

      verifyNoInteractions(traceService);
      verifyNoInteractions(associationService);
    }

    @Test
    void shouldThrowUserNotAuthorizedException_whenExperienceBelongsToAnotherStudent() {
      BddLogger.given("a declared experience belonging to another student");

      UUID experienceId = UUID.randomUUID();
      UUID traceId = UUID.randomUUID();
      Student anotherStudent = StudentFixture.create().toModel();

      DeclaredExperience experience = mock(DeclaredExperience.class);
      when(experience.getStudent()).thenReturn(anotherStudent);

      when(loggedInUserService.getLoggedInStudent()).thenReturn(student);
      when(experienceRepository.findById(experienceId)).thenReturn(Optional.of(experience));

      BddLogger.when("associating with a trace");

      BddLogger.then("it should throw UserNotAuthorizedException");

      assertThrows(
          UserNotAuthorizedException.class,
          () -> service.associateDeclaredExperienceWithTraces(experienceId, List.of(traceId)));

      verifyNoInteractions(traceService);
      verifyNoInteractions(associationService);
    }

    @Test
    void shouldThrowTraceNotFoundException_whenSomeIdsAreMissing() {
      BddLogger.given("a declared experience and some non-existing trace ids");

      UUID experienceId = UUID.randomUUID();
      DeclaredExperience experience = mock(DeclaredExperience.class);
      when(experience.getStudent()).thenReturn(student);

      Trace trace1 = TraceFixture.create().withUser(student.getUser()).toModel();
      UUID missingTraceId = UUID.randomUUID();

      when(loggedInUserService.getLoggedInStudent()).thenReturn(student);
      when(experienceRepository.findById(experienceId)).thenReturn(Optional.of(experience));
      when(traceService.findAllTracesById(List.of(trace1.getId(), missingTraceId)))
          .thenReturn(List.of(trace1));

      BddLogger.when("associating with a mix of valid and non-existing trace ids");

      BddLogger.then("it should throw TraceNotFoundException");

      assertThrows(
          TraceNotFoundException.class,
          () ->
              service.associateDeclaredExperienceWithTraces(
                  experienceId, List.of(trace1.getId(), missingTraceId)));

      verifyNoInteractions(associationService);
    }

    @Test
    void shouldThrowUserNotAuthorizedException_whenTraceBelongsToAnotherStudent() {
      BddLogger.given("a trace belonging to another student");

      UUID experienceId = UUID.randomUUID();
      DeclaredExperience experience = mock(DeclaredExperience.class);
      when(experience.getStudent()).thenReturn(student);

      Student anotherStudent = StudentFixture.create().toModel();
      Trace trace = TraceFixture.create().withUser(anotherStudent.getUser()).toModel();

      when(loggedInUserService.getLoggedInStudent()).thenReturn(student);
      when(experienceRepository.findById(experienceId)).thenReturn(Optional.of(experience));
      when(traceService.findAllTracesById(List.of(trace.getId()))).thenReturn(List.of(trace));

      BddLogger.when("associating with a trace owned by another student");

      BddLogger.then("it should throw UserNotAuthorizedException");

      assertThrows(
          UserNotAuthorizedException.class,
          () ->
              service.associateDeclaredExperienceWithTraces(experienceId, List.of(trace.getId())));

      verifyNoInteractions(associationService);
    }

    @Test
    void shouldHandleEmptyTraceList() {
      BddLogger.given("a declared experience and an empty trace list");

      UUID experienceId = UUID.randomUUID();
      DeclaredExperience experience = mock(DeclaredExperience.class);
      when(experience.getId()).thenReturn(experienceId);
      when(experience.getStudent()).thenReturn(student);

      when(loggedInUserService.getLoggedInStudent()).thenReturn(student);
      when(experienceRepository.findById(experienceId)).thenReturn(Optional.of(experience));
      when(traceService.findAllTracesById(List.of())).thenReturn(List.of());
      when(associationService.getAllOf(
              experienceId,
              DeclaredExperience.class,
              EAssociationType.getAllBy(DeclaredExperience.class)))
          .thenReturn(List.of());
      when(declaredSkillProgressService.findAllDeclaredSkillProgressesByIds(List.of()))
          .thenReturn(List.of());

      BddLogger.when("associating with an empty list");

      var result = service.associateDeclaredExperienceWithTraces(experienceId, List.of());

      BddLogger.then("it should create no association and return the current associations");

      verify(associationService).createAll(argThat(List::isEmpty));
      assertNotNull(result);
    }

    @Test
    void shouldCreateOnlyOneAssociation_whenRequestContainsDuplicatedId() {
      BddLogger.given("a request containing the same trace id twice");

      UUID experienceId = UUID.randomUUID();
      DeclaredExperience experience = mock(DeclaredExperience.class);
      when(experience.getId()).thenReturn(experienceId);
      when(experience.getStudent()).thenReturn(student);

      Trace trace = TraceFixture.create().withUser(student.getUser()).toModel();

      when(loggedInUserService.getLoggedInStudent()).thenReturn(student);
      when(experienceRepository.findById(experienceId)).thenReturn(Optional.of(experience));
      when(traceService.findAllTracesById(List.of(trace.getId()))).thenReturn(List.of(trace));
      when(associationService.getAllOf(
              experienceId,
              DeclaredExperience.class,
              EAssociationType.getAllBy(DeclaredExperience.class)))
          .thenReturn(List.of());
      when(traceService.findAllTracesById(List.of())).thenReturn(List.of());
      when(declaredSkillProgressService.findAllDeclaredSkillProgressesByIds(List.of()))
          .thenReturn(List.of());

      BddLogger.when("associating with the duplicated id");

      service.associateDeclaredExperienceWithTraces(
          experienceId, List.of(trace.getId(), trace.getId()));

      BddLogger.then("it should create a single association, not two");

      verify(traceService).findAllTracesById(List.of(trace.getId()));
      verify(associationService)
          .createAll(
              argThat(
                  list ->
                      list.size() == 1
                          && list.get(0).id1().equals(trace.getId())
                          && list.get(0).id2().equals(experienceId)));
    }

    @Test
    void shouldPropagateAssociationAlreadyExistException() {
      BddLogger.given("a trace already associated with the experience");

      UUID experienceId = UUID.randomUUID();
      DeclaredExperience experience = mock(DeclaredExperience.class);
      when(experience.getId()).thenReturn(experienceId);
      when(experience.getStudent()).thenReturn(student);

      Trace trace = TraceFixture.create().withUser(student.getUser()).toModel();

      when(loggedInUserService.getLoggedInStudent()).thenReturn(student);
      when(experienceRepository.findById(experienceId)).thenReturn(Optional.of(experience));
      when(traceService.findAllTracesById(List.of(trace.getId()))).thenReturn(List.of(trace));
      when(associationService.createAll(anyList()))
          .thenThrow(new AssociationAlreadyExistException());

      BddLogger.when("associating with an already associated trace");

      BddLogger.then("it should propagate AssociationAlreadyExistException");

      assertThrows(
          AssociationAlreadyExistException.class,
          () ->
              service.associateDeclaredExperienceWithTraces(experienceId, List.of(trace.getId())));
    }
  }

  @Nested
  class WhenDeletingAssociations {

    @Test
    void deleteAssociations_should_delete_when_associations_belong_to_declaredExperience() {
      BddLogger.given("a logged-in student and a declared experience with associations");

      UUID experienceId = UUID.randomUUID();
      UUID associationId1 = UUID.randomUUID();
      UUID associationId2 = UUID.randomUUID();

      DeclaredExperience experience = mock(DeclaredExperience.class);
      when(experience.getId()).thenReturn(experienceId);
      when(experience.getStudent()).thenReturn(student);

      Association association1 = mock(Association.class);
      when(association1.getId()).thenReturn(associationId1);

      Association association2 = mock(Association.class);
      when(association2.getId()).thenReturn(associationId2);

      when(loggedInUserService.getLoggedInStudent()).thenReturn(student);
      when(experienceRepository.findById(experienceId)).thenReturn(Optional.of(experience));
      when(associationService.getAllOf(
              experienceId,
              DeclaredExperience.class,
              List.of(
                  EAssociationType.TRACE_DECLARED_EXPERIENCE,
                  EAssociationType.DECLARED_EXPERIENCE_DECLARED_SKILL)))
          .thenReturn(List.of(association1, association2));

      BddLogger.when("deleteAssociations is called");

      service.deleteAssociations(experienceId, List.of(associationId1));

      BddLogger.then("deleteAllByIds should be called with the given ids");

      verify(associationService).deleteAllByIds(List.of(associationId1));
    }

    @Test
    void deleteAssociations_should_throw_when_declaredExperience_not_found() {
      BddLogger.given("a non-existent declared experience id");

      UUID experienceId = UUID.randomUUID();

      when(loggedInUserService.getLoggedInStudent()).thenReturn(student);
      when(experienceRepository.findById(experienceId)).thenReturn(Optional.empty());

      BddLogger.when("deleteAssociations is called");

      BddLogger.then("it should throw DeclaredExperienceNotFoundException");

      assertThrows(
          DeclaredExperienceNotFoundException.class,
          () -> service.deleteAssociations(experienceId, List.of(UUID.randomUUID())));

      verify(associationService, never()).deleteAllByIds(anyList());
    }

    @Test
    void deleteAssociations_should_throw_when_experience_belongs_to_other_student() {
      BddLogger.given("a declared experience belonging to another student");

      UUID experienceId = UUID.randomUUID();
      Student anotherStudent = StudentFixture.create().toModel();

      DeclaredExperience experience = mock(DeclaredExperience.class);
      when(experience.getStudent()).thenReturn(anotherStudent);

      when(loggedInUserService.getLoggedInStudent()).thenReturn(student);
      when(experienceRepository.findById(experienceId)).thenReturn(Optional.of(experience));

      BddLogger.when("deleteAssociations is called");

      BddLogger.then("it should throw UserNotAuthorizedException");

      assertThrows(
          UserNotAuthorizedException.class,
          () -> service.deleteAssociations(experienceId, List.of(UUID.randomUUID())));

      verify(associationService, never()).deleteAllByIds(anyList());
    }

    @Test
    void deleteAssociations_should_throw_when_ids_not_associated() {
      BddLogger.given("a declared experience and an id that is not one of its associations");

      UUID experienceId = UUID.randomUUID();
      UUID associationId = UUID.randomUUID();

      DeclaredExperience experience = mock(DeclaredExperience.class);
      when(experience.getId()).thenReturn(experienceId);
      when(experience.getStudent()).thenReturn(student);

      Association association = mock(Association.class);
      when(association.getId()).thenReturn(associationId);

      when(loggedInUserService.getLoggedInStudent()).thenReturn(student);
      when(experienceRepository.findById(experienceId)).thenReturn(Optional.of(experience));
      when(associationService.getAllOf(
              experienceId,
              DeclaredExperience.class,
              List.of(
                  EAssociationType.TRACE_DECLARED_EXPERIENCE,
                  EAssociationType.DECLARED_EXPERIENCE_DECLARED_SKILL)))
          .thenReturn(List.of(association));

      BddLogger.when("deleteAssociations is called with a non associated id");

      BddLogger.then("it should throw UserNotAuthorizedException");

      assertThrows(
          UserNotAuthorizedException.class,
          () -> service.deleteAssociations(experienceId, List.of(UUID.randomUUID())));

      verify(associationService, never()).deleteAllByIds(anyList());
    }
  }
}
