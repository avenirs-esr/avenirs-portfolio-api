package fr.avenirsesr.portfolio.api.domain.model.enums;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

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

  private static final Map<String, ELanguage> BY_CODE =
      Arrays.stream(values()).collect(Collectors.toMap(ELanguage::getCode, Function.identity()));

  public static ELanguage fromCode(String code) {
    return Optional.ofNullable(BY_CODE.get(code))
        .orElseThrow(() -> new IllegalArgumentException("Unknown language code: " + code));
  }
}
