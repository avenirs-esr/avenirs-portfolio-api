package fr.avenirsesr.portfolio.student.progress.declared.skill.application.adapter.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(requiredProperties = {"traceAssociationsCount", "declaredActivityAssociationsCount"})
public record DeclaredSkillAssociationCountDTO(
    int traceAssociationsCount, int declaredActivityAssociationsCount) {}
