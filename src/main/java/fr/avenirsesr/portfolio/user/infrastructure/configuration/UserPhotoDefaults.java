package fr.avenirsesr.portfolio.user.infrastructure.configuration;

import fr.avenirsesr.portfolio.common.file.domain.model.enums.EFileType;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * Fallback pictures served when a user has uploaded none.
 *
 * <p>These assets ship with the deployment and belong to no user, so they are read from a Spring
 * resource location rather than from the storage backend. They are specific to the portfolio api:
 * the shared file registry knows nothing about profiles or covers.
 */
@Getter
@Setter
@Configuration
public class UserPhotoDefaults {
  /**
   * Read here rather than taken from {@link
   * fr.avenirsesr.portfolio.common.file.infrastructure.configuration.FileStorageConstants}: the
   * order in which two {@code @PostConstruct} methods run across beans is undefined, so reading
   * another bean's static field during initialisation would be a race.
   */
  @Value("${file.storage.endpoint-prefix}")
  private String endpointPrefix;

  @Value("${file.storage.profile.default-path}")
  private String profileDefaultPath;

  @Value("${file.storage.cover.default-path}")
  private String coverDefaultPath;

  @Value("${file.storage.profile.default-endpoint}")
  private String defaultProfileFileUrl;

  @Value("${file.storage.cover.default-endpoint}")
  private String defaultCoverFileUrl;

  public static String PROFILE_DEFAULT_PATH;
  public static EFileType PROFILE_DEFAULT_FILE_TYPE = EFileType.PNG;
  public static String COVER_DEFAULT_PATH;
  public static EFileType COVER_DEFAULT_FILE_TYPE = EFileType.PNG;

  public static String DEFAULT_PROFILE_FILE_URL;
  public static String DEFAULT_COVER_FILE_URL;

  @PostConstruct
  private void init() {
    PROFILE_DEFAULT_PATH = profileDefaultPath;
    COVER_DEFAULT_PATH = coverDefaultPath;
    DEFAULT_PROFILE_FILE_URL = endpointPrefix + defaultProfileFileUrl;
    DEFAULT_COVER_FILE_URL = endpointPrefix + defaultCoverFileUrl;
  }
}
