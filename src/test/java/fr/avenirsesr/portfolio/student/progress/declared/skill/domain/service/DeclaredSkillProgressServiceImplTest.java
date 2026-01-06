package fr.avenirsesr.portfolio.student.progress.declared.skill.domain.service;

import static fr.avenirsesr.portfolio.common.validation.domain.constraints.FieldMaxLengths.DESCRIPTION_LENGTH;
import static java.util.UUID.randomUUID;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import fr.avenirsesr.portfolio.common.data.domain.model.PageCriteria;
import fr.avenirsesr.portfolio.common.data.domain.model.PagedResult;
import fr.avenirsesr.portfolio.common.externalskill.application.adapter.dto.ExternalSkillCategoryDTO;
import fr.avenirsesr.portfolio.common.externalskill.application.adapter.dto.ExternalSkillDetailsDTO;
import fr.avenirsesr.portfolio.common.externalskill.domain.model.enums.EExternalSkillCategoryType;
import fr.avenirsesr.portfolio.common.externalskill.domain.model.enums.EExternalSkillType;
import fr.avenirsesr.portfolio.common.security.domain.exception.UserNotAuthorizedException;
import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import fr.avenirsesr.portfolio.declaredskill.domain.exception.DeclaredSkillNotFoundException;
import fr.avenirsesr.portfolio.declaredskill.domain.exception.DuplicateDeclaredSkillException;
import fr.avenirsesr.portfolio.declaredskill.domain.exception.InvalidDescriptionException;
import fr.avenirsesr.portfolio.declaredskill.domain.model.DeclaredSkill;
import fr.avenirsesr.portfolio.declaredskill.domain.model.enums.EDeclaredSkillLevel;
import fr.avenirsesr.portfolio.declaredskill.domain.port.input.DeclaredSkillSyncService;
import fr.avenirsesr.portfolio.declaredskill.infrastructure.adapter.client.ExternalSkillClient;
import fr.avenirsesr.portfolio.declaredskill.infrastructure.fixture.DeclaredSkillProgressFixture;
import fr.avenirsesr.portfolio.program.infrastructure.fixture.*;
import fr.avenirsesr.portfolio.shared.domain.model.enums.EPortfolioType;
import fr.avenirsesr.portfolio.shared.domain.port.input.LoggedInUserService;
import fr.avenirsesr.portfolio.student.progress.declared.skill.domain.data.DeclaredSkillProgressDetails;
import fr.avenirsesr.portfolio.student.progress.declared.skill.domain.exception.DeclaredSkillProgressNotFoundException;
import fr.avenirsesr.portfolio.student.progress.declared.skill.domain.model.DeclaredSkillProgress;
import fr.avenirsesr.portfolio.student.progress.declared.skill.domain.port.output.repository.DeclaredSkillProgressRepository;
import fr.avenirsesr.portfolio.trace.domain.model.Trace;
import fr.avenirsesr.portfolio.trace.domain.port.input.TraceService;
import fr.avenirsesr.portfolio.trace.infrastructure.fixture.TraceFixture;
import fr.avenirsesr.portfolio.user.domain.model.Student;
import fr.avenirsesr.portfolio.user.infrastructure.fixture.StudentFixture;
import java.util.*;
import java.util.random.RandomGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
public class DeclaredSkillProgressServiceImplTest {
  @Mock private TraceService traceService;
  @Mock private DeclaredSkillSyncService declaredSkillSyncService;
  @Mock private DeclaredSkillProgressRepository declaredSkillProgressRepository;
  @Mock private ExternalSkillClient externalSkillClient;
  @Mock private LoggedInUserService loggedInUserService;
  @InjectMocks private DeclaredSkillProgressServiceImpl declaredSkillProgressService;
  private static final String CHARSET =
      "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
  private static final RandomGenerator random = RandomGenerator.getDefault();

  private Student student;

  @BeforeEach
  void setUp() {
    student = StudentFixture.create().toModel();

    when(loggedInUserService.getLoggedInStudent()).thenReturn(student);
  }

  @Nested
  class GivenAStudentProgressService {

    @BeforeEach
    void setupGiven() {
      BddLogger.given("a DeclaredSkillProgressService");
    }

    @Nested
    class WhenGettingDeclaredSkillsProgresses {

