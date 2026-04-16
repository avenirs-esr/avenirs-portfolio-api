package fr.avenirsesr.portfolio.student.progress.declared.skill.domain.service;

import static fr.avenirsesr.portfolio.common.validation.domain.constraints.FieldMaxLengths.RICH_TEXT_LENGTH;
import static java.util.UUID.randomUUID;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import fr.avenirsesr.portfolio.association.domain.model.EAssociationType;
import fr.avenirsesr.portfolio.association.domain.port.input.AssociationService;
import fr.avenirsesr.portfolio.association.domain.service.AssociationSearchHelper;
import fr.avenirsesr.portfolio.common.data.domain.model.PageCriteria;
import fr.avenirsesr.portfolio.common.data.domain.model.PagedResult;
import fr.avenirsesr.portfolio.common.data.domain.model.SortCriteria;
import fr.avenirsesr.portfolio.common.data.domain.model.enums.ESortField;
import fr.avenirsesr.portfolio.common.data.domain.model.enums.ESortOrder;
import fr.avenirsesr.portfolio.common.error.domain.exception.FieldValidationException;
import fr.avenirsesr.portfolio.common.externalskill.application.adapter.dto.ExternalSkillCategoryDTO;
import fr.avenirsesr.portfolio.common.externalskill.application.adapter.dto.ExternalSkillDetailsDTO;
import fr.avenirsesr.portfolio.common.externalskill.domain.model.enums.EExternalSkillCategoryType;
import fr.avenirsesr.portfolio.common.externalskill.domain.model.enums.EExternalSkillType;
import fr.avenirsesr.portfolio.common.security.domain.exception.UserNotAuthorizedException;
import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import fr.avenirsesr.portfolio.declaredskill.domain.exception.DeclaredSkillNotFoundException;
import fr.avenirsesr.portfolio.declaredskill.domain.exception.DuplicateDeclaredSkillException;
import fr.avenirsesr.portfolio.declaredskill.domain.model.DeclaredSkill;
import fr.avenirsesr.portfolio.declaredskill.domain.model.enums.EDeclaredSkillLevel;
import fr.avenirsesr.portfolio.declaredskill.domain.port.input.DeclaredSkillSyncService;
import fr.avenirsesr.portfolio.declaredskill.infrastructure.adapter.client.ExternalSkillClient;
import fr.avenirsesr.portfolio.declaredskill.infrastructure.fixture.DeclaredSkillProgressFixture;
import fr.avenirsesr.portfolio.program.infrastructure.fixture.*;
import fr.avenirsesr.portfolio.shared.domain.model.enums.EPortfolioType;
import fr.avenirsesr.portfolio.shared.domain.port.input.LoggedInUserService;
import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.exception.DeclaredActivityNotFoundException;
import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.model.DeclaredActivity;
import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.port.input.DeclaredActivityService;
import fr.avenirsesr.portfolio.student.progress.declared.skill.domain.data.DeclaredSkillAssociationsData;
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
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class DeclaredSkillProgressServiceImplTest {
  @Mock private TraceService traceService;
  @Mock private DeclaredSkillSyncService declaredSkillSyncService;
  @Mock private DeclaredSkillProgressRepository declaredSkillProgressRepository;
  @Mock private ExternalSkillClient externalSkillClient;
  @Mock private LoggedInUserService loggedInUserService;
  @Mock private DeclaredActivityService declaredActivityService;
  @Mock private AssociationService associationService;
  @Mock private AssociationSearchHelper associationSearchHelper;
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
        when(declaredSkillProgressRepository.findAllByStudent(
                student, criteria, new SortCriteria(ESortField.NAME, ESortOrder.ASC)))
            .thenReturn(expected);

        PagedResult<DeclaredSkillProgress> result =
            declaredSkillProgressService.getDeclaredSkillsProgresses(criteria);

        BddLogger.then(
            "it should return the expected paged declared skill progress and delegate to"
                + " repository");
        assertThat(result).isSameAs(expected);
        verify(declaredSkillProgressRepository)
            .findAllByStudent(student, criteria, new SortCriteria(ESortField.NAME, ESortOrder.ASC));
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
        when(traceService.getTracesLinkedWithDeclaredSkillProgress(declaredSkillProgress))
            .thenReturn(List.of(trace1, trace2));
        when(traceService.programNameOfTrace(trace1)).thenReturn(programName);
        when(traceService.programNameOfTrace(trace2)).thenReturn(programName);

        List<ExternalSkillCategoryDTO> categories =
            List.of(
                new ExternalSkillCategoryDTO("Domain", EExternalSkillCategoryType.DOMAIN),
                new ExternalSkillCategoryDTO("Issue", EExternalSkillCategoryType.ISSUE));
        ExternalSkillDetailsDTO externalSkillDetails =
            new ExternalSkillDetailsDTO(
                declaredSkillProgress.getSkill().getId(),
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
        String reflection = "Reflection for declared skill progress test";
        DeclaredSkill declaredSkill = mock(DeclaredSkill.class);

        BddLogger.when("calling the method with an available and not duplicate skill");
        when(declaredSkillSyncService.getOrCreateFromExternalSkill(skillId))
            .thenReturn(Optional.of(declaredSkill));
        when(declaredSkillProgressRepository.declaredSkillProgressAlreadyExists(any()))
            .thenReturn(false);

        declaredSkillProgressService.createDeclaredSkillProgress(skillId, type, level, reflection);

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
        String reflection = "Reflection for declared skill progress test";
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
                    skillId, type, level, reflection));

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
        String reflection = "Reflection for declared skill progress test";

        BddLogger.when("calling the method with an unknown skill");
        when(declaredSkillSyncService.getOrCreateFromExternalSkill(skillId))
            .thenReturn(Optional.empty());

        BddLogger.then("it should throw an DeclaredSkillNotFoundException");

        assertThrows(
            DeclaredSkillNotFoundException.class,
            () ->
                declaredSkillProgressService.createDeclaredSkillProgress(
                    skillId, type, level, reflection));

        verify(declaredSkillSyncService).getOrCreateFromExternalSkill(skillId);
        verifyNoInteractions(declaredSkillProgressRepository);
      }
    }

    @Nested
    class WhenUpdatingDeclaredSkillProgress {
      @Test
      void updateDeclaredSkillProgress_shouldSaveLevelAndReflection() {
        BddLogger.given("the method updateDeclaredSkillProgress");
        EDeclaredSkillLevel level = EDeclaredSkillLevel.ADVANCED;
        String reflection = "Reflection for declared skill progress test";
        DeclaredSkillProgress declaredSkillProgress =
            DeclaredSkillProgressFixture.create()
                .withStudent(student)
                .withLevel(EDeclaredSkillLevel.BEGINNER)
                .withReflection(null)
                .toModel();

        BddLogger.when(
            "calling the method with a given student, declaredSkillProgressId, level and"
                + " reflection");
        when(declaredSkillProgressRepository.findById(declaredSkillProgress.getId()))
            .thenReturn(Optional.of(declaredSkillProgress));

        declaredSkillProgressService.updateDeclaredSkillProgress(
            declaredSkillProgress.getId(), level, reflection);

        BddLogger.then("it should save level and reflection in declared skill progress");
        ArgumentCaptor<DeclaredSkillProgress> captor =
            ArgumentCaptor.forClass(DeclaredSkillProgress.class);
        verify(declaredSkillProgressRepository).save(captor.capture());

        DeclaredSkillProgress savedDeclaredSkillProgress = captor.getValue();
        assertEquals(declaredSkillProgress.getId(), savedDeclaredSkillProgress.getId());
        assertEquals(declaredSkillProgress.getStudent(), savedDeclaredSkillProgress.getStudent());
        assertEquals(declaredSkillProgress.getSkill(), savedDeclaredSkillProgress.getSkill());
        assertEquals(level, savedDeclaredSkillProgress.getLevel());
        assertEquals(reflection, savedDeclaredSkillProgress.getReflection());
      }

      @Test
      void updateDeclaredSkillProgress_shouldThrowFieldValidationException() {
        BddLogger.given("the method getDeclaredSkillProgressDetails");
        EDeclaredSkillLevel level = EDeclaredSkillLevel.BEGINNER;
        String reflection =
            random
                .ints(RICH_TEXT_LENGTH + 1, 0, CHARSET.length())
                .mapToObj(CHARSET::charAt)
                .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
                .toString();
        DeclaredSkillProgress declaredSkillProgress =
            DeclaredSkillProgressFixture.create().withStudent(student).toModel();

        BddLogger.when(
            "calling the method with a given student, declaredSkillProgressId, level and too long"
                + " reflection");
        assertThrows(
            FieldValidationException.class,
            () ->
                declaredSkillProgressService.updateDeclaredSkillProgress(
                    declaredSkillProgress.getId(), level, reflection));
      }

      @Test
      void updateDeclaredSkillProgress_shouldThrowDeclaredSkillProgressNotFoundException() {
        BddLogger.given("the method getDeclaredSkillProgressDetails");
        EDeclaredSkillLevel level = EDeclaredSkillLevel.BEGINNER;
        String reflection = "Reflection for declared skill progress test";
        DeclaredSkillProgress declaredSkillProgress =
            DeclaredSkillProgressFixture.create().withStudent(student).toModel();

        BddLogger.when("calling the method with a given student and bad declaredSkillProgressId");
        assertThrows(
            DeclaredSkillProgressNotFoundException.class,
            () ->
                declaredSkillProgressService.updateDeclaredSkillProgress(
                    declaredSkillProgress.getId(), level, reflection));
      }

      @Test
      void updateDeclaredSkillProgress_shouldThrowUserNotAuthorizedException() {
        BddLogger.given("the method getDeclaredSkillProgressDetails");
        Student anotherStudent = StudentFixture.create().toModel();
        EDeclaredSkillLevel level = EDeclaredSkillLevel.BEGINNER;
        String reflection = "Reflection for declared skill progress test";
        DeclaredSkillProgress declaredSkillProgress =
            DeclaredSkillProgressFixture.create().withStudent(anotherStudent).toModel();

        BddLogger.when("calling the method with another given student and level, reflection");
        when(declaredSkillProgressRepository.findById(declaredSkillProgress.getId()))
            .thenReturn(Optional.of(declaredSkillProgress));
        assertThrows(
            UserNotAuthorizedException.class,
            () ->
                declaredSkillProgressService.updateDeclaredSkillProgress(
                    declaredSkillProgress.getId(), level, reflection));
      }
    }

    @Nested
    class WhenDeletingDeclaredSkillsProgresses {

      @Test
      void deleteDeclaredSkillProgresses_shouldDeleteDeclaredSkillProgresses() {
        BddLogger.given("the method deleteDeclaredSkillProgresses");

        Student studentLoggedIn = student;
        DeclaredSkillProgress declaredSkillProgress1 =
            DeclaredSkillProgressFixture.create().withStudent(student).toModel();
        DeclaredSkillProgress declaredSkillProgress2 =
            DeclaredSkillProgressFixture.create().withStudent(student).toModel();

        BddLogger.when("calling the method with a given student");

        when(loggedInUserService.getLoggedInStudent()).thenReturn(studentLoggedIn);
        when(declaredSkillProgressRepository.findAllById(
                List.of(declaredSkillProgress1.getId(), declaredSkillProgress2.getId())))
            .thenReturn(List.of(declaredSkillProgress1, declaredSkillProgress2));
        doNothing().when(traceService).unassociateTraces(declaredSkillProgress1);
        doNothing().when(traceService).unassociateTraces(declaredSkillProgress2);

        declaredSkillProgressService.deleteDeclaredSkillProgresses(
            List.of(declaredSkillProgress1.getId(), declaredSkillProgress2.getId()));

        BddLogger.then("it should delete declared skill progresses and delete traces");

        verify(declaredSkillProgressRepository)
            .findAllById(List.of(declaredSkillProgress1.getId(), declaredSkillProgress2.getId()));
        verify(traceService).unassociateTraces(declaredSkillProgress1);
        verify(traceService).unassociateTraces(declaredSkillProgress2);
        verify(declaredSkillProgressRepository)
            .removeAllFromDatabase(List.of(declaredSkillProgress1, declaredSkillProgress2));
      }

      @Test
      void deleteDeclaredSkillProgresses_shouldThrowDeclaredSkillProgressNotFoundException() {
        BddLogger.given("the method deleteDeclaredSkillProgresses");

        Student studentLoggedIn = student;
        UUID randomUUID = UUID.randomUUID();
        DeclaredSkillProgress declaredSkillProgress1 =
            DeclaredSkillProgressFixture.create().withStudent(student).toModel();

        BddLogger.when("calling the method with non existing declared skill progress ids");

        when(loggedInUserService.getLoggedInStudent()).thenReturn(studentLoggedIn);
        when(declaredSkillProgressRepository.findAllById(
                List.of(declaredSkillProgress1.getId(), randomUUID)))
            .thenReturn(List.of(declaredSkillProgress1));

        BddLogger.then("it should throw declared skill not found exception");

        assertThrows(
            DeclaredSkillProgressNotFoundException.class,
            () ->
                declaredSkillProgressService.deleteDeclaredSkillProgresses(
                    List.of(declaredSkillProgress1.getId(), randomUUID)));
      }

      @Test
      void deleteDeclaredSkillProgresses_shouldThrowUserNotAuthorizedException() {
        BddLogger.given("the method deleteDeclaredSkillProgresses");

        Student studentLoggedIn = student;
        Student anotherStudent = StudentFixture.create().toModel();
        DeclaredSkillProgress declaredSkillProgress1 =
            DeclaredSkillProgressFixture.create().withStudent(anotherStudent).toModel();
        DeclaredSkillProgress declaredSkillProgress2 =
            DeclaredSkillProgressFixture.create().withStudent(anotherStudent).toModel();

        BddLogger.when("calling the method with an unauthorized student");

        when(loggedInUserService.getLoggedInStudent()).thenReturn(studentLoggedIn);
        when(declaredSkillProgressRepository.findAllById(
                List.of(declaredSkillProgress1.getId(), declaredSkillProgress2.getId())))
            .thenReturn(List.of(declaredSkillProgress1, declaredSkillProgress2));

        BddLogger.then("it should throw user not authorized exception");

        assertThrows(
            UserNotAuthorizedException.class,
            () ->
                declaredSkillProgressService.deleteDeclaredSkillProgresses(
                    List.of(declaredSkillProgress1.getId(), declaredSkillProgress2.getId())));
      }
    }

    @Nested
    class WhenAssociatingDeclaredSkillWithActivities {

      @Test
      void associateDeclaredSkillWithActivities_shouldCreateAssociationsSuccessfully() {
        BddLogger.given("the method associateDeclaredSkillWithActivities");

        DeclaredSkillProgress declaredSkillProgress =
            DeclaredSkillProgressFixture.create().withStudent(student).toModel();

        DeclaredActivity declaredActivity1 = mock(DeclaredActivity.class);
        DeclaredActivity declaredActivity2 = mock(DeclaredActivity.class);
        UUID activityId1 = randomUUID();
        UUID activityId2 = randomUUID();

        BddLogger.when("calling the method with valid skill and activity ids");

        when(declaredSkillProgressRepository.findById(declaredSkillProgress.getId()))
            .thenReturn(Optional.of(declaredSkillProgress));
        when(declaredActivity1.getId()).thenReturn(activityId1);
        when(declaredActivity1.getStudent()).thenReturn(student);
        when(declaredActivity2.getId()).thenReturn(activityId2);
        when(declaredActivity2.getStudent()).thenReturn(student);
        when(declaredActivityService.findAllDeclaredActivitiesByIds(
                List.of(activityId1, activityId2)))
            .thenReturn(List.of(declaredActivity1, declaredActivity2));

        // Mock pour getAssociationsOf
        when(associationService.getAllOf(any(), any(), any())).thenReturn(List.of());
        when(traceService.findAllTracesById(any())).thenReturn(List.of());
        when(declaredActivityService.findAllDeclaredActivitiesByIds(List.of()))
            .thenReturn(List.of());

        DeclaredSkillAssociationsData result =
            declaredSkillProgressService.associateDeclaredSkillWithActivities(
                declaredSkillProgress.getId(), List.of(activityId1, activityId2));

        BddLogger.then("it should create associations and return association data");

        verify(associationService)
            .createAll(
                argThat(
                    list ->
                        list.size() == 2
                            && list.stream()
                                .allMatch(
                                    assocData ->
                                        assocData.associationType()
                                                == EAssociationType.DECLARED_ACTIVITY_DECLARED_SKILL
                                            && assocData.id2().equals(declaredSkillProgress.getId())
                                            && (assocData.id1().equals(activityId1)
                                                || assocData.id1().equals(activityId2)))));

        assertNotNull(result);
      }

      @Test
      void
          associateDeclaredSkillWithActivities_shouldThrowDeclaredSkillProgressNotFoundException() {
        BddLogger.given("the method associateDeclaredSkillWithActivities");

        UUID declaredSkillId = randomUUID();
        UUID activityId = randomUUID();

        BddLogger.when("calling the method with non-existing skill id");

        when(declaredSkillProgressRepository.findById(declaredSkillId))
            .thenReturn(Optional.empty());

        BddLogger.then("it should throw DeclaredSkillProgressNotFoundException");

        assertThrows(
            DeclaredSkillProgressNotFoundException.class,
            () ->
                declaredSkillProgressService.associateDeclaredSkillWithActivities(
                    declaredSkillId, List.of(activityId)));

        verify(declaredSkillProgressRepository).findById(declaredSkillId);
        verifyNoInteractions(declaredActivityService);
        verifyNoInteractions(associationService);
      }

      @Test
      void associateDeclaredSkillWithActivities_shouldThrowUserNotAuthorizedForSkill() {
        BddLogger.given("the method associateDeclaredSkillWithActivities");

        Student anotherStudent = StudentFixture.create().toModel();
        DeclaredSkillProgress declaredSkillProgress =
            DeclaredSkillProgressFixture.create().withStudent(anotherStudent).toModel();
        UUID activityId = randomUUID();

        BddLogger.when("calling the method with skill belonging to another student");

        when(declaredSkillProgressRepository.findById(declaredSkillProgress.getId()))
            .thenReturn(Optional.of(declaredSkillProgress));

        BddLogger.then("it should throw UserNotAuthorizedException");

        assertThrows(
            UserNotAuthorizedException.class,
            () ->
                declaredSkillProgressService.associateDeclaredSkillWithActivities(
                    declaredSkillProgress.getId(), List.of(activityId)));

        verify(declaredSkillProgressRepository).findById(declaredSkillProgress.getId());
        verifyNoInteractions(declaredActivityService);
        verifyNoInteractions(associationService);
      }

      @Test
      void associateDeclaredSkillWithActivities_shouldThrowDeclaredActivityNotFoundException() {
        BddLogger.given("the method associateDeclaredSkillWithActivities");

        DeclaredSkillProgress declaredSkillProgress =
            DeclaredSkillProgressFixture.create().withStudent(student).toModel();
        UUID activityId1 = randomUUID();
        UUID activityId2 = randomUUID();
        DeclaredActivity declaredActivity1 = mock(DeclaredActivity.class);

        BddLogger.when("calling the method with some non-existing activity ids");

        when(declaredSkillProgressRepository.findById(declaredSkillProgress.getId()))
            .thenReturn(Optional.of(declaredSkillProgress));
        when(declaredActivity1.getId()).thenReturn(activityId1);
        when(declaredActivityService.findAllDeclaredActivitiesByIds(
                List.of(activityId1, activityId2)))
            .thenReturn(List.of(declaredActivity1)); // Only returns one activity, not both

        BddLogger.then("it should throw DeclaredActivityNotFoundException");

        assertThrows(
            DeclaredActivityNotFoundException.class,
            () ->
                declaredSkillProgressService.associateDeclaredSkillWithActivities(
                    declaredSkillProgress.getId(), List.of(activityId1, activityId2)));

        verify(declaredActivityService)
            .findAllDeclaredActivitiesByIds(List.of(activityId1, activityId2));
        verifyNoInteractions(associationService);
      }

      @Test
      void associateDeclaredSkillWithActivities_shouldThrowUserNotAuthorizedForActivities() {
        BddLogger.given("the method associateDeclaredSkillWithActivities");

        Student anotherStudent = StudentFixture.create().toModel();
        DeclaredSkillProgress declaredSkillProgress =
            DeclaredSkillProgressFixture.create().withStudent(student).toModel();

        DeclaredActivity declaredActivity1 = mock(DeclaredActivity.class);
        DeclaredActivity declaredActivity2 = mock(DeclaredActivity.class);
        UUID activityId1 = randomUUID();
        UUID activityId2 = randomUUID();

        BddLogger.when("calling the method with activities belonging to another student");

        when(declaredSkillProgressRepository.findById(declaredSkillProgress.getId()))
            .thenReturn(Optional.of(declaredSkillProgress));
        when(declaredActivity1.getId()).thenReturn(activityId1);
        when(declaredActivity1.getStudent()).thenReturn(student);
        when(declaredActivity2.getId()).thenReturn(activityId2);
        when(declaredActivity2.getStudent()).thenReturn(anotherStudent); // Different student
        when(declaredActivityService.findAllDeclaredActivitiesByIds(
                List.of(activityId1, activityId2)))
            .thenReturn(List.of(declaredActivity1, declaredActivity2));

        BddLogger.then("it should throw UserNotAuthorizedException");

        assertThrows(
            UserNotAuthorizedException.class,
            () ->
                declaredSkillProgressService.associateDeclaredSkillWithActivities(
                    declaredSkillProgress.getId(), List.of(activityId1, activityId2)));

        verify(declaredActivityService)
            .findAllDeclaredActivitiesByIds(List.of(activityId1, activityId2));
        verifyNoInteractions(associationService);
      }

      @Test
      void associateDeclaredSkillWithActivities_shouldHandleEmptyActivityList() {
        BddLogger.given("the method associateDeclaredSkillWithActivities");

        DeclaredSkillProgress declaredSkillProgress =
            DeclaredSkillProgressFixture.create().withStudent(student).toModel();

        BddLogger.when("calling the method with empty activity list");

        when(declaredSkillProgressRepository.findById(declaredSkillProgress.getId()))
            .thenReturn(Optional.of(declaredSkillProgress));
        when(declaredActivityService.findAllDeclaredActivitiesByIds(List.of()))
            .thenReturn(List.of());

        // Mock pour getAssociationsOf
        when(associationService.getAllOf(any(), any(), any())).thenReturn(List.of());
        when(traceService.findAllTracesById(any())).thenReturn(List.of());

        DeclaredSkillAssociationsData result =
            declaredSkillProgressService.associateDeclaredSkillWithActivities(
                declaredSkillProgress.getId(), List.of());

        BddLogger.then("it should create no associations and return empty association data");

        verify(associationService).createAll(argThat(List::isEmpty));
        assertNotNull(result);
      }
    }

    @Nested
    class WhenGettingAssociations {

      @Mock private DeclaredActivityService declaredActivityService;
      @Mock private AssociationService associationService;
      @Mock private AssociationSearchHelper associationSearchHelper;

      @BeforeEach
      void setUp() {
        declaredSkillProgressService =
            new DeclaredSkillProgressServiceImpl(
                traceService,
                declaredSkillSyncService,
                declaredSkillProgressRepository,
                externalSkillClient,
                loggedInUserService,
                declaredActivityService,
                associationService,
                associationSearchHelper);
      }

      @Test
      void
          getAssociationsOf_should_throw_DeclaredSkillProgressNotFoundException_when_skill_not_found() {
        BddLogger.given("A non-existent declared skill id");

        UUID declaredSkillId = randomUUID();

        BddLogger.when("getAssociationsOf is called with non-existing skill id");

        when(declaredSkillProgressRepository.findById(declaredSkillId))
            .thenReturn(Optional.empty());

        BddLogger.then("it should throw DeclaredSkillProgressNotFoundException");

        assertThrows(
            DeclaredSkillProgressNotFoundException.class,
            () -> declaredSkillProgressService.getAssociationsOf(declaredSkillId));

        verify(declaredSkillProgressRepository).findById(declaredSkillId);
        verifyNoInteractions(associationService);
      }

      @Test
      void
          getAssociationsOf_should_throw_UserNotAuthorizedException_when_skill_belongs_to_another_student() {
        BddLogger.given("A declared skill belonging to another student");

        Student anotherStudent = StudentFixture.create().toModel();
        UUID declaredSkillId = randomUUID();
        DeclaredSkillProgress declaredSkillProgress =
            DeclaredSkillProgressFixture.create().withStudent(anotherStudent).toModel();

        BddLogger.when("getAssociationsOf is called");

        when(declaredSkillProgressRepository.findById(declaredSkillId))
            .thenReturn(Optional.of(declaredSkillProgress));

        BddLogger.then("it should throw UserNotAuthorizedException");

        assertThrows(
            UserNotAuthorizedException.class,
            () -> declaredSkillProgressService.getAssociationsOf(declaredSkillId));

        verify(declaredSkillProgressRepository).findById(declaredSkillId);
        verifyNoInteractions(associationService);
      }
    }
  }
}
