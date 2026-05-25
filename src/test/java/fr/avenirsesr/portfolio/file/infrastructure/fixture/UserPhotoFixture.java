package fr.avenirsesr.portfolio.file.infrastructure.fixture;

import fr.avenirsesr.portfolio.common.data.domain.model.User;
import fr.avenirsesr.portfolio.common.data.domain.model.enums.EUserCategory;
import fr.avenirsesr.portfolio.file.domain.model.EFileType;
import fr.avenirsesr.portfolio.file.domain.model.EUserPhotoType;
import java.time.Instant;
import java.util.UUID;

public class UserPhotoFixture {
  private UUID id;
  private User user;
  private EUserCategory userCategory;
  private EUserPhotoType userPhotoType;
  private String fileName;
  private EFileType fileType;
  private long size;
  private int version;
  private boolean isActiveVersion;
  private String uri;
  private User uploadedBy;
  private Instant uploadedAt;
}
