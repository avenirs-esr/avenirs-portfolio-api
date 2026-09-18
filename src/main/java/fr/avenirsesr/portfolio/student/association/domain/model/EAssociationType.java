package fr.avenirsesr.portfolio.student.association.domain.model;

import fr.avenirsesr.portfolio.student.activity.domain.model.DeclaredActivity;
import fr.avenirsesr.portfolio.student.experience.domain.model.DeclaredExperience;
import fr.avenirsesr.portfolio.student.program.domain.model.DeclaredProgram;
import fr.avenirsesr.portfolio.student.skill.domain.model.DeclaredSkillProgress;
import fr.avenirsesr.portfolio.student.trace.domain.model.Trace;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;
import lombok.Getter;

@Getter
public enum EAssociationType {
  DECLARED_ACTIVITY_TRACE(DeclaredActivity.class, Trace.class),
  DECLARED_ACTIVITY_DECLARED_SKILL(DeclaredActivity.class, DeclaredSkillProgress.class),
  DECLARED_ACTIVITY_DECLARED_EXPERIENCE(DeclaredActivity.class, DeclaredExperience.class),
  TRACE_DECLARED_SKILL(Trace.class, DeclaredSkillProgress.class),
  TRACE_DECLARED_EXPERIENCE(Trace.class, DeclaredExperience.class),
  TRACE_DECLARED_PROGRAM(Trace.class, DeclaredProgram.class),
  DECLARED_EXPERIENCE_DECLARED_SKILL(DeclaredExperience.class, DeclaredSkillProgress.class),
  DECLARED_EXPERIENCE_DECLARED_PROGRAM(DeclaredExperience.class, DeclaredProgram.class),
  DECLARED_PROGRAM_DECLARED_SKILL(DeclaredProgram.class, DeclaredSkillProgress.class);

  private final Class<?> key1;
  private final Class<?> key2;

  EAssociationType(Class<?> key1, Class<?> key2) {
    this.key1 = key1;
    this.key2 = key2;
  }

  public static EAssociationType of(Class<?> clazz, Class<?> associatedClass) {
    return Arrays.stream(values())
        .filter(
            type ->
                (type.key1.equals(clazz) && type.key2.equals(associatedClass))
                    || (type.key2.equals(clazz) && type.key1.equals(associatedClass)))
        .findFirst()
        .orElseThrow(
            () ->
                new IllegalArgumentException(
                    clazz.getSimpleName()
                        + " cannot be associated with "
                        + associatedClass.getSimpleName()));
  }

  public static List<EAssociationType> getAllBy(Class<?> clazz) {
    return Arrays.stream(values()).filter(type -> type.involves(clazz)).toList();
  }

  public boolean involves(Class<?> clazz) {
    return key1.equals(clazz) || key2.equals(clazz);
  }

  public Function<Association, UUID> idExtractorFor(Class<?> subjectClass) {
    if (key1.equals(subjectClass)) return Association::getId1;
    if (key2.equals(subjectClass)) return Association::getId2;
    throw new IllegalArgumentException(subjectClass.getSimpleName() + " is not part of " + name());
  }

  public Class<?> associatedKeyOf(Class<?> subjectClass) {
    if (key1.equals(subjectClass)) return key2;
    if (key2.equals(subjectClass)) return key1;
    throw new IllegalArgumentException(subjectClass.getSimpleName() + " is not part of " + name());
  }

  public Function<Association, UUID> associatedIdExtractorFor(Class<?> subjectClass) {
    return idExtractorFor(associatedKeyOf(subjectClass));
  }
}
