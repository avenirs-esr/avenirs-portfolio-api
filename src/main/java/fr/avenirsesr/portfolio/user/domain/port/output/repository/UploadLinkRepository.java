package fr.avenirsesr.portfolio.user.domain.port.output.repository;

import fr.avenirsesr.portfolio.user.domain.model.UploadLink;

public interface UploadLinkRepository {
  void save(UploadLink uploadLink);
}
