package fr.avenirsesr.portfolio.trace.domain.port.output.seeder;

import fr.avenirsesr.portfolio.shared.domain.port.output.seeder.DataGeneratorInterface;

public interface TraceDataGenerator extends DataGeneratorInterface {
  String traceName();

  String traceAiJustification();

  String tracePersonalNote();
}
