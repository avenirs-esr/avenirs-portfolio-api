package fr.avenirsesr.portfolio.user.infrastructure.adapter.model;

import fr.avenirsesr.portfolio.user.domain.model.enums.EContextType;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

public class UploadLinkId implements Serializable {

  private UUID uploadId;
  private EContextType contextType;
  private UUID contextId;

  public UploadLinkId() {}

  public UploadLinkId(UUID uploadId, EContextType contextType, UUID contextId) {
    this.uploadId = uploadId;
    this.contextType = contextType;
    this.contextId = contextId;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof UploadLinkId)) return false;
    UploadLinkId that = (UploadLinkId) o;
    return Objects.equals(uploadId, that.uploadId)
        && contextType == that.contextType
        && Objects.equals(contextId, that.contextId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(uploadId, contextType, contextId);
  }
}
