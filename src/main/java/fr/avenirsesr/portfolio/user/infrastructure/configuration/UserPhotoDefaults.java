package fr.avenirsesr.portfolio.user.infrastructure.configuration;

import fr.avenirsesr.portfolio.common.file.domain.model.enums.EFileType;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
public class UserPhotoDefaults {
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
