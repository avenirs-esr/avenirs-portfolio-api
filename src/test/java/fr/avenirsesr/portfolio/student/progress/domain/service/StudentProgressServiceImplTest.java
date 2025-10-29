package fr.avenirsesr.portfolio.student.progress.domain.service;

import static fr.avenirsesr.portfolio.student.progress.domain.service.StudentProgressServiceImpl.DESCRIPTION_LENGTH_MAX;
import static java.util.UUID.randomUUID;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.mockStatic;

import fr.avenirsesr.portfolio.additionalskill.domain.exception.AdditionalSkillNotFoundException;
import fr.avenirsesr.portfolio.additionalskill.domain.exception.DuplicateAdditionalSkillException;
import fr.avenirsesr.portfolio.additionalskill.domain.exception.InvalidDescriptionException;
import fr.avenirsesr.portfolio.additionalskill.domain.model.AdditionalSkill;
import fr.avenirsesr.portfolio.additionalskill.domain.model.enums.EAdditionalSkillLevel;
import fr.avenirsesr.portfolio.additionalskill.domain.model.enums.EAdditionalSkillType;
import fr.avenirsesr.portfolio.additionalskill.domain.port.output.repository.AdditionalSkillRepository;
import fr.avenirsesr.portfolio.additionalskill.infrastructure.fixture.AdditionalSkillProgressFixture;
import fr.avenirsesr.portfolio.common.data.domain.model.PageCriteria;
import fr.avenirsesr.portfolio.common.data.domain.model.PagedResult;
import fr.avenirsesr.portfolio.common.data.domain.model.SortCriteria;
import fr.avenirsesr.portfolio.common.data.domain.model.enums.ESortField;
import fr.avenirsesr.portfolio.common.data.domain.model.enums.ESortOrder;
import fr.avenirsesr.portfolio.common.language.domain.model.enums.ELanguage;
import fr.avenirsesr.portfolio.common.security.domain.exception.UserNotAuthorizedException;
import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import fr.avenirsesr.portfolio.common.web.infrastructure.context.RequestContext;
import fr.avenirsesr.portfolio.common.web.infrastructure.context.RequestData;
import fr.avenirsesr.portfolio.program.domain.model.Skill;
import fr.avenirsesr.portfolio.program.domain.model.TrainingPath;
import fr.avenirsesr.portfolio.program.domain.model.enums.ESkillLevelStatus;
import fr.avenirsesr.portfolio.program.infrastructure.fixture.*;
import fr.avenirsesr.portfolio.shared.domain.model.enums.EPortfolioType;
import fr.avenirsesr.portfolio.student.progress.domain.data.AdditionalSkillProgressDetails;
import fr.avenirsesr.portfolio.student.progress.domain.data.SkillLevelProgressWithTraceCountData;
import fr.avenirsesr.portfolio.student.progress.domain.data.SkillProgressData;
import fr.avenirsesr.portfolio.student.progress.domain.exception.AdditionalSkillProgressNotFoundException;
import fr.avenirsesr.portfolio.student.progress.domain.model.*;
import fr.avenirsesr.portfolio.student.progress.domain.port.output.repository.AdditionalSkillProgressRepository;
import fr.avenirsesr.portfolio.student.progress.domain.port.output.repository.StudentProgressRepository;
import fr.avenirsesr.portfolio.student.progress.infrastructure.fixture.*;
import fr.avenirsesr.portfolio.trace.domain.model.Trace;
import fr.avenirsesr.portfolio.trace.domain.port.input.TraceService;
import fr.avenirsesr.portfolio.trace.domain.port.output.repository.TraceRepository;
import fr.avenirsesr.portfolio.trace.infrastructure.fixture.TraceFixture;
import fr.avenirsesr.portfolio.user.domain.model.Student;
import fr.avenirsesr.portfolio.user.domain.port.output.repository.StudentRepository;
import fr.avenirsesr.portfolio.user.infrastructure.fixture.StudentFixture;
import java.time.LocalDate;
import java.time.Period;
import java.util.*;
import java.util.random.RandomGenerator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
public class StudentProgressServiceImplTest {
  @Mock private StudentRepository studentRepository;
  @Mock private StudentProgressRepository studentProgressRepository;
  @Mock private TraceService traceService;
  @Mock private TraceRepository traceRepository;
  @Mock private AdditionalSkillRepository additionalSkillRepository;
  @Mock private AdditionalSkillProgressRepository additionalSkillProgressRepository;
  @InjectMocks private StudentProgressServiceImpl studentProgressService;
  private static final String CHARSET =
      "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
  private static final RandomGenerator random = RandomGenerator.getDefault();

  private Student student;
  private MockedStatic<RequestContext> mockedRequestContext;

  @BeforeEach
  void setUp() {
    student = StudentFixture.create().toModel();
    mockedRequestContext = mockStatic(RequestContext.class);
    mockedRequestContext
        .when(RequestContext::get)
        .thenReturn(new RequestData(Optional.ofNullable(student.getUser()), ELanguage.FRENCH));

    when(studentRepository.findById(eq(student.getId()))).thenReturn(Optional.of(student));
  }

  @AfterEach
  void tearDown() {
    mockedRequestContext.close();
  }

  @Nested
  class GivenAStudentProgressService {
    private StudentProgress progress;
    private List<SkillLevelProgress> skillLevelsProgress;
    private LocalDate now;
    private SortCriteria sortCriteria;
    private PageCriteria pageCriteria;

    @BeforeEach
    void setupGiven() {
      BddLogger.given("a StudentProgressServce");
    }

    @Nested
    class AndAStudentFollowingAtLeastOneProgramWithLearningMethod {
      private TrainingPath progressAPC;
      private StudentProgress progressAPCModel;

      @BeforeEach
      void setupAnd() {
        BddLogger.and("a student following at least one program with learrning method");
        progressAPC = TrainingPathFixture.createWithAPC().toModel();
        progressAPCModel =
            StudentProgressFixture.create()
                .withTrainingPath(progressAPC)
                .withStudent(student)
                .toModel();

        when(studentProgressRepository.findAllAPCByStudent(student))
            .thenReturn(List.of(progressAPCModel));
      }

