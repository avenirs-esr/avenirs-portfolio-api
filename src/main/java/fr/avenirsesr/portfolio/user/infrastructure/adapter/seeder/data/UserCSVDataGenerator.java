package fr.avenirsesr.portfolio.user.infrastructure.adapter.seeder.data;

import fr.avenirsesr.portfolio.shared.domain.port.output.seeder.AbstractDataGenerator;
import fr.avenirsesr.portfolio.shared.infrastructure.adapter.seeder.data.CsvReader;
import fr.avenirsesr.portfolio.user.domain.port.output.seeder.UserDataGenerator;
import java.util.ArrayList;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UserCSVDataGenerator extends AbstractDataGenerator implements UserDataGenerator {
  private static final String PATH = "seeder/users.csv";
  private static final String DELIMITER = ";";
  private static final ArrayList<CsvUserDto> data =
      new CsvReader<CsvUserDto>()
          .readCSV(
              DELIMITER,
              UserCSVDataGenerator.class.getClassLoader().getResourceAsStream(PATH),
              values -> new CsvUserDto(values[0], values[1], values[2], values[3], values[4]));

  @Override
  public String firstName() {
    var element = data.get(getRandom().nextInt(data.size()));
    data.remove(element);
    return element.firstName();
  }

  @Override
  public String lastName() {
    var element = data.get(getRandom().nextInt(data.size()));
    data.remove(element);
    return element.lastName();
  }

  @Override
  public String email() {
    var element = data.get(getRandom().nextInt(data.size()));
    data.remove(element);
    return element.email();
  }

  @Override
  public String studentDescription() {
    var element = data.get(getRandom().nextInt(data.size()));
    data.remove(element);
    return element.studentDescription();
  }

  @Override
  public String teacherDescription() {
    var element = data.get(getRandom().nextInt(data.size()));
    data.remove(element);
    return element.teacherDescription();
  }
}
