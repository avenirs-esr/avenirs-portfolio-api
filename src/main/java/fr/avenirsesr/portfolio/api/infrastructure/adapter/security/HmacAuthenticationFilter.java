package fr.avenirsesr.portfolio.api.infrastructure.adapter.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import fr.avenirsesr.portfolio.api.domain.exception.UserNotAuthorizedException;
import fr.avenirsesr.portfolio.api.infrastructure.adapter.model.UserPayload;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
public class HmacAuthenticationFilter extends OncePerRequestFilter {

  private final ObjectMapper objectMapper = new ObjectMapper();

  public HmacAuthenticationFilter() {}

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    String signature = request.getHeader("X-Context-Signature");
    String secretKey = request.getHeader("X-Context-Kid");
    String payload = request.getHeader("X-Signed-Context");
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
    UserPayload userPayload = objectMapper.readValue(payload, UserPayload.class);

    if (!payloadIsValid(userPayload)) {
      UserNotAuthorizedException exception = new UserNotAuthorizedException();
      log.error("Invalid HMAC authentication attempt. Payload is expired or invalid. {}",payload);
      throw exception;
    }

    if (signature != null && verifySignature(payload, signature, secretKey)) {
      Authentication auth = new HmacAuthenticationToken(userPayload.getId());
      SecurityContextHolder.getContext().setAuthentication(auth);

      filterChain.doFilter(request, response);
    } else {
      UserNotAuthorizedException exception = new UserNotAuthorizedException();
      log.error("Invalid HMAC authentication attempt.{}", String.valueOf(exception));
      throw exception;
    }
  }

  private boolean payloadIsValid(UserPayload payload) {
    return payload != null && payload.getExp().isAfter(Instant.now());
  }

  private boolean verifySignature(String payload, String signature, String secretKey) {
    try {
      Mac sha256Hmac = Mac.getInstance("HmacSHA256");
      SecretKeySpec secretKeySpec =
          new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
      sha256Hmac.init(secretKeySpec);

      byte[] signedBytes = sha256Hmac.doFinal(payload.getBytes(StandardCharsets.UTF_8));
      String computedSignature = Base64.getEncoder().encodeToString(signedBytes);

      return computedSignature.equals(signature);
    } catch (Exception e) {
      return false;
    }
  }
}
