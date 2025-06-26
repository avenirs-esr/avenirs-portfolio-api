package fr.avenirsesr.portfolio.api.infrastructure.adapter.util;

import fr.avenirsesr.portfolio.api.domain.exception.LanguageException;
import fr.avenirsesr.portfolio.api.domain.model.enums.ELanguage;
import fr.avenirsesr.portfolio.api.infrastructure.adapter.model.TranslationEntity;
import java.util.Set;

public class TranslationUtil {
  // TODO: Get fallback and selected language from configuration
  private static final ELanguage FALLBACK_LANGUAGE = ELanguage.FRENCH;
  private static final ELanguage SELECTED_LANGUAGE = ELanguage.FRENCH;

  public static <T extends TranslationEntity> T getTranslation(Set<T> translations) {
    return translations.stream()
        .filter(t -> t.getLanguage().equals(SELECTED_LANGUAGE))
        .findFirst()
        .orElseGet(
            () ->
                translations.stream()
                    .filter(t -> t.getLanguage().equals(FALLBACK_LANGUAGE))
                    .findFirst()
                    .orElseThrow(LanguageException::new));
  }
}
