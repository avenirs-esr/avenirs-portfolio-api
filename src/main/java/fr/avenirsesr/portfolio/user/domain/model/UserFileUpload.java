package fr.avenirsesr.portfolio.user.domain.model;

import fr.avenirsesr.portfolio.shared.domain.model.AvenirsBaseModel;
import fr.avenirsesr.portfolio.user.domain.model.enums.EUploadType;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserFileUpload extends AvenirsBaseModel {

  private final User user;
  private EUploadType type;
  private String url;
  private Long size;
  private String mediaType;

  private UserFileUpload(
      UUID id, User user, EUploadType type, String url, Long size, String mediaType) {
    super(id);
    this.user = user;
    this.type = type;
    this.url = url;
    this.size = size;
    this.mediaType = mediaType;
  }

  public static UserFileUpload create(
      UUID id, User user, EUploadType type, String url, Long size, String mediaType) {
    return new UserFileUpload(id, user, type, url, size, mediaType);
  }

  public static UserFileUpload toDomain(
      UUID id, User user, EUploadType type, String url, Long size, String mediaType) {
    return new UserFileUpload(id, user, type, url, size, mediaType);
  }
}
