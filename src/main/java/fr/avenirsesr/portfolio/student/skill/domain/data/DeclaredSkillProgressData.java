package fr.avenirsesr.portfolio.student.skill.domain.data;

import fr.avenirsesr.portfolio.student.skill.domain.model.DeclaredSkillProgress;

public record DeclaredSkillProgressData(
    DeclaredSkillProgress declaredSkillProgress, DeclaredSkillAssociationCount associationsCount) {}
