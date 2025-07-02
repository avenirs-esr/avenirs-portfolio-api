package fr.avenirsesr.portfolio.shared.domain.model.enums;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public enum ELanguage {
  FRENCH("fr_FR"),
  ENGLISH("en_US"),
  SPANISH("es_ES");

  private final String code;

  public static final ELanguage FALLBACK = FRENCH;

  ELanguage(String code) {
    this.code = code;
  }

  public String getCode() {
    return code;
  }

  private static final Map<String, ELanguage> BY_CODE =
      Arrays.stream(values()).collect(Collectors.toMap(ELanguage::getCode, Function.identity()));

  public static ELanguage fromCode(String code) {
    var lang = Optional.ofNullable(BY_CODE.get(code));

    if (lang.isEmpty()) {
      log.error("Unknown language code: {} - using fallback language {}", code, FALLBACK);
    }

    return lang.orElse(FALLBACK);
  }
}
