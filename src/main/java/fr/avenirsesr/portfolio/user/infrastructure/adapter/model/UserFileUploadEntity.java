package fr.avenirsesr.portfolio.user.infrastructure.adapter.model;

import fr.avenirsesr.portfolio.shared.infrastructure.adapter.model.AvenirsBaseEntity;
import fr.avenirsesr.portfolio.user.domain.model.enums.EUploadType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "user_file_upload")
@NoArgsConstructor
@Getter
@Setter
public class UserFileUploadEntity extends AvenirsBaseEntity {

  @ManyToOne(optional = false)
  private UserEntity user;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private EUploadType type;

  @Column(nullable = false)
  private String url;

  @Column(nullable = false)
  private Long size;

  @Column(nullable = false, name = "media_type")
  private String mediaType;

  private UserFileUploadEntity(
      UUID id, UserEntity user, EUploadType type, String url, Long size, String mediaType) {
    this.setId(id);
    this.user = user;
    this.type = type;
    this.url = url;
    this.size = size;
    this.mediaType = mediaType;
  }

  public static UserFileUploadEntity of(
      UUID id, UserEntity user, EUploadType type, String url, Long size, String mediaType) {
    return new UserFileUploadEntity(id, user, type, url, size, mediaType);
  }
}
