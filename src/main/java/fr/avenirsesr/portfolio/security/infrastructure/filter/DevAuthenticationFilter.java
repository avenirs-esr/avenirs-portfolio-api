package fr.avenirsesr.portfolio.security.infrastructure.filter;

import fr.avenirsesr.portfolio.security.infrastructure.model.HmacAuthenticationToken;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
public class DevAuthenticationFilter extends OncePerRequestFilter {

  public DevAuthenticationFilter() {}

  @Override
  protected boolean shouldNotFilter(@NonNull HttpServletRequest request) {
    return false;
  }

  @Override
  protected void doFilterInternal(
      @NonNull HttpServletRequest request,
      @NonNull HttpServletResponse response,
      @NonNull FilterChain filterChain)
      throws ServletException, IOException {
    String devUser = request.getHeader("user-id");
    if (devUser != null && !devUser.isBlank()) {
      Authentication auth = new HmacAuthenticationToken(UUID.fromString(devUser));
      SecurityContextHolder.getContext().setAuthentication(auth);
      log.debug("Dev authentication enabled for user: {}", devUser);
    }
    filterChain.doFilter(request, response);
  }
}