      @Nested
      class WhenCheckingIfTheStudentIsFollowingAnAPCProgram {
        private boolean result;

        @BeforeEach
        void setupWhen() {
          BddLogger.when("checking if the student is following an APC program");
          mockedRequestContext
              .when(RequestContext::get)
              .thenReturn(
                  new RequestData(Optional.ofNullable(student.getUser()), ELanguage.FRENCH));
          result = studentProgressService.isStudentFollowingAPCProgram();
        }

        @Test
        void thenItShouldReturnTrue() {
          BddLogger.then("it should return true");
          assertTrue(result);
          verify(studentProgressRepository).findAllAPCByStudent(student);
        }
      }
    }

    @Nested
    class AndAStudentNotFollowingAnyProgramWithLearningMethod {
      @BeforeEach
      void setupAnd() {
        BddLogger.and("a student not following any program with learning method");
        when(studentProgressRepository.findAllAPCByStudent(student)).thenReturn(List.of());
      }

      @Nested
      class WhenCheckingIfTheStudentIsFollowingAnAPCProgram {
        private boolean result;

        @BeforeEach
        void setupWhen() {
          BddLogger.when("checking if the student is following an APC program");
          mockedRequestContext
              .when(RequestContext::get)
              .thenReturn(
                  new RequestData(Optional.ofNullable(student.getUser()), ELanguage.FRENCH));
          result = studentProgressService.isStudentFollowingAPCProgram();
        }

        @Test
        void thenItShouldReturnFalse() {
          BddLogger.then("it should return false");
          assertFalse(result);
          verify(studentProgressRepository).findAllAPCByStudent(student);
        }
      }
    }

    @Nested
    class AndAStudentWithALargeAmountOfSkillLevels {
      private StudentProgress progress2;

      @BeforeEach
      void setupAnd() {
        BddLogger.and("a student with a large amount of skill levels");
        skillLevelsProgress = new ArrayList<SkillLevelProgress>();
        for (int i = 0; i < 8; i++) {
          skillLevelsProgress.add(
              SkillLevelProgressFixture.create(student)
                  .withStatus(ESkillLevelStatus.TO_BE_EVALUATED)
                  .toModel());
        }

        progress =
            StudentProgressFixture.create()
                .withStudent(student)
                .withSkillLevels(
                    List.of(
                        skillLevelsProgress.get(0),
                        skillLevelsProgress.get(1),
                        skillLevelsProgress.get(2),
                        skillLevelsProgress.get(3)))
                .toModel();

        progress2 =
            StudentProgressFixture.create()
                .withStudent(student)
                .withSkillLevels(
                    List.of(
                        skillLevelsProgress.get(4),
                        skillLevelsProgress.get(5),
                        skillLevelsProgress.get(6),
                        skillLevelsProgress.get(7)))
                .toModel();

        when(studentProgressRepository.findAllByStudent(eq(student)))
            .thenReturn(List.of(progress, progress2));
        when(traceRepository.linkedWith(any(SkillLevelProgress.class))).thenReturn(List.of());
      }

      @Nested
      class WhenGettingTheStudentProgressOverview {
        private Map<StudentProgress, List<SkillLevelProgressWithTraceCountData>> result;

        @BeforeEach
        void setupWhen() {
          BddLogger.when("getting the studen progress overview");
          mockedRequestContext
              .when(RequestContext::get)
              .thenReturn(
                  new RequestData(Optional.ofNullable(student.getUser()), ELanguage.FRENCH));
          result = studentProgressService.getStudentProgressOverview();
        }

        @Test
        void thenItShouldReturnSkillsOverviewWithLimitedSkills() {
          BddLogger.then(
              "it should return student progresses with limited number of skill level progresses");
          assertEquals(2, result.size(), "Should contain 2 StudentProgress");
          assertTrue(result.containsKey(progress));
          assertTrue(result.containsKey(progress2));

          int maxPerProgress = 3;
          assertTrue(result.get(progress).size() <= maxPerProgress);
          assertTrue(result.get(progress2).size() <= maxPerProgress);

          verify(studentProgressRepository).findAllByStudent(eq(student));
        }
      }

      @Nested
      class WhenGettingTheStudentProgressView {
        private Map<StudentProgress, List<SkillLevelProgressWithTraceCountData>> result;

        @BeforeEach
        void setupWhen() {
          BddLogger.when("getting the student progress view");
          mockedRequestContext
              .when(RequestContext::get)
              .thenReturn(
                  new RequestData(Optional.ofNullable(student.getUser()), ELanguage.FRENCH));
          result =
              studentProgressService.getStudentProgressView(
                  new SortCriteria(ESortField.DATE, ESortOrder.ASC));
        }

        @Test
        void thenItShouldReturnSkillsViewWithoutLimitedSkills() {
          BddLogger.then("it should return all skill levels progresses");
          assertEquals(2, result.size(), "Should contain 2 StudentProgress");
          assertEquals(
              8,
              result.keySet().stream()
                  .flatMap(studentProgress -> studentProgress.getAllSkillLevels().stream())
                  .toList()
                  .size(),
              "Should contain 8 skillLevelsProgresses");
          verify(studentProgressRepository).findAllByStudent(eq(student));
        }
      }
    }

    @Nested
    class AndAStudentWithoutStudentProgress {
      @BeforeEach
      void setupAnd() {
        BddLogger.and("a student without student progress");
        when(studentProgressRepository.findAllByStudent(eq(student))).thenReturn(List.of());
      }

      @Nested
      class WhenGettingTheStudentProgressOverview {
        private Map<StudentProgress, List<SkillLevelProgressWithTraceCountData>> result;

        @BeforeEach
        void setupWhen() {
          BddLogger.when("getting the student progress overview");
          mockedRequestContext
              .when(RequestContext::get)
              .thenReturn(
                  new RequestData(Optional.ofNullable(student.getUser()), ELanguage.FRENCH));
          result = studentProgressService.getStudentProgressOverview();
        }

