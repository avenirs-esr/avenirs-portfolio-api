package fr.avenirsesr.portfolio.student.progress.declared.skill.domain.data;

import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.data.DeclaredActivityAssociationData;
import fr.avenirsesr.portfolio.student.progress.declared.experience.domain.data.DeclaredExperienceAssociationData;
import fr.avenirsesr.portfolio.student.trace.domain.data.TraceAssociationData;
import java.util.List;

public record DeclaredSkillAssociationsData(
    List<TraceAssociationData> traceAssociations,
    List<DeclaredActivityAssociationData> declaredActivityAssociations,
    List<DeclaredExperienceAssociationData> declaredExperienceAssociations) {}
