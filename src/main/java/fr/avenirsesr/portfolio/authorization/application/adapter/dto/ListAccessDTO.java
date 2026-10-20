package fr.avenirsesr.portfolio.authorization.application.adapter.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(requiredProperties = {"canAccessList"})
public record ListAccessDTO(boolean canAccessList) {}
