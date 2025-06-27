package fr.avenirsesr.portfolio.api.infrastructure.adapter.util;

import fr.avenirsesr.portfolio.api.domain.exception.LanguageException;
import fr.avenirsesr.portfolio.api.domain.model.enums.ELanguage;
import fr.avenirsesr.portfolio.api.infrastructure.adapter.model.TranslationEntity;
import fr.avenirsesr.portfolio.api.infrastructure.context.RequestContext;
import fr.avenirsesr.portfolio.api.infrastructure.context.RequestData;
import java.util.Optional;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TranslationUtil {

  public static <T extends TranslationEntity> T getTranslation(Set<T> translations) {
    var preferredLanguage =
        Optional.ofNullable(RequestContext.get())
            .map(RequestData::preferredLanguage)
            .orElse(ELanguage.FALLBACK);

    return translations.stream()
        .filter(t -> t.getLanguage().equals(preferredLanguage))
        .findFirst()
        .orElseGet(
            () ->
                translations.stream()
                    .filter(t -> t.getLanguage().equals(ELanguage.FALLBACK))
                    .findFirst()
                    .orElseThrow(LanguageException::new));
  }
}
