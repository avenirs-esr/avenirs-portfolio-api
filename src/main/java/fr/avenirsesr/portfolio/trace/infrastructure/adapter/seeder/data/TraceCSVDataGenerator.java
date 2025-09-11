package fr.avenirsesr.portfolio.trace.infrastructure.adapter.seeder.data;

import fr.avenirsesr.portfolio.shared.domain.port.output.seeder.AbstractDataGenerator;
import fr.avenirsesr.portfolio.shared.infrastructure.adapter.seeder.data.CsvReader;
import fr.avenirsesr.portfolio.trace.domain.port.output.seeder.TraceDataGenerator;
import java.util.ArrayList;

public class TraceCSVDataGenerator extends AbstractDataGenerator implements TraceDataGenerator {
  private static final String PATH = "seeder/traces.csv";
  private static final String DELIMITER = ",";
  private static final ArrayList<CsvTraceDto> data =
      new CsvReader<CsvTraceDto>()
          .readCSV(
              DELIMITER,
              TraceCSVDataGenerator.class.getClassLoader().getResourceAsStream(PATH),
              values -> new CsvTraceDto(values[0], values[1], values[2]));

  @Override
  public String traceName() {
    var element = data.get(getRandom().nextInt(data.size()));
    data.remove(element);
    return element.name();
  }

  @Override
  public String traceAiJustification() {
    var element = data.get(getRandom().nextInt(data.size()));
    data.remove(element);
    return element.aiJustification();
  }

  @Override
  public String tracePersonalNote() {
    var element = data.get(getRandom().nextInt(data.size()));
    data.remove(element);
    return element.personalNote();
  }
}