      @Test
      void getDeclaredSkillsProgresses_shouldDelegateToRepositoryAndReturnResult() {
        BddLogger.given("the method getDeclaredSkillsProgresses");
        PageCriteria criteria = new PageCriteria(1, 8);
        PagedResult<DeclaredSkillProgress> expected = mock(PagedResult.class);

        BddLogger.when("calling the method with a given student");
        when(declaredSkillProgressRepository.findAllByStudent(student, criteria))
            .thenReturn(expected);

        PagedResult<DeclaredSkillProgress> result =
            declaredSkillProgressService.getDeclaredSkillsProgresses(criteria);

        BddLogger.then(
            "it should return the expected paged declared skill progress and delegate to"
                + " repository");
        assertThat(result).isSameAs(expected);
        verify(declaredSkillProgressRepository).findAllByStudent(student, criteria);
      }

      @Test
      void getDeclaredSkillProgressDetails_shouldReturnDeclaredSkillsProgressDetails() {
        BddLogger.given("the method getDeclaredSkillProgressDetails");
        String programName = EPortfolioType.LIFE_PROJECT.name();
        DeclaredSkillProgress declaredSkillProgress =
            DeclaredSkillProgressFixture.create().withStudent(student).toModel();
        Trace trace1 =
            TraceFixture.create()
                .withUser(student.getUser())
                .withDeclaredSkillProgresses(List.of(declaredSkillProgress))
                .toModel();
        Trace trace2 =
            TraceFixture.create()
                .withUser(student.getUser())
                .withDeclaredSkillProgresses(List.of(declaredSkillProgress))
                .toModel();

        BddLogger.when("calling the method with a given student and declaredSkillProgressId");
        when(declaredSkillProgressRepository.findById(any(), any()))
            .thenReturn(Optional.of(declaredSkillProgress));
        when(traceService.getTracesLinkedWithDeclaredSkillProgress(
                student.getUser(), declaredSkillProgress))
            .thenReturn(List.of(trace1, trace2));
        when(traceService.programNameOfTrace(trace1)).thenReturn(programName);
        when(traceService.programNameOfTrace(trace2)).thenReturn(programName);

        List<ExternalSkillCategoryDTO> categories =
            List.of(
                new ExternalSkillCategoryDTO("Domain", EExternalSkillCategoryType.DOMAIN),
                new ExternalSkillCategoryDTO("Issue", EExternalSkillCategoryType.ISSUE));
        ExternalSkillDetailsDTO externalSkillDetails =
            new ExternalSkillDetailsDTO(
                declaredSkillProgress.getSkill().getExternalSkillId(),
                "Test Skill",
                categories,
                EExternalSkillType.ROME4);
        when(externalSkillClient.getExternalSkillDetails(any(UUID.class)))
            .thenReturn(Optional.of(externalSkillDetails));

        DeclaredSkillProgressDetails declaredSkillProgressDetails =
            declaredSkillProgressService.getDeclaredSkillProgressDetails(
                declaredSkillProgress.getId());

        BddLogger.then("it should return the expected declared skill progress details");
        assertEquals(declaredSkillProgressDetails.declaredSkillProgress(), declaredSkillProgress);
        assertEquals(2, declaredSkillProgressDetails.tracesWithProjectName().size());
        assertEquals(
            trace1, declaredSkillProgressDetails.tracesWithProjectName().getFirst().trace());
        assertEquals(
            programName,
            declaredSkillProgressDetails.tracesWithProjectName().getFirst().programName());
        assertEquals(
            trace2, declaredSkillProgressDetails.tracesWithProjectName().getLast().trace());
        assertEquals(
            programName,
            declaredSkillProgressDetails.tracesWithProjectName().getLast().programName());
      }

      @Test
      void getDeclaredSkillProgressDetails_shouldThrowDeclaredSkillProgressNotFoundException() {
        BddLogger.given("the method getDeclaredSkillProgressDetails");
        DeclaredSkillProgress declaredSkillProgress =
            DeclaredSkillProgressFixture.create().withStudent(student).toModel();

        BddLogger.when("calling the method with a given student and bad declaredSkillProgressId");
        assertThrows(
            DeclaredSkillProgressNotFoundException.class,
            () ->
                declaredSkillProgressService.getDeclaredSkillProgressDetails(
                    declaredSkillProgress.getId()));
      }

