package fr.avenirsesr.portfolio.file.domain.model.shared;

import fr.avenirsesr.portfolio.shared.domain.model.AvenirsBaseModel;
import fr.avenirsesr.portfolio.user.domain.model.User;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class File extends AvenirsBaseModel {
  private final EFileType fileType;
  private final long size;
  private final int version;
  private boolean isActiveVersion;
  private final String uri;
  private final User uploadedBy;
  private final Instant uploadedAt;

  protected File(
      UUID id,
      EFileType fileType,
      long size,
      int version,
      boolean isActiveVersion,
      String uri,
      User uploadedBy,
      Instant uploadedAt) {
    super(id);
    this.fileType = fileType;
    this.size = size;
    this.version = version;
    this.isActiveVersion = isActiveVersion;
    this.uri = uri;
    this.uploadedBy = uploadedBy;
    this.uploadedAt = uploadedAt;
  }
}