        @Test
        void thenItShouldReturnEmptySkillsOverviewWhenNoProgress() {
          BddLogger.then("it should return empty student progress");
          assertTrue(result.isEmpty(), "StudentProgress should be empty");
          verify(studentProgressRepository).findAllByStudent(eq(student));
        }
      }

      @Nested
      class WhenGettingTheSkillsLifePojectView {
        private PagedResult<SkillProgressData> result;

        @BeforeEach
        void setupWhen() {
          BddLogger.when("getting the skills life project view");
          sortCriteria = new SortCriteria(ESortField.DATE, ESortOrder.ASC);
          pageCriteria = new PageCriteria(0, 5);
          mockedRequestContext
              .when(RequestContext::get)
              .thenReturn(
                  new RequestData(Optional.ofNullable(student.getUser()), ELanguage.FRENCH));
          result = studentProgressService.getAllTimeSkillsView(sortCriteria, pageCriteria);
        }

        @Test
        void thenItShouldReturnEmpty() {
          BddLogger.then("it should return an empty result");
          assertTrue(result.content().isEmpty(), "Results should be empty");
          assertEquals(0, result.pageInfo().totalElements(), "Total elements should be 0");
          verify(studentProgressRepository).findAllByStudent(eq(student));
        }
      }
    }

    @Nested
    class AndAStudentWithCurrentPastAndFutureProgresses {
      private StudentProgress pastProgress;
      private StudentProgress currentProgress;
      private StudentProgress futureProgress;

      @BeforeEach
      void setupAnd() {
        BddLogger.and("a student with current, past and future progresses");
        skillLevelsProgress = new ArrayList<SkillLevelProgress>();
        for (int i = 0; i < 6; i++) {
          skillLevelsProgress.add(
              SkillLevelProgressFixture.create(student)
                  .withStatus(ESkillLevelStatus.TO_BE_EVALUATED)
                  .toModel());
        }
        now = LocalDate.now();

        // Progress "current" : now between startDate and endDate
        currentProgress =
            StudentProgressFixture.create()
                .withStudent(student)
                .withSkillLevels(List.of(skillLevelsProgress.get(0), skillLevelsProgress.get(1)))
                .withStartDate(now.minusDays(5))
                .toModel();

        // Progress "past" : endDate before now
        pastProgress =
            StudentProgressFixture.create()
                .withStudent(student)
                .withSkillLevels(List.of(skillLevelsProgress.get(2), skillLevelsProgress.get(3)))
                .withStartDate(now.minusYears(10))
                .toModel();

        // Progress "future" : startDate after now
        futureProgress =
            StudentProgressFixture.create()
                .withStudent(student)
                .withSkillLevels(List.of(skillLevelsProgress.get(4), skillLevelsProgress.get(5)))
                .withStartDate(now.plusYears(1))
                .toModel();

        // Mock repository to return all progress
        when(studentProgressRepository.findAllByStudent(eq(student)))
            .thenReturn(List.of(currentProgress, pastProgress, futureProgress));
        when(traceRepository.linkedWith(any(SkillLevelProgress.class))).thenReturn(List.of());
      }

      @Nested
      class WhenGettingTheStudentProgressOverview {
        private Map<StudentProgress, List<SkillLevelProgressWithTraceCountData>> result;

        @BeforeEach
        void setupWhen() {
          BddLogger.when("getting the student progress overview");
          mockedRequestContext
              .when(RequestContext::get)
              .thenReturn(
                  new RequestData(Optional.ofNullable(student.getUser()), ELanguage.FRENCH));
          result = studentProgressService.getStudentProgressOverview();
        }

        @Test
        void thenItShouldReturnOnlyCurrentStudentProgressOnOverview() {
          BddLogger.then("it should only return current progress");
          assertEquals(1, result.size(), "Only current progress should be returned");
          assertTrue(result.containsKey(currentProgress), "Current progress should be present");
          assertFalse(result.containsKey(pastProgress), "Past progress should be filtered out");
          assertFalse(result.containsKey(futureProgress), "Future progress should be filtered out");

          int maxPerProgress = 3;
          assertTrue(result.get(currentProgress).size() <= maxPerProgress);

          verify(studentProgressRepository).findAllByStudent(eq(student));
        }
      }

      @Nested
      class WhenGettingTheStudentProgressView {
        private Map<StudentProgress, List<SkillLevelProgressWithTraceCountData>> result;

        @BeforeEach
        void setupWhen() {
          BddLogger.when("getting the student pogress view");
          mockedRequestContext
              .when(RequestContext::get)
              .thenReturn(
                  new RequestData(Optional.ofNullable(student.getUser()), ELanguage.FRENCH));
          result =
              studentProgressService.getStudentProgressView(
                  new SortCriteria(ESortField.DATE, ESortOrder.ASC));
        }

        @Test
        void thenItShouldOnlyReturnCurrentStudentProgress() {
          BddLogger.then("it should only return current progress");
          assertEquals(1, result.size(), "Only current progress should be returned");
          assertTrue(result.containsKey(currentProgress), "Current progress should be present");
          assertFalse(result.containsKey(pastProgress), "Past progress should be filtered out");
          assertFalse(result.containsKey(futureProgress), "Future progress should be filtered out");

          verify(studentProgressRepository).findAllByStudent(eq(student));
        }
      }

      @Nested
      class WhenGettingTheSkillsLifePojectView {
        private PagedResult<SkillProgressData> result;

        @BeforeEach
        void setupWhen() {
          BddLogger.when("getting the skills life project view");
          sortCriteria = new SortCriteria(ESortField.DATE, ESortOrder.ASC);
          pageCriteria = new PageCriteria(0, 10);
          mockedRequestContext
              .when(RequestContext::get)
              .thenReturn(
                  new RequestData(Optional.ofNullable(student.getUser()), ELanguage.FRENCH));
          result = studentProgressService.getAllTimeSkillsView(sortCriteria, pageCriteria);
        }

        @Test
        void thenItShouldFilterOutFutureStudentProgress() {
          BddLogger.then("it should only return past and current progresses");
          // 2 skills in past + 2 skills in current
          assertEquals(
              4, result.content().size(), "Only past and current progresses should be returned");
          assertTrue(
              result.content().stream()
                  .allMatch(
                      sp ->
                          sp.studentProgress().equals(pastProgress)
                              || sp.studentProgress().equals(currentProgress)));
          verify(studentProgressRepository).findAllByStudent(eq(student));
        }
      }
    }

