package fr.avenirsesr.portfolio.user.domain.port.output.repository;

import fr.avenirsesr.portfolio.user.domain.model.FileUploadLink;

public interface UploadLinkRepository {
  void save(FileUploadLink fileUploadLink);
}
