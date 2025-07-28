package fr.avenirsesr.portfolio.user.domain.model;

import fr.avenirsesr.portfolio.user.domain.model.enums.EContextType;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UploadLink {
  private UUID uploadId;
  private EContextType contextType;
  private UUID contextId;

  private UploadLink(UUID uploadId, EContextType contextType, UUID contextId) {
    this.uploadId = uploadId;
    this.contextType = contextType;
    this.contextId = contextId;
  }

  public static UploadLink create(UUID uploadId, EContextType contextType, UUID contextId) {
    return new UploadLink(uploadId, contextType, contextId);
  }

  public static UploadLink toDomain(UUID uploadId, EContextType contextType, UUID contextId) {
    return new UploadLink(uploadId, contextType, contextId);
  }
}
