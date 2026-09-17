package fr.avenirsesr.portfolio.student.skill.domain.service;

import static fr.avenirsesr.portfolio.common.validation.domain.constraints.FieldMaxLengths.RICH_DESCRIPTION_LENGTH;
import static java.util.UUID.randomUUID;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

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
import fr.avenirsesr.portfolio.shared.domain.port.input.LoggedInUserService;
import fr.avenirsesr.portfolio.student.activity.domain.data.DeclaredActivityAssociationData;
import fr.avenirsesr.portfolio.student.activity.domain.model.DeclaredActivity;
import fr.avenirsesr.portfolio.student.activity.domain.model.enums.EDeclaredActivityStatus;
import fr.avenirsesr.portfolio.student.activity.domain.port.input.DeclaredActivityService;
import fr.avenirsesr.portfolio.student.association.domain.data.AssociatedElementsData;
import fr.avenirsesr.portfolio.student.association.domain.data.AssociationSearchResultData;
import fr.avenirsesr.portfolio.student.association.domain.model.EAssociationContextType;
import fr.avenirsesr.portfolio.student.association.domain.model.EAssociationType;
import fr.avenirsesr.portfolio.student.association.domain.port.input.AssociationService;
import fr.avenirsesr.portfolio.student.association.domain.service.AssociationSearchHelper;
import fr.avenirsesr.portfolio.student.experience.domain.data.DeclaredExperienceAssociationData;
import fr.avenirsesr.portfolio.student.experience.domain.model.DeclaredExperience;
import fr.avenirsesr.portfolio.student.experience.domain.port.input.DeclaredExperienceService;
import fr.avenirsesr.portfolio.student.skill.domain.data.DeclaredSkillAssociationCount;
import fr.avenirsesr.portfolio.student.skill.domain.data.DeclaredSkillAssociationsData;
import fr.avenirsesr.portfolio.student.skill.domain.data.DeclaredSkillProgressData;
import fr.avenirsesr.portfolio.student.skill.domain.data.DeclaredSkillProgressDetails;
import fr.avenirsesr.portfolio.student.skill.domain.exception.DeclaredSkillNotFoundException;
import fr.avenirsesr.portfolio.student.skill.domain.exception.DeclaredSkillProgressNotFoundException;
import fr.avenirsesr.portfolio.student.skill.domain.exception.DuplicateDeclaredSkillException;
import fr.avenirsesr.portfolio.student.skill.domain.model.DeclaredSkill;
import fr.avenirsesr.portfolio.student.skill.domain.model.DeclaredSkillProgress;
import fr.avenirsesr.portfolio.student.skill.domain.model.enums.EDeclaredSkillLevel;
import fr.avenirsesr.portfolio.student.skill.domain.port.input.DeclaredSkillSyncService;
import fr.avenirsesr.portfolio.student.skill.domain.port.output.repository.DeclaredSkillProgressRepository;
import fr.avenirsesr.portfolio.student.skill.infrastructure.adapter.client.ExternalSkillClient;
import fr.avenirsesr.portfolio.student.skill.infrastructure.fixture.DeclaredSkillProgressFixture;
import fr.avenirsesr.portfolio.student.trace.domain.data.TraceAssociationData;
import fr.avenirsesr.portfolio.student.trace.domain.model.Trace;
import fr.avenirsesr.portfolio.student.trace.domain.port.input.TraceService;
import fr.avenirsesr.portfolio.student.trace.infrastructure.fixture.TraceFixture;
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
  @Mock private DeclaredExperienceService declaredExperienceService;
  @InjectMocks private DeclaredSkillProgressServiceImpl declaredSkillProgressService;
  private static final String CHARSET =
      "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
  private static final RandomGenerator random = RandomGenerator.getDefault();

  private Student student;

  @BeforeEach
  void setUp() {
    student = StudentFixture.create().toModel();

    lenient().when(loggedInUserService.getLoggedInStudent()).thenReturn(student);
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
      void
          getDeclaredSkillProgressDetails_withAList_shouldReturnDeclaredSkillsProgressDetailsForEach() {
        BddLogger.given("the method getDeclaredSkillProgressDetails(List<DeclaredSkillProgress>)");
        DeclaredSkillProgress firstSkill =
            DeclaredSkillProgressFixture.create().withStudent(student).toModel();
        DeclaredSkillProgress secondSkill =
            DeclaredSkillProgressFixture.create().withStudent(student).toModel();

        List<ExternalSkillCategoryDTO> categories =
            List.of(new ExternalSkillCategoryDTO("Domain", EExternalSkillCategoryType.DOMAIN));
        when(externalSkillClient.getExternalSkillDetails(any(UUID.class)))
            .thenAnswer(
                invocation ->
                    Optional.of(
                        new ExternalSkillDetailsDTO(
                            invocation.getArgument(0),
                            "Test Skill",
                            categories,
                            EExternalSkillType.ROME4)));

        BddLogger.when("calling the method with a list of declared skill progresses");
        List<DeclaredSkillProgressDetails> result =
            declaredSkillProgressService.getDeclaredSkillProgressDetails(
                List.of(firstSkill, secondSkill));

        BddLogger.then("it should return one enriched details entry per declared skill progress");
        assertThat(result)
            .extracting(DeclaredSkillProgressDetails::declaredSkillProgress)
            .containsExactly(firstSkill, secondSkill);
        assertThat(result)
            .allSatisfy(details -> assertThat(details.externalCategories()).isEqualTo(categories));
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
    class WhenAssociatingDeclaredSkill {

      @Test
      void associate_shouldAssociateTheGivenElementsWithTheDeclaredSkill() {
        BddLogger.given("the method associate");

        DeclaredSkillProgress declaredSkillProgress =
            DeclaredSkillProgressFixture.create().withStudent(student).toModel();
        List<UUID> activityIds = List.of(randomUUID(), randomUUID());

        BddLogger.when("calling the method with a valid skill id");

        when(declaredSkillProgressRepository.findById(declaredSkillProgress.getId()))
            .thenReturn(Optional.of(declaredSkillProgress));
        when(associationService.getAllAssociatedElementsOf(
                declaredSkillProgress.getId(), DeclaredSkillProgress.class))
            .thenReturn(new AssociatedElementsData(List.of(), List.of(), List.of(), List.of()));

        DeclaredSkillAssociationsData result =
            declaredSkillProgressService.associate(
                declaredSkillProgress.getId(),
                activityIds,
                EAssociationType.DECLARED_ACTIVITY_DECLARED_SKILL);

        BddLogger.then("it should associate them through the association service");

        verify(associationService)
            .associate(
                declaredSkillProgress.getId(),
                DeclaredSkillProgress.class,
                activityIds,
                EAssociationType.DECLARED_ACTIVITY_DECLARED_SKILL);

        assertNotNull(result);
      }

      @Test
      void associate_shouldThrowDeclaredSkillProgressNotFoundException() {
        BddLogger.given("the method associate");

        UUID declaredSkillId = randomUUID();
        UUID activityId = randomUUID();

        BddLogger.when("calling the method with non-existing skill id");

        when(declaredSkillProgressRepository.findById(declaredSkillId))
            .thenReturn(Optional.empty());

        BddLogger.then("it should throw DeclaredSkillProgressNotFoundException");

        assertThrows(
            DeclaredSkillProgressNotFoundException.class,
            () ->
                declaredSkillProgressService.associate(
                    declaredSkillId,
                    List.of(activityId),
                    EAssociationType.DECLARED_ACTIVITY_DECLARED_SKILL));

        verifyNoInteractions(associationService);
      }

      @Test
      void associate_shouldThrowUserNotAuthorizedWhenSkillBelongsToAnotherStudent() {
        BddLogger.given("the method associate");

        Student anotherStudent = StudentFixture.create().toModel();
        DeclaredSkillProgress declaredSkillProgress =
            DeclaredSkillProgressFixture.create().withStudent(anotherStudent).toModel();
        UUID activityId = randomUUID();

        BddLogger.when("calling the method with a skill belonging to another student");

        when(declaredSkillProgressRepository.findById(declaredSkillProgress.getId()))
            .thenReturn(Optional.of(declaredSkillProgress));

        BddLogger.then("it should throw UserNotAuthorizedException");

        assertThrows(
            UserNotAuthorizedException.class,
            () ->
                declaredSkillProgressService.associate(
                    declaredSkillProgress.getId(),
                    List.of(activityId),
                    EAssociationType.DECLARED_ACTIVITY_DECLARED_SKILL));

        verifyNoInteractions(associationService);
      }
    }

    @Nested
    class WhenGettingAssociations {

      @Mock private AssociationService associationService;
      @Mock private AssociationSearchHelper associationSearchHelper;

      @BeforeEach
      void setUp() {
        declaredSkillProgressService =
            new DeclaredSkillProgressServiceImpl(
                declaredSkillSyncService,
                declaredSkillProgressRepository,
                externalSkillClient,
                loggedInUserService,
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

      @Test
      void getAssociationsOf_should_return_empty_associations_when_none() {
        BddLogger.given("a declared skill progress without any association");

        DeclaredSkillProgress declaredSkillProgress =
            DeclaredSkillProgressFixture.create().withStudent(student).toModel();
        UUID declaredSkillProgressId = declaredSkillProgress.getId();

        when(declaredSkillProgressRepository.findById(declaredSkillProgressId))
            .thenReturn(Optional.of(declaredSkillProgress));
        when(associationService.getAllAssociatedElementsOf(
                declaredSkillProgressId, DeclaredSkillProgress.class))
            .thenReturn(new AssociatedElementsData(List.of(), List.of(), List.of(), List.of()));

        BddLogger.when("getAssociationsOf is called");

        DeclaredSkillAssociationsData result =
            declaredSkillProgressService.getAssociationsOf(declaredSkillProgressId);

        BddLogger.then("it should return empty association lists");

        assertTrue(result.traceAssociations().isEmpty());
        assertTrue(result.declaredActivityAssociations().isEmpty());
        assertTrue(result.declaredExperienceAssociations().isEmpty());
      }

      @Test
      void getAssociationsOf_should_return_the_associated_elements() {
        BddLogger.given(
            "a declared skill progress associated with a trace, a declared activity and a"
                + " declared experience");

        DeclaredSkillProgress declaredSkillProgress =
            DeclaredSkillProgressFixture.create().withStudent(student).toModel();
        UUID declaredSkillProgressId = declaredSkillProgress.getId();

        Trace trace = TraceFixture.create().toModel();
        DeclaredActivity declaredActivity = mock(DeclaredActivity.class);
        DeclaredExperience experience = mock(DeclaredExperience.class);

        when(declaredSkillProgressRepository.findById(declaredSkillProgressId))
            .thenReturn(Optional.of(declaredSkillProgress));
        when(associationService.getAllAssociatedElementsOf(
                declaredSkillProgressId, DeclaredSkillProgress.class))
            .thenReturn(
                new AssociatedElementsData(
                    List.of(new TraceAssociationData(randomUUID(), trace)),
                    List.of(
                        new DeclaredActivityAssociationData(
                            randomUUID(), declaredActivity, EDeclaredActivityStatus.IN_PROGRESS)),
                    List.of(),
                    List.of(new DeclaredExperienceAssociationData(randomUUID(), experience))));

        BddLogger.when("getAssociationsOf is called");

        DeclaredSkillAssociationsData result =
            declaredSkillProgressService.getAssociationsOf(declaredSkillProgressId);

        BddLogger.then("it should return every associated element");

        assertEquals(trace, result.traceAssociations().getFirst().trace());
        assertEquals(
            declaredActivity, result.declaredActivityAssociations().getFirst().declaredActivity());
        assertEquals(
            experience, result.declaredExperienceAssociations().getFirst().declaredExperience());
      }
    }

    @Nested
    class WhenDeletingAssociations {

      @Test
      void deleteAssociations_should_unassociate_the_given_associations() {
        BddLogger.given("A logged-in student and a declared skill progress with associations");

        DeclaredSkillProgress declaredSkillProgress =
            DeclaredSkillProgressFixture.create().withStudent(student).toModel();
        UUID declaredSkillProgressId = declaredSkillProgress.getId();
        List<UUID> idsToDelete = List.of(randomUUID(), randomUUID());

        when(declaredSkillProgressRepository.findById(declaredSkillProgressId))
            .thenReturn(Optional.of(declaredSkillProgress));

        BddLogger.when("deleteAssociations is called");

        declaredSkillProgressService.deleteAssociations(declaredSkillProgressId, idsToDelete);

        BddLogger.then("the association service should unassociate them");

        verify(associationService)
            .unassociate(declaredSkillProgressId, DeclaredSkillProgress.class, idsToDelete);
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

        verify(associationService, never()).unassociate(any(), any(), anyList());
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

        verify(associationService, never()).unassociate(any(), any(), anyList());
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
