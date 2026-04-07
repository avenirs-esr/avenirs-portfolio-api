package fr.avenirsesr.portfolio.student.progress.declared.activity.domain.data;

import fr.avenirsesr.portfolio.student.progress.declared.skill.domain.model.DeclaredSkillProgress;
import fr.avenirsesr.portfolio.trace.domain.model.Trace;
import java.util.List;
import java.util.UUID;

public record DeclaredActivityAssociationsData(
    List<DeclaredActivityTraceAssociationData> traceAssociations,
    List<DeclaredActivityDeclaredSkillAssociationData> declaredSkillAssociations) {
  public record DeclaredActivityTraceAssociationData(UUID associationId, Trace trace) {}

  public record DeclaredActivityDeclaredSkillAssociationData(
      UUID associationId, DeclaredSkillProgress declaredSkill) {}
}
