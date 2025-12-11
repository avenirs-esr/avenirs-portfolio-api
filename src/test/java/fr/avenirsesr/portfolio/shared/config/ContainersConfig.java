package fr.avenirsesr.portfolio.shared.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;

@TestConfiguration(proxyBeanMethods = false)
public class ContainersConfig {

  @Bean
  @ServiceConnection(name = "redis")
  public GenericContainer<?> valkeyContainer() {
    return new GenericContainer<>(DockerImageName.parse("valkey/valkey:8.0"))
        .withExposedPorts(6379);
  }
}
