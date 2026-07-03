package fr.avenirsesr.portfolio.file.domain.port.output.repository;

import fr.avenirsesr.portfolio.common.data.domain.port.output.repository.GenericRepositoryPort;
import fr.avenirsesr.portfolio.file.domain.model.File;
import fr.avenirsesr.portfolio.file.domain.model.enums.EFileCategory;
import java.util.List;
import java.util.UUID;

public interface FileRepository extends GenericRepositoryPort<File> {
  List<File> findAllByElement(UUID elementId);

  List<File> findAllByElementIdAndCategory(UUID elementId, EFileCategory fileCategory);
}
