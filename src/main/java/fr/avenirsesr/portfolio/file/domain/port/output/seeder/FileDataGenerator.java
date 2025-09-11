package fr.avenirsesr.portfolio.file.domain.port.output.seeder;

import fr.avenirsesr.portfolio.file.domain.model.shared.EFileType;
import fr.avenirsesr.portfolio.shared.domain.port.output.seeder.DataGeneratorInterface;

public interface FileDataGenerator extends DataGeneratorInterface {
  String fileName(EFileType fileType);
}
