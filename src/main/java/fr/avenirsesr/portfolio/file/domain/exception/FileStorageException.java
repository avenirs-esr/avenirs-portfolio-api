package fr.avenirsesr.portfolio.file.domain.exception;

import fr.avenirsesr.portfolio.common.error.domain.exception.BusinessException;
import fr.avenirsesr.portfolio.common.error.domain.model.enums.EErrorCode;

public class FileStorageException extends BusinessException {

  public FileStorageException(String message, Throwable cause) {
    super(EErrorCode.FILE_STORAGE_ERROR, message);
    initCause(cause);
  }
}
