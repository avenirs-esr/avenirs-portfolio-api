package fr.avenirsesr.portfolio.student.progress.declared.experience.domain.data;

import fr.avenirsesr.portfolio.student.progress.declared.experience.domain.model.DeclaredExperience;
import java.util.UUID;

public record DeclaredExperienceAssociationData(
    UUID associationId, DeclaredExperience declaredExperience) {}
