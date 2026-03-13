package fr.avenirsesr.portfolio.trace.domain.data;

import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.model.DeclaredActivity;
import fr.avenirsesr.portfolio.student.progress.declared.skill.domain.model.DeclaredSkillProgress;
import java.util.List;
import java.util.UUID;

public record TraceAssociationsData(
    List<DeclaredActivityAssociationData> declaredActivityAssociations,
    List<DeclaredSkillAssociationData> declaredSkillAssociations) {

  public record DeclaredActivityAssociationData(
      UUID associationId, DeclaredActivity declaredActivity) {}

  public record DeclaredSkillAssociationData(
      UUID associationId, DeclaredSkillProgress declaredSkill) {}
}
