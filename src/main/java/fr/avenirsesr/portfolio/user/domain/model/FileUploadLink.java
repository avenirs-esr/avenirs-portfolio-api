package fr.avenirsesr.portfolio.user.domain.model;

import fr.avenirsesr.portfolio.user.domain.model.enums.EContextType;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FileUploadLink {
  private UUID uploadId;
  private EContextType contextType;
  private UUID contextId;

  private FileUploadLink(UUID uploadId, EContextType contextType, UUID contextId) {
    this.uploadId = uploadId;
    this.contextType = contextType;
    this.contextId = contextId;
  }

  public static FileUploadLink create(UUID uploadId, EContextType contextType, UUID contextId) {
    return new FileUploadLink(uploadId, contextType, contextId);
  }

  public static FileUploadLink toDomain(UUID uploadId, EContextType contextType, UUID contextId) {
    return new FileUploadLink(uploadId, contextType, contextId);
  }
}
