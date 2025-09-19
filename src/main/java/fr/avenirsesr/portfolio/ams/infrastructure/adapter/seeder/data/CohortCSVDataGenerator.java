package fr.avenirsesr.portfolio.ams.infrastructure.adapter.seeder.data;

import fr.avenirsesr.portfolio.ams.domain.port.output.seeder.CohortDataGenerator;
import fr.avenirsesr.portfolio.common.seeder.domain.port.output.DataGenerator;
import fr.avenirsesr.portfolio.common.seeder.infrastructure.adapter.data.CsvReader;
import java.util.ArrayList;

public class CohortCSVDataGenerator extends DataGenerator implements CohortDataGenerator {
  private static final String PATH = "seeder/cohorts.csv";
  private static final String DELIMITER = ",";
  private static final ArrayList<CsvCohortDto> data =
      new CsvReader<CsvCohortDto>()
          .readCSV(
              DELIMITER,
              CohortCSVDataGenerator.class.getClassLoader().getResourceAsStream(PATH),
              values -> new CsvCohortDto(values[0], values[1]));

  @Override
  public String name() {
    var element = data.get(getRandom().nextInt(data.size()));
    return element.name();
  }

  @Override
  public String description() {
    var element = data.get(getRandom().nextInt(data.size()));
    return element.description();
  }
}
