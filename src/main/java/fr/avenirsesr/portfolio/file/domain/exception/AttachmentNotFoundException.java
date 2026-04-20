package fr.avenirsesr.portfolio.file.domain.exception;

import fr.avenirsesr.portfolio.common.error.domain.exception.BusinessException;
import fr.avenirsesr.portfolio.common.error.domain.model.enums.EErrorCode;
import java.util.UUID;

public class AttachmentNotFoundException extends BusinessException {

  public AttachmentNotFoundException() {
    super(EErrorCode.ATTACHMENT_NOT_FOUND);
  }

  public AttachmentNotFoundException(String customMessage) {
    super(EErrorCode.ATTACHMENT_NOT_FOUND, customMessage);
  }

  public AttachmentNotFoundException(UUID attachmentId) {
    super(EErrorCode.ATTACHMENT_NOT_FOUND, "Attachment id %s not found".formatted(attachmentId));
  }
}
