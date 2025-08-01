package fr.avenirsesr.portfolio.file.infrastructure.configuration;

import fr.avenirsesr.portfolio.file.domain.model.shared.EFileType;
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
  @Value("${file.storage.user.profile.default-path}")
  private String userProfileDefaultPath;

  @Value("${file.storage.user.cover.default-path}")
  private String userCoverDefaultPath;

  @Value("${file.storage.user.endpoint-prefix}")
  private String photoEndpointPrefix;

  @Value("${file.storage.user.profile.default-endpoint}")
  private String defaultProfileFileUrl;

  @Value("${file.storage.user.cover.default-endpoint}")
  private String defaultCoverFileUrl;

  public static String USER_PROFILE_DEFAULT_PATH;
  public static EFileType USER_PROFILE_DEFAULT_FILE_TYPE = EFileType.PNG;
  public static String USER_COVER_DEFAULT_PATH;
  public static EFileType USER_COVER_DEFAULT_FILE_TYPE = EFileType.PNG;

  public static String PHOTO_ENDPOINT_PREFIX;
  public static String DEFAULT_PROFILE_FILE_URL;
  public static String DEFAULT_COVER_FILE_URL;

  @PostConstruct
  private void init() {
    STORAGE_PATH = storagePath;
    USER_PROFILE_DEFAULT_PATH = userProfileDefaultPath;
    USER_COVER_DEFAULT_PATH = userCoverDefaultPath;
    PHOTO_ENDPOINT_PREFIX = photoEndpointPrefix;
    DEFAULT_PROFILE_FILE_URL = PHOTO_ENDPOINT_PREFIX + defaultProfileFileUrl;
    DEFAULT_COVER_FILE_URL = PHOTO_ENDPOINT_PREFIX + defaultCoverFileUrl;
  }
}
