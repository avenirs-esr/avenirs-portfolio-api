package fr.avenirsesr.portfolio.student.progress.domain.service;

import fr.avenirsesr.portfolio.shared.domain.model.PageCriteria;
import fr.avenirsesr.portfolio.shared.domain.model.PageInfo;
import fr.avenirsesr.portfolio.shared.domain.model.PagedResult;
import fr.avenirsesr.portfolio.shared.domain.model.SortCriteria;
import fr.avenirsesr.portfolio.student.progress.domain.model.SkillLevelProgress;
import fr.avenirsesr.portfolio.student.progress.domain.model.SkillProgress;
import fr.avenirsesr.portfolio.student.progress.domain.model.StudentProgress;
import fr.avenirsesr.portfolio.student.progress.domain.port.input.StudentProgressService;
import fr.avenirsesr.portfolio.student.progress.domain.port.output.repository.StudentProgressRepository;
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
  private static final int MAX_SKILLS = 6;
  private final StudentProgressRepository studentProgressRepository;

  @Override
  public boolean isStudentFollowingAPCProgram(Student student) {
    var studentProgress = studentProgressRepository.findAllAPCByStudent(student);
    return !studentProgress.isEmpty();
  }

  @Override
  public Map<StudentProgress, List<SkillLevelProgress>> getStudentProgressOverview(
      Student student) {
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
                        .toList()));
  }

  @Override
  public Map<StudentProgress, List<SkillLevelProgress>> getStudentProgressView(
      Student student, SortCriteria sortCriteria) {
    return studentProgressRepository.findAllByStudent(student).stream()
        .filter(StudentProgress::isCurrent)
        .sorted(StudentProgress.comparatorOf(sortCriteria))
        .collect(
            Collectors.toMap(
                Function.identity(),
                studentProgress ->
                    studentProgress.getCurrentSkillLevels().stream()
                        .sorted(SkillLevelProgress.comparatorOf(sortCriteria))
                        .toList(),
                (v1, v2) -> v1,
                LinkedHashMap::new));
  }

  @Override
  public PagedResult<SkillProgress> getAllTimeSkillsView(
      Student student, SortCriteria sortCriteria, PageCriteria pageCriteria) {

    var studentProgresses = studentProgressRepository.findAllByStudent(student).stream().toList();

    var skillProgresses =
        studentProgresses.stream()
            .filter(studentProgress -> studentProgress.getStartDate().isBefore(LocalDate.now()))
            .flatMap(
                studentProgress ->
                    studentProgress.getCurrentSkillLevels().stream()
                        .map(
                            currentSkillLevel ->
                                new SkillProgress(
                                    currentSkillLevel.getSkillLevel().getSkill(),
                                    studentProgress,
                                    currentSkillLevel)))
            .sorted(
                Comparator.comparing(
                        (SkillProgress skillProgress) ->
                            skillProgress.studentProgress().isCurrent() ? 0 : 1)
                    .thenComparing(SkillProgress.comparatorOf(sortCriteria)))
            .toList();

    return new PagedResult<>(
        skillProgresses.stream()
            .skip((long) (pageCriteria.page()) * pageCriteria.pageSize())
            .limit(pageCriteria.pageSize())
            .toList(),
        new PageInfo(pageCriteria.page(), pageCriteria.pageSize(), skillProgresses.size()));
  }
}
