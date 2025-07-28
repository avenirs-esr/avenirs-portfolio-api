package fr.avenirsesr.portfolio.file.domain.model;

import fr.avenirsesr.portfolio.shared.domain.model.AvenirsBaseModel;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class File extends AvenirsBaseModel {
  private final EFileType attachmentType;
  private final long size;
  private final int version;
  private boolean isActiveVersion;
  private final Instant uploadedAt;
  private final String uri;

  protected File(
      UUID id,
      EFileType attachmentType,
      long size,
      int version,
      boolean isActiveVersion,
      Instant uploadedAt,
      String uri) {
    super(id);
    this.attachmentType = attachmentType;
    this.size = size;
    this.version = version;
    this.isActiveVersion = isActiveVersion;
    this.uploadedAt = uploadedAt;
    this.uri = uri;
  }
}
