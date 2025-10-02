package fr.avenirsesr.portfolio.student.progress.domain.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import fr.avenirsesr.portfolio.common.data.domain.model.PageCriteria;
import fr.avenirsesr.portfolio.common.data.domain.model.PagedResult;
import fr.avenirsesr.portfolio.common.data.domain.model.SortCriteria;
import fr.avenirsesr.portfolio.common.data.domain.model.enums.ESortField;
import fr.avenirsesr.portfolio.common.data.domain.model.enums.ESortOrder;
import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import fr.avenirsesr.portfolio.program.domain.model.Skill;
import fr.avenirsesr.portfolio.program.domain.model.TrainingPath;
import fr.avenirsesr.portfolio.program.domain.model.enums.ESkillLevelStatus;
import fr.avenirsesr.portfolio.program.infrastructure.fixture.*;
import fr.avenirsesr.portfolio.student.progress.domain.dto.SkillLevelProgressWithTraceCountDTO;
import fr.avenirsesr.portfolio.student.progress.domain.dto.SkillProgressDTO;
import fr.avenirsesr.portfolio.student.progress.domain.model.*;
import fr.avenirsesr.portfolio.student.progress.domain.port.output.repository.StudentProgressRepository;
import fr.avenirsesr.portfolio.student.progress.infrastructure.fixture.*;
import fr.avenirsesr.portfolio.trace.domain.port.output.repository.TraceRepository;
import fr.avenirsesr.portfolio.user.domain.model.Student;
import fr.avenirsesr.portfolio.user.infrastructure.fixture.UserFixture;
import java.time.LocalDate;
import java.time.Period;
import java.util.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class StudentProgressServiceImplTest {
  @Mock private StudentProgressRepository studentProgressRepository;
  @Mock private TraceRepository traceRepository;
  @InjectMocks private StudentProgressServiceImpl studentProgressService;
  private Student student;

  @BeforeEach
  void setUp() {
    student = UserFixture.createStudent().toModel().toStudent();
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
                .withUser(student.getUser())
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
          result = studentProgressService.isStudentFollowingAPCProgram(student);
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
          result = studentProgressService.isStudentFollowingAPCProgram(student);
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
                .withUser(student.getUser())
                .withSkillLevels(
                    List.of(
                        skillLevelsProgress.get(0),
                        skillLevelsProgress.get(1),
                        skillLevelsProgress.get(2),
                        skillLevelsProgress.get(3)))
                .toModel();

        progress2 =
            StudentProgressFixture.create()
                .withUser(student.getUser())
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
        private Map<StudentProgress, List<SkillLevelProgressWithTraceCountDTO>> result;

        @BeforeEach
        void setupWhen() {
          BddLogger.when("getting the studen progress overview");
          result = studentProgressService.getStudentProgressOverview(student);
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
        private Map<StudentProgress, List<SkillLevelProgressWithTraceCountDTO>> result;

        @BeforeEach
        void setupWhen() {
          BddLogger.when("getting the student progress view");
          result =
              studentProgressService.getStudentProgressView(
                  student, new SortCriteria(ESortField.DATE, ESortOrder.ASC));
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
        private Map<StudentProgress, List<SkillLevelProgressWithTraceCountDTO>> result;

        @BeforeEach
        void setupWhen() {
          BddLogger.when("getting the student progress overview");
          result = studentProgressService.getStudentProgressOverview(student);
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
        private PagedResult<SkillProgressDTO> result;

        @BeforeEach
        void setupWhen() {
          BddLogger.when("getting the skills life project view");

          sortCriteria = new SortCriteria(ESortField.DATE, ESortOrder.ASC);
          pageCriteria = new PageCriteria(0, 5);
          result = studentProgressService.getAllTimeSkillsView(student, sortCriteria, pageCriteria);
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
                .withUser(student.getUser())
                .withSkillLevels(List.of(skillLevelsProgress.get(0), skillLevelsProgress.get(1)))
                .withStartDate(now.minusDays(5))
                .toModel();

        // Progress "past" : endDate before now
        pastProgress =
            StudentProgressFixture.create()
                .withUser(student.getUser())
                .withSkillLevels(List.of(skillLevelsProgress.get(2), skillLevelsProgress.get(3)))
                .withStartDate(now.minusYears(10))
                .toModel();

        // Progress "future" : startDate after now
        futureProgress =
            StudentProgressFixture.create()
                .withUser(student.getUser())
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
        private Map<StudentProgress, List<SkillLevelProgressWithTraceCountDTO>> result;

        @BeforeEach
        void setupWhen() {
          BddLogger.when("getting the student progress overview");
          result = studentProgressService.getStudentProgressOverview(student);
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
        private Map<StudentProgress, List<SkillLevelProgressWithTraceCountDTO>> result;

        @BeforeEach
        void setupWhen() {
          BddLogger.when("getting the student pogress view");
          result =
              studentProgressService.getStudentProgressView(
                  student, new SortCriteria(ESortField.DATE, ESortOrder.ASC));
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
        private PagedResult<SkillProgressDTO> result;

        @BeforeEach
        void setupWhen() {
          BddLogger.when("getting the skills life project view");
          sortCriteria = new SortCriteria(ESortField.DATE, ESortOrder.ASC);
          pageCriteria = new PageCriteria(0, 10);
          result = studentProgressService.getAllTimeSkillsView(student, sortCriteria, pageCriteria);
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
        progress = StudentProgressFixture.create().withUser(student.getUser()).toModel();

        when(studentProgressRepository.findAllByStudent(eq(student))).thenReturn(List.of(progress));
      }

      @Nested
      class WhenGettingTheStudentProgressView {
        private Map<StudentProgress, List<SkillLevelProgressWithTraceCountDTO>> result;

        @BeforeEach
        void setupWhen() {
          BddLogger.when("getting the student progress view");
          result = studentProgressService.getStudentProgressView(student, customSort);
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
                .withUser(student.getUser())
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
                .withUser(student.getUser())
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
        private List<SkillLevelProgressWithTraceCountDTO> skillsOfFirst, skillsOfSecond;

        @BeforeEach
        void setupWhen() {
          BddLogger.when("getting the student progress view");
        }

        @Nested
        class AndASortByNameCriteriaIsPassed {
          private Map<StudentProgress, List<SkillLevelProgressWithTraceCountDTO>> result;

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
            result =
                studentProgressService.getStudentProgressView(
                    student, new SortCriteria(sortField, order));
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
          private Map<StudentProgress, List<SkillLevelProgressWithTraceCountDTO>> result;

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
            result =
                studentProgressService.getStudentProgressView(
                    student, new SortCriteria(sortField, order));
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
          private PagedResult<SkillProgressDTO> result;

          @BeforeEach
          void setupAnd() {
            BddLogger.and("a sort by date criteria is passed");
            sortCriteria = new SortCriteria(ESortField.DATE, ESortOrder.DESC);
            pageCriteria = new PageCriteria(0, 5);
            result =
                studentProgressService.getAllTimeSkillsView(student, sortCriteria, pageCriteria);
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
          private PagedResult<SkillProgressDTO> result;

          @BeforeEach
          void setupAnd() {
            BddLogger.and("a page size criteria is passed");
            sortCriteria = new SortCriteria(ESortField.DATE, ESortOrder.ASC);
            pageCriteria = new PageCriteria(0, 1); // 1 element by page
            result =
                studentProgressService.getAllTimeSkillsView(student, sortCriteria, pageCriteria);
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
          private PagedResult<SkillProgressDTO> result;

          @BeforeEach
          void setupAnd() {
            BddLogger.and("a page index criteria is passed");
            sortCriteria = new SortCriteria(ESortField.DATE, ESortOrder.ASC);
            pageCriteria = new PageCriteria(1, 3); // second page, 3 elements by page
            result =
                studentProgressService.getAllTimeSkillsView(student, sortCriteria, pageCriteria);
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
                .withUser(student.getUser())
                .withSkillLevels(List.of(currentSkill))
                .withStartDate(now.minusDays(5))
                .toModel();

        finishedProgress =
            StudentProgressFixture.create()
                .withUser(student.getUser())
                .withSkillLevels(List.of(pastSkill))
                .withStartDate(now.minusMonths(2), Period.ofMonths(1))
                .toModel();

        when(studentProgressRepository.findAllByStudent(eq(student)))
            .thenReturn(List.of(finishedProgress, currentProgress));
        when(traceRepository.linkedWith(any(SkillLevelProgress.class))).thenReturn(List.of());
      }

      @Nested
      class WhenGettingTheSkillsLifePojectView {
        private PagedResult<SkillProgressDTO> result;

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
            result =
                studentProgressService.getAllTimeSkillsView(student, sortCriteria, pageCriteria);
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
                .withUser(student.getUser())
                .withSkillLevels(List.of(slpA1, slpB))
                .toModel();

        progress2 =
            StudentProgressFixture.create()
                .withUser(student.getUser())
                .withSkillLevels(List.of(slpA2))
                .toModel();

        when(studentProgressRepository.findAllByStudent(eq(student)))
            .thenReturn(List.of(progress1, progress2));

        result = studentProgressService.getAllSkillList(student);
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
        result = studentProgressService.getAllSkillList(student);
      }

      @Test
      void thenItShouldReturnEmptyList() {
        BddLogger.then("it should return an empty list when no student progress is found");
        assertTrue(result.isEmpty(), "Result should be empty");
        verify(studentProgressRepository).findAllByStudent(eq(student));
      }
    }
  }
}
