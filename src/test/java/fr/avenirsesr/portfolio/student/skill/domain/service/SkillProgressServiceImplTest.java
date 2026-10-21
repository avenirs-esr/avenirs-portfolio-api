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
import fr.avenirsesr.portfolio.common.security.domain.exception.UserNotAuthorizedException;
import fr.avenirsesr.portfolio.common.skill.domain.model.enums.ESkillKind;
import fr.avenirsesr.portfolio.common.skill.domain.model.enums.ESkillReferentialType;
import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import fr.avenirsesr.portfolio.shared.domain.port.input.LoggedInUserService;
import fr.avenirsesr.portfolio.skill.domain.data.SkillPathSegment;
import fr.avenirsesr.portfolio.skill.domain.exception.SkillNotAssessableException;
import fr.avenirsesr.portfolio.skill.domain.exception.SkillNotFoundException;
import fr.avenirsesr.portfolio.skill.domain.model.Skill;
import fr.avenirsesr.portfolio.skill.domain.port.input.SkillService;
import fr.avenirsesr.portfolio.skill.infrastructure.fixture.SkillFixture;
import fr.avenirsesr.portfolio.skill.infrastructure.fixture.SkillReferentialFixture;
import fr.avenirsesr.portfolio.student.association.domain.port.input.AssociationService;
import fr.avenirsesr.portfolio.student.skill.domain.data.SkillProgressAssociationCount;
import fr.avenirsesr.portfolio.student.skill.domain.data.SkillProgressData;
import fr.avenirsesr.portfolio.student.skill.domain.data.SkillProgressDetails;
import fr.avenirsesr.portfolio.student.skill.domain.exception.DuplicateSkillProgressException;
import fr.avenirsesr.portfolio.student.skill.domain.exception.SkillNotSelfDeclarableException;
import fr.avenirsesr.portfolio.student.skill.domain.exception.SkillProgressNotFoundException;
import fr.avenirsesr.portfolio.student.skill.domain.model.SkillProgress;
import fr.avenirsesr.portfolio.student.skill.domain.model.enums.ESkillLevel;
import fr.avenirsesr.portfolio.student.skill.domain.port.output.repository.SkillProgressRepository;
import fr.avenirsesr.portfolio.student.skill.infrastructure.fixture.SkillProgressFixture;
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
public class SkillProgressServiceImplTest {
  @Mock private SkillService skillService;
  @Mock private SkillProgressRepository skillProgressRepository;
  @Mock private LoggedInUserService loggedInUserService;
  @Mock private AssociationService associationService;
  @InjectMocks private SkillProgressServiceImpl skillProgressService;
  private static final String CHARSET =
      "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
  private static final RandomGenerator random = RandomGenerator.getDefault();

  private Student student;

  @BeforeEach
  void setUp() {
    student = StudentFixture.create().toModel();

    lenient().when(loggedInUserService.getLoggedInStudent()).thenReturn(student);
  }

  private SkillProgress buildSkillProgress(Skill skill) {
    return SkillProgress.create(student, skill, ESkillLevel.BEGINNER, null);
  }

  private void stubSkillResolution(Skill skill) {
    lenient()
        .when(skillService.getAllWithAncestorsById(List.of(skill.getId())))
        .thenReturn(List.of(skill));
  }

  @Nested
  class GivenAStudentProgressService {

    @BeforeEach
    void setupGiven() {
      BddLogger.given("a SkillProgressService");
    }

    @Nested
    class WhenGettingSkillProgresses {

      @Test
      void getSkillProgresses_shouldDelegateToRepositoryAndReturnResult() {
        BddLogger.given("the method getSkillProgresses");
        PageCriteria criteria = new PageCriteria(1, 8);
        Skill skill = SkillFixture.create().withTitle("Java Programming").toModel();
        SkillProgress skillProgress = buildSkillProgress(skill);
        PagedResult<SkillProgress> repositoryResult =
            new PagedResult<>(List.of(skillProgress), new PageInfo(1, 8, 1));

        BddLogger.when("calling the method with a given student and no isValorized filter");
        when(skillProgressRepository.findAllByStudent(
                student,
                criteria,
                (Boolean) null,
                new SortCriteria(ESortField.NAME, ESortOrder.ASC)))
            .thenReturn(repositoryResult);
        stubSkillResolution(skill);

        PagedResult<SkillProgressData> result =
            skillProgressService.getSkillProgresses(criteria, null);

        BddLogger.then(
            "it should return the expected paged skill progress and delegate to" + " repository");
        assertThat(result.pageInfo()).isSameAs(repositoryResult.pageInfo());
        assertThat(result.content())
            .containsExactly(
                new SkillProgressData(
                    skillProgress, skill, new SkillProgressAssociationCount(0, 0)));
        verify(skillProgressRepository)
            .findAllByStudent(
                student,
                criteria,
                (Boolean) null,
                new SortCriteria(ESortField.NAME, ESortOrder.ASC));
      }

