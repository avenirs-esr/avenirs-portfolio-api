package fr.avenirsesr.portfolio.file.domain.exception;

import fr.avenirsesr.portfolio.shared.domain.exception.BusinessException;
import fr.avenirsesr.portfolio.shared.domain.model.enums.EErrorCode;
import java.util.UUID;

public class FileNotFoundException extends BusinessException {

  public FileNotFoundException() {
    super(EErrorCode.FILE_NOT_FOUND);
  }

  public FileNotFoundException(String customMessage) {
    super(EErrorCode.FILE_NOT_FOUND, customMessage);
  }

  public FileNotFoundException(UUID fileId) {
    super(EErrorCode.FILE_NOT_FOUND, "File id %s not found".formatted(fileId));
  }
}
