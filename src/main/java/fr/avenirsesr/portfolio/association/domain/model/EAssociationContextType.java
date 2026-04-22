package fr.avenirsesr.portfolio.association.domain.model;

import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.model.DeclaredActivity;
import fr.avenirsesr.portfolio.student.progress.declared.experience.domain.model.DeclaredExperience;
import fr.avenirsesr.portfolio.student.progress.declared.skill.domain.model.DeclaredSkillProgress;
import fr.avenirsesr.portfolio.trace.domain.model.Trace;

public enum EAssociationContextType {
  TRACE(Trace.class),
  DECLARED_ACTIVITY(DeclaredActivity.class),
  DECLARED_SKILL(DeclaredSkillProgress.class),
  DECLARED_EXPERIENCE(DeclaredExperience.class);

  private final Class<?> contextClass;

  EAssociationContextType(Class<?> contextClass) {
    this.contextClass = contextClass;
  }

  public Class<?> toClass() {
    return contextClass;
  }
}
