package fr.avenirsesr.portfolio.file.domain.exception;

import fr.avenirsesr.portfolio.shared.domain.exception.BusinessException;
import fr.avenirsesr.portfolio.shared.domain.model.enums.EErrorCode;

public class FileSizeTooBigException extends BusinessException {

  public FileSizeTooBigException() {
    super(EErrorCode.MAX_FILE_SIZE_EXCEEDED);
  }

  public FileSizeTooBigException(String customMessage) {
    super(EErrorCode.MAX_FILE_SIZE_EXCEEDED, customMessage);
  }
}
