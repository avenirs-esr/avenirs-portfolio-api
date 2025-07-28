package fr.avenirsesr.portfolio.security.infrastructure.filter;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import fr.avenirsesr.portfolio.security.infrastructure.model.HmacAuthenticationToken;
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
    when(request.getHeader("user-id")).thenReturn(TEST_USER_ID.toString());

    filter.doFilterInternal(request, response, filterChain);

    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    assertNotNull(auth);
    assertTrue(auth instanceof HmacAuthenticationToken);
    assertEquals(TEST_USER_ID, auth.getPrincipal());
    verify(filterChain).doFilter(request, response);
  }

  @Test
  void shouldNotSetAuthenticationWhenUserIdHeaderIsMissing() throws ServletException, IOException {
    when(request.getHeader("user-id")).thenReturn(null);

    filter.doFilterInternal(request, response, filterChain);

    assertNull(SecurityContextHolder.getContext().getAuthentication());
    verify(filterChain).doFilter(request, response);
  }

  @Test
  void shouldNotSetAuthenticationWhenUserIdHeaderIsBlank() throws ServletException, IOException {
    when(request.getHeader("user-id")).thenReturn(" ");

    filter.doFilterInternal(request, response, filterChain);

    assertNull(SecurityContextHolder.getContext().getAuthentication());
    verify(filterChain).doFilter(request, response);
  }

  @Test
  void shouldThrowExceptionForInvalidUUID() {
    when(request.getHeader("user-id")).thenReturn("invalid-uuid");

    assertThrows(
        IllegalArgumentException.class,
        () -> filter.doFilterInternal(request, response, filterChain));
  }

  @Test
  void shouldAlwaysFilter() {
    assertFalse(filter.shouldNotFilter(request));
  }
}
