package fr.avenirsesr.portfolio.student.skill.domain.data;

import java.util.UUID;

public record DeclaredSkillAssociationData(
    UUID associationId, DeclaredSkillProgressData declaredSkill) {}
