package fr.avenirsesr.portfolio.student.progress.declared.experience.domain.data;

import fr.avenirsesr.portfolio.student.progress.declared.experience.domain.model.DeclaredExperience;

public record DeclaredExperienceData(
    DeclaredExperience declaredExperience, DeclaredExperienceAssociationCount associationsCount) {}