    @Nested
    class AndASortingCriteria {
      private SortCriteria customSort;

      @BeforeEach
      void setupAnd() {
        BddLogger.and("a sorting criteria");
        customSort = new SortCriteria(ESortField.DATE, ESortOrder.DESC);
        progress = StudentProgressFixture.create().withStudent(student).toModel();

        when(studentProgressRepository.findAllByStudent(eq(student))).thenReturn(List.of(progress));
      }

      @Nested
      class WhenGettingTheStudentProgressView {
        private Map<StudentProgress, List<SkillLevelProgressWithTraceCountData>> result;

        @BeforeEach
        void setupWhen() {
          BddLogger.when("getting the student progress view");
          mockedRequestContext
              .when(RequestContext::get)
              .thenReturn(
                  new RequestData(Optional.ofNullable(student.getUser()), ELanguage.FRENCH));
          result = studentProgressService.getStudentProgressView(customSort);
        }

        @Test
        void thenItShouldReturnSkillsViewWithCustomSortCriteria() {
          BddLogger.then("it should return skills view sorted by the criteria");
          assertEquals(1, result.size());
          assertEquals(progress, result.keySet().stream().toList().getFirst());
          verify(studentProgressRepository).findAllByStudent(student);
        }
      }
    }

    @Nested
    class AndAStudentWithMultipleProgressesAndSkills {
      private SkillLevelProgress skillAAOld, skillABOld, skillBANew, skillBBNew;
      private StudentProgress progressAOld, progressBNew;

      @BeforeEach
      void setupAnd() {
        BddLogger.and("a student with multiple progresses and skills");
        now = LocalDate.now();

        skillAAOld =
            SkillLevelProgressFixture.create(student)
                .withSkillLevel(
                    SkillLevelFixture.create()
                        .withSkill(SkillFixture.create().withName("Skill A").toModel())
                        .toModel())
                .withStatus(ESkillLevelStatus.TO_BE_EVALUATED)
                .withStartDate(now.minusMonths(2))
                .toModel();
        skillABOld =
            SkillLevelProgressFixture.create(student)
                .withSkillLevel(
                    SkillLevelFixture.create()
                        .withSkill(SkillFixture.create().withName("Skill B").toModel())
                        .toModel())
                .withStatus(ESkillLevelStatus.TO_BE_EVALUATED)
                .withStartDate(now.minusMonths(1))
                .toModel();
        progressAOld =
            StudentProgressFixture.create()
                .withStudent(student)
                .withTrainingPath(
                    TrainingPathFixture.create()
                        .withProgram(ProgramFixture.create().withName("Program A").toModel())
                        .toModel())
                .withStartDate(now.minusMonths(2))
                .withSkillLevels(List.of(skillAAOld, skillABOld))
                .toModel();

        skillBANew =
            SkillLevelProgressFixture.create(student)
                .withSkillLevel(
                    SkillLevelFixture.create()
                        .withSkill(SkillFixture.create().withName("Skill A").toModel())
                        .toModel())
                .withStatus(ESkillLevelStatus.TO_BE_EVALUATED)
                .withStartDate(now.minusWeeks(2))
                .toModel();
        skillBBNew =
            SkillLevelProgressFixture.create(student)
                .withSkillLevel(
                    SkillLevelFixture.create()
                        .withSkill(SkillFixture.create().withName("Skill B").toModel())
                        .toModel())
                .withStatus(ESkillLevelStatus.TO_BE_EVALUATED)
                .withStartDate(now.minusWeeks(1))
                .toModel();
        progressBNew =
            StudentProgressFixture.create()
                .withStudent(student)
                .withTrainingPath(
                    TrainingPathFixture.create()
                        .withProgram(ProgramFixture.create().withName("Program B").toModel())
                        .toModel())
                .withStartDate(now.minusWeeks(2))
                .withSkillLevels(List.of(skillBBNew, skillBANew))
                .toModel();

        when(studentProgressRepository.findAllByStudent(eq(student)))
            .thenReturn(List.of(progressBNew, progressAOld));
        when(traceRepository.linkedWith(any(SkillLevelProgress.class))).thenReturn(List.of());
      }

      @Nested
      class WhenGettingTheStudentProgressView {
        private ESortField sortField;
        private List<StudentProgress> orderedKeys;
        private List<SkillLevelProgressWithTraceCountData> skillsOfFirst, skillsOfSecond;

        @BeforeEach
        void setupWhen() {
          BddLogger.when("getting the student progress view");
        }

        @Nested
        class AndASortByNameCriteriaIsPassed {
          private Map<StudentProgress, List<SkillLevelProgressWithTraceCountData>> result;

          @BeforeEach
          void setupAnd() {
            BddLogger.and("a sort by name criteria is passed");
            sortField = ESortField.NAME;
          }

          @ParameterizedTest
          @EnumSource(ESortOrder.class)
          void thenItShouldSortStudentProgressAndSkillsByName(ESortOrder order) {
            BddLogger.then(
                "it should return all student progresses and all skill levels progresses ordered by"
                    + " name");
            mockedRequestContext
                .when(RequestContext::get)
                .thenReturn(
                    new RequestData(Optional.ofNullable(student.getUser()), ELanguage.FRENCH));
            result =
                studentProgressService.getStudentProgressView(new SortCriteria(sortField, order));
            orderedKeys = new ArrayList<>(result.keySet());
            if (order == ESortOrder.ASC) {
              assertEquals(progressAOld, orderedKeys.get(0));
              assertEquals(progressBNew, orderedKeys.get(1));
            } else {
              assertEquals(progressBNew, orderedKeys.get(0));
              assertEquals(progressAOld, orderedKeys.get(1));
            }

            // Vérifie que les skills de chaque progress sont triés par nom
            skillsOfFirst = result.get(orderedKeys.get(0));
            List<String> skillNamesOfFirst =
                skillsOfFirst.stream()
                    .map(slp -> slp.skillLevelProgress().getSkillLevel().getSkill().getName())
                    .toList();

            skillsOfSecond = result.get(orderedKeys.get(1));
            List<String> skillNamesOfSecond =
                skillsOfSecond.stream()
                    .map(slp -> slp.skillLevelProgress().getSkillLevel().getSkill().getName())
                    .toList();

            List<String> expectedOrder =
                (order == ESortOrder.ASC)
                    ? List.of("Skill A", "Skill B")
                    : List.of("Skill B", "Skill A");

            assertEquals(expectedOrder, skillNamesOfFirst);
            assertEquals(expectedOrder, skillNamesOfSecond);
          }
        }

