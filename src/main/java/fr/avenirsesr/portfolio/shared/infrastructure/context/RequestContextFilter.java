package fr.avenirsesr.portfolio.shared.infrastructure.context;

import fr.avenirsesr.portfolio.shared.domain.model.enums.ELanguage;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component("userRequestContextFilter")
@Slf4j
public class RequestContextFilter extends OncePerRequestFilter {
  @Override
  public void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    try {
      var languageCode = Optional.ofNullable(request.getHeader("Accept-Language"));
      if (languageCode.isEmpty()) {
        log.error(
            "Accept-Language header not present, using fallback language {}", ELanguage.FALLBACK);
        languageCode = Optional.of(ELanguage.FALLBACK.getCode());
      }
      ELanguage preferredLanguage = ELanguage.fromCode(languageCode.get());

      RequestContext.set(new RequestData(preferredLanguage));

      filterChain.doFilter(request, response);
    } finally {
      RequestContext.clear();
    }
  }
}
