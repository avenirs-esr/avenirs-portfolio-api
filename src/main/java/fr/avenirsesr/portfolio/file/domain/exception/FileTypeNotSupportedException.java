package fr.avenirsesr.portfolio.file.domain.exception;

import fr.avenirsesr.portfolio.common.error.domain.exception.BusinessException;
import fr.avenirsesr.portfolio.common.error.domain.model.enums.EErrorCode;

public class FileTypeNotSupportedException extends BusinessException {

  public FileTypeNotSupportedException() {
    super(EErrorCode.UNKNOWN_FILE_TYPE);
  }

  public FileTypeNotSupportedException(String customMessage) {
    super(EErrorCode.UNKNOWN_FILE_TYPE, customMessage);
  }
}
