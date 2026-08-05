package fr.avenirsesr.portfolio.student.experience.domain.data;

import fr.avenirsesr.portfolio.student.experience.domain.model.DeclaredExperience;

public record DeclaredExperienceData(
    DeclaredExperience declaredExperience, DeclaredExperienceAssociationCount associationsCount) {}
