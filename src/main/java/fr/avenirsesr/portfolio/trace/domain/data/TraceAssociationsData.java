package fr.avenirsesr.portfolio.trace.domain.data;

import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.model.DeclaredActivity;
import java.util.List;
import java.util.UUID;

public record TraceAssociationsData(
    List<SkillLevelAssociationData> skillLevelAssociations,
    List<DeclaredSkillAssociationData> declaredSkillAssociations,
    List<DeclaredActivityAssociationData> declaredActivityAssociations) {
  public record DeclaredActivityAssociationData(
      UUID associationId, DeclaredActivity declaredActivity) {}
}
