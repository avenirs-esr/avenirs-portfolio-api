package fr.avenirsesr.portfolio.backoffice.configuration.trace.domain.model;

import fr.avenirsesr.portfolio.backoffice.configuration.shared.domain.model.EConfiguration;

public enum ETraceConfiguration implements EConfiguration {
  MAX_REMINING_DAYS,
  MAX_REMINING_DAYS_BEFORE_WARNING,
  MAX_REMINING_DAYS_BEFORE_CRITICAL
}
