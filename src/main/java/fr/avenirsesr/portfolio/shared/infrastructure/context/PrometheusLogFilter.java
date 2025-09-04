package fr.avenirsesr.portfolio.shared.infrastructure.context;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.turbo.TurboFilter;
import ch.qos.logback.core.spi.FilterReply;
import org.slf4j.Marker;

public class PrometheusLogFilter extends TurboFilter {

  @Override
  public FilterReply decide(
      Marker marker, Logger logger, Level level, String format, Object[] params, Throwable t) {
    if (format != null && format.contains("/actuator/prometheus") && level == Level.DEBUG) {
      return FilterReply.DENY;
    }
    return FilterReply.NEUTRAL;
  }
}
