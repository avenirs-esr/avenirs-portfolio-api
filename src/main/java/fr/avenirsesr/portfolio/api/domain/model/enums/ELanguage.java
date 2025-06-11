package fr.avenirsesr.portfolio.api.domain.model.enums;

public enum ELanguage {
  FRENCH("fr_FR"),
  ENGLISH("en_US"),
  SPANISH("es_ES");

  private final String code;

  ELanguage(String code) {
    this.code = code;
  }

  public String getCode() {
    return code;
  }

  public static ELanguage fromCode(String code) {
    for (ELanguage language : values()) {
      if (language.getCode().equalsIgnoreCase(code)) {
        return language;
      }
    }
    throw new IllegalArgumentException("Unknown language code: " + code);
  }
}
