package fr.avenirsesr.portfolio.shared.infrastructure.context;

import fr.avenirsesr.portfolio.shared.domain.model.enums.ELanguage;

public record RequestData(ELanguage preferredLanguage) {}
