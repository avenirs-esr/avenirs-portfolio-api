package fr.avenirsesr.portfolio.file.domain.port.output.seeder;

import fr.avenirsesr.portfolio.common.seeder.domain.port.output.DataGeneratorInterface;
import fr.avenirsesr.portfolio.file.domain.model.enums.EFileType;

public interface FileDataGenerator extends DataGeneratorInterface {
  String fileName(EFileType fileType);
}
