package fr.avenirsesr.portfolio.user.infrastructure.adapter.model;

import fr.avenirsesr.portfolio.user.domain.model.enums.EContextType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@IdClass(FileUploadLinkId.class)
@Table(name = "file_upload_link")
@NoArgsConstructor
@Getter
@Setter
public class FileUploadLinkEntity {

  @Id
  @Column(nullable = false, name = "upload_id")
  private UUID uploadId;

  @Id
  @Enumerated(EnumType.STRING)
  @Column(nullable = false, name = "context_type")
  private EContextType contextType;

  @Id
  @Column(nullable = false, name = "context_id")
  private UUID contextId;

  private FileUploadLinkEntity(UUID uploadId, EContextType contextType, UUID contextId) {
    this.uploadId = uploadId;
    this.contextType = contextType;
    this.contextId = contextId;
  }

  public static FileUploadLinkEntity of(UUID uploadId, EContextType contextType, UUID contextId) {
    return new FileUploadLinkEntity(uploadId, contextType, contextId);
  }
}