      @Test
      void getDeclaredSkillsProgressDetails_shouldThrowUserNotAuthorizedException() {
        BddLogger.given("the method getDeclaredSkillProgressDetails");
        Student anotherStudent = StudentFixture.create().toModel();
        DeclaredSkillProgress declaredSkillProgress =
            DeclaredSkillProgressFixture.create().withStudent(anotherStudent).toModel();

        BddLogger.when("calling the method with another given student and declaredSkillProgressId");
        when(declaredSkillProgressRepository.findById(any(), any()))
            .thenReturn(Optional.of(declaredSkillProgress));
        assertThrows(
            UserNotAuthorizedException.class,
            () ->
                declaredSkillProgressService.getDeclaredSkillProgressDetails(
                    declaredSkillProgress.getId()));
      }
    }

    @Nested
    class WhenCreatingDeclaredSkillProgress {
      @Test
      void createDeclaredSkillProgress_shouldSaveWhenSkillIsAvailableAndNotDuplicate() {
        BddLogger.given("the method createDeclaredSkillProgress");
        UUID skillId = randomUUID();
        EExternalSkillType type = EExternalSkillType.ROME4;
        EDeclaredSkillLevel level = EDeclaredSkillLevel.BEGINNER;
        String description = "Description for declared skill progress test";
        DeclaredSkill declaredSkill = mock(DeclaredSkill.class);

        BddLogger.when("calling the method with an available and not duplicate skill");
        when(declaredSkillSyncService.getOrCreateFromExternalSkill(skillId))
            .thenReturn(Optional.of(declaredSkill));
        when(declaredSkillProgressRepository.declaredSkillProgressAlreadyExists(any()))
            .thenReturn(false);

        declaredSkillProgressService.createDeclaredSkillProgress(skillId, type, level, description);

        BddLogger.then("it should save the declared skill progress");
        verify(declaredSkillSyncService).getOrCreateFromExternalSkill(skillId);
        verify(declaredSkillProgressRepository).declaredSkillProgressAlreadyExists(any());
        verify(declaredSkillProgressRepository).save(any(DeclaredSkillProgress.class));
      }

      @Test
      void createDeclaredSkillProgress_shouldThrowDuplicateWhenAlreadyExists() {
        BddLogger.given("the method createDeclaredSkillProgress");
        UUID skillId = randomUUID();
        EExternalSkillType type = EExternalSkillType.ROME4;
        EDeclaredSkillLevel level = EDeclaredSkillLevel.BEGINNER;
        String description = "Description for declared skill progress test";
        DeclaredSkill declaredSkill = mock(DeclaredSkill.class);

        BddLogger.when("calling the method with a duplicate skill");
        when(declaredSkillSyncService.getOrCreateFromExternalSkill(skillId))
            .thenReturn(Optional.of(declaredSkill));
        when(declaredSkillProgressRepository.declaredSkillProgressAlreadyExists(any()))
            .thenReturn(true);

        BddLogger.then(
            "it should throw a DuplicateDeclaredSkillException and not save the progress");

        assertThrows(
            DuplicateDeclaredSkillException.class,
            () ->
                declaredSkillProgressService.createDeclaredSkillProgress(
                    skillId, type, level, description));

        verify(declaredSkillSyncService).getOrCreateFromExternalSkill(skillId);
        verify(declaredSkillProgressRepository).declaredSkillProgressAlreadyExists(any());
        verify(declaredSkillProgressRepository, never()).save(any());
      }

      @Test
      void createDeclaredSkillProgress_shouldRethrowWhenSkillNotFound() {
        BddLogger.given("the method createDeclaredSkillProgress");
        UUID skillId = randomUUID();
        EExternalSkillType type = EExternalSkillType.ROME4;
        EDeclaredSkillLevel level = EDeclaredSkillLevel.BEGINNER;
        String description = "Description for declared skill progress test";

        BddLogger.when("calling the method with an unknown skill");
        when(declaredSkillSyncService.getOrCreateFromExternalSkill(skillId))
            .thenReturn(Optional.empty());

        BddLogger.then("it should throw an DeclaredSkillNotFoundException");

        assertThrows(
            DeclaredSkillNotFoundException.class,
            () ->
                declaredSkillProgressService.createDeclaredSkillProgress(
                    skillId, type, level, description));

        verify(declaredSkillSyncService).getOrCreateFromExternalSkill(skillId);
        verifyNoInteractions(declaredSkillProgressRepository);
      }
    }

