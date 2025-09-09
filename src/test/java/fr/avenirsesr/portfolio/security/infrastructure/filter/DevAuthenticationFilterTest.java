package fr.avenirsesr.portfolio.security.infrastructure.filter;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import fr.avenirsesr.portfolio.security.infrastructure.model.HmacAuthenticationToken;
import fr.avenirsesr.portfolio.testutils.BddLogger;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

class DevAuthenticationFilterTest {

  private DevAuthenticationFilter filter;
  private HttpServletRequest request;
  private HttpServletResponse response;
  private FilterChain filterChain;

  private static final UUID TEST_USER_ID = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");

  @BeforeEach
  void setUp() {
    filter = new DevAuthenticationFilter();
    request = mock(HttpServletRequest.class);
    response = mock(HttpServletResponse.class);
    filterChain = mock(FilterChain.class);
    SecurityContextHolder.clearContext();
  }

  @Test
  void shouldSetAuthenticationWhenUserIdHeaderIsPresent() throws ServletException, IOException {
    BddLogger.given("a request with user ID header");
    when(request.getHeader("user-id")).thenReturn(TEST_USER_ID.toString());

    filter.doFilterInternal(request, response, filterChain);

    BddLogger.when("getting authentication");
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();

    BddLogger.then("it should set authentication");
    assertNotNull(auth);
    assertTrue(auth instanceof HmacAuthenticationToken);
    assertEquals(TEST_USER_ID, auth.getPrincipal());
    verify(filterChain).doFilter(request, response);
  }

  @Test
  void shouldNotSetAuthenticationWhenUserIdHeaderIsMissing() throws ServletException, IOException {
    BddLogger.given("a request without user ID header");
    when(request.getHeader("user-id")).thenReturn(null);

    filter.doFilterInternal(request, response, filterChain);

    BddLogger.when("getting authentication");
    assertNull(SecurityContextHolder.getContext().getAuthentication());

    BddLogger.then("it should not set authentication");
    verify(filterChain).doFilter(request, response);
  }

  @Test
  void shouldNotSetAuthenticationWhenUserIdHeaderIsBlank() throws ServletException, IOException {
    BddLogger.given("a request with blank user ID header");
    when(request.getHeader("user-id")).thenReturn(" ");

    filter.doFilterInternal(request, response, filterChain);

    BddLogger.when("getting authentication");
    assertNull(SecurityContextHolder.getContext().getAuthentication());

    BddLogger.then("it should not set authentication");
    verify(filterChain).doFilter(request, response);
  }

  @Test
  void shouldThrowExceptionForInvalidUUID() {
    BddLogger.given("a request with invalid user ID header");
    when(request.getHeader("user-id")).thenReturn("invalid-uuid");

    BddLogger.when("getting authentication");
    BddLogger.then("it should throw IllegalArgumentException");
    assertThrows(
        IllegalArgumentException.class,
        () -> filter.doFilterInternal(request, response, filterChain));
  }

  @Test
  void shouldAlwaysFilter() {
    BddLogger.given("any request");
    BddLogger.when("performing the request");
    BddLogger.then("it should filter");
    assertFalse(filter.shouldNotFilter(request));
  }
}
