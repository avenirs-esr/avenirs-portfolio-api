package fr.avenirsesr.portfolio.security.infrastructure.filter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import fr.avenirsesr.portfolio.security.infrastructure.model.enums.ESecurityKeys;
import fr.avenirsesr.portfolio.user.domain.exception.UserNotAuthorizedException;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.model.UserPayload;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.UUID;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class HmacAuthenticationFilterTest {

  @Spy @InjectMocks private HmacAuthenticationFilter filter;

  @Mock private HttpServletRequest request;

  @Mock private HttpServletResponse response;

  @Mock private FilterChain filterChain;

  private ObjectMapper objectMapper;
  private static final String TEST_KEY = "TEST_KEY";
  private static final String TEST_SECRET = "test-secret";
  private static final UUID TEST_UUID = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");

  @BeforeEach
  void setUp() {
    SecurityContextHolder.clearContext();
    objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
    ReflectionTestUtils.setField(
        filter,
        "permitAllPathsString",
        "/avenirs-portfolio-api/swagger-ui/**,/avenirs-portfolio-api/api-docs/**,/favicon.ico,/actuator/health");
  }

  @Test
  void shouldNotFilterPublicPaths() throws ServletException {
    when(request.getRequestURI()).thenReturn("/avenirs-portfolio-api/swagger-ui/index.html");
    assertTrue(filter.shouldNotFilter(request));

    when(request.getRequestURI()).thenReturn("/avenirs-portfolio-api/api-docs");
    assertTrue(filter.shouldNotFilter(request));

    when(request.getRequestURI()).thenReturn("/favicon.ico");
    assertTrue(filter.shouldNotFilter(request));

    when(request.getRequestURI()).thenReturn("/actuator/health");
    assertTrue(filter.shouldNotFilter(request));

    when(request.getRequestURI()).thenReturn("/api/protected");
    assertFalse(filter.shouldNotFilter(request));
  }

  @Test
  void shouldNotFilterPublicPath() {
    try {
      ReflectionTestUtils.setField(filter, "permitAllPathsString", "/public/path,/another/path");
      ReflectionTestUtils.setField(
          filter, "permitAllPathsList", java.util.Arrays.asList("/public/path", "/another/path"));

      when(request.getRequestURI()).thenReturn("/public/path/resource");

      assertTrue(filter.shouldNotFilter(request));
    } catch (Exception e) {
      fail("Test setup failed: " + e.getMessage());
    }
  }

  @Test
  void shouldFilterNonPublicPath() {
    try {
      ReflectionTestUtils.setField(filter, "permitAllPathsString", "/public/path,/another/path");
      ReflectionTestUtils.setField(
          filter, "permitAllPathsList", java.util.Arrays.asList("/public/path", "/another/path"));

      when(request.getRequestURI()).thenReturn("/protected/resource");

      assertFalse(filter.shouldNotFilter(request));
    } catch (Exception e) {
      fail("Test setup failed: " + e.getMessage());
    }
  }

  @Test
  void shouldAuthenticateWithValidSignature() throws ServletException, IOException {
    UserPayload userPayload = new UserPayload();
    userPayload.setSub(TEST_UUID);
    userPayload.setExp(Instant.now().plusSeconds(3600));

    String payload = objectMapper.writeValueAsString(userPayload);
    String signature = generateHmacSignature(payload);

    when(request.getHeader("X-Context-Kid")).thenReturn(TEST_KEY);
    when(request.getHeader("X-Context-Signature")).thenReturn(signature);
    when(request.getHeader("X-Signed-Context")).thenReturn(payload);

    try (var mockedStatic = mockStatic(ESecurityKeys.class)) {
      mockedStatic.when(() -> ESecurityKeys.getSecretByKey(TEST_KEY)).thenReturn(TEST_SECRET);

      filter.doFilterInternal(request, response, filterChain);

      verify(filterChain).doFilter(request, response);
      assertNotNull(SecurityContextHolder.getContext().getAuthentication());
      assertEquals(
          TEST_UUID, SecurityContextHolder.getContext().getAuthentication().getPrincipal());
    }
  }

  @Test
  void shouldRejectExpiredPayload() {
    UserPayload userPayload = new UserPayload();
    userPayload.setSub(TEST_UUID);
    userPayload.setExp(Instant.now().minusSeconds(3600));

    try {
      String payload = objectMapper.writeValueAsString(userPayload);
      String signature = generateHmacSignature(payload);

      when(request.getHeader("X-Context-Kid")).thenReturn(TEST_KEY);
      when(request.getHeader("X-Context-Signature")).thenReturn(signature);
      when(request.getHeader("X-Signed-Context")).thenReturn(payload);

      try (var mockedStatic = mockStatic(ESecurityKeys.class)) {
        mockedStatic.when(() -> ESecurityKeys.getSecretByKey(TEST_KEY)).thenReturn(TEST_SECRET);

        assertThrows(
            UserNotAuthorizedException.class,
            () -> filter.doFilterInternal(request, response, filterChain));
      }
    } catch (Exception e) {
      fail("Test setup failed: " + e.getMessage());
    }
  }

  @Test
  void shouldRejectInvalidSignature() {
    UserPayload userPayload = new UserPayload();
    userPayload.setSub(TEST_UUID);
    userPayload.setExp(Instant.now().plusSeconds(3600));

    try {
      String payload = objectMapper.writeValueAsString(userPayload);
      String invalidSignature = "invalid-signature";

      when(request.getHeader("X-Context-Kid")).thenReturn(TEST_KEY);
      when(request.getHeader("X-Context-Signature")).thenReturn(invalidSignature);
      when(request.getHeader("X-Signed-Context")).thenReturn(payload);

      try (var mockedStatic = mockStatic(ESecurityKeys.class)) {
        mockedStatic.when(() -> ESecurityKeys.getSecretByKey(TEST_KEY)).thenReturn(TEST_SECRET);

        assertThrows(
            UserNotAuthorizedException.class,
            () -> filter.doFilterInternal(request, response, filterChain));
      }
    } catch (Exception e) {
      fail("Test setup failed: " + e.getMessage());
    }
  }

  @Test
  void shouldHandleExceptionDuringSignatureVerification() {
    UserPayload userPayload = new UserPayload();
    userPayload.setSub(TEST_UUID);
    userPayload.setExp(Instant.now().plusSeconds(3600));

    try {
      String payload = objectMapper.writeValueAsString(userPayload);

      when(request.getHeader("X-Context-Kid")).thenReturn(TEST_KEY);
      when(request.getHeader("X-Context-Signature")).thenReturn("valid-looking-signature");
      when(request.getHeader("X-Signed-Context")).thenReturn(payload);

      try (var mockedStatic = mockStatic(ESecurityKeys.class)) {
        mockedStatic.when(() -> ESecurityKeys.getSecretByKey(TEST_KEY)).thenReturn(null);

        assertThrows(
            UserNotAuthorizedException.class,
            () -> filter.doFilterInternal(request, response, filterChain));
      }
    } catch (Exception e) {
      fail("Test setup failed: " + e.getMessage());
    }
  }

  @Test
  void shouldRejectNullPayload() {
    try {
      when(request.getHeader("X-Context-Kid")).thenReturn(TEST_KEY);
      when(request.getHeader("X-Context-Signature")).thenReturn("some-signature");
      when(request.getHeader("X-Signed-Context")).thenReturn("invalid-json");

      try (var mockedStatic = mockStatic(ESecurityKeys.class)) {
        mockedStatic.when(() -> ESecurityKeys.getSecretByKey(TEST_KEY)).thenReturn(TEST_SECRET);

        assertThrows(
            Exception.class, () -> filter.doFilterInternal(request, response, filterChain));
      }
    } catch (Exception e) {
      fail("Test setup failed: " + e.getMessage());
    }
  }

  @Test
  void shouldRejectNullPayloadString() {
    try {
      when(request.getHeader("X-Context-Kid")).thenReturn(TEST_KEY);
      when(request.getHeader("X-Context-Signature")).thenReturn("some-signature");
      when(request.getHeader("X-Signed-Context")).thenReturn(null);

      try (var mockedStatic = mockStatic(ESecurityKeys.class)) {
        mockedStatic.when(() -> ESecurityKeys.getSecretByKey(TEST_KEY)).thenReturn(TEST_SECRET);

        assertThrows(
            Exception.class, () -> filter.doFilterInternal(request, response, filterChain));
      }
    } catch (Exception e) {
      fail("Test setup failed: " + e.getMessage());
    }
  }

  @Test
  void shouldRejectMissingSecretKey() {
    UserPayload userPayload = new UserPayload();
    userPayload.setSub(TEST_UUID);
    userPayload.setExp(Instant.now().plusSeconds(3600));

    try {
      String payload = objectMapper.writeValueAsString(userPayload);
      String signature = generateHmacSignature(payload);

      when(request.getHeader("X-Context-Kid")).thenReturn("UNKNOWN_KEY");
      when(request.getHeader("X-Context-Signature")).thenReturn(signature);
      when(request.getHeader("X-Signed-Context")).thenReturn(payload);

      try (var mockedStatic = mockStatic(ESecurityKeys.class)) {
        mockedStatic.when(() -> ESecurityKeys.getSecretByKey("UNKNOWN_KEY")).thenReturn(null);

        assertThrows(
            UserNotAuthorizedException.class,
            () -> filter.doFilterInternal(request, response, filterChain));
      }
    } catch (Exception e) {
      fail("Test setup failed: " + e.getMessage());
    }
  }

  @Test
  void shouldRejectMissingSignature() {
    UserPayload userPayload = new UserPayload();
    userPayload.setSub(TEST_UUID);
    userPayload.setExp(Instant.now().plusSeconds(3600));

    try {
      String payload = objectMapper.writeValueAsString(userPayload);

      when(request.getHeader("X-Context-Kid")).thenReturn(TEST_KEY);
      when(request.getHeader("X-Context-Signature")).thenReturn(null);
      when(request.getHeader("X-Signed-Context")).thenReturn(payload);

      try (var mockedStatic = mockStatic(ESecurityKeys.class)) {
        mockedStatic.when(() -> ESecurityKeys.getSecretByKey(TEST_KEY)).thenReturn(TEST_SECRET);

        assertThrows(
            UserNotAuthorizedException.class,
            () -> filter.doFilterInternal(request, response, filterChain));
      }
    } catch (Exception e) {
      fail("Test setup failed: " + e.getMessage());
    }
  }

  @Test
  void shouldRejectPayloadWithNullExpiration() {
    UserPayload userPayload = new UserPayload();
    userPayload.setSub(TEST_UUID);
    userPayload.setExp(null);

    try {
      String payload = objectMapper.writeValueAsString(userPayload);
      String signature = generateHmacSignature(payload);

      when(request.getHeader("X-Context-Kid")).thenReturn(TEST_KEY);
      when(request.getHeader("X-Context-Signature")).thenReturn(signature);
      when(request.getHeader("X-Signed-Context")).thenReturn(payload);

      try (var mockedStatic = mockStatic(ESecurityKeys.class)) {
        mockedStatic.when(() -> ESecurityKeys.getSecretByKey(TEST_KEY)).thenReturn(TEST_SECRET);

        assertThrows(
            Exception.class, () -> filter.doFilterInternal(request, response, filterChain));
      }
    } catch (Exception e) {
      fail("Test setup failed: " + e.getMessage());
    }
  }

  private String generateHmacSignature(String payload) {
    try {
      Mac sha256Hmac = Mac.getInstance("HmacSHA256");
      SecretKeySpec secretKeySpec =
          new SecretKeySpec(
              HmacAuthenticationFilterTest.TEST_SECRET.getBytes(StandardCharsets.UTF_8),
              "HmacSHA256");
      sha256Hmac.init(secretKeySpec);

      byte[] signedBytes = sha256Hmac.doFinal(payload.getBytes(StandardCharsets.UTF_8));
      return Base64.getEncoder().encodeToString(signedBytes);
    } catch (Exception e) {
      throw new RuntimeException("Failed to generate HMAC signature", e);
    }
  }
}
