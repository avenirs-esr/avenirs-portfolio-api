package fr.avenirsesr.portfolio.file.infrastructure.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/** Connection settings of the S3-compatible backend, read when {@code file.storage.type=s3}. */
@Getter
@Setter
@ConfigurationProperties(prefix = "file.storage.s3")
public class S3StorageProperties {

  /** Base URL of the S3 API, for instance {@code https://s3.example.org}. */
  private String endpoint;

  /**
   * Region sent along with the request signature. The AWS SDK refuses to build a client without one
   * even when the backend ignores it, in which case any placeholder such as {@code us-east-1} does.
   */
  private String region = "us-east-1";

  /** Bucket holding every file of this environment. */
  private String bucket;

  private String accessKey;

  private String secretKey;

  /**
   * Addresses buckets as {@code <endpoint>/<bucket>} rather than {@code <bucket>.<endpoint>}.
   * Required by MinIO and Ceph deployments served from a bare host or an IP address.
   */
  private boolean pathStyleAccess = true;
}
