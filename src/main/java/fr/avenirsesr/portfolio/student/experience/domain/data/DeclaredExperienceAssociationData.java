package fr.avenirsesr.portfolio.student.experience.domain.data;

import fr.avenirsesr.portfolio.student.experience.domain.model.DeclaredExperience;
import java.util.UUID;

public record DeclaredExperienceAssociationData(
    UUID associationId, DeclaredExperience declaredExperience) {}
