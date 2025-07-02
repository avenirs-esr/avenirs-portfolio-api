package fr.avenirsesr.portfolio.navigation.access.application.adapter.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(requiredProperties = {"APC", "LIFE_PROJECT"})
public record NavigationAccessDTO(AccessInfoAPC APC, AccessInfoLifeProject LIFE_PROJECT) {
  @Schema(requiredProperties = {"enabledByInstitution", "hasProgram"})
  public record AccessInfoAPC(boolean enabledByInstitution, boolean hasProgram) {}

  @Schema(requiredProperties = {"enabledByInstitution"})
  public record AccessInfoLifeProject(boolean enabledByInstitution) {}
}
