package fr.avenirsesr.portfolio.api.domain.model.enums;

public enum ESortField {
  NAME,
  DATE;

  public static ESortField fromString(String value) {
    return ESortField.valueOf(value.toUpperCase());
  }
}
