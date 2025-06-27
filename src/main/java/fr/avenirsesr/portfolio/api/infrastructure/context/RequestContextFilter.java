package fr.avenirsesr.portfolio.api.infrastructure.context;

import fr.avenirsesr.portfolio.api.domain.model.enums.ELanguage;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component("userRequestContextFilter")
@Slf4j
public class RequestContextFilter extends OncePerRequestFilter {
  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    try {
      if (request.getHeader("Accept-Language") == null) {
        throw new IOException("Accept-Language header not present");
      }
      ELanguage preferredLanguage = ELanguage.fromCode(request.getHeader("Accept-Language"));

      RequestContext.set(new RequestData(preferredLanguage));

      log.info("RequestContextFilter set : {}", RequestContext.get());
      filterChain.doFilter(request, response);

    } finally {
      RequestContext.clear();
      log.info("RequestContextFilter cleared");
    }
  }
}