        @Nested
        class AndASortByDateCriteriaIsPassed {
          private Map<StudentProgress, List<SkillLevelProgressWithTraceCountData>> result;

          @BeforeEach
          void setupAnd() {
            BddLogger.and("a sort by date criteria is passed");
            sortField = ESortField.DATE;
          }

          @ParameterizedTest
          @EnumSource(ESortOrder.class)
          void thenItShouldSortStudentProgressAndSkillsByDate(ESortOrder order) {
            BddLogger.then(
                "it should return all student progresses and all skill levels progresses ordered by"
                    + " date");
            mockedRequestContext
                .when(RequestContext::get)
                .thenReturn(
                    new RequestData(Optional.ofNullable(student.getUser()), ELanguage.FRENCH));
            result =
                studentProgressService.getStudentProgressView(new SortCriteria(sortField, order));
            orderedKeys = new ArrayList<>(result.keySet());
            if (order == ESortOrder.ASC) {
              assertEquals(progressAOld, orderedKeys.get(0));
              assertEquals(progressBNew, orderedKeys.get(1));
            } else {
              assertEquals(progressBNew, orderedKeys.get(0));
              assertEquals(progressAOld, orderedKeys.get(1));
            }

            skillsOfFirst = result.get(orderedKeys.get(0));
            List<LocalDate> datesFirst =
                skillsOfFirst.stream().map(dto -> dto.skillLevelProgress().getStartDate()).toList();

            skillsOfSecond = result.get(orderedKeys.get(1));
            List<LocalDate> datesSecond =
                skillsOfSecond.stream()
                    .map(dto -> dto.skillLevelProgress().getStartDate())
                    .toList();

            List<LocalDate> expectedFirst =
                (order == ESortOrder.ASC)
                    ? datesFirst.stream().sorted().toList()
                    : datesFirst.stream().sorted(Comparator.reverseOrder()).toList();
            List<LocalDate> expectedSecond =
                (order == ESortOrder.ASC)
                    ? datesSecond.stream().sorted().toList()
                    : datesSecond.stream().sorted(Comparator.reverseOrder()).toList();

            assertEquals(expectedFirst, datesFirst);
            assertEquals(expectedSecond, datesSecond);
          }
        }
      }

      @Nested
      class WhenGettingTheSkillsLifePojectView {
        @BeforeEach
        void setupWhen() {
          BddLogger.when("getting the skills life project view");
        }

        @Nested
        class AndASortByDateCriteriaIsPassed {
          private PagedResult<SkillProgressData> result;

          @BeforeEach
          void setupAnd() {
            BddLogger.and("a sort by date criteria is passed");
            sortCriteria = new SortCriteria(ESortField.DATE, ESortOrder.DESC);
            pageCriteria = new PageCriteria(0, 5);
            mockedRequestContext
                .when(RequestContext::get)
                .thenReturn(
                    new RequestData(Optional.ofNullable(student.getUser()), ELanguage.FRENCH));
            result = studentProgressService.getAllTimeSkillsView(sortCriteria, pageCriteria);
          }

          @Test
          void thenItShouldSortSkillProgress() {
            BddLogger.then("it should return paged skill progress sorted by the criteria");
            assertEquals(skillBBNew.getSkillLevel().getSkill(), result.content().get(0).skill());
            verify(studentProgressRepository).findAllByStudent(eq(student));
          }
        }

        @Nested
        class AndAPageSizeCriteriaIsPassed {
          private PagedResult<SkillProgressData> result;

          @BeforeEach
          void setupAnd() {
            BddLogger.and("a page size criteria is passed");
            sortCriteria = new SortCriteria(ESortField.DATE, ESortOrder.ASC);
            pageCriteria = new PageCriteria(0, 1); // 1 element by page
            mockedRequestContext
                .when(RequestContext::get)
                .thenReturn(
                    new RequestData(Optional.ofNullable(student.getUser()), ELanguage.FRENCH));
            result = studentProgressService.getAllTimeSkillsView(sortCriteria, pageCriteria);
          }

          @Test
          void thenItShouldReturnPagedSkillsLifeProjectView() {
            BddLogger.then(
                "it should return paged skill progress according to the page size criteria");
            assertEquals(1, result.content().size(), "Page should contain 1 element");
            assertEquals(4, result.pageInfo().totalElements(), "Total elements should match");
            verify(studentProgressRepository).findAllByStudent(eq(student));
          }
        }

        @Nested
        class AndAPageIndexCriteriaIsPassed {
          private PagedResult<SkillProgressData> result;

          @BeforeEach
          void setupAnd() {
            BddLogger.and("a page index criteria is passed");
            sortCriteria = new SortCriteria(ESortField.DATE, ESortOrder.ASC);
            pageCriteria = new PageCriteria(1, 3); // second page, 3 elements by page
            mockedRequestContext
                .when(RequestContext::get)
                .thenReturn(
                    new RequestData(Optional.ofNullable(student.getUser()), ELanguage.FRENCH));
            result = studentProgressService.getAllTimeSkillsView(sortCriteria, pageCriteria);
          }

          @Test
          void thenItShouldSkipFirstPageElementsWhenFetchingSecondPage() {
            BddLogger.then("it should skip first page elements");
            assertEquals(
                1, result.content().size(), "Second page should contain only the last element");
            assertEquals(4, result.pageInfo().totalElements(), "Total elements should be 4");
            assertEquals(1, result.pageInfo().page(), "Current page should be 1");
          }
        }
      }
    }

