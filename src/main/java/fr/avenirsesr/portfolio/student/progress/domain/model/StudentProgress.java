package fr.avenirsesr.portfolio.student.progress.domain.model;

import fr.avenirsesr.portfolio.program.domain.model.Skill;
import fr.avenirsesr.portfolio.program.domain.model.TrainingPath;
import fr.avenirsesr.portfolio.program.domain.model.enums.ESkillLevelStatus;
import fr.avenirsesr.portfolio.shared.domain.model.AvenirsBaseModel;
import fr.avenirsesr.portfolio.shared.domain.model.SortCriteria;
import fr.avenirsesr.portfolio.user.domain.model.Student;
import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@Setter
public class StudentProgress extends AvenirsBaseModel {
  private final Student student;
  private final TrainingPath trainingPath;
  private LocalDate startDate;
  private LocalDate endDate;

  @Getter(AccessLevel.NONE)
  private final List<SkillLevelProgress> skillLevelProgresses;

  private StudentProgress(
      UUID id,
      Student student,
      TrainingPath trainingPath,
      LocalDate startDate,
      LocalDate endDate,
      List<SkillLevelProgress> skillLevelProgresses) {
    super(id);
    this.student = student;
    this.trainingPath = trainingPath;
    this.startDate = startDate;
    this.endDate = endDate;
    this.skillLevelProgresses = skillLevelProgresses;
  }

  public static StudentProgress create(
      Student student,
      TrainingPath trainingPath,
      LocalDate startDate,
      LocalDate endDate,
      List<SkillLevelProgress> skillLevels) {
    return new StudentProgress(
        UUID.randomUUID(), student, trainingPath, startDate, endDate, skillLevels);
  }

  public static StudentProgress toDomain(
      UUID id,
      Student student,
      TrainingPath trainingPath,
      LocalDate startDate,
      LocalDate endDate,
      List<SkillLevelProgress> skillLevels) {
    return new StudentProgress(id, student, trainingPath, startDate, endDate, skillLevels);
  }

  public boolean isCurrent() {
    LocalDate now = LocalDate.now();
    return !now.isBefore(startDate) && !now.isAfter(endDate);
  }

  public List<SkillLevelProgress> getAllSkillLevels() {
    return skillLevelProgresses;
  }

  public List<SkillLevelProgress> getCurrentSkillLevels() {
    Map<Skill, List<SkillLevelProgress>> startedSkillLevelsBySkill =
        skillLevelProgresses.stream()
            .collect(
                Collectors.groupingBy(
                    skillLevelProgress -> skillLevelProgress.getSkillLevel().getSkill(),
                    Collectors.collectingAndThen(
                        Collectors.filtering(
                            skillLevelProgress ->
                                skillLevelProgress.getStatus() != ESkillLevelStatus.NOT_STARTED,
                            Collectors.toCollection(ArrayList::new)),
                        Function.identity())));

    startedSkillLevelsBySkill.forEach(
        (skill, levelProgress) -> {
          levelProgress.sort(Comparator.comparing(SkillLevelProgress::getStartDate).reversed());
        });

    return startedSkillLevelsBySkill.values().stream()
        .filter(skillLevelProgresses -> !skillLevelProgresses.isEmpty())
        .map(List::getFirst)
        .toList();
  }

  public Map<Skill, Optional<SkillLevelProgress>> getLastAchievedSkillLevelBySkill() {
    return skillLevelProgresses.stream()
        .collect(
            Collectors.groupingBy(
                skillLevelProgress -> skillLevelProgress.getSkillLevel().getSkill(),
                Collectors.collectingAndThen(
                    Collectors.toList(),
                    list ->
                        list.stream()
                            .filter(s -> s.getStatus() != ESkillLevelStatus.NOT_STARTED)
                            .filter(s -> s.getEndDate().isBefore(LocalDate.now()))
                            .filter(s -> !getCurrentSkillLevels().contains(s))
                            .max(Comparator.comparing(SkillLevelProgress::getEndDate)))));
  }

  public static Comparator<StudentProgress> comparatorOf(SortCriteria sortCriteria) {
    Comparator<StudentProgress> comparator =
        switch (sortCriteria.field()) {
          case NAME ->
              Comparator.comparing(
                  studentProgress -> studentProgress.getTrainingPath().getProgram().getName());
          case DATE -> Comparator.comparing(StudentProgress::getStartDate);
        };

    comparator =
        switch (sortCriteria.order()) {
          case ASC -> comparator;
          case DESC -> comparator.reversed();
        };

    return comparator;
  }
}
