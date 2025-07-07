package fr.avenirsesr.portfolio.student.progress.domain.service;

import fr.avenirsesr.portfolio.shared.domain.model.SortCriteria;
import fr.avenirsesr.portfolio.shared.domain.model.enums.ESortField;
import fr.avenirsesr.portfolio.shared.domain.model.enums.ESortOrder;
import fr.avenirsesr.portfolio.student.progress.domain.model.Skill;
import fr.avenirsesr.portfolio.student.progress.domain.model.StudentProgress;
import fr.avenirsesr.portfolio.student.progress.domain.model.TrainingPath;
import fr.avenirsesr.portfolio.student.progress.domain.model.enums.ESkillLevelStatus;
import fr.avenirsesr.portfolio.student.progress.domain.port.input.StudentProgressService;
import fr.avenirsesr.portfolio.student.progress.domain.port.output.repository.StudentProgressRepository;
import fr.avenirsesr.portfolio.user.domain.model.Student;
import java.time.LocalDate;
import java.util.*;
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

  private static List<StudentProgress> filterOneSkillLevelPerSkill(
      List<StudentProgress> studentProgresses) {
    Map<String, StudentProgress> chosenByUserPathSkill = new HashMap<>();

    for (StudentProgress sp : studentProgresses) {
      UUID trainingPathId = sp.getTrainingPath().getId();
      Skill skill = sp.getSkillLevel().getSkill();
      UUID skillId = Objects.isNull(skill) ? null : skill.getId();

      if (!Objects.isNull(skillId) && sp.getSkillLevel().getEndDate().isAfter(LocalDate.now())) {
        String key = trainingPathId + "-" + skillId;

        if (!chosenByUserPathSkill.containsKey(key)) {
          chosenByUserPathSkill.put(key, sp);
          continue;
        }

        StudentProgress current = chosenByUserPathSkill.get(key);
        ESkillLevelStatus currentStatus = current.getSkillLevel().getStatus();
        ESkillLevelStatus newStatus = sp.getSkillLevel().getStatus();

        if (isStatusHigherPriority(newStatus, currentStatus)) {
          chosenByUserPathSkill.put(key, sp);
        }
      }
    }

    return chosenByUserPathSkill.values().stream()
        .filter(sp -> isValidStatus(sp.getSkillLevel().getStatus()))
        .toList();
  }

  private static boolean isValidStatus(ESkillLevelStatus status) {
    return status == ESkillLevelStatus.UNDER_REVIEW
        || status == ESkillLevelStatus.UNDER_ACQUISITION
        || status == ESkillLevelStatus.TO_BE_EVALUATED
        || status == ESkillLevelStatus.NOT_STARTED;
  }

  private static int getStatusPriority(ESkillLevelStatus status) {
    return switch (status) {
      case UNDER_REVIEW, UNDER_ACQUISITION -> 1;
      case TO_BE_EVALUATED, NOT_STARTED -> 2;
      default -> 3;
    };
  }

  private static boolean isStatusHigherPriority(
      ESkillLevelStatus newStatus, ESkillLevelStatus currentStatus) {
    return getStatusPriority(newStatus) < getStatusPriority(currentStatus);
  }

  private static Map<TrainingPath, Set<StudentProgress>> cleanStudentProgressList(
      List<StudentProgress> studentProgresses, boolean needToLimitSkills) {
    if (studentProgresses.isEmpty()) {
      return Collections.emptyMap();
    }
    List<StudentProgress> filtered = filterOneSkillLevelPerSkill(studentProgresses);

    Map<TrainingPath, Set<StudentProgress>> groupedByTrainingPath =
        filtered.stream()
            .collect(
                Collectors.groupingBy(
                    StudentProgress::getTrainingPath,
                    LinkedHashMap::new,
                    Collectors.toCollection(LinkedHashSet::new)));

    Map<TrainingPath, Set<StudentProgress>> sorted = new LinkedHashMap<>();

    groupedByTrainingPath.entrySet().stream()
        .sorted(Comparator.comparing(e -> e.getKey().getProgram().getName()))
        .forEachOrdered(e -> sorted.put(e.getKey(), e.getValue()));

    if (needToLimitSkills) {
      int trainingPathCount = sorted.size();
      int skillLimitPerPath = trainingPathCount > 0 ? MAX_SKILLS / trainingPathCount : 0;

      sorted.replaceAll(
          (tp, progresses) ->
              progresses.stream()
                  .sorted(Comparator.comparing(sp -> sp.getSkillLevel().getSkill().getName()))
                  .limit(skillLimitPerPath)
                  .collect(Collectors.toCollection(LinkedHashSet::new)));
    }

    return sorted;
  }

  @Override
  public Map<TrainingPath, Set<StudentProgress>> getSkillsOverview(Student student) {
    SortCriteria sortCriteria = new SortCriteria(ESortField.NAME, ESortOrder.ASC);
    return cleanStudentProgressList(
        studentProgressRepository.findAllByStudent(student, sortCriteria), true);
  }

  @Override
  public boolean isStudentFollowingAPCProgram(Student student) {
    var studentProgress = studentProgressRepository.findAllAPCByStudent(student);
    return !studentProgress.isEmpty();
  }

  @Override
  public Map<TrainingPath, Set<StudentProgress>> getSkillsView(
      Student student, SortCriteria sortCriteria) {
    if (sortCriteria == null) {
      sortCriteria = new SortCriteria(ESortField.NAME, ESortOrder.ASC);
    }
    return cleanStudentProgressList(
        studentProgressRepository.findAllByStudent(student, sortCriteria), false);
  }

  @Override
  public List<TrainingPath> getAllStudentProgress(Student student) {
    return studentProgressRepository.findAllWithoutSkillsByStudent(student).stream()
        .sorted(Comparator.comparing(p -> p.getProgram().getName()))
        .collect(Collectors.toList());
  }
}
