package fr.avenirsesr.portfolio.student.skill.application.adapter.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(requiredProperties = {"traceAssociationsCount", "declaredActivityAssociationsCount"})
public record SkillProgressAssociationCountDTO(
    int traceAssociationsCount, int declaredActivityAssociationsCount) {}
