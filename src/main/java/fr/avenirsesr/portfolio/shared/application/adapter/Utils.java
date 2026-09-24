package fr.avenirsesr.portfolio.shared.application.adapter;

import jakarta.servlet.http.HttpServletRequest;
import java.net.URI;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

public interface Utils {
  static String extractOrigin(HttpServletRequest request) {
    try {
      URI referer = URI.create(request.getHeader("Referer"));

      return ServletUriComponentsBuilder.fromRequestUri(request)
          .replacePath("/apim")
          .scheme(referer.getScheme() != null ? referer.getScheme() : request.getScheme())
          .host(referer.getHost() != null ? referer.getHost() : request.getServerName())
          .port(referer.getHost() != null ? referer.getPort() : request.getServerPort())
          .build()
          .toUriString();
    } catch (Exception e) {
      return null;
    }
  }
}
