package fr.avenirsesr.portfolio.student.skill.domain.data;

import fr.avenirsesr.portfolio.student.activity.domain.data.DeclaredActivityAssociationData;
import fr.avenirsesr.portfolio.student.experience.domain.data.DeclaredExperienceAssociationData;
import fr.avenirsesr.portfolio.student.trace.domain.data.TraceAssociationData;
import java.util.List;

public record DeclaredSkillAssociationsData(
    List<TraceAssociationData> traceAssociations,
    List<DeclaredActivityAssociationData> declaredActivityAssociations,
    List<DeclaredExperienceAssociationData> declaredExperienceAssociations) {}
