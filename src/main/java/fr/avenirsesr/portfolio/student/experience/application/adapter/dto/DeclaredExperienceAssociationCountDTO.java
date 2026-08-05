package fr.avenirsesr.portfolio.student.experience.application.adapter.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(requiredProperties = {"traceAssociationsCount", "declaredSkillAssociationsCount"})
public record DeclaredExperienceAssociationCountDTO(
    int traceAssociationsCount, int declaredSkillAssociationsCount) {}