    @Nested
    class AndAStudentWithCurrentAndFinishedProgresses {
      private SkillLevelProgress currentSkill, pastSkill;
      private StudentProgress currentProgress, finishedProgress;

      @BeforeEach
      void setupAnd() {
        BddLogger.and("a student with current and finished progresses");
        now = LocalDate.now();
        currentSkill =
            SkillLevelProgressFixture.create(student)
                .withStatus(ESkillLevelStatus.TO_BE_EVALUATED)
                .toModel();
        pastSkill =
            SkillLevelProgressFixture.create(student)
                .withStatus(ESkillLevelStatus.TO_BE_EVALUATED)
                .toModel();

        currentProgress =
            StudentProgressFixture.create()
                .withStudent(student)
                .withSkillLevels(List.of(currentSkill))
                .withStartDate(now.minusDays(5))
                .toModel();

        finishedProgress =
            StudentProgressFixture.create()
                .withStudent(student)
                .withSkillLevels(List.of(pastSkill))
                .withStartDate(now.minusMonths(2), Period.ofMonths(1))
                .toModel();

        when(studentProgressRepository.findAllByStudent(eq(student)))
            .thenReturn(List.of(finishedProgress, currentProgress));
        when(traceRepository.linkedWith(any(SkillLevelProgress.class))).thenReturn(List.of());
      }

      @Nested
      class WhenGettingTheSkillsLifeProjectView {
        private PagedResult<SkillProgressData> result;

        @BeforeEach
        void setupWhen() {
          BddLogger.when("getting the skills life view project view");
        }

        @Nested
        class AndADescSortingDateCriteriaIsPassed {
          @BeforeEach
          void setupAnd() {
            BddLogger.and("a desc sorting date criteria is passed");
            sortCriteria = new SortCriteria(ESortField.DATE, ESortOrder.DESC);
            pageCriteria = new PageCriteria(0, 10);
            mockedRequestContext
                .when(RequestContext::get)
                .thenReturn(
                    new RequestData(Optional.ofNullable(student.getUser()), ELanguage.FRENCH));
            result = studentProgressService.getAllTimeSkillsView(sortCriteria, pageCriteria);
          }

          @Test
          void thenItShouldReturnCurrentProgressesBeforeFinishedProgresses() {
            BddLogger.then("it should return current progresses before finished progresses");
            assertFalse(result.content().isEmpty(), "Result should not be empty");
            StudentProgress firstProgress = result.content().getFirst().studentProgress();
            assertEquals(currentProgress, firstProgress, "Current progress should appear first");
          }
        }
      }
    }

    @Nested
    class WhenGettingAllSkillList {
      private Skill skillA;
      private Skill skillB;
      private SkillLevelProgress slpA1;
      private SkillLevelProgress slpA2;
      private SkillLevelProgress slpB;
      private StudentProgress progress1;
      private StudentProgress progress2;
      private List<Skill> result;

      @BeforeEach
      void setupWhen() {
        BddLogger.when("getting all skills list of a student");
        skillA = SkillFixture.create().withName("Skill A").toModel();
        skillB = SkillFixture.create().withName("Skill B").toModel();

        slpA1 =
            SkillLevelProgressFixture.create(student)
                .withSkillLevel(SkillLevelFixture.create().withSkill(skillA).toModel())
                .toModel();

        // même skill pour tester le distinct()
        slpA2 =
            SkillLevelProgressFixture.create(student)
                .withSkillLevel(SkillLevelFixture.create().withSkill(skillA).toModel())
                .toModel();

        slpB =
            SkillLevelProgressFixture.create(student)
                .withSkillLevel(SkillLevelFixture.create().withSkill(skillB).toModel())
                .toModel();

        progress1 =
            StudentProgressFixture.create()
                .withStudent(student)
                .withSkillLevels(List.of(slpA1, slpB))
                .toModel();

        progress2 =
            StudentProgressFixture.create()
                .withStudent(student)
                .withSkillLevels(List.of(slpA2))
                .toModel();

        when(studentProgressRepository.findAllByStudent(eq(student)))
            .thenReturn(List.of(progress1, progress2));

        mockedRequestContext
            .when(RequestContext::get)
            .thenReturn(new RequestData(Optional.ofNullable(student.getUser()), ELanguage.FRENCH));
        result = studentProgressService.getAllSkillList();
      }

      @Test
      void thenItShouldReturnDistinctSkills() {
        BddLogger.then("it should return distinct skills from all student progresses");
        assertEquals(2, result.size(), "Should contain 2 distinct skills");
        assertTrue(result.contains(skillA), "Should contain Skill A");
        assertTrue(result.contains(skillB), "Should contain Skill B");
        verify(studentProgressRepository).findAllByStudent(eq(student));
      }
    }

    @Nested
    class WhenGettingAllSkillListWithNoProgress {
      private List<Skill> result;

      @BeforeEach
      void setupWhen() {
        BddLogger.when("getting all skills list with no student progress");
        when(studentProgressRepository.findAllByStudent(eq(student))).thenReturn(List.of());
        mockedRequestContext
            .when(RequestContext::get)
            .thenReturn(new RequestData(Optional.ofNullable(student.getUser()), ELanguage.FRENCH));
        result = studentProgressService.getAllSkillList();
      }

      @Test
      void thenItShouldReturnEmptyList() {
        BddLogger.then("it should return an empty list when no student progress is found");
        assertTrue(result.isEmpty(), "Result should be empty");
        verify(studentProgressRepository).findAllByStudent(eq(student));
      }
    }

    @Nested
    class WhenGettingAdditionalSkillsProgresses {

      @Test
      void getAdditionalSkillsProgresses_shouldDelegateToRepositoryAndReturnResult() {
        BddLogger.given("the method getAdditionalSkillsProgresses");
        PageCriteria criteria = new PageCriteria(1, 8);
        PagedResult<AdditionalSkillProgress> expected = mock(PagedResult.class);

        BddLogger.when("calling the method with a given student");
        when(additionalSkillProgressRepository.findAllByStudent(student, criteria))
            .thenReturn(expected);

        PagedResult<AdditionalSkillProgress> result =
            studentProgressService.getAdditionalSkillsProgresses(criteria);

        BddLogger.then(
            "it should return the expected paged additional skill progress and delegate to"
                + " repository");
        assertThat(result).isSameAs(expected);
        verify(additionalSkillProgressRepository).findAllByStudent(student, criteria);
      }

