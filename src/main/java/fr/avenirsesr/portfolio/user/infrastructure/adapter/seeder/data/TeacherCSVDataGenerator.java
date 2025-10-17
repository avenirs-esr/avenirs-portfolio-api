package fr.avenirsesr.portfolio.user.infrastructure.adapter.seeder.data;

import fr.avenirsesr.portfolio.common.seeder.domain.port.output.DataGenerator;
import fr.avenirsesr.portfolio.common.seeder.infrastructure.adapter.data.CsvReader;
import fr.avenirsesr.portfolio.user.domain.port.output.seeder.TeacherDataGenerator;
import java.util.ArrayList;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TeacherCSVDataGenerator extends DataGenerator implements TeacherDataGenerator {
  private static final String PATH = "seeder/users.csv";
  private static final String DELIMITER = ";";
  private static final ArrayList<CsvUserDto> data =
      new CsvReader<CsvUserDto>()
          .readCSV(
              DELIMITER,
              TeacherCSVDataGenerator.class.getClassLoader().getResourceAsStream(PATH),
              values -> new CsvUserDto(values[0], values[1], values[2], values[3], values[4]));

  @Override
  public String teacherDescription() {
    var element = data.get(getRandom().nextInt(data.size()));
    data.remove(element);
    return element.teacherDescription();
  }
}
