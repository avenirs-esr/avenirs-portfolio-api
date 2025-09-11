package fr.avenirsesr.portfolio.ams.infrastructure.adapter.seeder.data;

import fr.avenirsesr.portfolio.ams.domain.port.output.seeder.AmsDataGenerator;
import fr.avenirsesr.portfolio.shared.domain.model.enums.ELanguage;
import fr.avenirsesr.portfolio.shared.domain.port.output.seeder.AbstractDataGenerator;
import fr.avenirsesr.portfolio.shared.infrastructure.adapter.seeder.data.CsvReader;
import java.util.ArrayList;
import java.util.Map;

public class AmsCSVDataGenerator extends AbstractDataGenerator implements AmsDataGenerator {
  private static final String PATH_FRENCH = "seeder/ams.fr.csv";
  private static final String PATH_ENGLISH = "seeder/ams.en.csv";
  private static final String PATH_SPANISH = "seeder/ams.es.csv";
  private static final String DELIMITER = ",";
  private static final Map<ELanguage, ArrayList<CsvAmsDto>> data =
      Map.of(
          ELanguage.FRENCH,
          new CsvReader<CsvAmsDto>()
              .readCSV(
                  DELIMITER,
                  AmsCSVDataGenerator.class.getClassLoader().getResourceAsStream(PATH_FRENCH),
                  values -> new CsvAmsDto(values[0])),
          ELanguage.ENGLISH,
          new CsvReader<CsvAmsDto>()
              .readCSV(
                  DELIMITER,
                  AmsCSVDataGenerator.class.getClassLoader().getResourceAsStream(PATH_ENGLISH),
                  values -> new CsvAmsDto(values[0])),
          ELanguage.SPANISH,
          new CsvReader<CsvAmsDto>()
              .readCSV(
                  DELIMITER,
                  AmsCSVDataGenerator.class.getClassLoader().getResourceAsStream(PATH_SPANISH),
                  values -> new CsvAmsDto(values[0])));

  @Override
  public String title() {
    var element = data.get(getLanguage()).get(getRandom().nextInt(data.size()));
    return element.title();
  }
}
