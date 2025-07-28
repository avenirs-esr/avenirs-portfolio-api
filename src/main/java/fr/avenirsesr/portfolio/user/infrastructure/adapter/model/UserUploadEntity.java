package fr.avenirsesr.portfolio.user.infrastructure.adapter.model;

import fr.avenirsesr.portfolio.shared.infrastructure.adapter.model.AvenirsBaseEntity;
import fr.avenirsesr.portfolio.user.domain.model.enums.EUploadType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "user_upload")
@NoArgsConstructor
@Getter
@Setter
public class UserUploadEntity extends AvenirsBaseEntity {

  @Column(nullable = false, name = "user_id")
  private UUID userId;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private EUploadType type;

  @Column(nullable = false)
  private String url;

  @Column(nullable = false)
  private Long size;

  @Column(nullable = false, name = "media_type")
  private String mediaType;

  private UserUploadEntity(
      UUID id, UUID userId, EUploadType type, String url, Long size, String mediaType) {
    this.setId(id);
    this.userId = userId;
    this.type = type;
    this.url = url;
    this.size = size;
    this.mediaType = mediaType;
  }

  public static UserUploadEntity of(
      UUID id, UUID userId, EUploadType type, String url, Long size, String mediaType) {
    return new UserUploadEntity(id, userId, type, url, size, mediaType);
  }
}
