package fr.avenirsesr.portfolio.program.infrastructure.adapter.seeder.data;

import fr.avenirsesr.portfolio.common.language.domain.model.enums.ELanguage;
import fr.avenirsesr.portfolio.common.seeder.domain.port.output.DataGenerator;
import fr.avenirsesr.portfolio.common.seeder.infrastructure.adapter.data.CsvReader;
import fr.avenirsesr.portfolio.program.domain.port.output.seeder.ProgramDataGenerator;
import java.util.ArrayList;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ProgramCSVDataGenerator extends DataGenerator implements ProgramDataGenerator {
  private static final String PATH_FRENCH = "seeder/programs.fr.csv";
  private static final String PATH_ENGLISH = "seeder/programs.en.csv";
  private static final String PATH_SPANISH = "seeder/programs.es.csv";
  private static final String DELIMITER = ",";
  private static final Map<ELanguage, ArrayList<CsvProgramDto>> data =
      Map.of(
          ELanguage.FRENCH,
          new CsvReader<CsvProgramDto>()
              .readCSV(
                  DELIMITER,
                  ProgramCSVDataGenerator.class.getClassLoader().getResourceAsStream(PATH_FRENCH),
                  values ->
                      new CsvProgramDto(values[0], values[1], values[2], values[3], values[4])),
          ELanguage.ENGLISH,
          new CsvReader<CsvProgramDto>()
              .readCSV(
                  DELIMITER,
                  ProgramCSVDataGenerator.class.getClassLoader().getResourceAsStream(PATH_ENGLISH),
                  values ->
                      new CsvProgramDto(values[0], values[1], values[2], values[3], values[4])),
          ELanguage.SPANISH,
          new CsvReader<CsvProgramDto>()
              .readCSV(
                  DELIMITER,
                  ProgramCSVDataGenerator.class.getClassLoader().getResourceAsStream(PATH_SPANISH),
                  values ->
                      new CsvProgramDto(values[0], values[1], values[2], values[3], values[4])));

  @Override
  public String university() {
    var element = data.get(getLanguage()).get(getRandom().nextInt(data.get(getLanguage()).size()));
    return element.university();
  }

  @Override
  public String program() {
    var element = data.get(getLanguage()).get(getRandom().nextInt(data.get(getLanguage()).size()));
    return element.program();
  }

  @Override
  public String skill() {
    var element = data.get(getLanguage()).get(getRandom().nextInt(data.get(getLanguage()).size()));
    return element.skill();
  }

  @Override
  public String skillLevelName() {
    try {
      var element =
          data.get(getLanguage()).get(getRandom().nextInt(data.get(getLanguage()).size()));
      return element.skillLevelName();
    } catch (Exception e) {
      log.error(e.getMessage());
      throw new RuntimeException(e);
    }
  }

  @Override
  public String skillLevelDescription() {
    var element = data.get(getLanguage()).get(getRandom().nextInt(data.get(getLanguage()).size()));
    return element.SkillLevelDescription();
  }
}
