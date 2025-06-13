package fr.avenirsesr.portfolio.api.infrastructure.adapter.util;

import fr.avenirsesr.portfolio.api.domain.exception.LanguageException;
import fr.avenirsesr.portfolio.api.domain.model.enums.ELanguage;
import fr.avenirsesr.portfolio.api.infrastructure.adapter.model.TranslationEntity;
import java.util.Set;

public class TranslationUtil {

  public static <T extends TranslationEntity> T getTranslation(
      Set<T> translations, ELanguage language, ELanguage fallback) {
    return translations.stream()
        .filter(t -> t.getLanguage().equals(language))
        .findFirst()
        .orElseGet(
            () ->
                translations.stream()
                    .filter(t -> t.getLanguage().equals(fallback))
                    .findFirst()
                    .orElseThrow(
                        LanguageException::new)); // TODO: Get fallback language from configuration
  }
}
