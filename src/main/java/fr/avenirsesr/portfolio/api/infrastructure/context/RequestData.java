package fr.avenirsesr.portfolio.api.infrastructure.context;

import fr.avenirsesr.portfolio.api.domain.model.enums.ELanguage;

public record RequestData(ELanguage preferredLanguage) {}
