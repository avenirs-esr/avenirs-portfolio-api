package fr.avenirsesr.portfolio.file.domain.port.input;

import fr.avenirsesr.portfolio.file.domain.model.EFileCategory;
import fr.avenirsesr.portfolio.file.domain.model.File;
import fr.avenirsesr.portfolio.file.domain.model.FileDownload;
import java.util.UUID;

public interface FileResourceService {
  File upload(
      UUID elementId,
      EFileCategory fileCategory,
      String fileName,
      String mimeType,
      long size,
      byte[] content);

  File getFile(UUID fileId);

  byte[] fetchContent(UUID fileId);

  FileDownload download(UUID fileId);
}
