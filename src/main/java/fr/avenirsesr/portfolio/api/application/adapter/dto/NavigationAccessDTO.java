package fr.avenirsesr.portfolio.api.application.adapter.dto;

public record NavigationAccessDTO(AccessInfoAPC APC, AccessInfoLifeProject LIFE_PROJECT) {
  public record AccessInfoAPC(boolean enabledByInstitution, boolean hasProgram) {}

  public record AccessInfoLifeProject(boolean enabledByInstitution) {}
}
