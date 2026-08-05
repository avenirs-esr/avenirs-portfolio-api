package fr.avenirsesr.portfolio.student.trace.domain.port.output.seeder;

import fr.avenirsesr.portfolio.common.seeder.domain.port.output.DataGeneratorInterface;

public interface TraceDataGenerator extends DataGeneratorInterface {
  String traceName();

  String traceAiJustification();

  String tracePersonalNote();

  String traceLink();
}
