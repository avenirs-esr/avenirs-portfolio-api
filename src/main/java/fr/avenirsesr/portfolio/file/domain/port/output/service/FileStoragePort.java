package fr.avenirsesr.portfolio.file.domain.port.output.service;

import fr.avenirsesr.portfolio.file.domain.model.FileResource;
import java.io.IOException;

public interface FileStoragePort {
  String upload(FileResource fileResource) throws IOException;
}
