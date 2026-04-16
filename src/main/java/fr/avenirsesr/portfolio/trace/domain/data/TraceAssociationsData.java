package fr.avenirsesr.portfolio.trace.domain.data;

import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.data.DeclaredActivityAssociationData;
import fr.avenirsesr.portfolio.student.progress.declared.experience.domain.data.DeclaredExperienceAssociationData;
import fr.avenirsesr.portfolio.student.progress.declared.skill.domain.data.DeclaredSkillAssociationData;
import java.util.List;

public record TraceAssociationsData(
    List<DeclaredActivityAssociationData> declaredActivityAssociations,
    List<DeclaredSkillAssociationData> declaredSkillAssociations,
    List<DeclaredExperienceAssociationData> declaredExperienceAssociations) {}
