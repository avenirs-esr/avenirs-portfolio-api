package fr.avenirsesr.portfolio.shared.application.adapter;

import fr.avenirsesr.portfolio.file.domain.exception.FileStorageException;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.URI;
import org.springframework.web.multipart.MultipartFile;
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

  static byte[] readBytes(MultipartFile file) {
    try {
      return file.getBytes();
    } catch (IOException e) {
      throw new FileStorageException("Failed to read uploaded file", e);
    }
  }
}
