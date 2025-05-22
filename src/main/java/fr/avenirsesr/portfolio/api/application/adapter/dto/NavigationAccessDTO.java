package fr.avenirsesr.portfolio.api.application.adapter.dto;

public record NavigationAccessDTO(AccessInfo APC, AccessInfo LIFE_PROJECT) {
  public record AccessInfo(boolean enabledByInstitution, boolean hasProgram) {}
}
