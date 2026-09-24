package fr.avenirsesr.portfolio.file.domain.port.output.seeder;

import fr.avenirsesr.portfolio.common.file.domain.model.enums.EFileType;
import fr.avenirsesr.portfolio.common.seeder.domain.port.output.DataGeneratorInterface;

public interface FileDataGenerator extends DataGeneratorInterface {
  String fileName(EFileType fileType);
}
