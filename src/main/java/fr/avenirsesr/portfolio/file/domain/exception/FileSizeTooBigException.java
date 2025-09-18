package fr.avenirsesr.portfolio.file.domain.exception;

import fr.avenirsesr.portfolio.common.error.domain.exception.BusinessException;
import fr.avenirsesr.portfolio.common.error.domain.model.enums.EErrorCode;

public class FileSizeTooBigException extends BusinessException {

  public FileSizeTooBigException() {
    super(EErrorCode.MAX_FILE_SIZE_EXCEEDED);
  }

  public FileSizeTooBigException(String customMessage) {
    super(EErrorCode.MAX_FILE_SIZE_EXCEEDED, customMessage);
  }
}
