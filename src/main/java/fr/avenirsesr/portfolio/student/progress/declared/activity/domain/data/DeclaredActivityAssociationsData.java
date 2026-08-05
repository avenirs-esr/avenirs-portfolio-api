package fr.avenirsesr.portfolio.student.progress.declared.activity.domain.data;

import fr.avenirsesr.portfolio.student.progress.declared.skill.domain.data.DeclaredSkillAssociationData;
import fr.avenirsesr.portfolio.student.trace.domain.data.TraceAssociationData;
import java.util.List;

public record DeclaredActivityAssociationsData(
    List<TraceAssociationData> traceAssociations,
    List<DeclaredSkillAssociationData> declaredSkillAssociations) {}
