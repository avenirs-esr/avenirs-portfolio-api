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
import fr.avenirsesr.portfolio.student.association.domain.port.input.AssociationService;
import fr.avenirsesr.portfolio.student.skill.domain.data.DeclaredSkillAssociationCount;
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
  @Mock private DeclaredSkillSyncService declaredSkillSyncService;
  @Mock private DeclaredSkillProgressRepository declaredSkillProgressRepository;
  @Mock private ExternalSkillClient externalSkillClient;
  @Mock private LoggedInUserService loggedInUserService;
  @Mock private AssociationService associationService;
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
  }
}
