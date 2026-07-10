package fr.avenirsesr.portfolio.activity.infrastructure.fixture;

import fr.avenirsesr.portfolio.common.data.domain.model.User;
import fr.avenirsesr.portfolio.file.domain.model.File;
import fr.avenirsesr.portfolio.file.domain.model.enums.EFileType;
import fr.avenirsesr.portfolio.user.infrastructure.fixture.UserFixture;
import java.time.Instant;
import java.util.UUID;

public class ActivityBannerFixture {
  private final UUID id = UUID.randomUUID();
  private final EFileType fileType = EFileType.PNG;
  private final String fileName = "activity banner";
  private final long size = 1000L;
  private final int version = 1;
  private final String uri = "exemple.com/image.png";
  private final User uploadedBy = UserFixture.create().toModel();
  private final Instant uploadedAt = Instant.now();

  public static ActivityBannerFixture create() {
    return new ActivityBannerFixture();
  }

  public File toModel() {
    return File.toDomain(
        id,
        fileType,
        fileName,
        size,
        version,
        uri,
        uploadedBy,
        uploadedAt,
        true,
        uploadedAt,
        uploadedAt);
  }
}
