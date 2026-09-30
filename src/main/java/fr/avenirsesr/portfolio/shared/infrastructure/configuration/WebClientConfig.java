package fr.avenirsesr.portfolio.shared.infrastructure.configuration;

import fr.avenirsesr.portfolio.common.security.infrastructure.adapter.model.AvenirsSecurityHeaders;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

  @Bean
  public WebClient webClient() {
    return WebClient.builder().filter(forwardUserContext()).build();
  }

  private ExchangeFilterFunction forwardUserContext() {
    return (request, next) -> {
      if (!(RequestContextHolder.getRequestAttributes()
          instanceof ServletRequestAttributes attributes)) {
        return next.exchange(request);
      }

      HttpServletRequest current = attributes.getRequest();
      String signedContext = current.getHeader(AvenirsSecurityHeaders.SIGNED_CONTEXT);
      String signature = current.getHeader(AvenirsSecurityHeaders.CONTEXT_SIGNATURE);
      if (signedContext == null || signature == null) {
        return next.exchange(request);
      }

      ClientRequest forwarded =
          ClientRequest.from(request)
              .headers(
                  headers -> {
                    headers.remove(AvenirsSecurityHeaders.API_KEY);
                    headers.set(AvenirsSecurityHeaders.SIGNED_CONTEXT, signedContext);
                    headers.set(AvenirsSecurityHeaders.CONTEXT_SIGNATURE, signature);
                  })
              .build();
      return next.exchange(forwarded);
    };
  }
}