      @Test
      void getSkillProgresses_shouldDelegateIsValorizedFilterToRepository() {
        BddLogger.given("the method getSkillProgresses");
        PageCriteria criteria = new PageCriteria(1, 8);
        Skill skill = SkillFixture.create().withTitle("Java Programming").toModel();
        SkillProgress skillProgress = buildSkillProgress(skill);
        PagedResult<SkillProgress> repositoryResult =
            new PagedResult<>(List.of(skillProgress), new PageInfo(1, 8, 1));

        BddLogger.when("calling the method with a given student and isValorized=true");
        when(skillProgressRepository.findAllByStudent(
                student, criteria, true, new SortCriteria(ESortField.NAME, ESortOrder.ASC)))
            .thenReturn(repositoryResult);
        stubSkillResolution(skill);

        PagedResult<SkillProgressData> result =
            skillProgressService.getSkillProgresses(criteria, true);

        BddLogger.then("it should delegate the isValorized filter to the repository");
        assertThat(result.pageInfo()).isSameAs(repositoryResult.pageInfo());
        assertThat(result.content())
            .containsExactly(
                new SkillProgressData(
                    skillProgress, skill, new SkillProgressAssociationCount(0, 0)));
        verify(skillProgressRepository)
            .findAllByStudent(
                student, criteria, true, new SortCriteria(ESortField.NAME, ESortOrder.ASC));
      }

      @Test
      void getSkillProgressDetails_shouldReturnSkillProgressDetails() {
        BddLogger.given("the method getSkillProgressDetails");
        SkillProgress skillProgress = SkillProgressFixture.create().withStudent(student).toModel();
        Skill skill = skillProgress.getSkill();
        List<SkillPathSegment> path =
            List.of(
                new SkillPathSegment("Domain", ESkillKind.DOMAIN),
                new SkillPathSegment("Issue", ESkillKind.ISSUE));

        BddLogger.when("calling the method with a given student and skillProgressId");
        when(skillProgressRepository.findById(any(), any())).thenReturn(Optional.of(skillProgress));
        stubSkillResolution(skill);
        when(skillService.getDetailedPaths(List.of(skill))).thenReturn(Map.of(skill.getId(), path));

        SkillProgressDetails skillProgressDetails =
            skillProgressService.getSkillProgressDetails(skillProgress.getId());

        BddLogger.then("it should return the expected skill progress details");
        assertEquals(skillProgressDetails.skillProgress(), skillProgress);
        assertEquals(skill, skillProgressDetails.skill());
        assertEquals(path, skillProgressDetails.path());
      }

      @Test
      void getSkillProgressDetails_withAList_shouldReturnSkillProgressDetailsForEach() {
        BddLogger.given("the method getSkillProgressDetails(List<SkillProgress>)");
        SkillProgress firstSkill = SkillProgressFixture.create().withStudent(student).toModel();
        SkillProgress secondSkill = SkillProgressFixture.create().withStudent(student).toModel();

        List<UUID> skillIds =
            List.of(firstSkill.getSkill().getId(), secondSkill.getSkill().getId());
        when(skillService.getAllWithAncestorsById(skillIds))
            .thenReturn(List.of(firstSkill.getSkill(), secondSkill.getSkill()));
        when(skillService.getDetailedPaths(anyList()))
            .thenReturn(
                Map.of(
                    firstSkill.getSkill().getId(),
                    List.of(new SkillPathSegment("Domain", ESkillKind.DOMAIN)),
                    secondSkill.getSkill().getId(),
                    List.of(new SkillPathSegment("Domain", ESkillKind.DOMAIN))));

        BddLogger.when("calling the method with a list of skill progresses");
        List<SkillProgressDetails> result =
            skillProgressService.getSkillProgressDetails(List.of(firstSkill, secondSkill));

        BddLogger.then("it should return one enriched details entry per skill progress");
        assertThat(result)
            .extracting(SkillProgressDetails::skillProgress)
            .containsExactly(firstSkill, secondSkill);
        assertThat(result)
            .allSatisfy(
                details ->
                    assertThat(details.path())
                        .containsExactly(new SkillPathSegment("Domain", ESkillKind.DOMAIN)));
      }