    @Nested
    class WhenUpdatingDeclaredSkillProgress {
      @Test
      void updateDeclaredSkillProgress_shouldSaveLevelAndDescription() {
        BddLogger.given("the method updateDeclaredSkillProgress");
        EDeclaredSkillLevel level = EDeclaredSkillLevel.ADVANCED;
        String description = "Description for declared skill progress test";
        DeclaredSkillProgress declaredSkillProgress =
            DeclaredSkillProgressFixture.create()
                .withStudent(student)
                .withLevel(EDeclaredSkillLevel.BEGINNER)
                .withDescription(null)
                .toModel();

        BddLogger.when(
            "calling the method with a given student, declaredSkillProgressId, level and"
                + " description");
        when(declaredSkillProgressRepository.findById(declaredSkillProgress.getId()))
            .thenReturn(Optional.of(declaredSkillProgress));

        declaredSkillProgressService.updateDeclaredSkillProgress(
            declaredSkillProgress.getId(), level, description);

        BddLogger.then("it should save level and description in declared skill progress");
        ArgumentCaptor<DeclaredSkillProgress> captor =
            ArgumentCaptor.forClass(DeclaredSkillProgress.class);
        verify(declaredSkillProgressRepository).save(captor.capture());

        DeclaredSkillProgress savedDeclaredSkillProgress = captor.getValue();
        assertEquals(declaredSkillProgress.getId(), savedDeclaredSkillProgress.getId());
        assertEquals(declaredSkillProgress.getStudent(), savedDeclaredSkillProgress.getStudent());
        assertEquals(declaredSkillProgress.getSkill(), savedDeclaredSkillProgress.getSkill());
        assertEquals(level, savedDeclaredSkillProgress.getLevel());
        assertEquals(description, savedDeclaredSkillProgress.getDescription());
      }

      @Test
      void updateDeclaredSkillProgress_shouldThrowInvalidDescriptionException() {
        BddLogger.given("the method getDeclaredSkillProgressDetails");
        EDeclaredSkillLevel level = EDeclaredSkillLevel.BEGINNER;
        String description =
            random
                .ints(DESCRIPTION_LENGTH + 1, 0, CHARSET.length())
                .mapToObj(CHARSET::charAt)
                .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
                .toString();
        DeclaredSkillProgress declaredSkillProgress =
            DeclaredSkillProgressFixture.create().withStudent(student).toModel();

        BddLogger.when(
            "calling the method with a given student, declaredSkillProgressId, level and too long"
                + " description");
        assertThrows(
            InvalidDescriptionException.class,
            () ->
                declaredSkillProgressService.updateDeclaredSkillProgress(
                    declaredSkillProgress.getId(), level, description));
      }

      @Test
      void updateDeclaredSkillProgress_shouldThrowDeclaredSkillProgressNotFoundException() {
        BddLogger.given("the method getDeclaredSkillProgressDetails");
        EDeclaredSkillLevel level = EDeclaredSkillLevel.BEGINNER;
        String description = "Description for declared skill progress test";
        DeclaredSkillProgress declaredSkillProgress =
            DeclaredSkillProgressFixture.create().withStudent(student).toModel();

        BddLogger.when("calling the method with a given student and bad declaredSkillProgressId");
        assertThrows(
            DeclaredSkillProgressNotFoundException.class,
            () ->
                declaredSkillProgressService.updateDeclaredSkillProgress(
                    declaredSkillProgress.getId(), level, description));
      }

      @Test
      void updateDeclaredSkillProgress_shouldThrowUserNotAuthorizedException() {
        BddLogger.given("the method getDeclaredSkillProgressDetails");
        Student anotherStudent = StudentFixture.create().toModel();
        EDeclaredSkillLevel level = EDeclaredSkillLevel.BEGINNER;
        String description = "Description for declared skill progress test";
        DeclaredSkillProgress declaredSkillProgress =
            DeclaredSkillProgressFixture.create().withStudent(anotherStudent).toModel();

        BddLogger.when("calling the method with another given student and level, description");
        when(declaredSkillProgressRepository.findById(declaredSkillProgress.getId()))
            .thenReturn(Optional.of(declaredSkillProgress));
        assertThrows(
            UserNotAuthorizedException.class,
            () ->
                declaredSkillProgressService.updateDeclaredSkillProgress(
                    declaredSkillProgress.getId(), level, description));
      }
    }
  }
}
