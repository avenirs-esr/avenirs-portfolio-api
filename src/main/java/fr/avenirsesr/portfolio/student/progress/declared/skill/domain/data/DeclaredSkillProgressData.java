package fr.avenirsesr.portfolio.student.progress.declared.skill.domain.data;

import fr.avenirsesr.portfolio.student.progress.declared.skill.domain.model.DeclaredSkillProgress;

public record DeclaredSkillProgressData(
    DeclaredSkillProgress declaredSkillProgress, DeclaredSkillAssociationCount associationsCount) {}
