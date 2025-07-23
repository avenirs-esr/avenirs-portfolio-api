package fr.avenirsesr.portfolio.student.progress.domain.service;

import fr.avenirsesr.portfolio.shared.domain.model.PageCriteria;
import fr.avenirsesr.portfolio.shared.domain.model.PageInfo;
import fr.avenirsesr.portfolio.shared.domain.model.PagedResult;
import fr.avenirsesr.portfolio.shared.domain.model.SortCriteria;
import fr.avenirsesr.portfolio.shared.domain.model.enums.ESortField;
import fr.avenirsesr.portfolio.shared.domain.model.enums.ESortOrder;
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
import org.springframework.stereotype.Service;

@Slf4j
@AllArgsConstructor
@Service
public class StudentProgressServiceImpl implements StudentProgressService {
  private static final int MAX_SKILLS = 6;
  private final StudentProgressRepository studentProgressRepository;

  @Override
  public boolean isStudentFollowingAPCProgram(Student student) {
    var studentProgress = studentProgressRepository.findAllAPCByStudent(student);
    return !studentProgress.isEmpty();
  }

  @Override
  public Map<StudentProgress, List<SkillLevelProgress>> getSkillsOverview(Student student) {
    SortCriteria sortCriteria = new SortCriteria(ESortField.NAME, ESortOrder.ASC);
    var studentProgresses =
        studentProgressRepository.findAllByStudent(student, sortCriteria).stream()
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
  public List<StudentProgress> getSkillsView(Student student, SortCriteria sortCriteria) {
    return studentProgressRepository.findAllByStudent(student, sortCriteria).stream()
        .filter(StudentProgress::isCurrent)
        .toList();
  }

  @Override
  public PagedResult<SkillProgress> getSkillsLifeProjectView(
      Student student, SortCriteria sortCriteria, PageCriteria pageCriteria) {
    sortCriteria =
        sortCriteria != null ? sortCriteria : new SortCriteria(ESortField.NAME, ESortOrder.ASC);

    var studentProgresses =
        studentProgressRepository.findAllByStudent(student, sortCriteria).stream().toList();

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
            .skip((long) (pageCriteria.page() - 1) * pageCriteria.pageSize())
            .limit(pageCriteria.pageSize())
            .toList(),
        new PageInfo(pageCriteria.page(), pageCriteria.pageSize(), skillProgresses.size()));
  }
}