      @Test
      void getAdditionalSkillProgressDetails_shouldReturnAdditionalSkillsProgressDetails() {
        BddLogger.given("the method getAdditionalSkillProgressDetails");
        String programName = EPortfolioType.LIFE_PROJECT.name();
        AdditionalSkillProgress additionalSkillProgress =
            AdditionalSkillProgressFixture.create().withStudent(student).toModel();
        Trace trace1 =
            TraceFixture.create()
                .withUser(student.getUser())
                .withAdditionalSkillProgresses(List.of(additionalSkillProgress))
                .toModel();
        Trace trace2 =
            TraceFixture.create()
                .withUser(student.getUser())
                .withAdditionalSkillProgresses(List.of(additionalSkillProgress))
                .toModel();

        BddLogger.when("calling the method with a given student and additionalSkillProgressId");
        when(additionalSkillProgressRepository.findById(additionalSkillProgress.getId()))
            .thenReturn(Optional.of(additionalSkillProgress));
        when(traceService.getTracesLinkedWithAdditionalSkillProgress(
                student.getUser(), additionalSkillProgress))
            .thenReturn(List.of(trace1, trace2));
        when(traceService.programNameOfTrace(trace1)).thenReturn(programName);
        when(traceService.programNameOfTrace(trace2)).thenReturn(programName);

        AdditionalSkillProgressDetails additionalSkillProgressDetails =
            studentProgressService.getAdditionalSkillProgressDetails(
                additionalSkillProgress.getId());

        BddLogger.then("it should return the expected additional skill progress details");
        assertEquals(
            additionalSkillProgressDetails.additionalSkillProgress(), additionalSkillProgress);
        assertEquals(2, additionalSkillProgressDetails.tracesWithProjectName().size());
        assertEquals(
            trace1, additionalSkillProgressDetails.tracesWithProjectName().getFirst().trace());
        assertEquals(
            programName,
            additionalSkillProgressDetails.tracesWithProjectName().getFirst().programName());
        assertEquals(
            trace2, additionalSkillProgressDetails.tracesWithProjectName().getLast().trace());
        assertEquals(
            programName,
            additionalSkillProgressDetails.tracesWithProjectName().getLast().programName());
      }

      @Test
      void getAdditionalSkillProgressDetails_shouldThrowAdditionalSkillProgressNotFoundException() {
        BddLogger.given("the method getAdditionalSkillProgressDetails");
        AdditionalSkillProgress additionalSkillProgress =
            AdditionalSkillProgressFixture.create().withStudent(student).toModel();

        BddLogger.when("calling the method with a given student and bad additionalSkillProgressId");
        assertThrows(
            AdditionalSkillProgressNotFoundException.class,
            () ->
                studentProgressService.getAdditionalSkillProgressDetails(
                    additionalSkillProgress.getId()));
      }

      @Test
      void getAdditionalSkillsProgressDetails_shouldThrowUserNotAuthorizedException() {
        BddLogger.given("the method getAdditionalSkillProgressDetails");
        Student anotherStudent = StudentFixture.create().toModel();
        AdditionalSkillProgress additionalSkillProgress =
            AdditionalSkillProgressFixture.create().withStudent(anotherStudent).toModel();

        BddLogger.when(
            "calling the method with another given student and additionalSkillProgressId");
        when(additionalSkillProgressRepository.findById(additionalSkillProgress.getId()))
            .thenReturn(Optional.of(additionalSkillProgress));
        assertThrows(
            UserNotAuthorizedException.class,
            () ->
                studentProgressService.getAdditionalSkillProgressDetails(
                    additionalSkillProgress.getId()));
      }
    }

    @Nested
    class WhenCreatingAdditionalSkillProgress {
      @Test
      void createAdditionalSkillProgress_shouldSaveWhenSkillIsAvailableAndNotDuplicate() {
        BddLogger.given("the method createAdditionalSkillProgress");
        UUID skillId = randomUUID();
        EAdditionalSkillType type = EAdditionalSkillType.ROME4;
        EAdditionalSkillLevel level = EAdditionalSkillLevel.BEGINNER;
        String description = "Description for additional skill progress test";
        AdditionalSkill additionalSkill = mock(AdditionalSkill.class);

        BddLogger.when("calling the method with an available and not duplicate skill");
        when(additionalSkillRepository.findById(skillId)).thenReturn(Optional.of(additionalSkill));
        when(additionalSkillProgressRepository.additionalSkillProgressAlreadyExists(any()))
            .thenReturn(false);

        mockedRequestContext
            .when(RequestContext::get)
            .thenReturn(new RequestData(Optional.of(student.getUser()), ELanguage.FRENCH));

        studentProgressService.createAdditionalSkillProgress(skillId, type, level, description);

        BddLogger.then("it should save the additional skill progress");
        verify(additionalSkillRepository).findById(skillId);
        verify(additionalSkillProgressRepository).additionalSkillProgressAlreadyExists(any());
        verify(additionalSkillProgressRepository).save(any(AdditionalSkillProgress.class));
      }

      @Test
      void createAdditionalSkillProgress_shouldThrowDuplicateWhenAlreadyExists() {
        BddLogger.given("the method createAdditionalSkillProgress");
        UUID skillId = randomUUID();
        EAdditionalSkillType type = EAdditionalSkillType.ROME4;
        EAdditionalSkillLevel level = EAdditionalSkillLevel.BEGINNER;
        String description = "Description for additional skill progress test";
        AdditionalSkill additionalSkill = mock(AdditionalSkill.class);

        BddLogger.when("calling the method with a duplicate skill");
        when(additionalSkillRepository.findById(skillId)).thenReturn(Optional.of(additionalSkill));
        when(additionalSkillProgressRepository.additionalSkillProgressAlreadyExists(any()))
            .thenReturn(true);

        BddLogger.then(
            "it should throw a DuplicateAdditionalSkillException and not save the progress");
        mockedRequestContext
            .when(RequestContext::get)
            .thenReturn(new RequestData(Optional.of(student.getUser()), ELanguage.FRENCH));

        assertThrows(
            DuplicateAdditionalSkillException.class,
            () ->
                studentProgressService.createAdditionalSkillProgress(
                    skillId, type, level, description));

        verify(additionalSkillRepository).findById(skillId);
        verify(additionalSkillProgressRepository).additionalSkillProgressAlreadyExists(any());
        verify(additionalSkillProgressRepository, never()).save(any());
      }