      @Test
      void getSkillProgressDetails_shouldThrowSkillProgressNotFoundException() {
        BddLogger.given("the method getSkillProgressDetails");
        SkillProgress skillProgress = SkillProgressFixture.create().withStudent(student).toModel();

        BddLogger.when("calling the method with a given student and bad skillProgressId");
        assertThrows(
            SkillProgressNotFoundException.class,
            () -> skillProgressService.getSkillProgressDetails(skillProgress.getId()));
      }

      @Test
      void getSkillProgressDetails_shouldThrowUserNotAuthorizedException() {
        BddLogger.given("the method getSkillProgressDetails");
        Student anotherStudent = StudentFixture.create().toModel();
        SkillProgress skillProgress =
            SkillProgressFixture.create().withStudent(anotherStudent).toModel();

        BddLogger.when("calling the method with another given student and skillProgressId");
        when(skillProgressRepository.findById(any(), any())).thenReturn(Optional.of(skillProgress));
        assertThrows(
            UserNotAuthorizedException.class,
            () -> skillProgressService.getSkillProgressDetails(skillProgress.getId()));
      }
    }

    @Nested
    class WhenCreatingSkillProgress {
      @Test
      void createSkillProgress_shouldSaveWhenSkillIsAvailableAndNotDuplicate() {
        BddLogger.given("the method createSkillProgress");
        ESkillLevel level = ESkillLevel.BEGINNER;
        String reflection = "Reflection for skill progress test";
        Skill skill = SkillFixture.create().toModel();

        BddLogger.when("calling the method with an available and not duplicate skill");
        when(skillService.getWithAncestorsById(skill.getId())).thenReturn(skill);
        when(skillProgressRepository.skillProgressAlreadyExists(any())).thenReturn(false);
        when(skillProgressRepository.save(any(SkillProgress.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        skillProgressService.createSkillProgress(skill.getId(), level, reflection);

        BddLogger.then("it should save the skill progress");
        verify(skillService).getWithAncestorsById(skill.getId());
        verify(skillProgressRepository).skillProgressAlreadyExists(any());
        verify(skillProgressRepository).save(any(SkillProgress.class));
      }

      @Test
      void createSkillProgress_shouldThrowDuplicateWhenAlreadyExists() {
        BddLogger.given("the method createSkillProgress");
        ESkillLevel level = ESkillLevel.BEGINNER;
        String reflection = "Reflection for skill progress test";
        Skill skill = SkillFixture.create().toModel();

        BddLogger.when("calling the method with a duplicate skill");
        when(skillService.getWithAncestorsById(skill.getId())).thenReturn(skill);
        when(skillProgressRepository.skillProgressAlreadyExists(any())).thenReturn(true);

        BddLogger.then(
            "it should throw a DuplicateSkillProgressException and not save the progress");

        assertThrows(
            DuplicateSkillProgressException.class,
            () -> skillProgressService.createSkillProgress(skill.getId(), level, reflection));

        verify(skillProgressRepository).skillProgressAlreadyExists(any());
        verify(skillProgressRepository, never()).save(any());
      }

      @Test
      void createSkillProgress_shouldRethrowWhenSkillNotFound() {
        BddLogger.given("the method createSkillProgress");
        UUID skillId = randomUUID();
        ESkillLevel level = ESkillLevel.BEGINNER;
        String reflection = "Reflection for skill progress test";

        BddLogger.when("calling the method with an unknown skill");
        when(skillService.getWithAncestorsById(skillId)).thenThrow(new SkillNotFoundException());

        BddLogger.then("it should throw a SkillNotFoundException");

        assertThrows(
            SkillNotFoundException.class,
            () -> skillProgressService.createSkillProgress(skillId, level, reflection));

        verifyNoInteractions(skillProgressRepository);
      }

      @Test
      void createSkillProgress_shouldThrowWhenTheSkillIsNotAssessable() {
        BddLogger.given("the method createSkillProgress");
        ESkillLevel level = ESkillLevel.BEGINNER;
        Skill categorySkill = SkillFixture.create().withAssessable(false).toModel();

        BddLogger.when("calling the method with a skill that only categorizes its children");
        when(skillService.getWithAncestorsById(categorySkill.getId())).thenReturn(categorySkill);

        BddLogger.then("it should throw a SkillNotAssessableException");
        assertThrows(
            SkillNotAssessableException.class,
            () -> skillProgressService.createSkillProgress(categorySkill.getId(), level, null));

        verifyNoInteractions(skillProgressRepository);
      }

      @Test
      void createSkillProgress_shouldThrowWhenTheReferentialBelongsToAnInstitution() {
        BddLogger.given("the method createSkillProgress");
        ESkillLevel level = ESkillLevel.BEGINNER;
        Skill institutionSkill =
            SkillFixture.create()
                .withReferential(
                    SkillReferentialFixture.create()
                        .withType(ESkillReferentialType.APC)
                        .withInstitutionId(randomUUID())
                        .toModel())
                .toModel();

        BddLogger.when("calling the method with a skill of an institution referential");
        when(skillService.getWithAncestorsById(institutionSkill.getId()))
            .thenReturn(institutionSkill);

        BddLogger.then("it should throw a SkillNotSelfDeclarableException");
        assertThrows(
            SkillNotSelfDeclarableException.class,
            () -> skillProgressService.createSkillProgress(institutionSkill.getId(), level, null));

        verifyNoInteractions(skillProgressRepository);
      }
    }

    @Nested
    class WhenUpdatingSkillProgress {
      @Test
      void updateSkillProgress_shouldSaveLevelAndReflection() {
        BddLogger.given("the method updateSkillProgress");
        ESkillLevel level = ESkillLevel.ADVANCED;
        String reflection = "Reflection for skill progress test";
        boolean valorized = true;
        SkillProgress skillProgress =
            SkillProgressFixture.create()
                .withStudent(student)
                .withLevel(ESkillLevel.BEGINNER)
                .withReflection(null)
                .withValorized(false)
                .toModel();

        BddLogger.when(
            "calling the method with a given student, skillProgressId, level, reflection"
                + " and valorized");
        when(skillProgressRepository.findById(skillProgress.getId()))
            .thenReturn(Optional.of(skillProgress));
        when(skillProgressRepository.save(any(SkillProgress.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));
        when(skillService.getWithAncestorsById(skillProgress.getSkill().getId()))
            .thenReturn(skillProgress.getSkill());

        skillProgressService.updateSkillProgress(
            skillProgress.getId(), level, reflection, valorized);

        BddLogger.then("it should save level, reflection and valorized in skill progress");
        ArgumentCaptor<SkillProgress> captor = ArgumentCaptor.forClass(SkillProgress.class);
        verify(skillProgressRepository).save(captor.capture());

        SkillProgress savedSkillProgress = captor.getValue();
        assertEquals(skillProgress.getId(), savedSkillProgress.getId());
        assertEquals(skillProgress.getStudent(), savedSkillProgress.getStudent());
        assertEquals(skillProgress.getSkill(), savedSkillProgress.getSkill());
        assertEquals(level, savedSkillProgress.getLevel());
        assertEquals(reflection, savedSkillProgress.getReflection());
        assertEquals(valorized, savedSkillProgress.isValorized());
      }

      @Test
      void updateSkillProgress_shouldThrowFieldValidationException() {
        BddLogger.given("the method getSkillProgressDetails");
        ESkillLevel level = ESkillLevel.BEGINNER;
        String reflection =
            random
                .ints(RICH_DESCRIPTION_LENGTH + 1, 0, CHARSET.length())
                .mapToObj(CHARSET::charAt)
                .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
                .toString();
        SkillProgress skillProgress = SkillProgressFixture.create().withStudent(student).toModel();

        BddLogger.when(
            "calling the method with a given student, skillProgressId, level and too long"
                + " reflection");
        assertThrows(
            FieldValidationException.class,
            () ->
                skillProgressService.updateSkillProgress(
                    skillProgress.getId(), level, reflection, false));
      }

      @Test
      void updateSkillProgress_shouldThrowSkillProgressNotFoundException() {
        BddLogger.given("the method getSkillProgressDetails");
        ESkillLevel level = ESkillLevel.BEGINNER;
        String reflection = "Reflection for skill progress test";
        SkillProgress skillProgress = SkillProgressFixture.create().withStudent(student).toModel();

        BddLogger.when("calling the method with a given student and bad skillProgressId");
        assertThrows(
            SkillProgressNotFoundException.class,
            () ->
                skillProgressService.updateSkillProgress(
                    skillProgress.getId(), level, reflection, false));
      }

      @Test
      void updateSkillProgress_shouldThrowUserNotAuthorizedException() {
        BddLogger.given("the method getSkillProgressDetails");
        Student anotherStudent = StudentFixture.create().toModel();
        ESkillLevel level = ESkillLevel.BEGINNER;
        String reflection = "Reflection for skill progress test";
        SkillProgress skillProgress =
            SkillProgressFixture.create().withStudent(anotherStudent).toModel();

        BddLogger.when("calling the method with another given student and level, reflection");
        when(skillProgressRepository.findById(skillProgress.getId()))
            .thenReturn(Optional.of(skillProgress));
        assertThrows(
            UserNotAuthorizedException.class,
            () ->
                skillProgressService.updateSkillProgress(
                    skillProgress.getId(), level, reflection, false));
      }
    }

    @Nested
    class WhenDeletingSkillProgresses {

      @Test
      void deleteSkillProgresses_shouldDeleteSkillProgresses() {
        BddLogger.given("the method deleteSkillProgresses");

        Student studentLoggedIn = student;
        SkillProgress skillProgress1 = SkillProgressFixture.create().withStudent(student).toModel();
        SkillProgress skillProgress2 = SkillProgressFixture.create().withStudent(student).toModel();

        BddLogger.when("calling the method with a given student");

        when(loggedInUserService.getLoggedInStudent()).thenReturn(studentLoggedIn);
        when(skillProgressRepository.findAllById(
                List.of(skillProgress1.getId(), skillProgress2.getId())))
            .thenReturn(List.of(skillProgress1, skillProgress2));
        doNothing()
            .when(associationService)
            .deleteAllOf(
                List.of(skillProgress1.getId(), skillProgress2.getId()), SkillProgress.class);

        skillProgressService.deleteSkillProgresses(
            List.of(skillProgress1.getId(), skillProgress2.getId()));

        BddLogger.then("it should delete skill progresses and delete traces");

        verify(skillProgressRepository)
            .findAllById(List.of(skillProgress1.getId(), skillProgress2.getId()));
        verify(skillProgressRepository)
            .removeAllFromDatabase(List.of(skillProgress1, skillProgress2));
        verify(associationService)
            .deleteAllOf(
                List.of(skillProgress1.getId(), skillProgress2.getId()), SkillProgress.class);
      }

      @Test
      void deleteSkillProgresses_shouldThrowSkillProgressNotFoundException() {
        BddLogger.given("the method deleteSkillProgresses");

        Student studentLoggedIn = student;
        UUID randomUUID = UUID.randomUUID();
        SkillProgress skillProgress1 = SkillProgressFixture.create().withStudent(student).toModel();

        BddLogger.when("calling the method with non existing skill progress ids");

        when(loggedInUserService.getLoggedInStudent()).thenReturn(studentLoggedIn);
        when(skillProgressRepository.findAllById(List.of(skillProgress1.getId(), randomUUID)))
            .thenReturn(List.of(skillProgress1));

        BddLogger.then("it should throw skill progress not found exception");

        assertThrows(
            SkillProgressNotFoundException.class,
            () ->
                skillProgressService.deleteSkillProgresses(
                    List.of(skillProgress1.getId(), randomUUID)));
      }

      @Test
      void deleteSkillProgresses_shouldThrowUserNotAuthorizedException() {
        BddLogger.given("the method deleteSkillProgresses");

        Student studentLoggedIn = student;
        Student anotherStudent = StudentFixture.create().toModel();
        SkillProgress skillProgress1 =
            SkillProgressFixture.create().withStudent(anotherStudent).toModel();
        SkillProgress skillProgress2 =
            SkillProgressFixture.create().withStudent(anotherStudent).toModel();

        BddLogger.when("calling the method with an unauthorized student");

        when(loggedInUserService.getLoggedInStudent()).thenReturn(studentLoggedIn);
        when(skillProgressRepository.findAllById(
                List.of(skillProgress1.getId(), skillProgress2.getId())))
            .thenReturn(List.of(skillProgress1, skillProgress2));

        BddLogger.then("it should throw user not authorized exception");

        assertThrows(
            UserNotAuthorizedException.class,
            () ->
                skillProgressService.deleteSkillProgresses(
                    List.of(skillProgress1.getId(), skillProgress2.getId())));
      }
    }
  }
}
