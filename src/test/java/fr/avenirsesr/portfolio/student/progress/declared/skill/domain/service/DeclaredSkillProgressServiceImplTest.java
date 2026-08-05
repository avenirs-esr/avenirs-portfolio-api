package fr.avenirsesr.portfolio.student.progress.declared.skill.domain.service;

import static fr.avenirsesr.portfolio.common.validation.domain.constraints.FieldMaxLengths.RICH_DESCRIPTION_LENGTH;
import static java.util.UUID.randomUUID;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import fr.avenirsesr.portfolio.student.association.domain.data.AssociationSearchResultData;
import fr.avenirsesr.portfolio.student.association.domain.exception.AssociationAlreadyExistException;
import fr.avenirsesr.portfolio.student.association.domain.model.Association;
import fr.avenirsesr.portfolio.student.association.domain.model.EAssociationContextType;
import fr.avenirsesr.portfolio.student.association.domain.model.EAssociationType;
import fr.avenirsesr.portfolio.student.association.domain.port.input.AssociationService;
import fr.avenirsesr.portfolio.student.association.domain.service.AssociationSearchHelper;
import fr.avenirsesr.portfolio.common.data.domain.model.PageCriteria;
import fr.avenirsesr.portfolio.common.data.domain.model.PageInfo;
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
import fr.avenirsesr.portfolio.shared.domain.port.input.LoggedInUserService;
import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.exception.DeclaredActivityNotFoundException;
import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.model.DeclaredActivity;
import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.port.input.DeclaredActivityService;
import fr.avenirsesr.portfolio.student.progress.declared.experience.domain.exception.DeclaredExperienceNotFoundException;
import fr.avenirsesr.portfolio.student.progress.declared.experience.domain.model.DeclaredExperience;
import fr.avenirsesr.portfolio.student.progress.declared.experience.domain.model.enums.EExperienceType;
import fr.avenirsesr.portfolio.student.progress.declared.experience.domain.port.input.DeclaredExperienceService;
import fr.avenirsesr.portfolio.student.progress.declared.skill.domain.data.DeclaredSkillAssociationCount;
import fr.avenirsesr.portfolio.student.progress.declared.skill.domain.data.DeclaredSkillAssociationsData;
import fr.avenirsesr.portfolio.student.progress.declared.skill.domain.data.DeclaredSkillProgressData;
import fr.avenirsesr.portfolio.student.progress.declared.skill.domain.data.DeclaredSkillProgressDetails;
import fr.avenirsesr.portfolio.student.progress.declared.skill.domain.exception.DeclaredSkillProgressNotFoundException;
import fr.avenirsesr.portfolio.student.progress.declared.skill.domain.model.DeclaredSkillProgress;
import fr.avenirsesr.portfolio.student.progress.declared.skill.domain.port.output.repository.DeclaredSkillProgressRepository;
import fr.avenirsesr.portfolio.trace.domain.model.Trace;
import fr.avenirsesr.portfolio.trace.domain.port.input.TraceService;
import fr.avenirsesr.portfolio.user.domain.model.Student;
import fr.avenirsesr.portfolio.user.infrastructure.fixture.StudentFixture;
import java.time.LocalDate;
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
  @Mock private DeclaredExperienceService declaredExperienceService;
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

  private DeclaredSkillProgress buildDeclaredSkillProgress() {
    DeclaredSkill skill =
        DeclaredSkill.create(
            randomUUID(), "Java Programming", EExternalSkillType.ROME4, List.of("Technology"));
    return DeclaredSkillProgress.create(student, skill, EDeclaredSkillLevel.BEGINNER, null);
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
        DeclaredSkillProgress declaredSkillProgress = buildDeclaredSkillProgress();
        PagedResult<DeclaredSkillProgress> repositoryResult =
            new PagedResult<>(List.of(declaredSkillProgress), new PageInfo(1, 8, 1));

        BddLogger.when("calling the method with a given student and no isValorized filter");
        when(declaredSkillProgressRepository.findAllByStudent(
                student,
                criteria,
                (Boolean) null,
                new SortCriteria(ESortField.NAME, ESortOrder.ASC)))
            .thenReturn(repositoryResult);

        PagedResult<DeclaredSkillProgressData> result =
            declaredSkillProgressService.getDeclaredSkillsProgresses(criteria, null);

        BddLogger.then(
            "it should return the expected paged declared skill progress and delegate to"
                + " repository");
        assertThat(result.pageInfo()).isSameAs(repositoryResult.pageInfo());
        assertThat(result.content())
            .containsExactly(
                new DeclaredSkillProgressData(
                    declaredSkillProgress, new DeclaredSkillAssociationCount(0, 0)));
        verify(declaredSkillProgressRepository)
            .findAllByStudent(
                student,
                criteria,
                (Boolean) null,
                new SortCriteria(ESortField.NAME, ESortOrder.ASC));
      }

      @Test
      void getDeclaredSkillsProgresses_shouldDelegateIsValorizedFilterToRepository() {
        BddLogger.given("the method getDeclaredSkillsProgresses");
        PageCriteria criteria = new PageCriteria(1, 8);
        DeclaredSkillProgress declaredSkillProgress = buildDeclaredSkillProgress();
        PagedResult<DeclaredSkillProgress> repositoryResult =
            new PagedResult<>(List.of(declaredSkillProgress), new PageInfo(1, 8, 1));

        BddLogger.when("calling the method with a given student and isValorized=true");
        when(declaredSkillProgressRepository.findAllByStudent(
                student, criteria, true, new SortCriteria(ESortField.NAME, ESortOrder.ASC)))
            .thenReturn(repositoryResult);

        PagedResult<DeclaredSkillProgressData> result =
            declaredSkillProgressService.getDeclaredSkillsProgresses(criteria, true);

        BddLogger.then("it should delegate the isValorized filter to the repository");
        assertThat(result.pageInfo()).isSameAs(repositoryResult.pageInfo());
        assertThat(result.content())
            .containsExactly(
                new DeclaredSkillProgressData(
                    declaredSkillProgress, new DeclaredSkillAssociationCount(0, 0)));
        verify(declaredSkillProgressRepository)
            .findAllByStudent(
                student, criteria, true, new SortCriteria(ESortField.NAME, ESortOrder.ASC));
      }

      @Test
      void getDeclaredSkillProgressDetails_shouldReturnDeclaredSkillsProgressDetails() {
        BddLogger.given("the method getDeclaredSkillProgressDetails");
        DeclaredSkillProgress declaredSkillProgress =
            DeclaredSkillProgressFixture.create().withStudent(student).toModel();

        BddLogger.when("calling the method with a given student and declaredSkillProgressId");
        when(declaredSkillProgressRepository.findById(any(), any()))
            .thenReturn(Optional.of(declaredSkillProgress));

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
        boolean valorized = true;
        DeclaredSkillProgress declaredSkillProgress =
            DeclaredSkillProgressFixture.create()
                .withStudent(student)
                .withLevel(EDeclaredSkillLevel.BEGINNER)
                .withReflection(null)
                .withValorized(false)
                .toModel();

        BddLogger.when(
            "calling the method with a given student, declaredSkillProgressId, level, reflection"
                + " and valorized");
        when(declaredSkillProgressRepository.findById(declaredSkillProgress.getId()))
            .thenReturn(Optional.of(declaredSkillProgress));

        declaredSkillProgressService.updateDeclaredSkillProgress(
            declaredSkillProgress.getId(), level, reflection, valorized);

        BddLogger.then("it should save level, reflection and valorized in declared skill progress");
        ArgumentCaptor<DeclaredSkillProgress> captor =
            ArgumentCaptor.forClass(DeclaredSkillProgress.class);
        verify(declaredSkillProgressRepository).save(captor.capture());

        DeclaredSkillProgress savedDeclaredSkillProgress = captor.getValue();
        assertEquals(declaredSkillProgress.getId(), savedDeclaredSkillProgress.getId());
        assertEquals(declaredSkillProgress.getStudent(), savedDeclaredSkillProgress.getStudent());
        assertEquals(declaredSkillProgress.getSkill(), savedDeclaredSkillProgress.getSkill());
        assertEquals(level, savedDeclaredSkillProgress.getLevel());
        assertEquals(reflection, savedDeclaredSkillProgress.getReflection());
        assertEquals(valorized, savedDeclaredSkillProgress.isValorized());
      }

      @Test
      void updateDeclaredSkillProgress_shouldThrowFieldValidationException() {
        BddLogger.given("the method getDeclaredSkillProgressDetails");
        EDeclaredSkillLevel level = EDeclaredSkillLevel.BEGINNER;
        String reflection =
            random
                .ints(RICH_DESCRIPTION_LENGTH + 1, 0, CHARSET.length())
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
                    declaredSkillProgress.getId(), level, reflection, false));
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
                    declaredSkillProgress.getId(), level, reflection, false));
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
                    declaredSkillProgress.getId(), level, reflection, false));
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
        doNothing()
            .when(associationService)
            .deleteAllOf(
                List.of(declaredSkillProgress1.getId(), declaredSkillProgress2.getId()),
                DeclaredSkillProgress.class);

        declaredSkillProgressService.deleteDeclaredSkillProgresses(
            List.of(declaredSkillProgress1.getId(), declaredSkillProgress2.getId()));

        BddLogger.then("it should delete declared skill progresses and delete traces");

        verify(declaredSkillProgressRepository)
            .findAllById(List.of(declaredSkillProgress1.getId(), declaredSkillProgress2.getId()));
        verify(declaredSkillProgressRepository)
            .removeAllFromDatabase(List.of(declaredSkillProgress1, declaredSkillProgress2));
        verify(associationService)
            .deleteAllOf(
                List.of(declaredSkillProgress1.getId(), declaredSkillProgress2.getId()),
                DeclaredSkillProgress.class);
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

        when(associationService.getAllOf(
                eq(declaredSkillProgress.getId()),
                eq(DeclaredSkillProgress.class),
                eq(
                    List.of(
                        EAssociationType.TRACE_DECLARED_SKILL,
                        EAssociationType.DECLARED_ACTIVITY_DECLARED_SKILL,
                        EAssociationType.DECLARED_EXPERIENCE_DECLARED_SKILL))))
            .thenReturn(List.of());
        when(traceService.findAllTracesById(List.of())).thenReturn(List.of());
        when(declaredActivityService.findAllDeclaredActivitiesByIds(List.of()))
            .thenReturn(List.of());
        when(declaredExperienceService.findAllByIds(List.of())).thenReturn(List.of());

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

        when(associationService.getAllOf(
                eq(declaredSkillProgress.getId()),
                eq(DeclaredSkillProgress.class),
                eq(
                    List.of(
                        EAssociationType.TRACE_DECLARED_SKILL,
                        EAssociationType.DECLARED_ACTIVITY_DECLARED_SKILL,
                        EAssociationType.DECLARED_EXPERIENCE_DECLARED_SKILL))))
            .thenReturn(List.of());
        when(traceService.findAllTracesById(List.of())).thenReturn(List.of());
        when(declaredActivityService.findAllDeclaredActivitiesByIds(List.of()))
            .thenReturn(List.of());
        when(declaredExperienceService.findAllByIds(List.of())).thenReturn(List.of());

        DeclaredSkillAssociationsData result =
            declaredSkillProgressService.associateDeclaredSkillWithActivities(
                declaredSkillProgress.getId(), List.of());

        BddLogger.then("it should create no associations and return empty association data");

        verify(associationService).createAll(argThat(List::isEmpty));
        assertNotNull(result);
      }
    }

    @Nested
    class WhenAssociatingDeclaredSkillWithDeclaredExperiences {

      @Test
      void associateDeclaredSkillWithDeclaredExperiences_shouldCreateAssociationsSuccessfully() {
        BddLogger.given("the method associateDeclaredSkillWithDeclaredExperiences");

        DeclaredSkillProgress declaredSkillProgress =
            DeclaredSkillProgressFixture.create().withStudent(student).toModel();

        DeclaredExperience declaredExperience1 = mock(DeclaredExperience.class);
        DeclaredExperience declaredExperience2 = mock(DeclaredExperience.class);
        UUID experienceId1 = randomUUID();
        UUID experienceId2 = randomUUID();

        BddLogger.when("calling the method with valid skill and experience ids");

        when(declaredSkillProgressRepository.findById(declaredSkillProgress.getId()))
            .thenReturn(Optional.of(declaredSkillProgress));
        when(declaredExperience1.getId()).thenReturn(experienceId1);
        when(declaredExperience1.getStudent()).thenReturn(student);
        when(declaredExperience2.getId()).thenReturn(experienceId2);
        when(declaredExperience2.getStudent()).thenReturn(student);
        when(declaredExperienceService.findAllByIds(List.of(experienceId1, experienceId2)))
            .thenReturn(List.of(declaredExperience1, declaredExperience2));

        when(associationService.getAllOf(
                eq(declaredSkillProgress.getId()),
                eq(DeclaredSkillProgress.class),
                eq(
                    List.of(
                        EAssociationType.TRACE_DECLARED_SKILL,
                        EAssociationType.DECLARED_ACTIVITY_DECLARED_SKILL,
                        EAssociationType.DECLARED_EXPERIENCE_DECLARED_SKILL))))
            .thenReturn(List.of());
        when(traceService.findAllTracesById(List.of())).thenReturn(List.of());
        when(declaredActivityService.findAllDeclaredActivitiesByIds(List.of()))
            .thenReturn(List.of());
        when(declaredExperienceService.findAllByIds(List.of())).thenReturn(List.of());

        DeclaredSkillAssociationsData result =
            declaredSkillProgressService.associateDeclaredSkillWithDeclaredExperiences(
                declaredSkillProgress.getId(), List.of(experienceId1, experienceId2));

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
                                                == EAssociationType
                                                    .DECLARED_EXPERIENCE_DECLARED_SKILL
                                            && assocData.id2().equals(declaredSkillProgress.getId())
                                            && (assocData.id1().equals(experienceId1)
                                                || assocData.id1().equals(experienceId2)))));

        assertNotNull(result);
      }

      @Test
      void
          associateDeclaredSkillWithDeclaredExperiences_shouldThrowDeclaredSkillProgressNotFoundException() {
        BddLogger.given("the method associateDeclaredSkillWithDeclaredExperiences");

        UUID declaredSkillId = randomUUID();
        UUID experienceId = randomUUID();

        BddLogger.when("calling the method with non-existing skill id");

        when(declaredSkillProgressRepository.findById(declaredSkillId))
            .thenReturn(Optional.empty());

        BddLogger.then("it should throw DeclaredSkillProgressNotFoundException");

        assertThrows(
            DeclaredSkillProgressNotFoundException.class,
            () ->
                declaredSkillProgressService.associateDeclaredSkillWithDeclaredExperiences(
                    declaredSkillId, List.of(experienceId)));

        verify(declaredSkillProgressRepository).findById(declaredSkillId);
        verifyNoInteractions(declaredExperienceService);
        verifyNoInteractions(associationService);
      }

      @Test
      void associateDeclaredSkillWithDeclaredExperiences_shouldThrowUserNotAuthorizedForSkill() {
        BddLogger.given("the method associateDeclaredSkillWithDeclaredExperiences");

        Student anotherStudent = StudentFixture.create().toModel();
        DeclaredSkillProgress declaredSkillProgress =
            DeclaredSkillProgressFixture.create().withStudent(anotherStudent).toModel();
        UUID experienceId = randomUUID();

        BddLogger.when("calling the method with skill belonging to another student");

        when(declaredSkillProgressRepository.findById(declaredSkillProgress.getId()))
            .thenReturn(Optional.of(declaredSkillProgress));

        BddLogger.then("it should throw UserNotAuthorizedException");

        assertThrows(
            UserNotAuthorizedException.class,
            () ->
                declaredSkillProgressService.associateDeclaredSkillWithDeclaredExperiences(
                    declaredSkillProgress.getId(), List.of(experienceId)));

        verify(declaredSkillProgressRepository).findById(declaredSkillProgress.getId());
        verifyNoInteractions(declaredExperienceService);
        verifyNoInteractions(associationService);
      }

      @Test
      void
          associateDeclaredSkillWithDeclaredExperiences_shouldThrowDeclaredExperienceNotFoundException() {
        BddLogger.given("the method associateDeclaredSkillWithDeclaredExperiences");

        DeclaredSkillProgress declaredSkillProgress =
            DeclaredSkillProgressFixture.create().withStudent(student).toModel();
        UUID experienceId1 = randomUUID();
        UUID experienceId2 = randomUUID();
        DeclaredExperience declaredExperience1 = mock(DeclaredExperience.class);

        BddLogger.when("calling the method with some non-existing experience ids");

        when(declaredSkillProgressRepository.findById(declaredSkillProgress.getId()))
            .thenReturn(Optional.of(declaredSkillProgress));
        when(declaredExperience1.getId()).thenReturn(experienceId1);
        when(declaredExperienceService.findAllByIds(List.of(experienceId1, experienceId2)))
            .thenReturn(List.of(declaredExperience1)); // Only returns one experience, not both

        BddLogger.then("it should throw DeclaredExperienceNotFoundException");

        assertThrows(
            DeclaredExperienceNotFoundException.class,
            () ->
                declaredSkillProgressService.associateDeclaredSkillWithDeclaredExperiences(
                    declaredSkillProgress.getId(), List.of(experienceId1, experienceId2)));

        verify(declaredExperienceService).findAllByIds(List.of(experienceId1, experienceId2));
        verifyNoInteractions(associationService);
      }

      @Test
      void
          associateDeclaredSkillWithDeclaredExperiences_shouldThrowUserNotAuthorizedForExperiences() {
        BddLogger.given("the method associateDeclaredSkillWithDeclaredExperiences");

        Student anotherStudent = StudentFixture.create().toModel();
        DeclaredSkillProgress declaredSkillProgress =
            DeclaredSkillProgressFixture.create().withStudent(student).toModel();

        DeclaredExperience declaredExperience1 = mock(DeclaredExperience.class);
        DeclaredExperience declaredExperience2 = mock(DeclaredExperience.class);
        UUID experienceId1 = randomUUID();
        UUID experienceId2 = randomUUID();

        BddLogger.when("calling the method with an experience belonging to another student");

        when(declaredSkillProgressRepository.findById(declaredSkillProgress.getId()))
            .thenReturn(Optional.of(declaredSkillProgress));
        when(declaredExperience1.getId()).thenReturn(experienceId1);
        when(declaredExperience1.getStudent()).thenReturn(student);
        when(declaredExperience2.getId()).thenReturn(experienceId2);
        when(declaredExperience2.getStudent()).thenReturn(anotherStudent); // Different student
        when(declaredExperienceService.findAllByIds(List.of(experienceId1, experienceId2)))
            .thenReturn(List.of(declaredExperience1, declaredExperience2));

        BddLogger.then("it should throw UserNotAuthorizedException");

        assertThrows(
            UserNotAuthorizedException.class,
            () ->
                declaredSkillProgressService.associateDeclaredSkillWithDeclaredExperiences(
                    declaredSkillProgress.getId(), List.of(experienceId1, experienceId2)));

        verify(declaredExperienceService).findAllByIds(List.of(experienceId1, experienceId2));
        verifyNoInteractions(associationService);
      }

      @Test
      void associateDeclaredSkillWithDeclaredExperiences_shouldHandleEmptyExperienceList() {
        BddLogger.given("the method associateDeclaredSkillWithDeclaredExperiences");

        DeclaredSkillProgress declaredSkillProgress =
            DeclaredSkillProgressFixture.create().withStudent(student).toModel();

        BddLogger.when("calling the method with empty experience list");

        when(declaredSkillProgressRepository.findById(declaredSkillProgress.getId()))
            .thenReturn(Optional.of(declaredSkillProgress));
        when(declaredExperienceService.findAllByIds(List.of())).thenReturn(List.of());

        when(associationService.getAllOf(
                eq(declaredSkillProgress.getId()),
                eq(DeclaredSkillProgress.class),
                eq(
                    List.of(
                        EAssociationType.TRACE_DECLARED_SKILL,
                        EAssociationType.DECLARED_ACTIVITY_DECLARED_SKILL,
                        EAssociationType.DECLARED_EXPERIENCE_DECLARED_SKILL))))
            .thenReturn(List.of());
        when(traceService.findAllTracesById(List.of())).thenReturn(List.of());
        when(declaredActivityService.findAllDeclaredActivitiesByIds(List.of()))
            .thenReturn(List.of());

        DeclaredSkillAssociationsData result =
            declaredSkillProgressService.associateDeclaredSkillWithDeclaredExperiences(
                declaredSkillProgress.getId(), List.of());

        BddLogger.then("it should create no associations and return empty association data");

        verify(associationService).createAll(argThat(List::isEmpty));
        assertNotNull(result);
      }

      @Test
      void associateDeclaredSkillWithDeclaredExperiences_shouldDeduplicateRepeatedIdsInRequest() {
        BddLogger.given(
            "the method associateDeclaredSkillWithDeclaredExperiences called with the same"
                + " experience id twice");

        DeclaredSkillProgress declaredSkillProgress =
            DeclaredSkillProgressFixture.create().withStudent(student).toModel();

        DeclaredExperience declaredExperience = mock(DeclaredExperience.class);
        UUID experienceId = randomUUID();

        BddLogger.when("calling the method with a duplicated experience id");

        when(declaredSkillProgressRepository.findById(declaredSkillProgress.getId()))
            .thenReturn(Optional.of(declaredSkillProgress));
        when(declaredExperience.getId()).thenReturn(experienceId);
        when(declaredExperience.getStudent()).thenReturn(student);
        when(declaredExperienceService.findAllByIds(List.of(experienceId)))
            .thenReturn(List.of(declaredExperience));

        when(associationService.getAllOf(
                eq(declaredSkillProgress.getId()),
                eq(DeclaredSkillProgress.class),
                eq(
                    List.of(
                        EAssociationType.TRACE_DECLARED_SKILL,
                        EAssociationType.DECLARED_ACTIVITY_DECLARED_SKILL,
                        EAssociationType.DECLARED_EXPERIENCE_DECLARED_SKILL))))
            .thenReturn(List.of());
        when(traceService.findAllTracesById(List.of())).thenReturn(List.of());
        when(declaredActivityService.findAllDeclaredActivitiesByIds(List.of()))
            .thenReturn(List.of());
        when(declaredExperienceService.findAllByIds(List.of())).thenReturn(List.of());

        declaredSkillProgressService.associateDeclaredSkillWithDeclaredExperiences(
            declaredSkillProgress.getId(), List.of(experienceId, experienceId));

        BddLogger.then("it should create a single association, not two");

        verify(declaredExperienceService).findAllByIds(List.of(experienceId));
        verify(associationService)
            .createAll(
                argThat(
                    list ->
                        list.size() == 1
                            && list.get(0).id1().equals(experienceId)
                            && list.get(0).id2().equals(declaredSkillProgress.getId())
                            && list.get(0).associationType()
                                == EAssociationType.DECLARED_EXPERIENCE_DECLARED_SKILL));
      }

      @Test
      void
          associateDeclaredSkillWithDeclaredExperiences_shouldPropagateAssociationAlreadyExistException() {
        BddLogger.given("an experience already associated with the declared skill progress");

        DeclaredSkillProgress declaredSkillProgress =
            DeclaredSkillProgressFixture.create().withStudent(student).toModel();

        DeclaredExperience declaredExperience = mock(DeclaredExperience.class);
        UUID experienceId = randomUUID();

        when(declaredSkillProgressRepository.findById(declaredSkillProgress.getId()))
            .thenReturn(Optional.of(declaredSkillProgress));
        when(declaredExperience.getId()).thenReturn(experienceId);
        when(declaredExperience.getStudent()).thenReturn(student);
        when(declaredExperienceService.findAllByIds(List.of(experienceId)))
            .thenReturn(List.of(declaredExperience));
        when(associationService.createAll(anyList()))
            .thenThrow(new AssociationAlreadyExistException());

        BddLogger.when("calling the method with an already associated experience");

        BddLogger.then("it should propagate AssociationAlreadyExistException");

        assertThrows(
            AssociationAlreadyExistException.class,
            () ->
                declaredSkillProgressService.associateDeclaredSkillWithDeclaredExperiences(
                    declaredSkillProgress.getId(), List.of(experienceId)));
      }
    }

    @Nested
    class WhenGettingAssociations {

      @Mock private DeclaredActivityService declaredActivityService;
      @Mock private AssociationService associationService;
      @Mock private AssociationSearchHelper associationSearchHelper;
      @Mock private DeclaredExperienceService declaredExperienceService;

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
                associationSearchHelper,
                declaredExperienceService);
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

      private DeclaredExperience buildDeclaredExperience(
          String title, EExperienceType type, LocalDate startDate, LocalDate endDate) {
        return DeclaredExperience.create(
            student,
            title,
            type,
            "Organization",
            "Sector",
            "Paris",
            "Description",
            null,
            null,
            null,
            startDate,
            endDate);
      }

      @Test
      void getAssociationsOf_should_return_empty_declaredExperienceAssociations_when_none() {
        BddLogger.given("a declared skill progress without any declared experience association");

        DeclaredSkillProgress declaredSkillProgress =
            DeclaredSkillProgressFixture.create().withStudent(student).toModel();
        UUID declaredSkillProgressId = declaredSkillProgress.getId();

        when(declaredSkillProgressRepository.findById(declaredSkillProgressId))
            .thenReturn(Optional.of(declaredSkillProgress));
        when(associationService.getAllOf(
                declaredSkillProgressId,
                DeclaredSkillProgress.class,
                List.of(
                    EAssociationType.TRACE_DECLARED_SKILL,
                    EAssociationType.DECLARED_ACTIVITY_DECLARED_SKILL,
                    EAssociationType.DECLARED_EXPERIENCE_DECLARED_SKILL)))
            .thenReturn(List.of());
        when(traceService.findAllTracesById(List.of())).thenReturn(List.of());
        when(declaredActivityService.findAllDeclaredActivitiesByIds(List.of()))
            .thenReturn(List.of());
        when(declaredActivityService.getDeclaredActivityStatus(List.of())).thenReturn(Map.of());
        when(declaredExperienceService.findAllByIds(List.of())).thenReturn(List.of());

        BddLogger.when("getAssociationsOf is called");

        DeclaredSkillAssociationsData result =
            declaredSkillProgressService.getAssociationsOf(declaredSkillProgressId);

        BddLogger.then("it should return an empty declaredExperienceAssociations list");

        assertNotNull(result.declaredExperienceAssociations());
        assertTrue(result.declaredExperienceAssociations().isEmpty());
      }

      @Test
      void getAssociationsOf_should_return_associated_professional_declaredExperience() {
        BddLogger.given("a declared skill progress associated with a professional experience");

        DeclaredSkillProgress declaredSkillProgress =
            DeclaredSkillProgressFixture.create().withStudent(student).toModel();
        UUID declaredSkillProgressId = declaredSkillProgress.getId();

        DeclaredExperience experience =
            buildDeclaredExperience(
                "Backend Developer", EExperienceType.PROFESSIONAL, LocalDate.of(2022, 1, 10), null);
        UUID associationId = randomUUID();
        Association association =
            Association.create(
                experience.getId(),
                declaredSkillProgressId,
                EAssociationType.DECLARED_EXPERIENCE_DECLARED_SKILL);

        when(declaredSkillProgressRepository.findById(declaredSkillProgressId))
            .thenReturn(Optional.of(declaredSkillProgress));
        when(associationService.getAllOf(
                declaredSkillProgressId,
                DeclaredSkillProgress.class,
                List.of(
                    EAssociationType.TRACE_DECLARED_SKILL,
                    EAssociationType.DECLARED_ACTIVITY_DECLARED_SKILL,
                    EAssociationType.DECLARED_EXPERIENCE_DECLARED_SKILL)))
            .thenReturn(List.of(association));
        when(traceService.findAllTracesById(List.of())).thenReturn(List.of());
        when(declaredActivityService.findAllDeclaredActivitiesByIds(List.of()))
            .thenReturn(List.of());
        when(declaredActivityService.getDeclaredActivityStatus(List.of())).thenReturn(Map.of());
        when(declaredExperienceService.findAllByIds(List.of(experience.getId())))
            .thenReturn(List.of(experience));

        BddLogger.when("getAssociationsOf is called");

        DeclaredSkillAssociationsData result =
            declaredSkillProgressService.getAssociationsOf(declaredSkillProgressId);

        BddLogger.then("it should return the professional declared experience association");

        assertEquals(1, result.declaredExperienceAssociations().size());
        var experienceAssociation = result.declaredExperienceAssociations().getFirst();
        assertEquals(association.getId(), experienceAssociation.associationId());
        assertEquals(experience, experienceAssociation.declaredExperience());
        assertEquals(
            EExperienceType.PROFESSIONAL,
            experienceAssociation.declaredExperience().getExperienceType());
      }

      @Test
      void getAssociationsOf_should_return_associated_personal_declaredExperience() {
        BddLogger.given("a declared skill progress associated with a personal experience");

        DeclaredSkillProgress declaredSkillProgress =
            DeclaredSkillProgressFixture.create().withStudent(student).toModel();
        UUID declaredSkillProgressId = declaredSkillProgress.getId();

        DeclaredExperience experience =
            buildDeclaredExperience(
                "Bénévolat associatif",
                EExperienceType.PERSONAL,
                LocalDate.of(2023, 3, 1),
                LocalDate.of(2023, 9, 1));
        Association association =
            Association.create(
                experience.getId(),
                declaredSkillProgressId,
                EAssociationType.DECLARED_EXPERIENCE_DECLARED_SKILL);

        when(declaredSkillProgressRepository.findById(declaredSkillProgressId))
            .thenReturn(Optional.of(declaredSkillProgress));
        when(associationService.getAllOf(
                declaredSkillProgressId,
                DeclaredSkillProgress.class,
                List.of(
                    EAssociationType.TRACE_DECLARED_SKILL,
                    EAssociationType.DECLARED_ACTIVITY_DECLARED_SKILL,
                    EAssociationType.DECLARED_EXPERIENCE_DECLARED_SKILL)))
            .thenReturn(List.of(association));
        when(traceService.findAllTracesById(List.of())).thenReturn(List.of());
        when(declaredActivityService.findAllDeclaredActivitiesByIds(List.of()))
            .thenReturn(List.of());
        when(declaredActivityService.getDeclaredActivityStatus(List.of())).thenReturn(Map.of());
        when(declaredExperienceService.findAllByIds(List.of(experience.getId())))
            .thenReturn(List.of(experience));

        BddLogger.when("getAssociationsOf is called");

        DeclaredSkillAssociationsData result =
            declaredSkillProgressService.getAssociationsOf(declaredSkillProgressId);

        BddLogger.then("it should return the personal declared experience association");

        assertEquals(1, result.declaredExperienceAssociations().size());
        assertEquals(
            EExperienceType.PERSONAL,
            result
                .declaredExperienceAssociations()
                .getFirst()
                .declaredExperience()
                .getExperienceType());
      }

      @Test
      void
          getAssociationsOf_should_return_multiple_declaredExperienceAssociations_ordered_antichronologically() {
        BddLogger.given(
            "a declared skill progress associated with several experiences whose associations"
                + " are returned from most to least recently created");

        DeclaredSkillProgress declaredSkillProgress =
            DeclaredSkillProgressFixture.create().withStudent(student).toModel();
        UUID declaredSkillProgressId = declaredSkillProgress.getId();

        DeclaredExperience professionalExperience =
            buildDeclaredExperience(
                "Backend Developer", EExperienceType.PROFESSIONAL, LocalDate.of(2022, 1, 10), null);
        DeclaredExperience personalExperience =
            buildDeclaredExperience(
                "Bénévolat associatif",
                EExperienceType.PERSONAL,
                LocalDate.of(2023, 3, 1),
                LocalDate.of(2023, 9, 1));

        Association mostRecentAssociation =
            Association.create(
                personalExperience.getId(),
                declaredSkillProgressId,
                EAssociationType.DECLARED_EXPERIENCE_DECLARED_SKILL);
        Association oldestAssociation =
            Association.create(
                professionalExperience.getId(),
                declaredSkillProgressId,
                EAssociationType.DECLARED_EXPERIENCE_DECLARED_SKILL);

        // associationService already returns associations ordered by createdAt DESC (most recent
        // first); the service must preserve that order rather than re-sort in memory.
        List<Association> associationsInRepositoryOrder =
            List.of(mostRecentAssociation, oldestAssociation);

        when(declaredSkillProgressRepository.findById(declaredSkillProgressId))
            .thenReturn(Optional.of(declaredSkillProgress));
        when(associationService.getAllOf(
                declaredSkillProgressId,
                DeclaredSkillProgress.class,
                List.of(
                    EAssociationType.TRACE_DECLARED_SKILL,
                    EAssociationType.DECLARED_ACTIVITY_DECLARED_SKILL,
                    EAssociationType.DECLARED_EXPERIENCE_DECLARED_SKILL)))
            .thenReturn(associationsInRepositoryOrder);
        when(traceService.findAllTracesById(List.of())).thenReturn(List.of());
        when(declaredActivityService.findAllDeclaredActivitiesByIds(List.of()))
            .thenReturn(List.of());
        when(declaredActivityService.getDeclaredActivityStatus(List.of())).thenReturn(Map.of());
        when(declaredExperienceService.findAllByIds(
                List.of(personalExperience.getId(), professionalExperience.getId())))
            .thenReturn(List.of(professionalExperience, personalExperience));

        BddLogger.when("getAssociationsOf is called");

        DeclaredSkillAssociationsData result =
            declaredSkillProgressService.getAssociationsOf(declaredSkillProgressId);

        BddLogger.then(
            "it should return the declared experience associations in the same antichronological"
                + " order, most recently associated first");

        assertEquals(2, result.declaredExperienceAssociations().size());
        assertEquals(
            personalExperience,
            result.declaredExperienceAssociations().get(0).declaredExperience());
        assertEquals(
            professionalExperience,
            result.declaredExperienceAssociations().get(1).declaredExperience());
      }

      @Test
      void
          getAssociationsOf_should_not_regress_trace_and_declaredActivity_associations_when_experience_associations_present() {
        BddLogger.given(
            "a declared skill progress associated with a trace, a declared activity and a"
                + " declared experience");

        DeclaredSkillProgress declaredSkillProgress =
            DeclaredSkillProgressFixture.create().withStudent(student).toModel();
        UUID declaredSkillProgressId = declaredSkillProgress.getId();

        Trace trace =
            fr.avenirsesr.portfolio.trace.infrastructure.fixture.TraceFixture.create().toModel();
        DeclaredActivity declaredActivity = mock(DeclaredActivity.class);
        UUID declaredActivityId = randomUUID();
        when(declaredActivity.getId()).thenReturn(declaredActivityId);

        DeclaredExperience experience =
            buildDeclaredExperience(
                "Backend Developer", EExperienceType.PROFESSIONAL, LocalDate.of(2022, 1, 10), null);

        Association traceAssociation =
            Association.create(
                trace.getId(), declaredSkillProgressId, EAssociationType.TRACE_DECLARED_SKILL);
        Association activityAssociation =
            Association.create(
                declaredActivityId,
                declaredSkillProgressId,
                EAssociationType.DECLARED_ACTIVITY_DECLARED_SKILL);
        Association experienceAssociation =
            Association.create(
                experience.getId(),
                declaredSkillProgressId,
                EAssociationType.DECLARED_EXPERIENCE_DECLARED_SKILL);

        when(declaredSkillProgressRepository.findById(declaredSkillProgressId))
            .thenReturn(Optional.of(declaredSkillProgress));
        when(associationService.getAllOf(
                declaredSkillProgressId,
                DeclaredSkillProgress.class,
                List.of(
                    EAssociationType.TRACE_DECLARED_SKILL,
                    EAssociationType.DECLARED_ACTIVITY_DECLARED_SKILL,
                    EAssociationType.DECLARED_EXPERIENCE_DECLARED_SKILL)))
            .thenReturn(List.of(traceAssociation, activityAssociation, experienceAssociation));
        when(traceService.findAllTracesById(List.of(trace.getId()))).thenReturn(List.of(trace));
        when(declaredActivityService.findAllDeclaredActivitiesByIds(List.of(declaredActivityId)))
            .thenReturn(List.of(declaredActivity));
        when(declaredActivityService.getDeclaredActivityStatus(List.of(declaredActivity)))
            .thenReturn(Map.of());
        when(declaredExperienceService.findAllByIds(List.of(experience.getId())))
            .thenReturn(List.of(experience));

        BddLogger.when("getAssociationsOf is called");

        DeclaredSkillAssociationsData result =
            declaredSkillProgressService.getAssociationsOf(declaredSkillProgressId);

        BddLogger.then(
            "it should still return the trace and declared activity associations alongside the"
                + " declared experience association");

        assertEquals(1, result.traceAssociations().size());
        assertEquals(1, result.declaredActivityAssociations().size());
        assertEquals(1, result.declaredExperienceAssociations().size());
      }
    }

    @Nested
    class WhenDeletingAssociations {

      @Test
      void deleteAssociations_should_delete_when_associations_belong_to_declaredSkillProgress() {
        BddLogger.given("A logged-in student and a declared skill progress with associations");

        DeclaredSkillProgress declaredSkillProgress =
            DeclaredSkillProgressFixture.create().withStudent(student).toModel();
        UUID declaredSkillProgressId = declaredSkillProgress.getId();
        UUID associationId1 = randomUUID();
        UUID associationId2 = randomUUID();

        Association association1 = mock(Association.class);
        Association association2 = mock(Association.class);

        when(declaredSkillProgressRepository.findById(declaredSkillProgressId))
            .thenReturn(Optional.of(declaredSkillProgress));
        when(associationService.getAllOf(
                declaredSkillProgressId,
                DeclaredSkillProgress.class,
                List.of(
                    EAssociationType.TRACE_DECLARED_SKILL,
                    EAssociationType.DECLARED_ACTIVITY_DECLARED_SKILL)))
            .thenReturn(List.of(association1, association2));
        when(association1.getId()).thenReturn(associationId1);
        when(association2.getId()).thenReturn(associationId2);

        BddLogger.when("deleteAssociations is called");

        declaredSkillProgressService.deleteAssociations(
            declaredSkillProgressId, List.of(associationId1));

        BddLogger.then("deleteAllByIds should be called with the given ids");

        verify(associationService).deleteAllByIds(List.of(associationId1));
      }

      @Test
      void deleteAssociations_should_throw_when_declaredSkillProgress_not_found() {
        BddLogger.given("A non-existent declared skill progress id");

        UUID declaredSkillProgressId = randomUUID();

        when(declaredSkillProgressRepository.findById(declaredSkillProgressId))
            .thenReturn(Optional.empty());

        BddLogger.when("deleteAssociations is called");

        BddLogger.then("it should throw DeclaredSkillProgressNotFoundException");

        assertThrows(
            DeclaredSkillProgressNotFoundException.class,
            () ->
                declaredSkillProgressService.deleteAssociations(
                    declaredSkillProgressId, List.of(randomUUID())));

        verify(associationService, never()).deleteAllByIds(anyList());
      }

      @Test
      void deleteAssociations_should_throw_when_skill_belongs_to_other_student() {
        BddLogger.given("A declared skill progress belonging to another student");

        Student anotherStudent = StudentFixture.create().toModel();
        UUID declaredSkillProgressId = randomUUID();
        DeclaredSkillProgress declaredSkillProgress =
            DeclaredSkillProgressFixture.create().withStudent(anotherStudent).toModel();

        when(declaredSkillProgressRepository.findById(declaredSkillProgressId))
            .thenReturn(Optional.of(declaredSkillProgress));

        BddLogger.when("deleteAssociations is called");

        BddLogger.then("it should throw UserNotAuthorizedException");

        assertThrows(
            UserNotAuthorizedException.class,
            () ->
                declaredSkillProgressService.deleteAssociations(
                    declaredSkillProgressId, List.of(randomUUID())));

        verify(associationService, never()).deleteAllByIds(anyList());
      }

      @Test
      void deleteAssociations_should_throw_when_ids_not_associated() {
        BddLogger.given("A declared skill progress and an id that is not one of its associations");

        DeclaredSkillProgress declaredSkillProgress =
            DeclaredSkillProgressFixture.create().withStudent(student).toModel();
        UUID declaredSkillProgressId = declaredSkillProgress.getId();
        UUID associationId = randomUUID();

        Association association = mock(Association.class);

        when(declaredSkillProgressRepository.findById(declaredSkillProgressId))
            .thenReturn(Optional.of(declaredSkillProgress));
        when(associationService.getAllOf(
                declaredSkillProgressId,
                DeclaredSkillProgress.class,
                List.of(
                    EAssociationType.TRACE_DECLARED_SKILL,
                    EAssociationType.DECLARED_ACTIVITY_DECLARED_SKILL)))
            .thenReturn(List.of(association));
        when(association.getId()).thenReturn(associationId);

        BddLogger.when("deleteAssociations is called with a non associated id");

        BddLogger.then("it should throw UserNotAuthorizedException");

        assertThrows(
            UserNotAuthorizedException.class,
            () ->
                declaredSkillProgressService.deleteAssociations(
                    declaredSkillProgressId, List.of(randomUUID())));

        verify(associationService, never()).deleteAllByIds(anyList());
      }
    }
  }

  @Test
  void searchDeclaredSkillsForAssociation_should_use_TRACE_DECLARED_SKILL_when_context_is_Trace() {
    UUID contextId = randomUUID();
    PageCriteria pageCriteria = new PageCriteria(0, 10);
    PagedResult<DeclaredSkillProgress> skillSearch =
        new PagedResult<>(List.of(), new PageInfo(0, 10, 0));

    when(declaredSkillProgressRepository.findAllByStudent(
            eq(student),
            eq(pageCriteria),
            eq("kw"),
            eq(new SortCriteria(ESortField.NAME, ESortOrder.ASC))))
        .thenReturn(skillSearch);

    PagedResult<AssociationSearchResultData> expected =
        new PagedResult<>(List.of(), new PageInfo(0, 10, 0));
    when(associationSearchHelper.searchForAssociation(
            eq(contextId),
            eq(Trace.class),
            eq(EAssociationType.TRACE_DECLARED_SKILL),
            any(),
            eq(skillSearch),
            any(),
            any(),
            any(),
            any()))
        .thenReturn(expected);

    var result =
        declaredSkillProgressService.searchDeclaredSkillsForAssociation(
            contextId, EAssociationContextType.TRACE, "kw", pageCriteria);

    assertThat(result).isSameAs(expected);
  }

  @Test
  void
      searchDeclaredSkillsForAssociation_should_use_DECLARED_ACTIVITY_DECLARED_SKILL_when_context_is_Activity() {
    UUID contextId = randomUUID();
    PageCriteria pageCriteria = new PageCriteria(0, 10);
    PagedResult<DeclaredSkillProgress> skillSearch =
        new PagedResult<>(List.of(), new PageInfo(0, 10, 0));

    when(declaredSkillProgressRepository.findAllByStudent(
            eq(student),
            eq(pageCriteria),
            eq("kw"),
            eq(new SortCriteria(ESortField.NAME, ESortOrder.ASC))))
        .thenReturn(skillSearch);

    PagedResult<AssociationSearchResultData> expected =
        new PagedResult<>(List.of(), new PageInfo(0, 10, 0));
    when(associationSearchHelper.searchForAssociation(
            eq(contextId),
            eq(DeclaredActivity.class),
            eq(EAssociationType.DECLARED_ACTIVITY_DECLARED_SKILL),
            any(),
            eq(skillSearch),
            any(),
            any(),
            any(),
            any()))
        .thenReturn(expected);

    var result =
        declaredSkillProgressService.searchDeclaredSkillsForAssociation(
            contextId, EAssociationContextType.DECLARED_ACTIVITY, "kw", pageCriteria);

    assertThat(result).isSameAs(expected);
  }

  @Test
  void searchDeclaredSkillsForAssociation_should_pass_nulls_when_context_class_unknown() {
    PageCriteria pageCriteria = new PageCriteria(0, 10);
    PagedResult<DeclaredSkillProgress> skillSearch =
        new PagedResult<>(List.of(), new PageInfo(0, 10, 0));

    when(declaredSkillProgressRepository.findAllByStudent(
            eq(student),
            eq(pageCriteria),
            eq("kw"),
            eq(new SortCriteria(ESortField.NAME, ESortOrder.ASC))))
        .thenReturn(skillSearch);

    PagedResult<AssociationSearchResultData> expected =
        new PagedResult<>(List.of(), new PageInfo(0, 10, 0));
    when(associationSearchHelper.searchForAssociation(
            eq(null), eq(null), eq(null), eq(null), eq(skillSearch), any(), any(), any(), any()))
        .thenReturn(expected);

    var result =
        declaredSkillProgressService.searchDeclaredSkillsForAssociation(
            null, null, "kw", pageCriteria);

    assertThat(result).isSameAs(expected);
  }
}
