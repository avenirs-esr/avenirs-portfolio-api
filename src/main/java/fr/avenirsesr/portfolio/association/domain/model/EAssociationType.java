package fr.avenirsesr.portfolio.association.domain.model;

import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.model.DeclaredActivity;
import fr.avenirsesr.portfolio.student.progress.declared.experience.domain.model.DeclaredExperience;
import fr.avenirsesr.portfolio.student.progress.declared.skill.domain.model.DeclaredSkillProgress;
import fr.avenirsesr.portfolio.trace.domain.model.Trace;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;
import lombok.Getter;

@Getter
public enum EAssociationType {
  DECLARED_ACTIVITY_TRACE(DeclaredActivity.class, Trace.class),
  DECLARED_ACTIVITY_DECLARED_SKILL(DeclaredActivity.class, DeclaredSkillProgress.class),
  TRACE_DECLARED_SKILL(Trace.class, DeclaredSkillProgress.class),
  TRACE_DECLARED_EXPERIENCE(Trace.class, DeclaredExperience.class),
  DECLARED_EXPERIENCE_DECLARED_SKILL(DeclaredExperience.class, DeclaredSkillProgress.class);

  private final Class<?> key1;
  private final Class<?> key2;

  EAssociationType(Class<?> key1, Class<?> key2) {
    this.key1 = key1;
    this.key2 = key2;
  }

  public static List<EAssociationType> getAllBy(Class<?> clazz) {
    return Arrays.stream(values())
        .filter(type -> type.key1.equals(clazz) || type.key2.equals(clazz))
        .toList();
  }

  public Function<Association, UUID> idExtractorFor(Class<?> subjectClass) {
    if (key1.equals(subjectClass)) return Association::getId1;
    if (key2.equals(subjectClass)) return Association::getId2;
    throw new IllegalArgumentException(subjectClass.getSimpleName() + " is not part of " + name());
  }
}
