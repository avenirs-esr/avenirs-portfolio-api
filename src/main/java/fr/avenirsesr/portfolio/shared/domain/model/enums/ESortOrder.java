package fr.avenirsesr.portfolio.shared.domain.model.enums;

public enum ESortOrder {
  ASC,
  DESC;

  public static ESortOrder fromString(String value) {
    return ESortOrder.valueOf(value.toUpperCase());
  }
}
