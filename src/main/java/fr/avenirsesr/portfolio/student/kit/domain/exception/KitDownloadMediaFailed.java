package fr.avenirsesr.portfolio.student.kit.domain.exception;

import fr.avenirsesr.portfolio.common.error.domain.exception.BusinessException;
import fr.avenirsesr.portfolio.common.error.domain.model.enums.EErrorCode;

public class KitDownloadMediaFailed extends BusinessException {
  public KitDownloadMediaFailed() {
    super(EErrorCode.KIT_DOWNLOAD_MEDIA_FAILED);
  }
}
