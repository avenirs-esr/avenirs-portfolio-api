package fr.avenirsesr.portfolio.file.infrastructure.adapter.seeder.data;

import fr.avenirsesr.portfolio.file.domain.model.shared.EFileType;
import fr.avenirsesr.portfolio.file.domain.port.output.seeder.FileDataGenerator;
import fr.avenirsesr.portfolio.shared.domain.port.output.seeder.AbstractDataGenerator;
import fr.avenirsesr.portfolio.shared.infrastructure.adapter.seeder.data.CsvReader;
import java.util.ArrayList;

public class FileCSVDataGenerator extends AbstractDataGenerator implements FileDataGenerator {
  private static final String PATH = "seeder/files.csv";
  private static final String DELIMITER = ",";
  private static final ArrayList<CsvFileDto> data =
      new CsvReader<CsvFileDto>()
          .readCSV(
              DELIMITER,
              FileCSVDataGenerator.class.getClassLoader().getResourceAsStream(PATH),
              values -> new CsvFileDto(values[0]));

  @Override
  public String fileName(EFileType fileType) {
    var element = data.get(getRandom().nextInt(data.size()));
    return element.fileName();
  }
}
