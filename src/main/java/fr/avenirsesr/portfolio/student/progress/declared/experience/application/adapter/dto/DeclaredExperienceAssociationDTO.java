package fr.avenirsesr.portfolio.student.progress.declared.experience.application.adapter.dto;

import java.util.UUID;

public record DeclaredExperienceAssociationDTO(
    UUID associationId, DeclaredExperienceViewDTO declaredExperience) {}
