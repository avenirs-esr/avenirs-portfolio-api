package fr.avenirsesr.portfolio.student.progress.domain.model;

import fr.avenirsesr.portfolio.program.domain.model.Skill;
import fr.avenirsesr.portfolio.program.domain.model.TrainingPath;
import fr.avenirsesr.portfolio.program.domain.model.enums.ESkillLevelStatus;
import fr.avenirsesr.portfolio.user.domain.model.Student;
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
public class StudentProgress {
  private final UUID id;
  private final Student student;
  private final TrainingPath trainingPath;

  @Getter(AccessLevel.NONE)
  private final List<SkillLevelProgress> skillLevelProgresses;

  private StudentProgress(
      UUID id,
      Student student,
      TrainingPath trainingPath,
      List<SkillLevelProgress> skillLevelProgresses) {
    this.id = id;
    this.student = student;
    this.trainingPath = trainingPath;
    this.skillLevelProgresses = skillLevelProgresses;
  }

  public static StudentProgress create(
      Student student, TrainingPath trainingPath, List<SkillLevelProgress> skillLevels) {
    return new StudentProgress(UUID.randomUUID(), student, trainingPath, skillLevels);
  }

  public static StudentProgress toDomain(
      UUID id, Student student, TrainingPath trainingPath, List<SkillLevelProgress> skillLevels) {
    return new StudentProgress(id, student, trainingPath, skillLevels);
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

  @Override
  public String toString() {
    return "StudentProgress[%s]".formatted(id);
  }
}
