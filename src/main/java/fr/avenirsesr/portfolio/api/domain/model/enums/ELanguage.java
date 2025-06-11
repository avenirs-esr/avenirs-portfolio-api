package fr.avenirsesr.portfolio.api.domain.model.enums;

import fr.avenirsesr.portfolio.api.domain.exception.LanguageException;

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
    throw new LanguageException();
  }
}
