package fr.avenirsesr.portfolio.student.activity.domain.data;

import fr.avenirsesr.portfolio.student.skill.domain.data.DeclaredSkillAssociationData;
import fr.avenirsesr.portfolio.student.trace.domain.data.TraceAssociationData;
import java.util.List;

public record DeclaredActivityAssociationsData(
    List<TraceAssociationData> traceAssociations,
    List<DeclaredSkillAssociationData> declaredSkillAssociations) {}
