package fr.avenirsesr.portfolio.student.progress.declared.skill.domain.data;

import fr.avenirsesr.portfolio.student.progress.declared.skill.domain.model.DeclaredSkillProgress;
import java.util.UUID;

public record DeclaredSkillAssociationData(
    UUID associationId, DeclaredSkillProgress declaredSkill) {}
