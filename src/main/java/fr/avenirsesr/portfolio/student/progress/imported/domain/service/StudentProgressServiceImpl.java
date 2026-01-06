package fr.avenirsesr.portfolio.student.progress.imported.domain.service;

import fr.avenirsesr.portfolio.common.data.domain.model.PageCriteria;
import fr.avenirsesr.portfolio.common.data.domain.model.PageInfo;
import fr.avenirsesr.portfolio.common.data.domain.model.PagedResult;
import fr.avenirsesr.portfolio.common.data.domain.model.SortCriteria;
import fr.avenirsesr.portfolio.program.domain.model.Skill;
import fr.avenirsesr.portfolio.shared.domain.port.input.LoggedInUserService;
import fr.avenirsesr.portfolio.student.progress.imported.domain.data.SkillLevelProgressWithTraceCountData;
import fr.avenirsesr.portfolio.student.progress.imported.domain.data.SkillProgressData;
import fr.avenirsesr.portfolio.student.progress.imported.domain.model.SkillLevelProgress;
import fr.avenirsesr.portfolio.student.progress.imported.domain.model.StudentProgress;
import fr.avenirsesr.portfolio.student.progress.imported.domain.port.input.StudentProgressService;
import fr.avenirsesr.portfolio.student.progress.imported.domain.port.output.repository.SkillLevelProgressRepository;
import fr.avenirsesr.portfolio.student.progress.imported.domain.port.output.repository.StudentProgressRepository;
import fr.avenirsesr.portfolio.trace.domain.port.output.repository.TraceRepository;
import fr.avenirsesr.portfolio.user.domain.model.Student;
import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
public class StudentProgressServiceImpl implements StudentProgressService {
  public static final int DESCRIPTION_LENGTH_MAX = 400;
  private static final int MAX_SKILLS = 6;
  private final StudentProgressRepository studentProgressRepository;
  private final SkillLevelProgressRepository skillLevelProgressRepository;
  private final TraceRepository traceRepository;
  private final LoggedInUserService loggedInUserService;

  @Override
  public boolean isStudentFollowingAPCProgram() {
    Student student = loggedInUserService.getLoggedInStudent();
    var studentProgress = studentProgressRepository.findAllAPCByStudent(student);
    return !studentProgress.isEmpty();
  }

  @Override
  public List<StudentProgress> getAllCurrentStudentProgress() {
    Student student = loggedInUserService.getLoggedInStudent();
    log.debug("{} fetched his student progresses", student);
    return studentProgressRepository.findAllByStudent(student).stream()
        .filter(StudentProgress::isCurrent)
        .toList();
  }

  @Override
  public Map<StudentProgress, List<SkillLevelProgressWithTraceCountData>>
      getStudentProgressOverview() {
    Student student = loggedInUserService.getLoggedInStudent();
    var studentProgresses =
        studentProgressRepository.findAllByStudent(student).stream()
            .filter(StudentProgress::isCurrent)
            .toList();

    return studentProgresses.stream()
        .collect(
            Collectors.toMap(
                Function.identity(),
                studentProgress ->
                    studentProgress.getCurrentSkillLevels().stream()
                        .limit(MAX_SKILLS / studentProgresses.size())
                        .map(
                            skillLevelProgress ->
                                new SkillLevelProgressWithTraceCountData(
                                    skillLevelProgress,
                                    traceRepository.linkedWith(skillLevelProgress).size()))
                        .toList()));
  }

  @Override
  public Map<StudentProgress, List<SkillLevelProgressWithTraceCountData>> getStudentProgressView(
      SortCriteria sortCriteria) {
    Student student = loggedInUserService.getLoggedInStudent();
    return studentProgressRepository.findAllByStudent(student).stream()
        .filter(StudentProgress::isCurrent)
        .sorted(StudentProgress.comparatorOf(sortCriteria))
        .collect(
            Collectors.toMap(
                Function.identity(),
                studentProgress ->
                    studentProgress.getCurrentSkillLevels().stream()
                        .sorted(SkillLevelProgress.comparatorOf(sortCriteria))
                        .map(
                            skillLevelProgress ->
                                new SkillLevelProgressWithTraceCountData(
                                    skillLevelProgress,
                                    traceRepository.linkedWith(skillLevelProgress).size()))
                        .toList(),
                (v1, v2) -> v1,
                LinkedHashMap::new));
  }

  @Override
  public PagedResult<SkillProgressData> getAllTimeSkillsView(
      SortCriteria sortCriteria, PageCriteria pageCriteria) {
    Student student = loggedInUserService.getLoggedInStudent();
    var studentProgresses = studentProgressRepository.findAllByStudent(student).stream().toList();

    var skillProgresses =
        studentProgresses.stream()
            .filter(studentProgress -> studentProgress.getStartDate().isBefore(LocalDate.now()))
            .flatMap(
                studentProgress ->
                    studentProgress.getCurrentSkillLevels().stream()
                        .map(
                            currentSkillLevel ->
                                new SkillProgressData(
                                    currentSkillLevel.getSkillLevel().getSkill(),
                                    studentProgress,
                                    new SkillLevelProgressWithTraceCountData(
                                        currentSkillLevel,
                                        traceRepository.linkedWith(currentSkillLevel).size()))))
            .sorted(
                Comparator.comparing(
                        (SkillProgressData skillProgress) ->
                            skillProgress.studentProgress().isCurrent() ? 0 : 1)
                    .thenComparing(SkillProgressData.comparatorOf(sortCriteria)))
            .toList();

    return new PagedResult<>(
        skillProgresses.stream()
            .skip((long) (pageCriteria.page()) * pageCriteria.pageSize())
            .limit(pageCriteria.pageSize())
            .toList(),
        new PageInfo(pageCriteria.page(), pageCriteria.pageSize(), skillProgresses.size()));
  }

  @Override
  public PagedResult<SkillLevelProgress> searchSkillLevel(
      String keyword, PageCriteria pageCriteria) {
    Student student = loggedInUserService.getLoggedInStudent();
    log.debug("Searching SkillLevelProgress for {} with pagination {}", student, pageCriteria);

    return skillLevelProgressRepository.findAllByStudent(student, pageCriteria, keyword);
  }

  @Override
  public List<SkillLevelProgress> getSkillLevelsBySkillId(UUID skillId) {
    Student student = loggedInUserService.getLoggedInStudent();
    return skillLevelProgressRepository.findAllByStudentAndSkillId(student, skillId);
  }

  @Override
  public List<Skill> getAllSkillList() {
    Student student = loggedInUserService.getLoggedInStudent();
    return studentProgressRepository.findAllByStudent(student).stream()
        .flatMap(
            studentProgress ->
                studentProgress.getAllSkillLevels().stream()
                    .map(skillLevelProgress -> skillLevelProgress.getSkillLevel().getSkill()))
        .distinct()
        .toList();
  }
}
