package fr.avenirsesr.portfolio.additionalskill.infrastructure.configuration;

import org.apache.http.HttpHost;
import org.opensearch.client.RestClient;
import org.opensearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenSearchConfig {

  @Value("${opensearch.docker.protocol}")
  private String opensearchProtocol;

  @Value("${opensearch.docker.host}")
  private String opensearchHost;

  @Value("${opensearch.docker.port}")
  private int opensearchPort;

  @Bean
  public RestHighLevelClient restHighLevelClient() {
    return new RestHighLevelClient(
        RestClient.builder(new HttpHost(opensearchHost, opensearchPort, opensearchProtocol)));
  }
}
