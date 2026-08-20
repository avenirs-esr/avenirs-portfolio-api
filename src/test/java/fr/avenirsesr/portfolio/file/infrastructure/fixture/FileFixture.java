package fr.avenirsesr.portfolio.file.infrastructure.fixture;

import fr.avenirsesr.portfolio.common.data.domain.model.User;
import fr.avenirsesr.portfolio.file.domain.model.File;
import fr.avenirsesr.portfolio.file.domain.model.enums.EFileType;
import fr.avenirsesr.portfolio.user.infrastructure.fixture.UserFixture;
import java.time.Instant;
import java.util.UUID;

public class FileFixture {
  private final UUID id = UUID.randomUUID();
  private final EFileType fileType = EFileType.PDF;
  private final String fileName = "file.pdf";
  private final long size = 1000L;
  private final String uri = "exemple.com/file.pdf";
  private final User uploadedBy = UserFixture.create().toModel();
  private final Instant uploadedAt = Instant.now();

  public static FileFixture create() {
    return new FileFixture();
  }

  public File toModel() {
    return File.toDomain(
        id, fileType, fileName, size, uri, uploadedBy, uploadedAt, false, uploadedAt, uploadedAt);
  }
}
