package fr.avenirsesr.portfolio.file.infrastructure.adapter.model;

import fr.avenirsesr.portfolio.file.domain.model.shared.EFileType;
import fr.avenirsesr.portfolio.shared.infrastructure.adapter.model.AvenirsBaseEntity;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.model.UserEntity;
import jakarta.persistence.*;
import java.time.Instant;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@MappedSuperclass
public abstract class FileEntity extends AvenirsBaseEntity {
  @Column(nullable = false, name = "file_type")
  @Enumerated(EnumType.STRING)
  private EFileType fileType;

  @Column(nullable = false)
  private long size;

  @Column(nullable = false)
  private int version;

  @Column(nullable = false, name = "is_active_version")
  private boolean isActiveVersion;

  @Column(nullable = false)
  private String uri;

  @ManyToOne()
  @JoinColumn(name = "uploaded_by", nullable = false)
  private UserEntity uploadedBy;

  @Column(nullable = false, name = "uploaded_at")
  private Instant uploadedAt;
}
