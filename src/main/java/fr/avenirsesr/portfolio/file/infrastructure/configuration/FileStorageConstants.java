package fr.avenirsesr.portfolio.file.infrastructure.configuration;

import fr.avenirsesr.portfolio.file.domain.model.enums.EFileType;
import jakarta.annotation.PostConstruct;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
public class FileStorageConstants {
  @Value("${file.storage.local-path}")
  private String storagePath;

  public static String STORAGE_PATH;

  public static UUID PLACEHOLDER_FILE_UUID =
      UUID.fromString("00000000-0000-0000-0000-000000000000");

  // --- Users ---
  @Value("${file.storage.profile.default-path}")
  private String profileDefaultPath;

  @Value("${file.storage.cover.default-path}")
  private String coverDefaultPath;

  @Value("${file.storage.endpoint-prefix}")
  private String defaultPhotoEndpointPrefix;

  @Value("${file.storage.profile.default-endpoint}")
  private String defaultProfileFileUrl;

  @Value("${file.storage.cover.default-endpoint}")
  private String defaultCoverFileUrl;

  public static String PROFILE_DEFAULT_PATH;
  public static EFileType PROFILE_DEFAULT_FILE_TYPE = EFileType.PNG;
  public static String COVER_DEFAULT_PATH;
  public static EFileType COVER_DEFAULT_FILE_TYPE = EFileType.PNG;

  public static String PHOTO_ENDPOINT_PREFIX;
  public static String DEFAULT_PROFILE_FILE_URL;
  public static String DEFAULT_COVER_FILE_URL;

  /**
   * Builds the URL serving the content of the given file, handled by {@code StorageController}.
   *
   * <p>This is the only supported way to expose a file: {@code File.uri} holds a storage locator
   * that is meaningful to the storage adapter alone, never to a client.
   */
  public static String publicUrlOf(UUID fileId) {
    return PHOTO_ENDPOINT_PREFIX + "/" + fileId;
  }

  @PostConstruct
  private void init() {
    STORAGE_PATH = storagePath;
    PROFILE_DEFAULT_PATH = profileDefaultPath;
    COVER_DEFAULT_PATH = coverDefaultPath;
    PHOTO_ENDPOINT_PREFIX = defaultPhotoEndpointPrefix;
    DEFAULT_PROFILE_FILE_URL = PHOTO_ENDPOINT_PREFIX + defaultProfileFileUrl;
    DEFAULT_COVER_FILE_URL = PHOTO_ENDPOINT_PREFIX + defaultCoverFileUrl;
  }
}
