package fr.avenirsesr.portfolio.activity.infrastructure.fixture;

import fr.avenirsesr.portfolio.common.file.domain.model.enums.EFileType;
import fr.avenirsesr.portfolio.file.domain.model.File;
import fr.avenirsesr.portfolio.user.infrastructure.fixture.UserFixture;
import java.time.Instant;
import java.util.UUID;

public class ActivityBannerFixture {
  private final UUID id = UUID.randomUUID();
  private final EFileType fileType = EFileType.PNG;
  private final String fileName = "activity banner";
  private final long size = 1000L;
  private final String uri = "exemple.com/image.png";
  private final UUID uploadedById = UserFixture.create().toModel().getId();
  private final Instant uploadedAt = Instant.now();

  public static ActivityBannerFixture create() {
    return new ActivityBannerFixture();
  }

  public File toModel() {
    return File.toDomain(
        id, fileType, fileName, size, uri, uploadedById, uploadedAt, false, uploadedAt, uploadedAt);
  }
}
