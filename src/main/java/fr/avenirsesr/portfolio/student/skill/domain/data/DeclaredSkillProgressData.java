package fr.avenirsesr.portfolio.student.skill.domain.data;

import fr.avenirsesr.portfolio.skill.domain.model.Skill;
import fr.avenirsesr.portfolio.student.skill.domain.model.DeclaredSkillProgress;

public record DeclaredSkillProgressData(
    DeclaredSkillProgress declaredSkillProgress,
    Skill skill,
    DeclaredSkillAssociationCount associationsCount) {}
