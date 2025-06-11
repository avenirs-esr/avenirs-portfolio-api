package fr.avenirsesr.portfolio.api.infrastructure.adapter.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import fr.avenirsesr.portfolio.api.domain.exception.UserNotAuthorizedException;
import fr.avenirsesr.portfolio.api.domain.model.enums.ESecurityKeys;
import fr.avenirsesr.portfolio.api.infrastructure.adapter.model.UserPayload;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@Component
public class HmacAuthenticationFilter extends OncePerRequestFilter {

  @Value("${security.permit-all-paths}")
  private String permitAllPathsString;

  private List<String> permitAllPathsList;

  public HmacAuthenticationFilter() {}

  @Override
  protected void doFilterInternal(
      @NonNull HttpServletRequest request,
      @NonNull HttpServletResponse response,
      @NonNull FilterChain filterChain)
      throws ServletException, IOException {

    String signature = request.getHeader("X-Context-Signature");
    String secretKey = ESecurityKeys.getSecretByKey(request.getHeader("X-Context-Kid"));
    String payload = request.getHeader("X-Signed-Context");
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
    UserPayload userPayload = objectMapper.readValue(payload, UserPayload.class);

    if (!payloadIsValid(userPayload)) {
      UserNotAuthorizedException exception = new UserNotAuthorizedException();
      log.error("Invalid HMAC authentication attempt. Payload is expired or invalid. {}", payload);
      throw exception;
    }

    if (signature != null && verifySignature(payload, signature, secretKey)) {
      Authentication auth = new HmacAuthenticationToken(userPayload.getSub());
      SecurityContextHolder.getContext().setAuthentication(auth);

      filterChain.doFilter(request, response);
    } else {
      UserNotAuthorizedException exception = new UserNotAuthorizedException();
      log.error("Invalid HMAC authentication attempt.{}", String.valueOf(exception));
      throw exception;
    }
  }

  @Override
  protected boolean shouldNotFilter(@NonNull HttpServletRequest request) throws ServletException {
    if (permitAllPathsList == null) {
      permitAllPathsList =
          Arrays.stream(permitAllPathsString.split(","))
              .map(path -> path.trim().replace("/**", ""))
              .toList();
    }

    String path = request.getRequestURI();
    return permitAllPathsList.stream().anyMatch(path::startsWith);
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