      @Test
      void createAdditionalSkillProgress_shouldRethrowWhenSkillNotFound() {
        BddLogger.given("the method createAdditionalSkillProgress");
        UUID skillId = randomUUID();
        EAdditionalSkillType type = EAdditionalSkillType.ROME4;
        EAdditionalSkillLevel level = EAdditionalSkillLevel.BEGINNER;
        String description = "Description for additional skill progress test";

        BddLogger.when("calling the method with an unknown skill");
        when(additionalSkillRepository.findById(skillId))
            .thenThrow(new AdditionalSkillNotFoundException());

        BddLogger.then("it should throw an AdditionalSkillNotFoundException");
        mockedRequestContext
            .when(RequestContext::get)
            .thenReturn(new RequestData(Optional.of(student.getUser()), ELanguage.FRENCH));

        assertThrows(
            AdditionalSkillNotFoundException.class,
            () ->
                studentProgressService.createAdditionalSkillProgress(
                    skillId, type, level, description));

        verify(additionalSkillRepository).findById(skillId);
        verifyNoInteractions(additionalSkillProgressRepository);
      }
    }

    @Nested
    class WhenUpdatingAdditionalSkillProgress {
      @Test
      void updateAdditionalSkillProgress_shouldSaveLevelAndDescription() {
        BddLogger.given("the method updateAdditionalSkillProgress");
        EAdditionalSkillLevel level = EAdditionalSkillLevel.ADVANCED;
        String description = "Description for additional skill progress test";
        AdditionalSkillProgress additionalSkillProgress =
            AdditionalSkillProgressFixture.create()
                .withStudent(student)
                .withLevel(EAdditionalSkillLevel.BEGINNER)
                .withDescription(null)
                .toModel();

        BddLogger.when(
            "calling the method with a given student, additionalSkillProgressId, level and"
                + " description");
        when(additionalSkillProgressRepository.findById(additionalSkillProgress.getId()))
            .thenReturn(Optional.of(additionalSkillProgress));

        studentProgressService.updateAdditionalSkillProgress(
            additionalSkillProgress.getId(), level, description);

        BddLogger.then("it should save level and description in additional skill progress");
        ArgumentCaptor<AdditionalSkillProgress> captor =
            ArgumentCaptor.forClass(AdditionalSkillProgress.class);
        verify(additionalSkillProgressRepository).save(captor.capture());

        AdditionalSkillProgress savedAdditionalSkillProgress = captor.getValue();
        assertEquals(additionalSkillProgress.getId(), savedAdditionalSkillProgress.getId());
        assertEquals(
            additionalSkillProgress.getStudent(), savedAdditionalSkillProgress.getStudent());
        assertEquals(additionalSkillProgress.getSkill(), savedAdditionalSkillProgress.getSkill());
        assertEquals(level, savedAdditionalSkillProgress.getLevel());
        assertEquals(description, savedAdditionalSkillProgress.getDescription());
      }

      @Test
      void updateAdditionalSkillProgress_shouldThrowInvalidDescriptionException() {
        BddLogger.given("the method getAdditionalSkillProgressDetails");
        EAdditionalSkillLevel level = EAdditionalSkillLevel.BEGINNER;
        String description =
            random
                .ints(DESCRIPTION_LENGTH_MAX + 1, 0, CHARSET.length())
                .mapToObj(CHARSET::charAt)
                .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
                .toString();
        AdditionalSkillProgress additionalSkillProgress =
            AdditionalSkillProgressFixture.create().withStudent(student).toModel();

        BddLogger.when(
            "calling the method with a given student, additionalSkillProgressId, level and too long"
                + " description");
        assertThrows(
            InvalidDescriptionException.class,
            () ->
                studentProgressService.updateAdditionalSkillProgress(
                    additionalSkillProgress.getId(), level, description));
      }

      @Test
      void updateAdditionalSkillProgress_shouldThrowAdditionalSkillProgressNotFoundException() {
        BddLogger.given("the method getAdditionalSkillProgressDetails");
        EAdditionalSkillLevel level = EAdditionalSkillLevel.BEGINNER;
        String description = "Description for additional skill progress test";
        AdditionalSkillProgress additionalSkillProgress =
            AdditionalSkillProgressFixture.create().withStudent(student).toModel();

        BddLogger.when("calling the method with a given student and bad additionalSkillProgressId");
        assertThrows(
            AdditionalSkillProgressNotFoundException.class,
            () ->
                studentProgressService.updateAdditionalSkillProgress(
                    additionalSkillProgress.getId(), level, description));
      }

      @Test
      void updateAdditionalSkillProgress_shouldThrowUserNotAuthorizedException() {
        BddLogger.given("the method getAdditionalSkillProgressDetails");
        Student anotherStudent = StudentFixture.create().toModel();
        EAdditionalSkillLevel level = EAdditionalSkillLevel.BEGINNER;
        String description = "Description for additional skill progress test";
        AdditionalSkillProgress additionalSkillProgress =
            AdditionalSkillProgressFixture.create().withStudent(anotherStudent).toModel();

        BddLogger.when("calling the method with another given student and level, description");
        when(additionalSkillProgressRepository.findById(additionalSkillProgress.getId()))
            .thenReturn(Optional.of(additionalSkillProgress));
        assertThrows(
            UserNotAuthorizedException.class,
            () ->
                studentProgressService.updateAdditionalSkillProgress(
                    additionalSkillProgress.getId(), level, description));
      }
    }
  }
}
