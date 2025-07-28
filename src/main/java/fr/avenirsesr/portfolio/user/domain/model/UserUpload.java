package fr.avenirsesr.portfolio.user.domain.model;

import fr.avenirsesr.portfolio.shared.domain.model.AvenirsBaseModel;
import fr.avenirsesr.portfolio.user.domain.model.enums.EUploadType;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserUpload extends AvenirsBaseModel {

  private UUID userId;
  private EUploadType type;
  private String url;
  private Long size;
  private String mediaType;

  private UserUpload(
      UUID id, UUID userId, EUploadType type, String url, Long size, String mediaType) {
    super(id);
    this.userId = userId;
    this.type = type;
    this.url = url;
    this.size = size;
    this.mediaType = mediaType;
  }

  public static UserUpload create(
      UUID id, UUID userId, EUploadType type, String url, Long size, String mediaType) {
    return new UserUpload(id, userId, type, url, size, mediaType);
  }

  public static UserUpload toDomain(
      UUID id, UUID userId, EUploadType type, String url, Long size, String mediaType) {
    return new UserUpload(id, userId, type, url, size, mediaType);
  }
}
