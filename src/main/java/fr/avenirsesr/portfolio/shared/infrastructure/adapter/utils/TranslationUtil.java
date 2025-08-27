package fr.avenirsesr.portfolio.shared.infrastructure.adapter.utils;

import fr.avenirsesr.portfolio.shared.domain.exception.LanguageException;
import fr.avenirsesr.portfolio.shared.domain.model.enums.ELanguage;
import fr.avenirsesr.portfolio.shared.infrastructure.adapter.model.TranslationEntity;
import fr.avenirsesr.portfolio.shared.infrastructure.context.RequestContext;
import fr.avenirsesr.portfolio.shared.infrastructure.context.RequestData;
import java.util.Optional;
import java.util.Set;

public interface TranslationUtil {

  static <T extends TranslationEntity> T getTranslation(Set<T> translations) {
    var preferredLanguage =
        Optional.ofNullable(RequestContext.get())
            .map(RequestData::preferredLanguage)
            .orElse(ELanguage.FALLBACK);

    return getTranslation(translations, preferredLanguage);
  }

  static <T extends TranslationEntity> T getTranslation(
      Set<T> translations, ELanguage selectedLanguage) {
    return translations.stream()
        .filter(t -> t.getLanguage().equals(selectedLanguage))
        .findFirst()
        .orElseThrow(
            () ->
                new LanguageException(
                    String.format("Fallback language [%s] not setup", ELanguage.FALLBACK)));
  }
}
